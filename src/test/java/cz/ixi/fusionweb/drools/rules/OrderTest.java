package cz.ixi.fusionweb.drools.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import cz.ixi.fusionweb.drools.model.OrderCreatedEvent;
import cz.ixi.fusionweb.drools.model.ProductBoughtEvent;
import cz.ixi.fusionweb.entities.Notification;

/**
 * Tests rules considering orders.
 */
public class OrderTest {

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
	kbuilder.add(new ClassPathResource("order.drl", getClass()), ResourceType.DRL);
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
	String rule = "Report how many orders were created and items bought in in the last hour";
	SessionPseudoClock clock = ksession.getSessionClock();
	ksession.fireAllRules();

	clock.advanceTime(5, TimeUnit.MINUTES);

	ksession.insert(new OrderCreatedEvent(1, "rick", 2, 60.0));
	ksession.insert(new ProductBoughtEvent(1, 1, "rick", "dvd"));
	ksession.insert(new ProductBoughtEvent(1, 2, "rick", "cd"));

	ksession.insert(new OrderCreatedEvent(2, "rick2", 1, 50.0));
	ksession.insert(new ProductBoughtEvent(2, 3, "rick2", "dvd"));

	ksession.fireAllRules();

	clock.advanceTime(61, TimeUnit.MINUTES);

	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, notificationsGeneral.getCreatedNotifications());
	assertTrue(notificationsGeneral.getDescription().contains("2 order(s)"));
	assertTrue(notificationsGeneral.getDescription().contains("3 product(s)"));

	clock.advanceTime(5, TimeUnit.MINUTES);

	ksession.insert(new OrderCreatedEvent(3, "rick2", 4, 50.0));
	ksession.insert(new ProductBoughtEvent(3, 4, "rick3", "dvd"));
	ksession.insert(new ProductBoughtEvent(3, 4, "rick3", "dvd"));
	ksession.insert(new ProductBoughtEvent(3, 4, "rick3", "dvd"));
	ksession.insert(new ProductBoughtEvent(3, 4, "rick3", "dvd"));
	ksession.fireAllRules();

	clock.advanceTime(61, TimeUnit.MINUTES);
	assertEquals(2, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(2, notificationsGeneral.getCreatedNotifications());
	assertTrue(notificationsGeneral.getDescription().contains("1 order(s)"));
	assertTrue(notificationsGeneral.getDescription().contains("4 product(s)"));
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
