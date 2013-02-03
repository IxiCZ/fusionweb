package cz.ixi.fusionweb.drools.model;

public class CustomerMyOrdersNavigationEvent extends GeneralUserActionEvent {

    public CustomerMyOrdersNavigationEvent(String username) {
	super(username);
    }

    @Override
    public String toString() {
	return "Customer " + getUsername() + " visited 'MyOrders' menu option.";
    }
}
