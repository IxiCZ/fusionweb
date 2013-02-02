package cz.ixi.fusionweb.drools.model;

import java.util.List;

public class UserNavigationsEvent {

    private String username;
    private List<NavigationEvent> navigations;
    
    public UserNavigationsEvent(String username, List<NavigationEvent> navigations) {
	super();
	this.username = username;
	this.navigations = navigations;
    }

    public String getUsername() {
        return username;
    }

    public List<NavigationEvent> getNavigations() {
        return navigations;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNavigations(List<NavigationEvent> navigations) {
        this.navigations = navigations;
    }

    @Override
    public String toString() {
	return "UserNavigationsEvent [username=" + username + ", navigations=" + navigations + "]";
    }
    
}
