package cz.ixi.fusionweb.drools.model;

/**
 * Class representing product navigation event.
 */
public class ProductNavigationEvent extends NavigationEvent {

    private String productName;
    private Integer productId;

    public ProductNavigationEvent(String username, Integer productId, String productName) {
	super(username);
	this.productName = productName;
	this.productId = productId;
    }

    public String getProductName() {
	return productName;
    }

    public void setProductName(String productName) {
	this.productName = productName;
    }

    public Integer getProductId() {
	return productId;
    }

    public void setProductId(Integer productId) {
	this.productId = productId;
    }

    @Override
    public String toString() {
	return "ProductNavigationEvent [productName=" + productName + ", productId=" + productId + "]";
    }
}
