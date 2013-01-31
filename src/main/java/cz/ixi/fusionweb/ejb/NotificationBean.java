package cz.ixi.fusionweb.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import cz.ixi.fusionweb.entities.Notification;

/**
 * EJB stateless bean handling product Notifications.
 */
@Stateless
public class NotificationBean extends AbstractFacade<Notification> {

    @PersistenceContext(unitName = "fusionWebPrimaryDB")
    private EntityManager em;

    public NotificationBean() {
	super(Notification.class);
    }

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Notification> findAll() {
	Query createQuery = this.em.createQuery("SELECT n FROM Notification n ORDER BY n.dateCreated, n.id DESC");

	return createQuery.getResultList();

    }

    @SuppressWarnings("unchecked")
    public List<Notification> findRange(int[] range) {
	Query createQuery = this.em.createQuery("SELECT n FROM Notification n ORDER BY n.dateCreated, n.id DESC");
	createQuery.setMaxResults(range[1] - range[0]);
	createQuery.setFirstResult(range[0]);

	return createQuery.getResultList();
    }
}
