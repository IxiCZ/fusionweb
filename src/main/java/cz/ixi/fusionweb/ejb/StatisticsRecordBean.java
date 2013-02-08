package cz.ixi.fusionweb.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import cz.ixi.fusionweb.entities.StatisticsFrequency;
import cz.ixi.fusionweb.entities.StatisticsRecord;

/**
 * EJB stateless bean handling product Statistic Records.
 */
@Stateless
public class StatisticsRecordBean extends AbstractFacade<StatisticsRecord> {

    @PersistenceContext(unitName = "fusionWebPrimaryDB")
    private EntityManager em;

    public StatisticsRecordBean() {
	super(StatisticsRecord.class);
    }

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<StatisticsRecord> findAll() {
	Query createQuery = this.em.createQuery("SELECT n FROM StatisticsRecord n ORDER BY n.dateCreated DESC");

	return createQuery.getResultList();

    }

    @SuppressWarnings("unchecked")
    public List<StatisticsRecord> findRange(int[] range) {
	Query createQuery = this.em.createQuery("SELECT n FROM StatisticsRecord n ORDER BY n.dateCreated DESC");
	createQuery.setMaxResults(range[1] - range[0]);
	createQuery.setFirstResult(range[0]);

	return createQuery.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<StatisticsRecord> findRange(int[] range, StatisticsFrequency frequency) {
	Query createQuery = this.em.createQuery("SELECT n FROM StatisticsRecord n WHERE n.frequency = :frequency ORDER BY n.dateCreated DESC");
	createQuery.setParameter("frequency", frequency);
	createQuery.setMaxResults(range[1] - range[0]);
	createQuery.setFirstResult(range[0]);

	return createQuery.getResultList();
    }
    

    public int count(StatisticsFrequency frequency) {
	Query createQuery = this.em.createQuery("SELECT COUNT(n) FROM StatisticsRecord n WHERE n.frequency = :frequency");
	createQuery.setParameter("frequency", frequency);

	return ((Long) createQuery.getSingleResult()).intValue();
    }
}
