package cz.ixi.fusionweb.web;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;

import cz.ixi.fusionweb.ejb.OrderItemBean;
import cz.ixi.fusionweb.entities.Order;
import cz.ixi.fusionweb.entities.OrderItem;

@Named(value = "orderItemController")
@SessionScoped
public class OrderItemController implements Serializable{

    private static final long serialVersionUID = 1L;
    private static final String ORDER_DETAIL_PATH = "/orderDetail/orderDetail";
    
    @EJB
    private OrderItemBean ejbFacade;
    private DataModel<OrderItem> items = null;
    private Order currentOrder;

    public OrderItemController() {
    }

    private OrderItemBean getFacade() {
	return ejbFacade;
    }

    public Order getCurrentOrder() {
	return currentOrder;
    }

    public String prepareView(Order order) {
	currentOrder = order;

	return ORDER_DETAIL_PATH;
    }

    public DataModel<OrderItem> getItems() {
	if (currentOrder == null) {
	    return new ListDataModel<OrderItem>();
	}
	items = new ListDataModel<OrderItem>(getFacade().findOrderItemsByOrder(currentOrder.getId()));

        return items;
    }
}
