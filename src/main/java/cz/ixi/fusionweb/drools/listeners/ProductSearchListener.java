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

    public void search(String searchedKeyword, User user) {
	if (products.searchProductCount(searchedKeyword) == 0) {
	    drools.insertFact(new ProductSearchUnsuccessfulEvent(searchedKeyword, user));
	} else {
	    drools.insertFact(new ProductSearchSuccessfulEvent(searchedKeyword, user));
	}
    }

}
