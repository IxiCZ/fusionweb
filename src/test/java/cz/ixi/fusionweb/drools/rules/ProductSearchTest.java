package cz.ixi.fusionweb.drools.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import org.drools.runtime.rule.FactHandle;
import org.drools.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cz.ixi.fusionweb.drools.model.ProductSearchUnsuccessfulEvent;

/**
 * Tests rules considering searching products.
 */
public class ProductSearchTest {

    private StatefulKnowledgeSession ksession;
    private KnowledgeBase kbase;
    private FiredRulesListener firedRules;
    private ProductSearchUnsuccsessfulChannelMock productSearchUnsuccessful;

    @Before
    public void setUp() {
	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	kbuilder.add(new ClassPathResource("imports-and-declarations.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("product-searching.drl", getClass()), ResourceType.DRL);
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

	productSearchUnsuccessful = new ProductSearchUnsuccsessfulChannelMock();
	ksession.registerChannel("productSearchUnsuccessful", productSearchUnsuccessful);
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
    public void insertSearchedTextEvent() {
	ksession.fireAllRules();
	assertTrue(ksession.getObjects().isEmpty());

	ksession.insert(new ProductSearchUnsuccessfulEvent("test", null));
	ksession.fireAllRules();
	assertEquals(2, ksession.getObjects().size());

	ksession.insert(new ProductSearchUnsuccessfulEvent("test2", null));
	ksession.fireAllRules();
	assertEquals(4, ksession.getObjects().size());

	ksession.insert(new ProductSearchUnsuccessfulEvent("test", null));
	ksession.fireAllRules();
	assertEquals(5, ksession.getObjects().size());
    }

    @Test
    public void retractSearchedTextEvent() {
	String insertRule = "Insert searched text event";

	FactHandle firstEvent = ksession.insert(new ProductSearchUnsuccessfulEvent("test", null));
	ksession.fireAllRules();
	assertEquals(2, ksession.getObjects().size());
	assertEquals(1, firedRules.howManyTimesIsRuleFired(insertRule));

	ksession.retract(firstEvent);
	ksession.fireAllRules();
	assertEquals(0, ksession.getObjects().size());

	FactHandle secondEvent = ksession.insert(new ProductSearchUnsuccessfulEvent("test2", null));
	ksession.fireAllRules();
	assertEquals(2, ksession.getObjects().size());
	assertEquals(2, firedRules.howManyTimesIsRuleFired(insertRule));

	ksession.insert(new ProductSearchUnsuccessfulEvent("test2", null));
	ksession.fireAllRules();
	assertEquals(3, ksession.getObjects().size());
	assertEquals(2, firedRules.howManyTimesIsRuleFired(insertRule));

	ksession.retract(secondEvent);
	ksession.fireAllRules();
	assertEquals(2, ksession.getObjects().size());
	assertEquals(2, firedRules.howManyTimesIsRuleFired(insertRule));
    }

    @Test
    public void createNotification() {
	String rule = "Create notification if too many unsuccessful seach events";
	int neededNumberOfEvents = 10;
	for (int i = 1; i < neededNumberOfEvents; i++) {
	    ksession.insert(new ProductSearchUnsuccessfulEvent("test", null));
	}
	ksession.insert(new ProductSearchUnsuccessfulEvent("test-not right", null));
	ksession.fireAllRules();
	assertFalse(firedRules.isRuleFired(rule));

	ksession.insert(new ProductSearchUnsuccessfulEvent("test", null));
	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, productSearchUnsuccessful.getCreatedNotifications());

	for (int i = 1; i < neededNumberOfEvents; i++) {
	    ksession.insert(new ProductSearchUnsuccessfulEvent("test", null));
	}
	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, productSearchUnsuccessful.getCreatedNotifications());
    }

    @Test
    public void createAnotherNotificationInTime() {
	String rule = "Create notification if too many unsuccessful seach events";
	SessionPseudoClock clock = ksession.getSessionClock();
	int neededNumberOfEvents = 10;

	// at "0" 5 events
	for (int i = 1; i <= neededNumberOfEvents / 2; i++) {
	    ksession.insert(new ProductSearchUnsuccessfulEvent("test", null));
	}
	ksession.fireAllRules();
	assertFalse(firedRules.isRuleFired(rule));

	clock.advanceTime(30, TimeUnit.MINUTES);

	// at "30" 5 events => fire and not another fire for an hour
	for (int i = 1; i <= neededNumberOfEvents / 2; i++) {
	    ksession.insert(new ProductSearchUnsuccessfulEvent("test", null));
	}
	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, productSearchUnsuccessful.getCreatedNotifications());

	clock.advanceTime(30, TimeUnit.MINUTES);
	
	// at "30" 5 events 
	for (int i = 1; i <= neededNumberOfEvents / 2; i++) {
	    ksession.insert(new ProductSearchUnsuccessfulEvent("test", null));
	}
	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, productSearchUnsuccessful.getCreatedNotifications());
	
	clock.advanceTime(31, TimeUnit.MINUTES);

	// at "1.31" 5 events => should fire again
	for (int i = 1; i <= neededNumberOfEvents / 2; i++) {
	    ksession.insert(new ProductSearchUnsuccessfulEvent("test", null));
	}
	ksession.fireAllRules();
	assertEquals(2, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(2, productSearchUnsuccessful.getCreatedNotifications());
    }

    class ProductSearchUnsuccsessfulChannelMock implements Channel {

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
