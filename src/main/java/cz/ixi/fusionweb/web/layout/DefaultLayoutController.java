package cz.ixi.fusionweb.web.layout;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import cz.ixi.fusionweb.entities.Product;

/**
 * Products on main page.
 */
@Named(value = "defaultLayoutController")
@ApplicationScoped
//@Singleton
public class DefaultLayoutController implements Serializable  {



    private static final long serialVersionUID = 4756077195500020883L;
    
    private Product mainProduct;

    private List<Product> displayedProducts;
    

    public Product getMainProduct() {
        return mainProduct;
    }

    public void setMainProduct(Product mainProduct) {
        this.mainProduct = mainProduct;
    }

    public List<Product> getDisplayedProducts() {
        return displayedProducts;
    }

    public void setDisplayedProducts(List<Product> displayedProducts) {
        this.displayedProducts = displayedProducts;
    }
    
  
    
    
}
