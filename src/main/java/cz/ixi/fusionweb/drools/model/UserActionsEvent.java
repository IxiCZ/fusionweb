package cz.ixi.fusionweb.drools.model;

import java.util.List;

public class UserActionsEvent {

    private String username;
    private List<GeneralUserActionEvent> actions;
    
    public UserActionsEvent(String username, List<GeneralUserActionEvent> actions) {
	super();
	this.username = username;
	this.actions = actions;
    }

    public String getUsername() {
        return username;
    }

    public List<GeneralUserActionEvent> getActions() {
        return actions;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setActions(List<GeneralUserActionEvent> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
	return "UserNavigationsEvent [username=" + username + ", actions=" + actions + "]";
    }
    
}
