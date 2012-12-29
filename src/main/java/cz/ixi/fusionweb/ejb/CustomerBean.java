package cz.ixi.fusionweb.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import cz.ixi.fusionweb.entities.Customer;

/**
 * EJB stateless bean handling customers.
 */
@Stateless
public class CustomerBean extends AbstractFacade<Customer>{

    @PersistenceContext(unitName = "fusionWebPrimaryDB")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }
    
}
