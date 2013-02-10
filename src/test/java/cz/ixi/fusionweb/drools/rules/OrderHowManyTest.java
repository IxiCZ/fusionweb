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

import cz.ixi.fusionweb.drools.model.OrderCreatedEvent;
import cz.ixi.fusionweb.drools.model.ProductBoughtEvent;

/**
 * Tests rules considering orders.
 */
public class OrderHowManyTest {

    private StatefulKnowledgeSession ksession;
    private KnowledgeBase kbase;
    private FiredRulesListener firedRules;
    private StatisticsRecordHourlyChannelMock statisticsHourly;
    private StatisticsRecordDailyChannelMock statisticsDaily;

    @Before
    public void setUp() {
	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	kbuilder.add(new ClassPathResource("imports-and-declarations.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("order-how-many.drl", getClass()), ResourceType.DRL);
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
    public void reportRightNumberOfBoughtProductsAndCreatedOrdersInLastHour() {
	String rule = "Report how many orders were created and items bought in in the last hour";
	SessionPseudoClock clock = ksession.getSessionClock();
	Calendar cal = new GregorianCalendar(2013, 01, 01, 14, 0, 0);
	clock.advanceTime(cal.getTimeInMillis(), TimeUnit.MILLISECONDS);
	ksession.fireAllRules();

	clock.advanceTime(5, TimeUnit.MINUTES);
	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("0 order(s)"));
	assertTrue(statisticsHourly.getDescription().contains("0 product(s)"));

	OrderCreatedEvent oce = new OrderCreatedEvent(1, "rick", 2, 60.0);
	oce.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(oce);
	ProductBoughtEvent pbe = new ProductBoughtEvent(1, 1, "rick", "dvd");
	pbe.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(pbe);
	pbe = new ProductBoughtEvent(1, 2, "rick", "cd");
	pbe.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(pbe);

	oce = new OrderCreatedEvent(2, "rick2", 1, 50.0);
	oce.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(oce);
	pbe = new ProductBoughtEvent(2, 3, "rick2", "dvd");
	pbe.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(pbe);

	ksession.fireAllRules();

	clock.advanceTime(55, TimeUnit.MINUTES);

	assertEquals(2, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(2, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("2 order(s)"));
	assertTrue(statisticsHourly.getDescription().contains("3 product(s)"));

	clock.advanceTime(5, TimeUnit.MINUTES);

	oce = new OrderCreatedEvent(3, "rick2", 4, 50.0);
	oce.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(oce);

	for (int i = 1; i <= 4; i++) {
	    ProductBoughtEvent pbe2 = new ProductBoughtEvent(3, 4, "rick3", "dvd");
	    pbe2.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(pbe2);
	}
	ksession.fireAllRules();

	clock.advanceTime(55, TimeUnit.MINUTES);
	assertEquals(3, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(3, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("1 order(s)"));
	assertTrue(statisticsHourly.getDescription().contains("4 product(s)"));
    }

    @Test
    public void reportRightNumberOfBoughtProductsAndCreatedOrdersInLastDay() {
	String rule = "Report how many orders were created and items bought in in the last day";
	SessionPseudoClock clock = ksession.getSessionClock();
	Calendar cal = new GregorianCalendar(2013, 01, 01, 14, 0, 0);
	clock.advanceTime(cal.getTimeInMillis(), TimeUnit.MILLISECONDS);
	ksession.fireAllRules();

	clock.advanceTime(5, TimeUnit.MINUTES);

	OrderCreatedEvent oce = new OrderCreatedEvent(1, "rick", 2, 60.0);
	oce.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(oce);
	ProductBoughtEvent pbe = new ProductBoughtEvent(1, 1, "rick", "dvd");
	pbe.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(pbe);
	pbe = new ProductBoughtEvent(1, 2, "rick", "cd");
	pbe.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(pbe);

	oce = new OrderCreatedEvent(2, "rick2", 1, 50.0);
	oce.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(oce);
	pbe = new ProductBoughtEvent(2, 3, "rick2", "dvd");
	pbe.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(pbe);

	ksession.fireAllRules();

	clock.advanceTime(10, TimeUnit.HOURS);
	
	ksession.fireAllRules();

	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, statisticsDaily.getCreatedStatistics());
	assertTrue(statisticsDaily.getDescription().contains("2 order(s)"));
	assertTrue(statisticsDaily.getDescription().contains("3 product(s)"));

	clock.advanceTime(5, TimeUnit.MINUTES);

	oce = new OrderCreatedEvent(3, "rick2", 4, 50.0);
	oce.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(oce);

	for (int i = 1; i <= 4; i++) {
	    ProductBoughtEvent pbe2 = new ProductBoughtEvent(3, 4, "rick3", "dvd");
	    pbe2.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(pbe2);
	}
	ksession.fireAllRules();

	clock.advanceTime(1, TimeUnit.DAYS);
	assertEquals(2, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(2, statisticsDaily.getCreatedStatistics());
	assertTrue(statisticsDaily.getDescription().contains("1 order(s)"));
	assertTrue(statisticsDaily.getDescription().contains("4 product(s)"));
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
