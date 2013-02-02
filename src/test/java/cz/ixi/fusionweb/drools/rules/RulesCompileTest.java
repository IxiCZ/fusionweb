package cz.ixi.fusionweb.drools.rules;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.io.impl.ClassPathResource;
import org.junit.Assert;
import org.junit.Test;

import cz.ixi.fusionweb.drools.functions.MostVisitedFunction;

/**
 * Tests whether the resources are compilable.
 */
public class RulesCompileTest {

    @Test
    public void testCompile() {
	PackageBuilderConfiguration pkgConf = new PackageBuilderConfiguration();
	pkgConf.addAccumulateFunction("mostVisited", MostVisitedFunction.class);

	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(pkgConf);
	kbuilder.add(new ClassPathResource("imports-and-declarations.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("main-product.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("product-searching.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("customer-registration.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("discussion.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("customer-log-in.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("order.drl", getClass()), ResourceType.DRL);
	kbuilder.add(new ClassPathResource("visiting.drl", getClass()), ResourceType.DRL);
	
	// kbuilder.add(new ClassPathResource("track-debug.drl", getClass()), ResourceType.DRL);

	Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
    }
}
