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
	if (getUsername() != null) {
	    return "User " + getUsername() + " unsuccesfully searched '" + getSearchedText() + "'.";
	}
	return "Anonymous User unsuccesfully searched '" + getSearchedText() + "'.";
    }
}
