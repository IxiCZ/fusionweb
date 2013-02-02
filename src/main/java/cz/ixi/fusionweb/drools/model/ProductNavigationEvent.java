package cz.ixi.fusionweb.drools.model;

/**
 * Class representing product navigation event.
 */
public class ProductNavigationEvent extends NavigationEvent {

    private String productName;

    public String getProductName() {
	return productName;
    }

    public void setProductName(String productName) {
	this.productName = productName;
    }

    public ProductNavigationEvent(String username, Integer id, String productName) {
	super(id, username);
	this.productName = productName;
    }

    @Override
    public String toString() {
	return "Product[" + getId() + ": productName=" + productName + "] navigation event";
    }

}
