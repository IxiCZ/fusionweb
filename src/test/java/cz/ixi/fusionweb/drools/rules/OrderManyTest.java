package cz.ixi.fusionweb.drools.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cz.ixi.fusionweb.drools.model.ProductBoughtEvent;
import cz.ixi.fusionweb.entities.Notification;

/**
 * Tests rules considering orders.
 */
public class OrderManyTest {

    private StatefulKnowledgeSession ksession;
    private KnowledgeBase kbase;
    private FiredRulesListener firedRules;
    private NotificationsGeneralChannelMock notificationsGeneral;

    @Before
    public void setUp() {
	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	kbuilder.add(new ClassPathResource("imports-and-declarations.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("order-many.drl", getClass()), ResourceType.DRL);
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
    public void reportManyProductPurchases() {
	String rule = "Create notification if the product is bought a lot";
	ksession.fireAllRules();

	for (int i = 1; i <= 99; i++) {
	    ksession.insert(new ProductBoughtEvent(1, 2, "rick", "cd"));
	    ksession.insert(new ProductBoughtEvent(2, 3, "rick2", "dvd"));
	    ksession.insert(new ProductBoughtEvent(3, 4, "rick3", "bluray dvd"));
	}

	ksession.fireAllRules();
	assertFalse(firedRules.isRuleFired(rule));

	ksession.insert(new ProductBoughtEvent(1, 2, "rick", "cd"));
	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, notificationsGeneral.getCreatedNotifications());
	assertTrue(notificationsGeneral.getDescription().contains("product 2"));
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
