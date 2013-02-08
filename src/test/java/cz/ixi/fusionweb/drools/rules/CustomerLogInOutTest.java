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
import org.drools.compiler.PackageBuilderConfiguration;
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

import cz.ixi.fusionweb.drools.functions.MostVisitedFunction;
import cz.ixi.fusionweb.drools.model.CustomerLogInEvent;

/**
 * Tests rules considering searching products.
 */
public class CustomerLogInOutTest {

    private StatefulKnowledgeSession ksession;
    private KnowledgeBase kbase;
    private FiredRulesListener firedRules;
    private StatisticsRecordHourlyChannelMock statisticsHourly;
    private StatisticsRecordDailyChannelMock statisticsDaily;

    @Before
    public void setUp() {
	PackageBuilderConfiguration pkgConf = new PackageBuilderConfiguration();
	pkgConf.addAccumulateFunction("mostVisited", MostVisitedFunction.class);

	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(pkgConf);
	kbuilder.add(new ClassPathResource("imports-and-declarations.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("customer-log-in.drl", getClass()), ResourceType.DRL);
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
    public void reportRightNumberOfLoggedCustomersInLastHour() {
	String rule = "Report how many customers logged in in the last hour";
	SessionPseudoClock clock = ksession.getSessionClock();
	Calendar cal = new GregorianCalendar(2013, 01, 01, 14, 0, 0);
	clock.advanceTime(cal.getTimeInMillis(), TimeUnit.MILLISECONDS);
	ksession.fireAllRules();

	clock.advanceTime(5, TimeUnit.MINUTES);
	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("0 customers"));

	// inserted 14:05
	for (int i = 1; i <= 3; i++) {
	    CustomerLogInEvent cLogInEvent = new CustomerLogInEvent("rick" + i);
	    cLogInEvent.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(cLogInEvent);
	}
	ksession.fireAllRules();

	clock.advanceTime(55, TimeUnit.MINUTES);

	ksession.fireAllRules();

	assertEquals(2, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(2, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("3 customers"));

	clock.advanceTime(5, TimeUnit.MINUTES);

	for (int i = 1; i <= 2; i++) {
	    CustomerLogInEvent cLogInEvent = new CustomerLogInEvent("frodo" + i);
	    cLogInEvent.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(cLogInEvent);
	}//
	ksession.fireAllRules();

	clock.advanceTime(55, TimeUnit.MINUTES);
	assertEquals(3, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(3, statisticsHourly.getCreatedStatistics());
	assertTrue(statisticsHourly.getDescription().contains("2 customers"));
    }

    @Test
    public void reportRightNumberOfLoggedCustomersInLastDay() {
	String rule = "Report how many customers logged in in the last day";
	SessionPseudoClock clock = ksession.getSessionClock();
	Calendar cal = new GregorianCalendar(2013, 01, 01, 14, 0, 0);
	clock.advanceTime(cal.getTimeInMillis(), TimeUnit.MILLISECONDS);
	ksession.fireAllRules();

	clock.advanceTime(5, TimeUnit.MINUTES);

	for (int i = 1; i <= 10; i++) {
	    CustomerLogInEvent cLogInEvent = new CustomerLogInEvent("rick" + i);
	    cLogInEvent.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(cLogInEvent);
	}
	ksession.fireAllRules();

	clock.advanceTime(70, TimeUnit.MINUTES);

	ksession.fireAllRules();
	
	
	for (int i = 1; i <= 5; i++) {
	    CustomerLogInEvent cLogInEvent = new CustomerLogInEvent("rick" + i);
	    cLogInEvent.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(cLogInEvent);
	}
	ksession.fireAllRules();

	clock.advanceTime(10, TimeUnit.HOURS);

	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, statisticsDaily.getCreatedStatistics());
	assertTrue(statisticsDaily.getDescription().contains("15 customers"));

	clock.advanceTime(5, TimeUnit.MINUTES);

	for (int i = 1; i <= 2; i++) {
	    CustomerLogInEvent cLogInEvent = new CustomerLogInEvent("frodo" + i);
	    cLogInEvent.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(cLogInEvent);
	}//
	ksession.fireAllRules();

	clock.advanceTime(1, TimeUnit.DAYS);
	ksession.fireAllRules();
	assertEquals(2, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(2, statisticsDaily.getCreatedStatistics());
	assertTrue(statisticsDaily.getDescription().contains("2 customers"));
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
