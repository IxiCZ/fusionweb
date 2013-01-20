package cz.ixi.fusionweb.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import cz.ixi.fusionweb.entities.DiscussionEntry;

/**
 * EJB stateless bean handling product discussion entries.
 */
@Stateless
public class DiscussionEntryBean extends AbstractFacade<DiscussionEntry> {

    @PersistenceContext(unitName = "fusionWebPrimaryDB")
    private EntityManager em;

    public DiscussionEntryBean() {
	super(DiscussionEntry.class);
    }

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }
}
