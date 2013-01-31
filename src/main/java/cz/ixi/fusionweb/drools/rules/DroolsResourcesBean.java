package cz.ixi.fusionweb.drools.rules;

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
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.conf.EventProcessingOption;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;

import cz.ixi.fusionweb.drools.functions.MostVisitedFunction;
import cz.ixi.fusionweb.drools.model.ProductNavigationEvent;
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

    @PostConstruct
    public void init() {
	PackageBuilderConfiguration pkgConf = new PackageBuilderConfiguration();
	pkgConf.addAccumulateFunction("mostVisited", MostVisitedFunction.class);

	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(pkgConf);
	kbuilder.add(new ClassPathResource("imports-and-declarations.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("main-product.drl", getClass()), ResourceType.DRL);
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
	ksession.fireAllRules();
	System.out.println("ksession created");
    }

    @Lock(LockType.WRITE)
    public void insertFact(ProductNavigationEvent fact) {
	System.out.println("inserting ProductNavigationEvent fact: " + fact);

	ksession.getWorkingMemoryEntryPoint("ProductNavigationStream").insert(fact);
	ksession.fireAllRules();
    }

    @Lock(LockType.WRITE)
    public void insertFact(Object fact) {
	System.out.println("inserting fact: " + fact);

	ksession.insert(fact);
	ksession.fireAllRules();
    }

    @PreDestroy
    public void destroy() {
	ksession.dispose();
    }
}
