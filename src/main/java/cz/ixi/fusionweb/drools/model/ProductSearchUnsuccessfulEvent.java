package cz.ixi.fusionweb.drools.model;

import cz.ixi.fusionweb.entities.User;

/**
 * Class representing unsuccessful product search event.
 */
public class ProductSearchUnsuccessfulEvent extends ProductSearchEvent{

  
    public ProductSearchUnsuccessfulEvent() {
	super();
    }

    public ProductSearchUnsuccessfulEvent(String searchedText, User user) {
	super(searchedText, user);
    }

  
    @Override
    public String toString() {
	if (getUser() == null) {
	    return "Unsuccessful product search event of searched text: " + getSearchedText() + " by anonymous user";
	}
	return "Unsuccessful product search event of searched text: " + getSearchedText() + " by " + getUser();
    }
}
