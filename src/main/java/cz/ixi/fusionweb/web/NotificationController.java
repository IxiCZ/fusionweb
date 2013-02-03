package cz.ixi.fusionweb.web;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import cz.ixi.fusionweb.ejb.NotificationBean;
import cz.ixi.fusionweb.entities.Notification;
import cz.ixi.fusionweb.entities.NotificationSeverity;
import cz.ixi.fusionweb.web.util.AbstractPaginationHelper;
import cz.ixi.fusionweb.web.util.JsfUtil;
import cz.ixi.fusionweb.web.util.PageNavigation;

@ManagedBean(name = "notificationController")
@SessionScoped
public class NotificationController implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String BUNDLE = "/Bundle";
    private static final int PAGE_SIZE = 10;

    @EJB
    private NotificationBean ejbFacade;

    private AbstractPaginationHelper pagination;
    private DataModel<Notification> items = null;
    private NotificationSeverity currentSeverity = null;

    public NotificationController() {
    }

    private NotificationBean getFacade() {
	return ejbFacade;
    }

    public NotificationSeverity getCurrtenSeverity() {
	return currentSeverity;
    }

    public AbstractPaginationHelper getPagination() {
	if (pagination == null) {
	    pagination = new AbstractPaginationHelper(PAGE_SIZE) {
		@Override
		public int getItemsCount() {
		    if (currentSeverity == null) {
			return getFacade().count();
		    }
		    return getFacade().count(currentSeverity);
		}

		@Override
		public DataModel<Notification> createPageDataModel() {
		    if (currentSeverity == null) {
			return new ListDataModel<Notification>(getFacade().findRange(
				new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }));
		    }
		    return new ListDataModel<Notification>(getFacade().findRange(
			    new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }, currentSeverity));
		}
	    };
	}

	return pagination;
    }

    public PageNavigation prepareList() {	
	recreateModel();

	return PageNavigation.LIST;
    }

    public PageNavigation prepareAllList() {
	currentSeverity = null;
	return prepareList();
    }

    public PageNavigation prepareSevereList() {
	currentSeverity = NotificationSeverity.SEVERE;
	return prepareList();
    }

    public PageNavigation prepareWarningList() {
	currentSeverity = NotificationSeverity.WARNING;
	return prepareList();
    }

    public PageNavigation prepareInfoList() {
	currentSeverity = NotificationSeverity.INFO;
	return prepareList();
    }

    public PageNavigation prepareGoodList() {
	currentSeverity = NotificationSeverity.GOOD;
	return prepareList();
    }
    
    public String prepareAllListOutside() {
	recreateModel();
	currentSeverity = null;
	return "/administrator/notification/List";
    }

    public PageNavigation destroy() {
	try {
	    getFacade().remove((Notification) getItems().getRowData());
	    JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("CategoryDeleted"));
	} catch (Exception e) {
	    JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
	}
	recreateModel();

	return PageNavigation.LIST;
    }

    @SuppressWarnings("unchecked")
    public DataModel<Notification> getItems() {
	if (items == null) {
	    items = (DataModel<Notification>) getPagination().createPageDataModel();
	}

	return items;
    }

    private void recreateModel() {
	items = null;
    }

    public PageNavigation next() {
	getPagination().nextPage();
	recreateModel();

	return PageNavigation.LIST;
    }

    public PageNavigation previous() {
	getPagination().previousPage();
	recreateModel();

	return PageNavigation.LIST;
    }

    @FacesConverter(forClass = Notification.class)
    public static class NotificationControllerConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
	    if ((value == null) || (value.length() == 0)) {
		return null;
	    }

	    NotificationController controller = (NotificationController) facesContext.getApplication().getELResolver()
		    .getValue(facesContext.getELContext(), null, "notificationController");

	    return controller.ejbFacade.find(getKey(value));
	}

	java.lang.Integer getKey(String value) {
	    java.lang.Integer key;
	    key = Integer.valueOf(value);

	    return key;
	}

	String getStringKey(java.lang.Integer value) {
	    StringBuilder sb = new StringBuilder();
	    sb.append(value);

	    return sb.toString();
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
	    if (object == null) {
		return null;
	    }

	    if (object instanceof Notification) {
		Notification o = (Notification) object;

		return getStringKey(o.getId());
	    } else {
		throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName()
			+ "; expected type: " + NotificationController.class.getName());
	    }
	}
    }
}
