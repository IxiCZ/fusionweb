package cz.ixi.fusionweb.drools.model;

public class ProductAddedIntoShoppingCartEvent extends GeneralUserActionEvent {

    private Integer productId;
    private String productName;

    public ProductAddedIntoShoppingCartEvent(String username, Integer productId, String productName) {
	super(username);
	this.productId = productId;
	this.productName = productName;
    }

    public Integer getProductId() {
	return productId;
    }

    public String getProductName() {
	return productName;
    }

    public void setProductId(Integer productId) {
	this.productId = productId;
    }

    public void setProductName(String productName) {
	this.productName = productName;
    }

    @Override
    public String toString() {
	if (getUsername() != null) {
	    return "User " + getUsername() + " added product " + productName + "(" + productId + ") into shoppingCart.";
	}
	return "Anonymous User added product " + productName + "(" + productId + ") into shoppingCart.";
    }
}
