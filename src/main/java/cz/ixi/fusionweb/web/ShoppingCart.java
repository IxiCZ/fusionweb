package cz.ixi.fusionweb.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cz.ixi.fusionweb.drools.listeners.OrderListener;
import cz.ixi.fusionweb.ejb.OrderBean;
import cz.ixi.fusionweb.entities.Order;
import cz.ixi.fusionweb.entities.OrderItem;
import cz.ixi.fusionweb.entities.Product;
import cz.ixi.fusionweb.entities.Role;
import cz.ixi.fusionweb.entities.User;
import cz.ixi.fusionweb.web.util.JsfUtil;

@Named(value = "shoppingCart")
@SessionScoped
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private OrderListener droolsOrders;

    @EJB
    OrderBean facade;

    private List<OrderItem> cartItems;

    public String addItem(Product p) {
	System.out.println("added item: " + p);
	
	if (cartItems == null) {
	    cartItems = new ArrayList<OrderItem>();
	}

	for (OrderItem oi : cartItems) {
	    if (oi.getProduct().equals(p)) {
		oi.setQuantity(oi.getQuantity() + 1);
		return "";
	    }
	}
	cartItems.add(new OrderItem(null, p, 1));

	return "";
    }

    public boolean removeItem(Product p) {
	OrderItem toRemove = null;
	for (OrderItem oi : cartItems) {
	    if (oi.getProduct().equals(p)) {
		toRemove = oi;
		break;
	    }
	}
	if (toRemove != null) {
	    return cartItems.remove(toRemove);
	}
	return false;
    }

    public boolean removeItem(OrderItem item) {
	return cartItems.remove(item);
    }

    public double getTotal() {
	if ((cartItems == null) || cartItems.isEmpty()) {
	    return 0f;
	}

	double total = 0f;

	for (OrderItem oi : cartItems) {
	    total += oi.getProduct().getPrice() * oi.getQuantity();
	}

	return total;
    }

    public String checkout(User user) {
	if (user == null) {
	    JsfUtil.addErrorMessage(JsfUtil.getStringFromBundle("/Bundle", "LoginBeforeCheckout"));
	    return "";
	} else {
	    if (user.getRoles().contains(Role.ADMINISTRATOR)) {
		JsfUtil.addErrorMessage(JsfUtil.getStringFromBundle("/Bundle", "AdministratorNotAllowed"));
		return "";
	    }

	    Order order = new Order();
	    order.setDateCreated(Calendar.getInstance().getTime());
	    order.setAmount(getTotal());
	    order.setCustomer(user);
	    for (OrderItem oi : getCartItems()) {
		oi.setOrder(order);
	    }
	    order.setOrderItemList(cartItems);
	    facade.create(order);
	    droolsOrders.newOrder(order, user);

	    System.out.println("order created");

	    JsfUtil.addSuccessMessage(JsfUtil.getStringFromBundle("/Bundle", "Cart_Checkout_Success"));
	    clear();
	    return "/customer/customerOrders";
	}

    }

    public void clear() {
	cartItems.clear();
    }

    public List<OrderItem> getCartItems() {
	return cartItems;
    }

    public int getCartSize() {
	if (cartItems == null) {
	    return 0;
	}
	return cartItems.size();
    }

    public int getCartAbsoluteSize() {
	if (cartItems == null) {
	    return 0;
	}
	int size = 0;
	for (OrderItem oi : cartItems) {
	    size += oi.getQuantity();
	}
	return size;
    }

    public String cartPage() {
	return "/common/shoppingCart";
    }
}
