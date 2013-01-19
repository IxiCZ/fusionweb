package cz.ixi.fusionweb.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import cz.ixi.fusionweb.entities.Customer;

/**
 * EJB stateless bean handling customers.
 */
@Stateless
public class CustomerBean extends AbstractFacade<Customer> {

    @PersistenceContext(unitName = "fusionWebPrimaryDB")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Customer> findAll() {
	Query createQuery = this.em.createQuery("SELECT c FROM Customer c");
	
	return (List<Customer>) createQuery.getResultList();
    }

    public Customer getCustomerByUsername(String username) {
	Query createNamedQuery = getEntityManager().createNamedQuery("Customer.findByUsername");

	createNamedQuery.setParameter("username", username);

	if (createNamedQuery.getResultList().size() > 0) {
	    return (Customer) createNamedQuery.getSingleResult();
	} else {
	    return null;
	}
    }

}
