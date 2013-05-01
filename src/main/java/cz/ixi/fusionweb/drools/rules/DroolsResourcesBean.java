package cz.ixi.fusionweb.drools.rules;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.DependsOn;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.ObjectFilter;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.DefaultAgendaEventListener;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;

import cz.ixi.fusionweb.drools.channels.NotificationsGeneralChannel;
import cz.ixi.fusionweb.drools.channels.ProductSearchUnsuccsessfulChannel;
import cz.ixi.fusionweb.drools.channels.StatisticsRecordDailyChannel;
import cz.ixi.fusionweb.drools.channels.StatisticsRecordHourlyChannel;
import cz.ixi.fusionweb.drools.channels.TooManyCustomerRegistrationsChannel;
import cz.ixi.fusionweb.drools.model.CustomerLogInEvent;
import cz.ixi.fusionweb.drools.model.GeneralUserActionEvent;
import cz.ixi.fusionweb.web.layout.DefaultLayoutController;

/**
 * Class which initialize drools working memory from rules and provides methods
 * to use it.
 */
@Singleton
@Startup
@Lock(LockType.READ)
@DependsOn({ "StartupDBConfigBean" })
public class DroolsResourcesBean {

    private StatefulKnowledgeSession ksession;

    @Inject
    private DefaultLayoutController defaultLayout;
    @Inject
    private ProductSearchUnsuccsessfulChannel productSearchUnsuccessful;
    @Inject
    private TooManyCustomerRegistrationsChannel tooManyCustomerRegistrations;
    @Inject
    private NotificationsGeneralChannel notificationsGeneral;
    @Inject
    private StatisticsRecordHourlyChannel statisticsHourly;
    @Inject
    private StatisticsRecordDailyChannel statisticsDaily;

    /**
     * Constructs the session from resources.	
     */
    @PostConstruct
    public void init() {
	KnowledgeBuilderConfiguration kbconf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
	kbconf.setProperty("drools.accumulate.function.mostVisitedProduct",
		"cz.ixi.fusionweb.drools.functions.MostVisitedProductFunction");
	kbconf.setProperty("drools.accumulate.function.mostVisitedCategory",
		"cz.ixi.fusionweb.drools.functions.MostVisitedCategoryFunction");

	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbconf);
	kbuilder.add(new ClassPathResource("imports-and-declarations.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("main-product.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("product-searching.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("customer-registration.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("discussion.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("customer-log-in.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("order-how-many.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("order-many.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("visiting.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("visiting-category.drl", getClass()), ResourceType.DRL);

	kbuilder.add(new ClassPathResource("track-debug.drl", getClass()), ResourceType.DRL);

	if (kbuilder.hasErrors()) {
	    if (kbuilder.getErrors().size() > 0) {
		for (KnowledgeBuilderError kerror : kbuilder.getErrors()) {
		    Logger.getLogger(DroolsResourcesBean.class.getName()).log(Level.SEVERE,
			    "Error while constructing the session " + kerror, kerror);
		}
	    }
	} else {
	    Logger.getLogger(DroolsResourcesBean.class.getName()).log(Level.INFO,
		    "No errors in kbuilder while constructing the session ");
	}
	KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
	config.setOption(EventProcessingOption.STREAM);

	KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
	kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
	ksession = kbase.newStatefulKnowledgeSession();

	ksession.registerChannel("defaultLayout", defaultLayout);
	ksession.registerChannel("productSearchUnsuccessful", productSearchUnsuccessful);
	ksession.registerChannel("tooManyCustomerRegistrations", tooManyCustomerRegistrations);
	ksession.registerChannel("notificationsGeneral", notificationsGeneral);
	ksession.registerChannel("statisticsHourly", statisticsHourly);
	ksession.registerChannel("statisticsDaily", statisticsDaily);

	ksession.addEventListener(new DefaultAgendaEventListener() {
	    @Override
	    public void activationCreated(ActivationCreatedEvent event) {
		Logger.getLogger(DroolsResourcesBean.class.getName()).log(Level.INFO,
			"DROOLS - fired rule: " + event.getActivation().getRule().getName());
	    }
	});

	ksession.fireAllRules();
	Logger.getLogger(DroolsResourcesBean.class.getName()).log(Level.INFO, "DROOLS - Session successfully created.");
    }

    /**
     * Inserts the fact and fires the rules.
     * 
     * @param fact fact to be inserted into the session
     */
    @Lock(LockType.WRITE)
    public void insertFact(Object fact) {
	Logger.getLogger(DroolsResourcesBean.class.getName()).log(Level.INFO, "DROOLS - Inserting fact: " + fact);

	ksession.insert(fact);
	ksession.fireAllRules();
    }

    /**
     * Fires the rules.
     */
    @Lock(LockType.WRITE)
    public void fireAllRules() {
	Logger.getLogger(DroolsResourcesBean.class.getName()).log(Level.INFO, "DROOLS - Rules fired by automatic timer at: " + new Date());
	ksession.fireAllRules();
    }

    /**
     * Returns all events representing logging of a customer.
     * 
     * @return all events representing logging of a customer
     */
    @Lock(LockType.READ)
    public SortedSet<CustomerLogInEvent> getAllRecentlyLoggedCustomerEvents() {
	SortedSet<CustomerLogInEvent> logInEvents = new TreeSet<CustomerLogInEvent>();
	for (Object o : ksession.getObjects(new LoggedCustomerFilter())) {
	    logInEvents.add((CustomerLogInEvent) o);
	}
	return logInEvents;
    }

    /**
     * Returns set of events relating to given username.
     * 
     * @param username user of who the events should be returned
     * @return  set of events relating to given username
     */
    @Lock(LockType.READ)
    public SortedSet<GeneralUserActionEvent> getAllActionEventsForCustomer(String username) {
	SortedSet<GeneralUserActionEvent> events = new TreeSet<GeneralUserActionEvent>();
	for (Object o : ksession.getObjects(new ActionEventForCutomerFilter(username))) {
	    events.add((GeneralUserActionEvent) o);
	}
	return events;
    }

    /**
     * Disposes the session.
     */
    @PreDestroy
    public void destroy() {
	Logger.getLogger(DroolsResourcesBean.class.getName()).log(Level.INFO, "DROOLS - Disposing the session.");
	ksession.dispose();
    }

    /**
     * Filter for CustomerLogInEvent event type.
     */
    private class LoggedCustomerFilter implements ObjectFilter {

	@Override
	public boolean accept(Object object) {
	    return (object instanceof CustomerLogInEvent);
	}

    }

    /**
     * Filter for GeneralUserActionEvent with given username.
     */
    private class ActionEventForCutomerFilter implements ObjectFilter {

	private String username;

	public ActionEventForCutomerFilter(String username) {
	    this.username = username;
	}

	@Override
	public boolean accept(Object object) {
	    if (!(object instanceof GeneralUserActionEvent)) {
		return false;
	    }
	    GeneralUserActionEvent event = (GeneralUserActionEvent) object;
	    return (this.username.equals(event.getUsername()));
	}

    }

}
