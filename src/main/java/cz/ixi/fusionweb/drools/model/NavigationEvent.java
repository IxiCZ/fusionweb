package cz.ixi.fusionweb.drools.model;

/**
 * Class representing general navigation event.
 */
public class NavigationEvent {

    private String username;

    public NavigationEvent(String username) {
	super();
	this.username = username;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }
}
