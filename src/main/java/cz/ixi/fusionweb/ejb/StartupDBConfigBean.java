package cz.ixi.fusionweb.ejb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.io.IOUtils;

import cz.ixi.fusionweb.entities.Administrator;
import cz.ixi.fusionweb.entities.Customer;
import cz.ixi.fusionweb.entities.DiscussionEntry;
import cz.ixi.fusionweb.entities.Notification;
import cz.ixi.fusionweb.entities.NotificationSeverity;
import cz.ixi.fusionweb.entities.Order;
import cz.ixi.fusionweb.entities.OrderItem;
import cz.ixi.fusionweb.entities.Product;
import cz.ixi.fusionweb.entities.ProductCategory;

/**
 * Fills the database during the startup.
 */
@Singleton
@Startup
public class StartupDBConfigBean {

    @EJB
    private ProductCategoryBean categories;
    @EJB
    private ProductBean products;
    @EJB
    private AdministratorBean administrators;
    @EJB
    private CustomerBean customers;
    @EJB
    private OrderBean orders;
    @EJB
    private OrderItemBean orderItems;
    @EJB
    private DiscussionEntryBean discussionEntries;
    @EJB
    private NotificationBean notifications;

    @PostConstruct
    public void createData() {
	// PRODUCT CATEGORIES:
	ProductCategory computers = new ProductCategory("Computers");
	ProductCategory cellPhones = new ProductCategory("Cell phones");
	ProductCategory televisions = new ProductCategory("Televisions");
	ProductCategory cameras = new ProductCategory("Cameras");
	ProductCategory mp3players = new ProductCategory("MP3 players");

	categories.create(computers);
	categories.create(cellPhones);
	categories.create(televisions);
	categories.create(cameras);
	categories.create(mp3players);

	// PRODUCTS:
	createProductsInCatgory(computers, "computerProducts.txt");
	createProductsInCatgory(cellPhones, "cellPhoneProducts.txt");
	createProductsInCatgory(televisions, "televisonProducts.txt");
	createProductsInCatgory(cameras, "cameraProducts.txt");
	createProductsInCatgory(mp3players, "mp3PlayerProducts.txt");

	// USERS:
	// administrators:
	Administrator hugo = new Administrator("hugo", "1234", "Hugo", "Coconut", "hugo@coconut.com",
		"Mysterious Island", "Atlantic Ocean");
	administrators.create(hugo);

	// customers
	Customer rick = new Customer("rick", "1234", "Richie", "Rich", "richie@rich.com", "Villa Riccardo", "Florence");
	Customer will = new Customer("will", "1234", "William", "The Conqueror", "william@theconqueror.com",
		"Tower of London", "London");
	Customer frodo = new Customer("frodo", "1234", "Frodo", "Baggins", "frodo@baggins.com", "Bag End",
		"New Zealand");

	customers.create(rick);
	customers.create(will);
	customers.create(frodo);

	// ORDERS:
	List<Product> allProducts = products.findAll();

	Order frodoOrder = new Order(frodo, 0, new Date());
	List<OrderItem> itemsFrodo = new ArrayList<OrderItem>();
	OrderItem frodoTv = new OrderItem(frodoOrder, allProducts.get(30), 1);
	itemsFrodo.add(frodoTv);
	frodoOrder.setAmount(frodoTv.getQuantity() * frodoTv.getProduct().getPrice());
	frodoOrder.setOrderItemList(itemsFrodo);
	orders.create(frodoOrder);

	for (int i = 1; i < 8; i++) {
	    Order rickOrder = new Order(rick, 0, new Date());
	    List<OrderItem> items = new ArrayList<OrderItem>();

	    OrderItem firstItems = new OrderItem(rickOrder, allProducts.get(i * 6), allProducts.size() % (2 * i));
	    items.add(firstItems);
	    OrderItem secondItems = new OrderItem(rickOrder, allProducts.get(allProducts.size() - i * 4),
		    allProducts.size() % (7 * i));
	    items.add(secondItems);
	    OrderItem thirdItems = new OrderItem(rickOrder, allProducts.get(allProducts.size() / (2 * i)),
		    allProducts.size() % (5 * i));
	    items.add(thirdItems);

	    rickOrder.setAmount(firstItems.getQuantity() * firstItems.getProduct().getPrice()
		    + secondItems.getQuantity() * secondItems.getProduct().getPrice() + thirdItems.getQuantity()
		    * thirdItems.getProduct().getPrice());
	    rickOrder.setOrderItemList(items);

	    if (i % 2 == 0) {
		Order willOrder = new Order(will, 0, new Date());
		items.clear();
		firstItems = new OrderItem(willOrder, allProducts.get(i * 5), allProducts.size() % (3 * i));
		items.add(firstItems);
		secondItems = new OrderItem(willOrder, allProducts.get(allProducts.size() - i * 6), allProducts.size()
			% (6 * i));
		items.add(secondItems);
		willOrder.setAmount(firstItems.getQuantity() * firstItems.getProduct().getPrice()
			+ secondItems.getQuantity() * secondItems.getProduct().getPrice());
		willOrder.setOrderItemList(items);
		orders.create(willOrder);
	    }
	    orders.create(rickOrder);
	}

	// DISCUSSION
	for (Product p : allProducts) {
	    discussionEntries
		    .create(new DiscussionEntry(p, new Date(), will, "Finally!", "Oh my god! I've been waiting for "
			    + p.getName() + " for so long to be afordable! There is no better "
			    + p.getCategory().getName().substring(0, p.getCategory().getName().length() - 1)
			    + ". I cannot express my happiness, that I found it here just for $" + p.getPrice() + "!!!"));
	}
	for (Product p : allProducts) {
	    if (p.getId() % 2 == 0) {
		discussionEntries.create(new DiscussionEntry(p, new Date(), rick, "Looks nice",
			"I think this would looked nice in my villa."));
	    }
	}
	discussionEntries.create(new DiscussionEntry(allProducts.get(30), new Date(), frodo, "Looking forward to",
		"I've just ordered it! I am looking forward to seeing all LOTR characters in HD."));

	for (int i = 1; i <= 25; i++) {
	    Notification note = new Notification(
		    NotificationSeverity.INFO,
		    new Date(),
		    i
			    + ". notification. Some user did domething that should be noticed and be responded to with determination and force so horrible that the ser won't ever do such thing again.");

	    if (i % 3 == 0) {
		note.setSeverity(NotificationSeverity.WARNING);
	    }
	    if (i % 4 == 0) {
		note.setSeverity(NotificationSeverity.SEVERE);
	    }
	    if (i % 5 == 0) {
		note.setSeverity(NotificationSeverity.GOOD);
	    }
	    notifications.create(note);
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}

	Logger.getLogger(StartupDBConfigBean.class.getName()).log(Level.INFO,
		"Creating initial items in the database finished");
    }

    private void createProductsInCatgory(ProductCategory category, String name) {
	InputStream is = Thread.currentThread().getContextClassLoader()
		.getResourceAsStream(category.getName() + "/" + name);
	if (is == null) {
	    Logger.getLogger(StartupDBConfigBean.class.getName()).log(Level.SEVERE,
		    "No products file with name: " + name);
	    return;
	}
	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	try {
	    String line = reader.readLine();
	    while (line != null) {
		String[] lineParts = line.split("---");
		if (lineParts.length != 5) {
		    Logger.getLogger(StartupDBConfigBean.class.getName()).log(Level.SEVERE,
			    "Products file " + name + " does not have right format (number of parts).");
		    return;
		}
		double price = 0.0;
		try {
		    price = Double.parseDouble(lineParts[1]);
		} catch (NumberFormatException e) {
		    Logger.getLogger(StartupDBConfigBean.class.getName()).log(Level.SEVERE,
			    "Products file " + name + "  does not have right format (price).");
		    return;
		}
		Product product = new Product(category, lineParts[0], price, lineParts[2]);
		product.setImg(lineParts[3]);
		product.setImgSrc(loadImage(category.getName() + "/" + lineParts[4]));
		products.create(product);
		line = reader.readLine();
	    }

	} catch (IOException e) {
	    Logger.getLogger(StartupDBConfigBean.class.getName()).log(Level.SEVERE, "Error reading " + name, e);
	    e.printStackTrace();
	} finally {
	    if (reader != null) {
		try {
		    reader.close();
		} catch (IOException e) {
		    Logger.getLogger(StartupDBConfigBean.class.getName()).log(Level.SEVERE, "Error closing " + name, e);
		}
	    }
	}
	Logger.getLogger(StartupDBConfigBean.class.getName()).log(Level.INFO,
		"Products from " + name + " successfully imported.");
    }

    private byte[] loadImage(String name) {
	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	try {
	    if (is != null) {
		return IOUtils.toByteArray(is);
	    }
	} catch (IOException e) {
	    Logger.getLogger(StartupDBConfigBean.class.getName()).log(Level.SEVERE, "Error loading image" + name, e);
	    e.printStackTrace();
	} finally {
	    if (is != null) {
		try {
		    is.close();
		} catch (IOException e) {
		    Logger.getLogger(StartupDBConfigBean.class.getName()).log(Level.SEVERE,
			    "Error closing stream " + name, e);
		    e.printStackTrace();
		}
	    }
	}
	return null;
    }

}
