package cz.ixi.fusionweb.web;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;

import cz.ixi.fusionweb.drools.model.CustomerLogInEvent;
import cz.ixi.fusionweb.drools.model.GeneralUserActionEvent;
import cz.ixi.fusionweb.drools.rules.DroolsResourcesBean;
import cz.ixi.fusionweb.web.util.PageNavigation;

@ManagedBean(name = "userMonitoringController")
@SessionScoped
public class UserMonitoringController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private DroolsResourcesBean drools;

    private DataModel<CustomerLogInEvent> users;
    private DataModel<GeneralUserActionEvent> userEventItems = null;
    private String currentUsername;

    public UserMonitoringController() {
    }

    public String getCurrentUsername() {
	return currentUsername;
    }

   /* private void d() {
	List<String> usernames = drools.getAllRecentlyLoggedCustomers();
	for (String username : usernames) {
	    System.out.println("- User: " + username);
	    for (GeneralUserActionEvent ne : drools.getAllActionEventsForCustomer(username)) {
		System.out.println("--- " + ne);
	    }
	}
    }*/

    public String prepareUsersList() {
//	d();
	recreateUsersModel();

	return "/administrator/monitoring/user/List";
    }

    public PageNavigation prepareView() {
	recreateUserEventItemsModel();
	currentUsername = ((CustomerLogInEvent) getUsers().getRowData()).getUsername();

	return PageNavigation.VIEW; 
    }

    public DataModel<CustomerLogInEvent> getUsers() {
	if (users == null) {
	    users = new ListDataModel<CustomerLogInEvent>(new ArrayList<CustomerLogInEvent>(drools.getAllRecentlyLoggedCustomerEvents()));
	}

	return users;
    }

    public DataModel<GeneralUserActionEvent> getUserEventItems() {
	if (userEventItems == null) {
	    userEventItems = new ListDataModel<GeneralUserActionEvent>(new ArrayList<GeneralUserActionEvent>(drools.getAllActionEventsForCustomer(currentUsername)));
	}

	return userEventItems;
    }

    private void recreateUsersModel() {
	users = null;
    }

    private void recreateUserEventItemsModel() {
	userEventItems = null;
    }
}
