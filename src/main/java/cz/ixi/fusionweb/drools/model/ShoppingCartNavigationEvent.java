package cz.ixi.fusionweb.drools.model;

/**
 * Class representing shopping cart navigation event.
 */
public class ShoppingCartNavigationEvent extends GeneralUserActionEvent {

    public ShoppingCartNavigationEvent(String username) {
	super(username);
    }
    

    @Override
    public String toString() {
	if (getUsername() != null) {
	    return "User " + getUsername() + " visited shopping cart";
	}
	return "Anonymous User visited shopping cart";
    }
}
