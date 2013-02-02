package cz.ixi.fusionweb.drools.model;

public class OrderCreatedEvent {

    private Integer orderId;
    private String username;
    private int itemsCount;
    private double amount;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Integer orderId, String username, int itemsCount, double amount) {
	super();
	this.orderId = orderId;
	this.username = username;
	this.itemsCount = itemsCount;
	this.amount = amount;
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

    public int getItemsCount() {
	return itemsCount;
    }

    public double getAmount() {
	return amount;
    }

    public void setOrderId(Integer orderId) {
	this.orderId = orderId;
    }

    public void setItemsCount(int itemsCount) {
	this.itemsCount = itemsCount;
    }

    public void setAmount(double amount) {
	this.amount = amount;
    }

    @Override
    public String toString() {
	return "OrderCreatedInEvent [orderId=" + orderId + ", username=" + username + ", itemsCount=" + itemsCount
		+ ", amount=" + amount + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	OrderCreatedEvent other = (OrderCreatedEvent) obj;
	if (orderId == null) {
	    if (other.orderId != null)
		return false;
	} else if (!orderId.equals(other.orderId))
	    return false;
	return true;
    }
}
