package cz.ixi.fusionweb.web;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;

import cz.ixi.fusionweb.ejb.CustomerBean;
import cz.ixi.fusionweb.ejb.UserBean;
import cz.ixi.fusionweb.entities.Customer;
import cz.ixi.fusionweb.entities.User;
import cz.ixi.fusionweb.web.util.JsfUtil;

@SessionScoped
@ManagedBean(name = "customerController")
public class CustomerController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String BUNDLE = "/Bundle";
    private static final String REGISTRATION_PAGE = "/common/newCustomer";
    private static final String LOGIN_PAGE = "/common/login";

    @EJB
    private CustomerBean customers;

    @EJB
    private UserBean users;

    private Customer current;

    /**
     * Constructor
     */
    public CustomerController() {
    }

    public Customer getSelected() {
	if (current == null) {
	    current = new Customer();
	}

	return current;
    }

    private CustomerBean getFacade() {
	return customers;
    }

    public String prepareRegistration() {
	current = new Customer();

	return REGISTRATION_PAGE;
    }

    private boolean isUserDuplicated(User u) {
	return (users.getUserByUsername(u.getUsername()) == null) ? false : true;
    }

    public String create() {
	try {
	    if (!isUserDuplicated(current)) {
		getFacade().create(current);
		JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("CustomerRegistrated"));
	    } else {
		JsfUtil.addErrorMessage(ResourceBundle.getBundle(BUNDLE).getString("DuplicatedCustomerError"));
		return REGISTRATION_PAGE;
	    }

	    return LOGIN_PAGE;
	} catch (Exception e) {
	    JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("CustomerCreationError"));

	    return null;
	}
    }

    public SelectItem[] getItemsAvailableSelectOne() {
	return JsfUtil.getSelectItems(customers.findAll(), true);
    }

    @FacesConverter(forClass = Customer.class)
    public static class CustomerControllerConverter implements Converter {
	public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
	    if ((value == null) || (value.length() == 0)) {
		return null;
	    }

	    CustomerController controller = (CustomerController) facesContext.getApplication().getELResolver()
		    .getValue(facesContext.getELContext(), null, "customerController");

	    return controller.customers.getCustomerByUsername(value);
	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
	    if (object == null) {
		return null;
	    }

	    if (object instanceof Customer) {
		Customer o = (Customer) object;

		return o.getUsername();
	    } else {
		throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName()
			+ "; expected type: " + CustomerController.class.getName());
	    }
	}
    }
}
