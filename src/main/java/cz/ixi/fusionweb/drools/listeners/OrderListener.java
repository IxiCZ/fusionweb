package cz.ixi.fusionweb.drools.listeners;

import java.io.Serializable;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import cz.ixi.fusionweb.drools.model.OrderCreatedEvent;
import cz.ixi.fusionweb.drools.model.ProductBoughtEvent;
import cz.ixi.fusionweb.drools.rules.DroolsResourcesBean;
import cz.ixi.fusionweb.entities.Order;
import cz.ixi.fusionweb.entities.OrderItem;
import cz.ixi.fusionweb.entities.User;

/**
 * Inserts events considering orders into drools working memory.
 */
@ManagedBean(name = "orderListener")
public class OrderListener implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    DroolsResourcesBean drools;

    /**
     * Creates and inserts multiple events for created order: one representing
     * the created order and one for each bought product item.
     * 
     * @param order order from which are created the events
     * @param user user creating the order
     */
    public void newOrder(Order order, User user) {
	drools.insertFact(new OrderCreatedEvent(order.getId(), user.getUsername(), getTotalItemsCount(order), order
		.getAmount()));
	for (OrderItem oi : order.getOrderItemList()) {
	    for (int i = 1; i <= oi.getQuantity(); i++) {
		drools.insertFact(new ProductBoughtEvent(order.getId(), oi.getProduct().getId(), user.getUsername(), oi
			.getProduct().getName()));
	    }
	}
    }

    /**
     * Return total number items in the given order.
     * 
     * @param order of which the total items should be count
     * @return total number items in the given order
     */
    private int getTotalItemsCount(Order order) {
	int total = 0;
	for (Iterator<OrderItem> it = order.getOrderItemList().iterator(); it.hasNext();) {
	    OrderItem oi = it.next();
	    total += oi.getQuantity();
	}
	return total;
    }
}
