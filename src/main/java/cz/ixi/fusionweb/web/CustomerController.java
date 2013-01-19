package cz.ixi.fusionweb.web;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;

import cz.ixi.fusionweb.ejb.CustomerBean;
import cz.ixi.fusionweb.entities.Customer;
import cz.ixi.fusionweb.web.util.JsfUtil;

@ViewScoped
@ManagedBean(name = "customerController")
public class CustomerController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private CustomerBean customers;

    /**
     * Constructor
     */
    public CustomerController() {
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
