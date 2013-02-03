package cz.ixi.fusionweb.drools.model;

public class OrderCreatedEvent extends GeneralUserActionEvent{

    private Integer orderId;
    private int itemsCount;
    private double amount;

    public OrderCreatedEvent(Integer orderId, String username, int itemsCount, double amount) {
	super(username);
	this.orderId = orderId;
	this.itemsCount = itemsCount;
	this.amount = amount;
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
	return "OrderCreatedInEvent [orderId=" + orderId + ", username=" + getUsername() + ", itemsCount=" + itemsCount
		+ ", amount=" + amount + "]";
    }
}
