package cz.ixi.fusionweb.drools.model;


/**
 * Class representing successful product search event.
 */
public class ProductSearchSuccessfulEvent extends ProductSearchEvent{

    public ProductSearchSuccessfulEvent(String searchedText, String username) {
	super(searchedText, username);
    }

  
    @Override
    public String toString() {
	if (getUsername() != null) {
	    return "User " + getUsername() + " succesfully searched '" + getSearchedText() + "'.";
	}
	return "Anonymous User succesfully searched '" + getSearchedText() + "'.";
    }
}
