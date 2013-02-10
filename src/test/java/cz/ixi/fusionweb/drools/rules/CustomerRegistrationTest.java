package cz.ixi.fusionweb.drools.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

import cz.ixi.fusionweb.drools.model.CustomerRegistrationEvent;
import cz.ixi.fusionweb.drools.model.ProductSearchUnsuccessfulEvent;

/**
 * Tests rules considering searching products.
 */
public class CustomerRegistrationTest {

    private StatefulKnowledgeSession ksession;
    private KnowledgeBase kbase;
    private FiredRulesListener firedRules;
    private TooManyCustomerRegistrationsChannelMock tooManyCustomerRegistrations;

    @Before
    public void setUp() {
	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	kbuilder.add(new ClassPathResource("imports-and-declarations.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("customer-registration.drl", getClass()), ResourceType.DRL);
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

	tooManyCustomerRegistrations = new TooManyCustomerRegistrationsChannelMock();
	ksession.registerChannel("tooManyCustomerRegistrations", tooManyCustomerRegistrations);
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
    public void createNotification() {
	String rule = "Create notification if too many customer registrations";
	int neededNumberOfEvents = 100;
	for (int i = 1; i < neededNumberOfEvents; i++) {
	    ksession.insert(new CustomerRegistrationEvent());
	}
	ksession.fireAllRules();
	assertFalse(firedRules.isRuleFired(rule));

	ksession.insert(new CustomerRegistrationEvent());
	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, tooManyCustomerRegistrations.getCreatedNotifications());

	for (int i = 1; i < neededNumberOfEvents*2; i++) {
	    ksession.insert(new ProductSearchUnsuccessfulEvent("test", null));
	}
	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, tooManyCustomerRegistrations.getCreatedNotifications());
    }

    @Test
    public void createAnotherNotificationInTime() {
	String rule = "Create notification if too many customer registrations";
	SessionPseudoClock clock = ksession.getSessionClock();
	int neededNumberOfEvents = 100;

	// at "0" 50 events
	for (int i = 1; i <= neededNumberOfEvents / 2; i++) {
	    ksession.insert(new CustomerRegistrationEvent());
	}
	ksession.fireAllRules();
	assertFalse(firedRules.isRuleFired(rule));

	clock.advanceTime(30, TimeUnit.MINUTES);

	// at "30" 50 events => fire and not another fire for an hour
	for (int i = 1; i <= neededNumberOfEvents / 2; i++) {
	    ksession.insert(new CustomerRegistrationEvent());
	}
	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, tooManyCustomerRegistrations.getCreatedNotifications());

	clock.advanceTime(30, TimeUnit.MINUTES);
	
	// at "30" 50 events 
	for (int i = 1; i <= neededNumberOfEvents / 2; i++) {
	    ksession.insert(new CustomerRegistrationEvent());
	}
	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, tooManyCustomerRegistrations.getCreatedNotifications());
	
	clock.advanceTime(31, TimeUnit.MINUTES);

	// at "1.31" 50 events => should fire again
	for (int i = 1; i <= neededNumberOfEvents / 2; i++) {
	    ksession.insert(new CustomerRegistrationEvent());
	}
	ksession.fireAllRules();
	assertEquals(2, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(2, tooManyCustomerRegistrations.getCreatedNotifications());
    }

    class TooManyCustomerRegistrationsChannelMock implements Channel {

	private int createdNotifications;

	public int getCreatedNotifications() {
	    return createdNotifications;
	}

	@Override
	public void send(Object object) {
	    createdNotifications++;
	}
    }

}
