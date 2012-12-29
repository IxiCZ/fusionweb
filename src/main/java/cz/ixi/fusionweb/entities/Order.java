package cz.ixi.fusionweb.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 * Order entity.
 */
@Entity
@Table(name = "PRODUCT_ORDER")
@NamedQueries({
    @NamedQuery(name = "Order.findAll",query = "SELECT c FROM Order c")
    , @NamedQuery(name = "Order.findById", query = "SELECT c FROM Order c WHERE c.id = :id")
    , @NamedQuery(name = "Order.findByStatus", query = "SELECT c FROM Order c WHERE c.orderStatus = :orderStatus")
    , @NamedQuery(name = "Order.findByCustomerUsername", query = "SELECT c FROM Order c WHERE c.customer.username = :username")
    , @NamedQuery(name = "Order.findByAmount", query = "SELECT c FROM Order c WHERE c.amount = :amount")
    , @NamedQuery(name = "Order.findByDateCreated", query = "SELECT c FROM Order c WHERE c.dateCreated = :dateCreated")
})
public class Order implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @JoinColumn(name = "CUSTOMER_USERNAME", referencedColumnName = "USERNAME")
    @ManyToOne(optional = false)
    private Customer customer;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "DATE_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<OrderItem> orderItemList;
    
    @NotNull
    @Column(name = "ORDER_STATUS")
    private OrderStatus orderStatus;
    
    @Basic(optional = false)
    @DecimalMin(value = "0.1", message = "{c.order.amount}")
    @Column(name = "AMOUNT")
    private double amount;

    public Order() {
        orderStatus = OrderStatus.NEW;
    }

    public Order(Integer id) {
        this.id = id;
        orderStatus = OrderStatus.NEW;
    }

    public Order(Integer id, double amount, Date dateCreated) {
        this.id = id;
        this.amount = amount;
        this.dateCreated = dateCreated;
        orderStatus = OrderStatus.NEW;
    }
    
    public Order(Customer customer, double amount, Date dateCreated) {
        this.customer = customer;
        this.amount = amount;
        this.dateCreated = dateCreated;
        orderStatus = OrderStatus.NEW;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setCustomer(User person) {
        this.customer = (Customer) person;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Order)) {
            return false;
        }

        Order other = (Order) object;

        if (((this.id == null) && (other.id != null))
                || ((this.id != null) && !this.id.equals(other.id))) {
            return false;
        }

        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += ((id != null) ? id.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        return "Order[id=" + id + "]";
    }
}
