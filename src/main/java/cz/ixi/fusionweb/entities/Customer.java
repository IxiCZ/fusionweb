package cz.ixi.fusionweb.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * Customer entity.
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Customer.findAll", query = "SELECT c FROM Customer c"),
	@NamedQuery(name = "Customer.findByUsername", query = "SELECT c FROM Customer c WHERE c.username = :username"),
	@NamedQuery(name = "Customer.findByFirstname", query = "SELECT c FROM Customer c WHERE c.firstname = :firstname"),
	@NamedQuery(name = "Customer.findByLastname", query = "SELECT c FROM Customer c WHERE c.lastname = :lastname"),
	@NamedQuery(name = "Customer.findByEmail", query = "SELECT c FROM Customer c WHERE c.email = :email"),
	@NamedQuery(name = "Customer.findByAddress", query = "SELECT c FROM Customer c WHERE c.address = :address"),
	@NamedQuery(name = "Customer.findByCity", query = "SELECT c FROM Customer c WHERE c.city = :city") })
public class Customer extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private List<Order> orderList;

    public Customer() {
	this.orderList = new ArrayList<Order>();
    }

    public Customer(String username, String password) {
	super(new HashSet<Role>(Arrays.asList(new Role[] { Role.CUSTOMER })), username, password);
	this.orderList = new ArrayList<Order>();
    }

    public Customer(String username, String password, String firstname, String lastname, String email, String address,
	    String city) {
	super(new HashSet<Role>(Arrays.asList(new Role[] { Role.CUSTOMER })), username, password, firstname, lastname,
		email, address, city);
	this.orderList = new ArrayList<Order>();
    }

    public List<Order> getOrderList() {
	return orderList;
    }

    public void setOrderList(List<Order> orderList) {
	this.orderList = orderList;
    }

    @Override
    public String toString() {
	return getUsername() + " (" + getLastname() + " " + getFirstname() + ")";
    }
}
