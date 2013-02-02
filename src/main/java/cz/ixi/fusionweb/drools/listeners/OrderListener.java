package cz.ixi.fusionweb.drools.listeners;

import java.io.Serializable;

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

    public void newOrder(Order order, User user) {
	drools.insertFact(new OrderCreatedEvent(order.getId(), user.getUsername(), order.getOrderItemList().size(),
		order.getAmount()));
	for (OrderItem oi : order.getOrderItemList()) {
	    for (int i = 1; i <= oi.getQuantity(); i++) {
		drools.insertFact(new ProductBoughtEvent(order.getId(), oi.getProduct().getId(), user.getUsername(), oi
			.getProduct().getName()));
	    }
	}
    }
}
