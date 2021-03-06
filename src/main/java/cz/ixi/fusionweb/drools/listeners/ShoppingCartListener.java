package cz.ixi.fusionweb.drools.listeners;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import cz.ixi.fusionweb.drools.model.ProductAddedIntoShoppingCartEvent;
import cz.ixi.fusionweb.drools.model.ShoppingCartNavigationEvent;
import cz.ixi.fusionweb.drools.rules.DroolsResourcesBean;
import cz.ixi.fusionweb.entities.Product;
import cz.ixi.fusionweb.entities.User;

/**
 * Inserts events considering shopping cart into drools working memory.
 */
@ManagedBean(name = "shoppingCartListener")
public class ShoppingCartListener {

    @EJB
    DroolsResourcesBean drools;

    /**
     * Creates and inserts event representing a product adding to cart.
     * 
     * @param product product added to cart
     * @param user user who added the product to cart
     */
    public void productAddedIntoCart(Product product, User user) {
	if (user != null) {
	    drools.insertFact(new ProductAddedIntoShoppingCartEvent(user.getUsername(), product.getId(), product
		    .getName()));
	} else {
	    drools.insertFact(new ProductAddedIntoShoppingCartEvent(null, product.getId(), product.getName()));
	}
    }
    
    /**
     * Creates and inserts event representing a product adding to cart.
     * 
     * @param product product added to cart
     * @param user user who added the product to cart
     */
    public void shoppingCartVisited( User user) {
	if (user != null) {
	    drools.insertFact(new ShoppingCartNavigationEvent(user.getUsername()));
	} else {
	    drools.insertFact(new ShoppingCartNavigationEvent(null));
	}
    }
    
    

}
