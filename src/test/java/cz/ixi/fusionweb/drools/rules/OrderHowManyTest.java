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
import cz.ixi.fusionweb.drools.model.OrderCreatedEvent;
import cz.ixi.fusionweb.drools.model.ProductBoughtEvent;
import cz.ixi.fusionweb.entities.Notification;

/**
 * Tests rules considering orders.
 */
public class OrderHowManyTest {

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
    public void reportRightNumberOfBoughtProductsAndCreatedOrders() {
	String rule = "Report how many orders were created and items bought in in the last hour";
	SessionPseudoClock clock = ksession.getSessionClock();
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
	pbe = new  ProductBoughtEvent(2, 3, "rick2", "dvd");
	pbe.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(pbe);


	ksession.fireAllRules();

	clock.advanceTime(61, TimeUnit.MINUTES);

	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, notificationsGeneral.getCreatedNotifications());
	assertTrue(notificationsGeneral.getDescription().contains("2 order(s)"));
	assertTrue(notificationsGeneral.getDescription().contains("3 product(s)"));

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
