package cz.ixi.fusionweb.drools.rules;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * Class which initialize drools working memory from rules and provides methods
 * to use it.
 */
@Singleton
@Startup
@Lock(LockType.READ)
public class DroolsResourcesBean {

    private StatefulKnowledgeSession ksession;

    @PostConstruct
    public void init() {
	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	kbuilder.add(new ClassPathResource("rules.drl", getClass()), ResourceType.DRL);

	if (kbuilder.hasErrors()) {
	    if (kbuilder.getErrors().size() > 0) {
		for (KnowledgeBuilderError kerror : kbuilder.getErrors()) {
		    System.err.println(kerror);
		}
	    }
	}
	KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
	config.setOption(EventProcessingOption.STREAM);

	KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
	kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
	ksession = kbase.newStatefulKnowledgeSession();

	System.out.println("ksession created");
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
