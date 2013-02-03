package cz.ixi.fusionweb.drools.model;

public class CustomerLogOutEvent extends GeneralUserActionEvent {

    public CustomerLogOutEvent(String username) {
	super(username);
    }

    @Override
    public String toString() {

	return "Customer " + getUsername() + " logged out.";
    }
}
