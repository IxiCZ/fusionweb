package cz.ixi.fusionweb.drools.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.Channel;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cz.ixi.fusionweb.drools.model.ProductBoughtEvent;
import cz.ixi.fusionweb.drools.model.ProductNavigationEvent;
import cz.ixi.fusionweb.entities.Notification;

/**
 * Tests rules considering searching products.
 */
public class VisitingTest {

    private StatefulKnowledgeSession ksession;
    private KnowledgeBase kbase;
    private FiredRulesListener firedRules;
    private NotificationsGeneralChannelMock notificationsGeneral;
    private StatisticsRecordHourlyChannelMock statisticsHourly;
    private StatisticsRecordDailyChannelMock statisticsDaily;

    @Before
    public void setUp() {
	KnowledgeBuilderConfiguration kbconf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
	kbconf.setProperty("drools.accumulate.function.mostVisitedProduct",
		"cz.ixi.fusionweb.drools.functions.MostVisitedProductFunction");

	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbconf);
	kbuilder.add(new ClassPathResource("imports-and-declarations.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("visiting.drl", getClass()), ResourceType.DRL);
	Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());

	KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
	config.setOption(EventProcessingOption.STREAM);

	kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
	kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

	KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
	conf.setOption(ClockTypeOption.get("pseudo"));
	ksession = kbase.newStatefulKnowledgeSession(conf, null);

	firedRules = new FiredRulesListener();
	ksession.addEventListener(firedRules);

	notificationsGeneral = new NotificationsGeneralChannelMock();
	ksession.registerChannel("notificationsGeneral", notificationsGeneral);

	statisticsHourly = new StatisticsRecordHourlyChannelMock();
	ksession.registerChannel("statisticsHourly", statisticsHourly);

	statisticsDaily = new StatisticsRecordDailyChannelMock();
	ksession.registerChannel("statisticsDaily", statisticsDaily);
    }

    @After
    public void tearDown() {
	if (ksession != null) {
	    ksession.dispose();
	} else {
	    System.err.println("KSession was null.");
	}
    }

    @Test
    public void productVisitedButNotBougt() {
	String rule = "Create notification if there is visited, but not bought product.";
	SessionPseudoClock clock = ksession.getSessionClock();

	clock.advanceTime(5, TimeUnit.MINUTES);

	for (int i = 0; i < 100; i++) {
	    ksession.insert(new ProductNavigationEvent("rick", 42, "p"));
	    if (i % 2 == 0) {
		ksession.insert(new ProductBoughtEvent(4, 42, "rick", "p"));
	    }
	}

	ksession.fireAllRules();
	assertFalse(firedRules.isRuleFired(rule));

	for (int i = 0; i < 100; i++) {
	    ksession.insert(new ProductNavigationEvent("rick", 42, "p"));
	}

	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, notificationsGeneral.getCreatedNotifications());
	assertTrue(notificationsGeneral.getDescription().contains("product 42"));
    }

    @Test
    public void productReportInTheLastHour() {
	String ruleVisited = "Report most visited product in the last hour if any.";
	SessionPseudoClock clock = ksession.getSessionClock();
	Calendar cal = new GregorianCalendar(2013, 01, 01, 14, 0, 0);
	clock.advanceTime(cal.getTimeInMillis(), TimeUnit.MILLISECONDS);
	ksession.insert(clock);

	ksession.fireAllRules();

	clock.advanceTime(5, TimeUnit.MINUTES);

	ksession.fireAllRules();

	assertEquals(1, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(1, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("No product was visited in the last hour."));

	ksession.fireAllRules();

	for (int i = 0; i < 60; i++) {
	    ksession.fireAllRules();
	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	ksession.fireAllRules();

	assertEquals(2, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(2, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("No product was visited in the last hour."));

	ksession.fireAllRules();

	for (int i = 0; i < 5; i++) {
	    ksession.fireAllRules();
	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	ksession.fireAllRules();

	for (int i = 0; i < 100; i++) {
	    ProductNavigationEvent pne = new ProductNavigationEvent("rick", 1, "product");
	    pne.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(pne);
	}

	for (int i = 0; i < 99; i++) {
	    ProductNavigationEvent pne = new ProductNavigationEvent("rick", 42, "p");
	    pne.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(pne);
	}

	for (int i = 0; i < 60; i++) {
	    ksession.fireAllRules();

	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	assertEquals(3, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(3, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("product(1)"));

	for (int i = 0; i < 5; i++) {
	    ksession.fireAllRules();
	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	ProductNavigationEvent pne = new ProductNavigationEvent("rick", 5, "product");
	pne.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(pne);

	for (int i = 0; i < 60; i++) {
	    ksession.fireAllRules();

	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	assertEquals(4, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(4, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("product(5)"));
	
	for (int i = 0; i < 60; i++) {
	    ksession.fireAllRules();

	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	assertEquals(5, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(5, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("No product was visited in the last hour."));
    }

    @Test
    public void productReportInTheLastDay() {
	String ruleVisited = "Report most visited product in the last day if any.";
	SessionPseudoClock clock = ksession.getSessionClock();
	Calendar cal = new GregorianCalendar(2013, 01, 01, 14, 0, 0);
	clock.advanceTime(cal.getTimeInMillis(), TimeUnit.MILLISECONDS);
	ksession.insert(clock);

	ksession.fireAllRules();

	clock.advanceTime(5, TimeUnit.MINUTES);

	ksession.fireAllRules();

	for (int i = 0; i < 10; i++) {
	    ksession.fireAllRules();
	    clock.advanceTime(1, TimeUnit.HOURS);
	}

	ksession.fireAllRules();
	
	assertEquals(1, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(1, statisticsDaily.getCreatedStatistics());
	assertTrue(statisticsDaily.getDescription().contains("No product was visited in the last day."));

	ksession.fireAllRules();

	for (int i = 0; i < 5; i++) {
	    ksession.fireAllRules();
	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	ksession.fireAllRules();

	for (int i = 0; i < 100; i++) {
	    ProductNavigationEvent pne = new ProductNavigationEvent("rick", 1, "product");
	    pne.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(pne);
	}

	for (int i = 0; i < 99; i++) {
	    ProductNavigationEvent pne = new ProductNavigationEvent("rick", 42, "p");
	    pne.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(pne);
	}

	for (int i = 0; i < 24; i++) {
	    ksession.fireAllRules();

	    clock.advanceTime(1, TimeUnit.HOURS);
	}

	assertEquals(2, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(2, statisticsDaily.getCreatedStatistics());
	assertTrue(statisticsDaily.getDescription().contains("product(1)"));

	for (int i = 0; i < 24; i++) {
	    ksession.fireAllRules();

	    clock.advanceTime(1, TimeUnit.HOURS);
	}

	assertEquals(3, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(3, statisticsDaily.getCreatedStatistics());
	assertTrue(statisticsDaily.getDescription().contains("No product was visited in the last day."));
    }

    private class NotificationsGeneralChannelMock implements Channel {

	private int createdNotifications;
	private String description;

	public int getCreatedNotifications() {
	    return createdNotifications;
	}

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	@Override
	public void send(Object object) {
	    setDescription(((Notification) object).getDescription());
	    createdNotifications++;
	}
    }

    private class StatisticsRecordHourlyChannelMock implements Channel {

	private int createdStatistics;
	private String description;

	public int getCreatedStatistics() {
	    return createdStatistics;
	}

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	@Override
	public void send(Object object) {
	    setDescription(object.toString());
	    createdStatistics++;
	}
    }

    private class StatisticsRecordDailyChannelMock implements Channel {

	private int createdStatistics;
	private String description;

	public int getCreatedStatistics() {
	    return createdStatistics;
	}

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	@Override
	public void send(Object object) {
	    setDescription(object.toString());
	    createdStatistics++;
	}
    }
}
