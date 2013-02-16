package cz.ixi.fusionweb.drools.rules;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests whether the resources are compilable.
 */
public class RulesCompileTest {

    @Test
    public void testCompile() {
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

	Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
    }
}
