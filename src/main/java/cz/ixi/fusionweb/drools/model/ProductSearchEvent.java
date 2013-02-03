package cz.ixi.fusionweb.drools.model;


/**
 * Class representing product search event.
 */
public class ProductSearchEvent extends GeneralUserActionEvent {

    private String searchedText;

    public ProductSearchEvent(String searchedText, String username) {
	super(username);
	this.searchedText = searchedText;
    }

    public String getSearchedText() {
	return searchedText;
    }

    public void setSearchedText(String searchedText) {
	this.searchedText = searchedText;
    }


    @Override
    public String toString() {
	if (getUsername() == null) {
	    return "ProductSearch event of searched text: " + searchedText + " by anonymous user";
	}
	return "ProductSearch event of searched text: " + searchedText + " by " + getUsername();
    }
}
