package cz.ixi.fusionweb.drools.listeners;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import cz.ixi.fusionweb.drools.model.CustomerLogInEvent;
import cz.ixi.fusionweb.drools.model.CustomerRegistrationEvent;
import cz.ixi.fusionweb.drools.model.CustomerRegistrationUnsuccessfulEvent;
import cz.ixi.fusionweb.drools.rules.DroolsResourcesBean;
import cz.ixi.fusionweb.ejb.UserBean;
import cz.ixi.fusionweb.entities.Customer;
import cz.ixi.fusionweb.entities.User;

/**
 * Inserts events considering customer registration ang logging into drools
 * working memory.
 */
@ManagedBean(name = "customerLogActionsListener")
public class CustomerLogActionsListener {

    @EJB
    DroolsResourcesBean drools;

    @EJB
    private UserBean users;

    public void newCustomerRegistration(Customer customer) {
	if (users.getUserByUsername(customer.getUsername()) == null) {
	    drools.insertFact(new CustomerRegistrationEvent(customer.getUsername()));
	} else {
	    drools.insertFact(new CustomerRegistrationUnsuccessfulEvent(customer.getUsername()));
	}
    }

    public void customerLogIn(User customer) {
	drools.insertFact(new CustomerLogInEvent(customer.getUsername()));
    }
}
