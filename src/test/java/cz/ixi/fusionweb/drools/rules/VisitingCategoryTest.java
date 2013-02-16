package cz.ixi.fusionweb.drools.rules;

import static org.junit.Assert.assertEquals;
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

import cz.ixi.fusionweb.drools.model.CategoryNavigationEvent;

/**
 * Tests rules considering searching products.
 */
public class VisitingCategoryTest {

    private StatefulKnowledgeSession ksession;
    private KnowledgeBase kbase;
    private FiredRulesListener firedRules;
    private StatisticsRecordHourlyChannelMock statisticsHourly;
    private StatisticsRecordDailyChannelMock statisticsDaily;

    @Before
    public void setUp() {
	KnowledgeBuilderConfiguration kbconf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
	kbconf.setProperty("drools.accumulate.function.mostVisitedCategory",
		"cz.ixi.fusionweb.drools.functions.MostVisitedCategoryFunction");

	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbconf);
	kbuilder.add(new ClassPathResource("imports-and-declarations.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("visiting-category.drl", getClass()), ResourceType.DRL);
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
    public void categoryReportInTheLastHour() {
	String ruleVisited = "Report most visited category in the last hour if any.";
	SessionPseudoClock clock = ksession.getSessionClock();
	Calendar cal = new GregorianCalendar(2013, 01, 01, 14, 0, 0);
	clock.advanceTime(cal.getTimeInMillis(), TimeUnit.MILLISECONDS);
	ksession.insert(clock);

	ksession.fireAllRules();

	clock.advanceTime(5, TimeUnit.MINUTES);

	ksession.fireAllRules();

	assertEquals(1, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(1, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("No category in the menu was visited in the last hour."));

	ksession.fireAllRules();

	for (int i = 0; i < 60; i++) {
	    ksession.fireAllRules();
	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	ksession.fireAllRules();

	assertEquals(2, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(2, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("No category in the menu was visited in the last hour."));

	ksession.fireAllRules();

	for (int i = 0; i < 5; i++) {
	    ksession.fireAllRules();
	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	ksession.fireAllRules();

	for (int i = 0; i < 100; i++) {
	    CategoryNavigationEvent pne = new CategoryNavigationEvent("rick", 1, "category");
	    pne.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(pne);
	}

	for (int i = 0; i < 99; i++) {
	    CategoryNavigationEvent pne = new CategoryNavigationEvent("rick", 42, "p");
	    pne.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(pne);
	}

	for (int i = 0; i < 60; i++) {
	    ksession.fireAllRules();

	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	assertEquals(3, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(3, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("category(1)"));

	for (int i = 0; i < 60; i++) {
	    ksession.fireAllRules();

	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	assertEquals(4, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(4, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("No category in the menu was visited in the last hour."));

	ksession.fireAllRules();

	for (int i = 0; i < 5; i++) {
	    ksession.fireAllRules();
	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	ksession.fireAllRules();

	CategoryNavigationEvent pne = new CategoryNavigationEvent("rick", 5, "m");
	pne.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(pne);

	for (int i = 0; i < 60; i++) {
	    ksession.fireAllRules();

	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	assertEquals(5, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(5, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("m(5)"));
    }

    @Test
    public void categoryReportInTheLastDay() {
	String ruleVisited = "Report most visited category in the last day if any.";
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
	assertTrue(statisticsDaily.getDescription().contains("No category in the menu was visited in the last day."));

	ksession.fireAllRules();

	for (int i = 0; i < 5; i++) {
	    ksession.fireAllRules();
	    clock.advanceTime(1, TimeUnit.MINUTES);
	}

	ksession.fireAllRules();

	for (int i = 0; i < 100; i++) {
	    CategoryNavigationEvent pne = new CategoryNavigationEvent("rick", 1, "category");
	    pne.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(pne);
	}

	for (int i = 0; i < 99; i++) {
	    CategoryNavigationEvent pne = new CategoryNavigationEvent("rick", 42, "p");
	    pne.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(pne);
	}

	for (int i = 0; i < 24; i++) {
	    ksession.fireAllRules();

	    clock.advanceTime(1, TimeUnit.HOURS);
	}

	assertEquals(2, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(2, statisticsDaily.getCreatedStatistics());
	assertTrue(statisticsDaily.getDescription().contains("category(1)"));

	for (int i = 0; i < 24; i++) {
	    ksession.fireAllRules();

	    clock.advanceTime(1, TimeUnit.HOURS);
	}

	assertEquals(3, firedRules.howManyTimesIsRuleFired(ruleVisited));
	assertEquals(3, statisticsDaily.getCreatedStatistics());
	assertTrue(statisticsDaily.getDescription().contains("No category in the menu was visited in the last day."));
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
