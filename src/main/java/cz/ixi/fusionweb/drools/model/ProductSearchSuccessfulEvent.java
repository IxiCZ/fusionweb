package cz.ixi.fusionweb.drools.model;

import cz.ixi.fusionweb.entities.User;

/**
 * Class representing successful product search event.
 */
public class ProductSearchSuccessfulEvent extends ProductSearchEvent{

  
    public ProductSearchSuccessfulEvent() {
	super();
    }

    public ProductSearchSuccessfulEvent(String searchedText, User user) {
	super(searchedText, user);
    }

  
    @Override
    public String toString() {
	if (getUser() == null) {
	    return "Successful product search event of searched text: " + getSearchedText() + " by anonymous user";
	}
	return "Successful product search event of searched text: " + getSearchedText() + " by " + getUser();
    }
}
