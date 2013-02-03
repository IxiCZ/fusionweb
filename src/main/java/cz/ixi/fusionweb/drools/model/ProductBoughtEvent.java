package cz.ixi.fusionweb.drools.model;

public class ProductBoughtEvent extends GeneralUserActionEvent {

    private Integer orderId;
    private Integer productId;
    private String productName;

    public ProductBoughtEvent(Integer orderId, Integer productId, String username, String productName) {
	super(username);
	this.orderId = orderId;
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

    public Integer getOrderId() {
	return orderId;
    }

    public void setOrderId(Integer orderId) {
	this.orderId = orderId;
    }

    @Override
    public String toString() {
	return "User " + getUsername() + " bought product " + productName + " (" + productId + ").";
    }
}
