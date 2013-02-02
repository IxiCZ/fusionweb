package cz.ixi.fusionweb.drools.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cz.ixi.fusionweb.drools.functions.MostVisitedFunction;
import cz.ixi.fusionweb.drools.model.ProductNavigationEvent;
import cz.ixi.fusionweb.entities.Product;

/**
 * Tests rules considering main product.
 */
public class MainProductTest {

    private StatefulKnowledgeSession ksession;
    private KnowledgeBase kbase;
    private DefaultLayoutControllerMock defaultLayout;

    @Before
    public void setUp() {
	PackageBuilderConfiguration pkgConf = new PackageBuilderConfiguration();
	pkgConf.addAccumulateFunction("mostVisited", MostVisitedFunction.class);

	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(pkgConf);
	kbuilder.add(new ClassPathResource("imports-and-declarations.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("main-product.drl", getClass()), ResourceType.DRL);
	Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());

	KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
	config.setOption(EventProcessingOption.STREAM);

	kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
	kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

	KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
	conf.setOption(ClockTypeOption.get("pseudo"));
	ksession = kbase.newStatefulKnowledgeSession(conf, null);

	defaultLayout = new DefaultLayoutControllerMock();
	ksession.registerChannel("defaultLayout", defaultLayout);
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
    public void setMainProductOnStart() {
	assertNull(defaultLayout.getMainProduct());
	ksession.fireAllRules();
	assertEquals(30, (int) defaultLayout.getMainProduct().getId());
    }

    
    
    @Test
    public void takesIntoAccountOnlyRightStream() {
	for (int i = 1; i < 10; i++) {
	    ksession.insert(new ProductNavigationEvent("rick",10,""));
	}
	ksession.getWorkingMemoryEntryPoint("ProductNavigationStream").insert(new ProductNavigationEvent("rick",42,""));
	
	((SessionPseudoClock) ksession.getSessionClock()).advanceTime(40, TimeUnit.MINUTES);
	ksession.fireAllRules();
	assertEquals(42, (int) defaultLayout.getMainProduct().getId());
    }

    @Test
    public void takesIntoAccountOnlyEventsFromTimeWindow() {
	SessionPseudoClock clock = ksession.getSessionClock();
	WorkingMemoryEntryPoint entryPoint = ksession.getWorkingMemoryEntryPoint("ProductNavigationStream");

	// at "0"
	for (int i = 1; i < 10; i++) {
	    entryPoint.insert(new ProductNavigationEvent("rick",1,""));
	}

	clock.advanceTime(50, TimeUnit.MINUTES);

	// at "50"
	entryPoint.insert(new ProductNavigationEvent("rick",2,""));

	ksession.fireAllRules();
	assertEquals(1, (int) defaultLayout.getMainProduct().getId());

	clock.advanceTime(50, TimeUnit.MINUTES);

	// at "1:40"
	ksession.fireAllRules();
	assertEquals(2, (int) defaultLayout.getMainProduct().getId());
    }

    class DefaultLayoutControllerMock implements Channel {

	private Product mainProduct;

	public Product getMainProduct() {
	    return mainProduct;
	}

	public void setMainProduct(Product mainProduct) {
	    this.mainProduct = mainProduct;
	}

	@Override
	public void send(Object object) {
	    setMainProduct(new Product((Integer) object));
	}
    }
}
