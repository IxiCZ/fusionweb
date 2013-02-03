package cz.ixi.fusionweb.drools.model;

/**
 * Class representing general navigation event.
 */
public class GeneralUserActionEvent {

    private String username;

    public GeneralUserActionEvent(String username) {
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
