package cz.ixi.fusionweb.drools.listeners;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import cz.ixi.fusionweb.drools.model.CategoryNavigationEvent;
import cz.ixi.fusionweb.drools.model.ProductNavigationEvent;
import cz.ixi.fusionweb.drools.rules.DroolsResourcesBean;
import cz.ixi.fusionweb.entities.Product;
import cz.ixi.fusionweb.entities.ProductCategory;

/**
 * Methods of this class are used for listening on components (links, buttons,
 * ...) by means of actionListener tag to insert navigation events into drools
 * working memory.
 */
@ManagedBean(name = "navigationListener")
public class NavigationListener {

    @EJB
    DroolsResourcesBean drools;

    public void category(ProductCategory category) {
	drools.insertFact(new CategoryNavigationEvent(category.getId()));
    }
    
    public void product(Product product) {
  	drools.insertFact(new ProductNavigationEvent(product.getId()));
      }
}
