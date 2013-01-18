package cz.ixi.fusionweb.ejb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.io.IOUtils;

import cz.ixi.fusionweb.entities.Administrator;
import cz.ixi.fusionweb.entities.Customer;
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
     

    @PostConstruct
    public void createData() {
	// PRODUCT CATEGORIES:
	ProductCategory televisions = new ProductCategory("Televisions");
	ProductCategory mobiles = new ProductCategory("Mobiles");
	ProductCategory computers = new ProductCategory("Computers");

	categories.create(televisions);
	categories.create(mobiles);
	categories.create(computers);

	// PRODUCTS:
	// in computers category
	Product ultraDell = new Product(computers, "Dell Inspiron i15R-1633sLV 15.6-Inch Laptop", 459.99,
		"Intel Core i3-2370M 2.4 GHz, 6 GB DDR3, 750 GB 5400 rpm Hard Drive, 15.6-Inch Screen, Windows 7 Home Premium 64-bit");
	ultraDell.setImg("dell-inspiron.jpg");
	ultraDell.setImgSrc(loadImage("dell-inspiron.jpg"));

	Product asus = new Product(computers, "ASUS A54C-AB91 15.6-Inch Laptop (Black)", 421.79,
		"Intel Pentium_B970 Processor 2.3GHz, 4 GB SO-DIMM RAM, 500GB 5400rpm Hard Drive,"
			+ " 15.6-Inch Screen, Windows 7 Home Premium 64-bit");
	asus.setImg("asus-a54c.jpg");
	asus.setImgSrc(loadImage("asus-a54c.jpg"));

	Product hpPavilion = new Product(computers, "HP Pavilion g7-2238nr 17.3-Inch Laptop", 439.99,
		"AMD A-Series Dual-Core A6-4400M 2.7 GHz (1 MB Cache), 4 GB DDR3, 500 GB 5400 rpm Hard Drive,"
			+ " 17.3-Inch Screen, Windows 8, 3.15-hour battery life");
	hpPavilion.setImg("hp-pavillion.jpg");
	hpPavilion.setImgSrc(loadImage("hp-pavillion.jpg"));

	products.create(ultraDell);
	products.create(asus);
	products.create(hpPavilion);

	// in televisions category
	Product coby = new Product(
		televisions,
		"Coby LEDTV1926 19-Inch 720p LED HDTV",
		99.95,
		"19-inch widescreen LED HDTV attractive super-slim profile fits anywhere profile fits anywhere, "
			+ "Energy efficient technology meets Energy Star 3.0 and California Energy Commission standards (standby power <1W), "
			+ "Built-in digital TV tuner (ATSC/NTSC/ QAM), HDMI digital connection delivers perfect signal transmission, "
			+ "PC VGA connection for use as a computer monitor, Digital noise reduction, V-chip parental control, "
			+ "Closed- Caption, and Electronic Program Guide support, Multi-language on-screen display, "
			+ "Wall-mountable design (VESA 75mm x 75mm)");
	coby.setImg("coby-ledtv.jpg");
	coby.setImgSrc(loadImage("coby-ledtv.jpg"));
	
	products.create(coby);

	// in mobiles category
	Product samsungGalaxy = new Product(mobiles, "Samsung Galaxy S III/S3 GT-I9300 (Pebble Blue)", 559.65,
		"3G Network HSDPA 850 / 900 / 1900 / 2100, Android OS, v4.0 (Ice Cream Sandwich), "
			+ "2G Network GSM 850 / 900 / 1800 / 1900, Phone supports high speed HSDPA network, "
			+ "16 GB storage,1 GB RAM,Dimensions: 136.6 x 70.6 x 8.6mm");
	samsungGalaxy.setImg("samsung-galaxy-s3.jpg");
	samsungGalaxy.setImgSrc(loadImage("samsung-galaxy-s3.jpg"));
	
	Product iPhone = new Product(
		mobiles,
		"Apple iPhone 4 16GB (Black) - AT&T",
		437.98,
		"Size (LWH): 2.5 inches, 0.5 inches, 4.5 inches, Weight: 8 ounces, "
			+ "Minimum Rated Talk Time: 7 hours, Minimum Rated Standby Time: 300 hours, Battery Type: Lithium Ion");
	iPhone.setImg("iPhone4.jpg");
	iPhone.setImgSrc(loadImage("iPhone4.jpg"));

	products.create(samsungGalaxy);
	products.create(iPhone);

	// USERS:
	// administrators:
	Administrator hugo = new Administrator( "hugo", "1234", "Hugo", "Coconut", "hugo@coconut.com", "Mysterious Island", "Atlantic Ocean");
	administrators.create(hugo);
	
	// customers
	Customer rick = new Customer("rick", "1234", "Richie", "Rich", "richie@rich.com", "Villa Riccardo", "Florence");
	customers.create(rick);
	
	
	// ORDERS:
	Order rickOrder = new Order(customers.getCustomerByUsername("rick"), 0, new Date());    
        List<OrderItem> items = new ArrayList<OrderItem>();
        OrderItem orderedDellsByRick = new OrderItem (rickOrder, ultraDell, 2);
        items.add(orderedDellsByRick);
        OrderItem orderedSamsungByRick = new OrderItem (rickOrder, samsungGalaxy, 1);
        items.add(orderedSamsungByRick);
        rickOrder.setAmount(orderedDellsByRick.getQuantity()*orderedDellsByRick.getProduct().getPrice()
        	+ orderedSamsungByRick.getQuantity()*orderedSamsungByRick.getProduct().getPrice());
        rickOrder.setOrderItemList(items);
        
        orders.create(rickOrder);
    }

    private byte[] loadImage(String name) {
	try {
	    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	    if (is != null) {
		return IOUtils.toByteArray(is);
	    }
	} catch (IOException e) {
	    // TODO log;
	    e.printStackTrace();
	}
	return null;
    }

}
