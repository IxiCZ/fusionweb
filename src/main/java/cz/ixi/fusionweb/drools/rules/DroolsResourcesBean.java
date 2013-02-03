package cz.ixi.fusionweb.drools.rules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.conf.EventProcessingOption;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;

import cz.ixi.fusionweb.drools.channels.NotificationsGeneralChannel;
import cz.ixi.fusionweb.drools.channels.ProductSearchUnsuccsessfulChannel;
import cz.ixi.fusionweb.drools.channels.TooManyCustomerRegistrationsChannel;
import cz.ixi.fusionweb.drools.functions.MostVisitedFunction;
import cz.ixi.fusionweb.drools.model.UserActionsEvent;
import cz.ixi.fusionweb.web.layout.DefaultLayoutController;

/**
 * Class which initialize drools working memory from rules and provides methods
 * to use it.
 */
@Singleton
@Startup
@Lock(LockType.READ)
@DependsOn({ "StartupDBConfigBean", "StartupLayoutsBean" })
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

    @PostConstruct
    public void init() {
	PackageBuilderConfiguration pkgConf = new PackageBuilderConfiguration();
	pkgConf.addAccumulateFunction("mostVisited", MostVisitedFunction.class);

	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(pkgConf);
	kbuilder.add(new ClassPathResource("imports-and-declarations.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("main-product.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("product-searching.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("customer-registration.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("discussion.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("customer-log-in.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("order-how-many.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("order-many.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("visiting.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("user-navigation.drl", getClass()), ResourceType.DRL);

	kbuilder.add(new ClassPathResource("track-debug.drl", getClass()), ResourceType.DRL);

	if (kbuilder.hasErrors()) {
	    if (kbuilder.getErrors().size() > 0) {
		for (KnowledgeBuilderError kerror : kbuilder.getErrors()) {
		    System.err.println(kerror);
		}
	    }
	} else {
	    System.out.println("No errors in kbuilder.");
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

	// ksession.fireAllRules();
	System.out.println("ksession created");
    }


    @Lock(LockType.WRITE)
    public void insertFact(Object fact) {
	System.out.println("inserting fact: " + fact);

	ksession.insert(fact);
	ksession.fireAllRules();
    }

    @Lock(LockType.WRITE)
    public void fireAllRules() {
	System.out.println("rules fired at: " + new Date());
	ksession.fireAllRules();
    }

    @Lock(LockType.READ) 
    public List<UserActionsEvent> getAllUserNavigations() {
	List<UserActionsEvent> userNavs = new ArrayList<UserActionsEvent>();
	for(Object o: ksession.getObjects(new UserNavigationsEventFilter())) {
	    userNavs.add((UserActionsEvent)o);
	}
 	return userNavs;
     }

    @PreDestroy
    public void destroy() {
	ksession.dispose();
    }

    private class UserNavigationsEventFilter implements ObjectFilter {

	@Override
	public boolean accept(Object object) {
    	   return (object instanceof UserActionsEvent);
	}
	
    }
}
