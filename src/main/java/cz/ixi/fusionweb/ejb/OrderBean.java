package cz.ixi.fusionweb.ejb;

import cz.ixi.fusionweb.entities.Order;
import cz.ixi.fusionweb.entities.OrderStatus;
import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class OrderBean extends AbstractFacade<Order>
        implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @PersistenceContext(unitName = "fusionWebPrimaryDB")
    private EntityManager em;

    public OrderBean() {
        super(Order.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @SuppressWarnings("unchecked")
    public List<Order> getOrderByCustomerUsername(String username) {
        Query createNamedQuery = getEntityManager().createNamedQuery("Order.findByCustomerUsername");

        createNamedQuery.setParameter("username", username);

        return createNamedQuery.getResultList();
    }

    public List<Order> getOrderByStatus(OrderStatus status) {
        Query createNamedQuery = getEntityManager().createNamedQuery("Order.findByStatus");

        createNamedQuery.setParameter("status", status);

        @SuppressWarnings("unchecked")
	List<Order> orders = createNamedQuery.getResultList();

        return orders;
    }
}
