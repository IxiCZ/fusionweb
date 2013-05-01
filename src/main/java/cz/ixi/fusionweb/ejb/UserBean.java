package cz.ixi.fusionweb.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import cz.ixi.fusionweb.entities.User;

/**
 * EJB stateless bean handling users as general.
 */
@Stateless
public class UserBean extends AbstractFacade<User> {

    @PersistenceContext(unitName = "fusionWebPrimaryDB")
    private EntityManager em;

    public UserBean() {
	super(User.class);
    }

    protected EntityManager getEntityManager() {
	return em;
    }

    /**
     * Returns user of given username, null if doesn't exist.
     * 
     * @param username of user to be returned
     * @return user of given username, null if doesn't exist
     */
    public User getUserByUsername(String username) {
	Query createQuery = this.em.createQuery("SELECT u FROM User u WHERE u.username = :username");
	createQuery.setParameter("username", username);
	
	if (createQuery.getResultList().size() > 0) {
		return (User) createQuery.getSingleResult();
	} else {
		return null;
	}
    }
}
