package cz.ixi.fusionweb.drools.model;

public class CustomerLogInEvent extends GeneralUserActionEvent {

    public CustomerLogInEvent(String username) {
	super(username);
    }

    @Override
    public String toString() {
	return "CustomerLogInEvent [username=" + getUsername() + "]";
    }
}
