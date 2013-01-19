package cz.ixi.fusionweb.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import org.hibernate.Hibernate;

import cz.ixi.fusionweb.ejb.OrderBean;
import cz.ixi.fusionweb.entities.Order;
import cz.ixi.fusionweb.entities.OrderItem;
import cz.ixi.fusionweb.entities.OrderStatus;
import cz.ixi.fusionweb.entities.User;
import cz.ixi.fusionweb.web.util.AbstractPaginationHelper;
import cz.ixi.fusionweb.web.util.JsfUtil;
import cz.ixi.fusionweb.web.util.PageNavigation;

@Named(value = "orderController")
@SessionScoped
public class OrderController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String BUNDLE = "/Bundle";

    @EJB
    private OrderBean ejbFacade;
    private AbstractPaginationHelper pagination;
    private Order current;
    private DataModel<Order> items = null;
    private List<Order> myOrders;
    private String searchString;
    private int selectedItemIndex;

    public OrderController() {
    }

    public Order getSelected() {
	if (current == null) {
	    current = new Order();
	    selectedItemIndex = -1;
	}

	return current;
    }

    private OrderBean getFacade() {
	return ejbFacade;
    }

    public OrderStatus getStatusPaid() {
	return OrderStatus.PAID;
    }

    public OrderStatus getStatusSent() {
	return OrderStatus.SENT;
    }

    public OrderStatus getStatusCancelled() {
	return OrderStatus.CANCELLED;
    }

    public AbstractPaginationHelper getPagination() {
	if (pagination == null) {
	    pagination = new AbstractPaginationHelper(AbstractPaginationHelper.DEFAULT_SIZE) {
		@Override
		public int getItemsCount() {
		    return getFacade().count();
		}

		@Override
		public DataModel<Order> createPageDataModel() {
		    return new ListDataModel<Order>(getFacade().findRange(
			    new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }));
		}
	    };
	}

	return pagination;
    }

    public PageNavigation prepareList() {
	recreateModel();

	return PageNavigation.LIST;
    }

    public PageNavigation prepareView() {
	current = (Order) getItems().getRowData();
	selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
	current = ejbFacade.merge(current);
	Hibernate.initialize(current.getOrderItemList());

	return PageNavigation.VIEW;
    }

    public PageNavigation prepareEdit() {
	current = (Order) getItems().getRowData();
	selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
	current = ejbFacade.merge(current);
	Hibernate.initialize(current.getOrderItemList());

	return PageNavigation.EDIT;
    }

    public PageNavigation update() {
	double total = 0d;
	for (Iterator<OrderItem> it = current.getOrderItemList().iterator(); it.hasNext();) {
	    OrderItem oi = it.next();
	    total += oi.getProduct().getPrice() * oi.getQuantity();
	}
	current.setAmount(total);

	try {
	    getFacade().merge(current);
	    JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("CustomerOrderUpdated"));

	    return PageNavigation.VIEW;
	} catch (Exception e) {
	    JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));

	    return null;
	}
    }

    public PageNavigation prepareCreate() {
	current = new Order();
	selectedItemIndex = -1;

	return PageNavigation.CREATE;
    }

    public PageNavigation create() {
	try {
	    getFacade().create(current);
	    JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("CustomerOrderCreated"));

	    return prepareCreate();
	} catch (Exception e) {
	    JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));

	    return null;
	}
    }

    public PageNavigation destroy() {
	current = (Order) getItems().getRowData();
	selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
	performDestroy();
	recreateModel();

	return PageNavigation.LIST;
    }

    public PageNavigation markOrderAsFromList(OrderStatus status) {
	current = (Order) getItems().getRowData();
	selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
	markOrderAs(current, status);

	return PageNavigation.LIST;
    }

    public PageNavigation markOrderAs(Order order, OrderStatus status) {
	order.setOrderStatus(status);
	ejbFacade.edit(order);
	recreateModel();

	return PageNavigation.VIEW;
    }

    public List<Order> getMyOrders(User user) {
	if (user != null) {
	    myOrders = getFacade().getOrderByCustomerUsername(user.getUsername());

	    if (myOrders.isEmpty()) {
		return null;
	    } else {
		return myOrders;
	    }
	} else {
	    JsfUtil.addErrorMessage("Current user is not authenticated. Please do login before accessing your orders.");

	    return null;
	}
    }

    public PageNavigation destroyAndView() {
	performDestroy();
	recreateModel();
	updateCurrentItem();

	if (selectedItemIndex >= 0) {
	    return PageNavigation.VIEW;
	} else {
	    // all items were removed - go back to list
	    recreateModel();

	    return PageNavigation.LIST;
	}
    }

    private void performDestroy() {
	try {
	    getFacade().remove(current.getId());
	    JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("CustomerOrderDeleted"));
	} catch (Exception e) {
	    JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
	}
    }

    private void updateCurrentItem() {
	int count = getFacade().count();

	if (selectedItemIndex >= count) {
	    // selected index cannot be bigger than number of items:
	    selectedItemIndex = count - 1;

	    // go to previous page if last page disappeared:
	    if (pagination.getPageFirstItem() >= count) {
		pagination.previousPage();
	    }
	}

	if (selectedItemIndex >= 0) {
	    current = getFacade().findRange(new int[] { selectedItemIndex, selectedItemIndex + 1 }).get(0);
	}
    }

    @SuppressWarnings("unchecked")
    public DataModel<Order> getItems() {
	if (items == null) {
	    items = (DataModel<Order>) getPagination().createPageDataModel();
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

    public SelectItem[] getItemsAvailableSelectMany() {
	return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
	return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public SelectItem[] statusItemsAvailableSelectOne() {
	List<OrderStatus> statuses = new ArrayList<OrderStatus>();
	statuses.add(OrderStatus.NEW);
	statuses.add(OrderStatus.PAID);
	statuses.add(OrderStatus.SENT);
	statuses.add(OrderStatus.CANCELLED);
	return JsfUtil.getSelectItems(statuses, true);
    }

    /**
     * @return the searchString
     */
    public String getSearchString() {
	return searchString;
    }

    /**
     * @param searchString
     *            the searchString to set
     */
    public void setSearchString(String searchString) {
	this.searchString = searchString;
    }

    @FacesConverter(forClass = Order.class)
    public static class CustomerOrderControllerConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
	    if ((value == null) || (value.length() == 0)) {
		return null;
	    }

	    OrderController controller = (OrderController) facesContext.getApplication().getELResolver()
		    .getValue(facesContext.getELContext(), null, "customerOrderController");

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

	    if (object instanceof Order) {
		Order o = (Order) object;

		return getStringKey(o.getId());
	    } else {
		throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName()
			+ "; expected type: " + OrderController.class.getName());
	    }
	}
    }
}
