package cz.ixi.fusionweb.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import cz.ixi.fusionweb.entities.OrderItem;
import java.util.List;

/**
 * EJB stateless bean handling order items.
 */
@Stateless
public class OrderItemBean extends AbstractFacade<OrderItem> {

    @PersistenceContext(unitName = "fusionWebPrimaryDB")
    private EntityManager em;

    public OrderItemBean() {
        super(OrderItem.class);
    }

    protected EntityManager getEntityManager() {
        return em;
    }

    @SuppressWarnings("unchecked")
    public List<OrderItem> findOrderItemsByOrder(int orderId) {
        List<OrderItem> details = getEntityManager().createNamedQuery("OrderItem.findByOrderId")
                .setParameter("orderId", orderId).getResultList();

        return details;
    }
}
