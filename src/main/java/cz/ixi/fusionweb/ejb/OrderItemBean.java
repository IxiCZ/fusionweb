package cz.ixi.fusionweb.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import cz.ixi.fusionweb.entities.OrderItem;


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
}
