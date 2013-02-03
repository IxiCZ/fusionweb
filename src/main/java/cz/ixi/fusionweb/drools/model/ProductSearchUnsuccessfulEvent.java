package cz.ixi.fusionweb.drools.model;


/**
 * Class representing unsuccessful product search event.
 */
public class ProductSearchUnsuccessfulEvent extends ProductSearchEvent {

    public ProductSearchUnsuccessfulEvent(String searchedText, String username) {
	super(searchedText, username);
    }

    @Override
    public String toString() {
	if (getUsername() == null) {
	    return "Unsuccessful product search event of searched text: " + getSearchedText() + " by anonymous user";
	}
	return "Unsuccessful product search event of searched text: " + getSearchedText() + " by " + getUsername();
    }
}
