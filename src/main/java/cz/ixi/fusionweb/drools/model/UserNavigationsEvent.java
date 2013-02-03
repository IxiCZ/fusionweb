package cz.ixi.fusionweb.drools.model;

import java.util.List;

public class UserNavigationsEvent {

    private String username;
    private List<GeneralUserActionEvent> navigations;
    
    public UserNavigationsEvent(String username, List<GeneralUserActionEvent> navigations) {
	super();
	this.username = username;
	this.navigations = navigations;
    }

    public String getUsername() {
        return username;
    }

    public List<GeneralUserActionEvent> getNavigations() {
        return navigations;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNavigations(List<GeneralUserActionEvent> navigations) {
        this.navigations = navigations;
    }

    @Override
    public String toString() {
	return "UserNavigationsEvent [username=" + username + ", navigations=" + navigations + "]";
    }
    
}
