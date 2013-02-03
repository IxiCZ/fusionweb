package cz.ixi.fusionweb.drools.model;

public class DiscussionEntryEvent extends GeneralUserActionEvent {

    private String productName;
    private String text;

    public DiscussionEntryEvent(String productName, String text, String username) {
	super(username);
	this.productName = productName;
	this.text = text;
    }

    public String getProductName() {
	return productName;
    }

    public String getText() {
	return text;
    }

    public void setProductName(String productName) {
	this.productName = productName;
    }

    public void setText(String text) {
	this.text = text;
    }

    @Override
    public String toString() {
	return "User " + getUsername() + " added comment for product " + productName;

    }
}
