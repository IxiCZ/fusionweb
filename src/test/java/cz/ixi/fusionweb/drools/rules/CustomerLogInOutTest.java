package cz.ixi.fusionweb.drools.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
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
import cz.ixi.fusionweb.entities.Notification;

/**
 * Tests rules considering searching products.
 */
public class CustomerLogInOutTest {

    private StatefulKnowledgeSession ksession;
    private KnowledgeBase kbase;
    private FiredRulesListener firedRules;
    private NotificationsGeneralChannelMock notificationsGeneral;

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

	notificationsGeneral = new NotificationsGeneralChannelMock();
	ksession.registerChannel("notificationsGeneral", notificationsGeneral);
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
    public void reportRightNumberOfLoggedCustomers() {
	String rule = "Report how many customers logged in in the last hour";
	SessionPseudoClock clock = ksession.getSessionClock();
	ksession.fireAllRules();

	clock.advanceTime(5, TimeUnit.MINUTES);

	for (int i = 1; i <= 3; i++) {
	    CustomerLogInEvent cLogInEvent = new CustomerLogInEvent("rick" + i);
	    cLogInEvent.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(cLogInEvent);
	}
	ksession.fireAllRules();

	clock.advanceTime(61, TimeUnit.MINUTES);

	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, notificationsGeneral.getCreatedNotifications());
	assertTrue(notificationsGeneral.getDescription().contains("3 customers"));

	clock.advanceTime(5, TimeUnit.MINUTES);
	
	for (int i = 1; i <= 2; i++) {
	    CustomerLogInEvent cLogInEvent = new CustomerLogInEvent("frodo" + i);
	    cLogInEvent.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(cLogInEvent);
	}
	ksession.fireAllRules();

	clock.advanceTime(61, TimeUnit.MINUTES);
	assertEquals(2, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(2, notificationsGeneral.getCreatedNotifications());
	assertTrue(notificationsGeneral.getDescription().contains("2 customers"));
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

}
