package cz.ixi.fusionweb.ejb;

import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import cz.ixi.fusionweb.entities.Administrator;

/**
 * EJB stateless bean handling administrators.
 */
@Stateless
public class AdministratorBean extends AbstractFacade<Administrator>{

    @PersistenceContext(unitName = "fusionWebPrimaryDB")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }
    
}
