package cz.ixi.fusionweb.drools.model;

public class DiscussionEntryEvent {

    private String productName;
    private String text;
    private String username;

    public DiscussionEntryEvent() {
    }

    public DiscussionEntryEvent(String productName, String text, String username) {
	this.productName = productName;
	this.text = text;
	this.username = username;
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

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    @Override
    public String toString() {
	return "DiscussionEntryEvent [productName=" + productName + ", text=" + text + ", username=" + username + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((productName == null) ? 0 : productName.hashCode());
	result = prime * result + ((text == null) ? 0 : text.hashCode());
	result = prime * result + ((username == null) ? 0 : username.hashCode());
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
	DiscussionEntryEvent other = (DiscussionEntryEvent) obj;
	if (productName == null) {
	    if (other.productName != null)
		return false;
	} else if (!productName.equals(other.productName))
	    return false;
	if (text == null) {
	    if (other.text != null)
		return false;
	} else if (!text.equals(other.text))
	    return false;
	if (username == null) {
	    if (other.username != null)
		return false;
	} else if (!username.equals(other.username))
	    return false;
	return true;
    }
}
