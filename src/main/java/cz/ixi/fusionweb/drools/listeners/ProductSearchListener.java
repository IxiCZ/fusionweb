package cz.ixi.fusionweb.drools.listeners;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import cz.ixi.fusionweb.drools.model.ProductSearchSuccessfulEvent;
import cz.ixi.fusionweb.drools.model.ProductSearchUnsuccessfulEvent;
import cz.ixi.fusionweb.drools.rules.DroolsResourcesBean;
import cz.ixi.fusionweb.ejb.ProductBean;
import cz.ixi.fusionweb.entities.User;

/**
 * Inserts events of searching products into drools working memory.
 */
@ManagedBean(name = "productSearchListener")
public class ProductSearchListener {

    @EJB
    DroolsResourcesBean drools;

    @EJB
    private ProductBean products;

    /**
     * Creates and inserts an event representing either successful or unsuccessful product search.
     * 
     * @param searchedKeyword searched keyword
     * @param user user realizing the search
     */
    public void search(String searchedKeyword, User user) {
	if (products.searchProductCount(searchedKeyword) == 0) {
	    if (user != null) {
		drools.insertFact(new ProductSearchUnsuccessfulEvent(searchedKeyword, user.getUsername()));
	    } else {
		drools.insertFact(new ProductSearchUnsuccessfulEvent(searchedKeyword, null));
	    }
	} else {
	    if (user != null) {
		drools.insertFact(new ProductSearchSuccessfulEvent(searchedKeyword, user.getUsername()));
	    } else {
		drools.insertFact(new ProductSearchSuccessfulEvent(searchedKeyword, null));
	    }
	}
    }

}
