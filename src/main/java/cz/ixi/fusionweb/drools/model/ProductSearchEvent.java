package cz.ixi.fusionweb.drools.model;

import cz.ixi.fusionweb.entities.Customer;
import cz.ixi.fusionweb.entities.User;

/**
 * Class representing product search event.
 */
public class ProductSearchEvent {

    private String searchedText;
    private User user;

    public ProductSearchEvent() {
    }

    public ProductSearchEvent(String searchedText, User user) {
	this.searchedText = searchedText;
	this.user = user;
    }

    public String getSearchedText() {
	return searchedText;
    }

    public User getUser() {
	return user;
    }

    public void setSearchedText(String searchedText) {
	this.searchedText = searchedText;
    }

    public void setCustomer(Customer customer) {
	this.user = customer;
    }

    @Override
    public String toString() {
	if (user == null) {
	    return "ProductSearch event of searched text: " + searchedText + " by anonymous user";
	}
	return "ProductSearch event of searched text: " + searchedText + " by " + user;
    }
}
