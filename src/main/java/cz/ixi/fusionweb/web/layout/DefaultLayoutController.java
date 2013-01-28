package cz.ixi.fusionweb.web.layout;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.drools.runtime.Channel;

import cz.ixi.fusionweb.ejb.ProductBean;
import cz.ixi.fusionweb.entities.Product;

/**
 * Products on main page.
 */
@Named(value = "defaultLayoutController")
@ApplicationScoped
public class DefaultLayoutController implements Channel, Serializable {

    private static final long serialVersionUID = 4756077195500020883L;

    @EJB
    private ProductBean products;

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

    @Override
    public void send(Object object) {
	System.out.println("Setting of new main product, previous was: " + getMainProduct());
  	setMainProduct(products.find((Integer) object));
  	System.out.println("Setting of new main product, new is:       " + getMainProduct());
    }

}
