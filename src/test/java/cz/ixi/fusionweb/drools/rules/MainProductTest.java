package cz.ixi.fusionweb.drools.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
    public void takesIntoAccountOnlyEventsFromTimeWindow() {
	SessionPseudoClock clock = ksession.getSessionClock();

	clock.advanceTime(1, TimeUnit.MINUTES);
	ksession.fireAllRules();
	// at "0"
	for (int i = 1; i < 10; i++) {
	    ProductNavigationEvent pne = new ProductNavigationEvent("rick", 1, "");
	    pne.setTime(new Date(clock.getCurrentTime()));
	    ksession.insert(pne);
	}

	ksession.fireAllRules();
	clock.advanceTime(59, TimeUnit.MINUTES);

	// at "60"
	ProductNavigationEvent pne = new ProductNavigationEvent("rick", 2, "");
	pne.setTime(new Date(clock.getCurrentTime()));
	ksession.insert(pne);

	ksession.fireAllRules();
	assertEquals(1, (int) defaultLayout.getMainProduct().getId());
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
