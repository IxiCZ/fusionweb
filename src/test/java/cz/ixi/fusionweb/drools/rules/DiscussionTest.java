package cz.ixi.fusionweb.drools.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

import cz.ixi.fusionweb.drools.model.DiscussionEntryEvent;

/**
 * Tests rules considering discussion.
 */
public class DiscussionTest {

    private StatefulKnowledgeSession ksession;
    private KnowledgeBase kbase;
    private FiredRulesListener firedRules;
    private NotificationsGeneralChannelMock notificationsGeneral;

    @Before
    public void setUp() {
	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	kbuilder.add(new ClassPathResource("imports-and-declarations.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("discussion.drl", getClass()), ResourceType.DRL);
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
    public void createNotificationWhenContainsHelp() {
	String rule = "Create notification when discussion entry contains help";
	ksession.insert(new DiscussionEntryEvent("product", "wrong", "user"));
	ksession.fireAllRules();
	assertFalse(firedRules.isRuleFired(rule));

	ksession.insert(new DiscussionEntryEvent("product", "I really need HELP!", "user"));
	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, notificationsGeneral.getCreatedNotifications());

	ksession.insert(new DiscussionEntryEvent("product", "kjdashkjashdhelpkajshd", "user"));
	ksession.fireAllRules();
	assertEquals(2, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(2, notificationsGeneral.getCreatedNotifications());
    }

    @Test
    public void createNotificationWhenContainsAmazon() {
	String rule = "Create notification when discussion entry contains amazon";
	ksession.insert(new DiscussionEntryEvent("product", "wrong", "user"));
	ksession.fireAllRules();
	assertFalse(firedRules.isRuleFired(rule));

	ksession.insert(new DiscussionEntryEvent("product", "Amazon is better!", "user"));
	ksession.fireAllRules();
	assertEquals(1, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(1, notificationsGeneral.getCreatedNotifications());

	ksession.insert(new DiscussionEntryEvent("product", "kjdashkjaamazonajshd", "user"));
	ksession.fireAllRules();
	assertEquals(2, firedRules.howManyTimesIsRuleFired(rule));
	assertEquals(2, notificationsGeneral.getCreatedNotifications());
    }

    class NotificationsGeneralChannelMock implements Channel {

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
