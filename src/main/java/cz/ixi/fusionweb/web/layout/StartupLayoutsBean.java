package cz.ixi.fusionweb.web.layout;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import cz.ixi.fusionweb.ejb.ProductBean;

/**
 * Fills DefaultLayoutBean at startup.
 */
// TODO: rewrite to rules
@Startup
@Singleton
@DependsOn("StartupDBConfigBean")
public class StartupLayoutsBean {

    @Inject 
    private DefaultLayoutController defaultLayout;
    
    @EJB 
    private ProductBean products;

    @PostConstruct
    public void init(){
	// simple setup
	defaultLayout.setDisplayedProducts(products.findRange(new int[]{0,4}));
	
	System.out.println("displayed products: "+ defaultLayout.getDisplayedProducts());
    }
}
