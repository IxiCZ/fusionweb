package cz.ixi.fusionweb.drools.model;

public class ProductBoughtEvent {

    private Integer orderId;
    private Integer productId;
    private String username;
    private String productName;

    public ProductBoughtEvent() {
    }

    public ProductBoughtEvent(Integer orderId, Integer productId, String username, String productName) {
	super();
	this.orderId = orderId;
	this.productId = productId;
	this.username = username;
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

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public Integer getOrderId() {
	return orderId;
    }

    public void setOrderId(Integer orderId) {
	this.orderId = orderId;
    }

    @Override
    public String toString() {
	return "ProductBoughtEvent [orderId=" + orderId + ", productId=" + productId + ", username=" + username
		+ ", productName=" + productName + "]";
    }    
}
