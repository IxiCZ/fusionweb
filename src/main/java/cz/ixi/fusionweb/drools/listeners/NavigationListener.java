package cz.ixi.fusionweb.drools.listeners;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import cz.ixi.fusionweb.drools.model.CategoryNavigationEvent;
import cz.ixi.fusionweb.drools.model.CustomerMyOrdersNavigationEvent;
import cz.ixi.fusionweb.drools.model.ProductNavigationEvent;
import cz.ixi.fusionweb.drools.rules.DroolsResourcesBean;
import cz.ixi.fusionweb.entities.Product;
import cz.ixi.fusionweb.entities.ProductCategory;
import cz.ixi.fusionweb.entities.User;

/**
 * Methods of this class are used for listening on components (links, buttons,
 * ...) by means of actionListener tag to insert navigation events into drools
 * working memory.
 */
@ManagedBean(name = "navigationListener")
public class NavigationListener {

    @EJB
    DroolsResourcesBean drools;

    public void product(Product product, User user) {
	if (user != null) {
	    drools.insertFact(new ProductNavigationEvent(user.getUsername(), product.getId(), product.getName()));
	} else {
	    drools.insertFact(new ProductNavigationEvent(null, product.getId(), product.getName()));
	}
    }

    public void category(ProductCategory category, User user) {
	if (user != null) {
	    drools.insertFact(new CategoryNavigationEvent(user.getUsername(), category.getId(), category.getName()));
	} else {
	    drools.insertFact(new CategoryNavigationEvent(null, category.getId(), category.getName()));
	}
    }

    public void customerMenuMyOrders(User user) {
	drools.insertFact(new CustomerMyOrdersNavigationEvent(user.getUsername()));
    }
}
