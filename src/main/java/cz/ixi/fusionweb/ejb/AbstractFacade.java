package cz.ixi.fusionweb.ejb;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 * Class which represents abstract facade and makes possible to call
 * parameterized methods.
 * 
 * @param <T>entity
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade() {
    }

    public AbstractFacade(final Class<T> entityClass) {
	this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
	getEntityManager().persist(entity);
    }

    public void edit(T entity) {
	getEntityManager().merge(entity);
    }

    public T merge(T entity) {
	return getEntityManager().merge(entity);
    }

    public void remove(T entity) {
	getEntityManager().remove(getEntityManager().merge(entity));
    }

    public void remove(Number id) {
	getEntityManager().remove(find(id));
    }

    public T find(Object id) {
	return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
	javax.persistence.criteria.CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(
		entityClass);
	cq.select(cq.from(entityClass));

	return getEntityManager().createQuery(cq).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<T> findRange(int[] range) {
	javax.persistence.criteria.CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(
		entityClass);
	cq.select(cq.from(entityClass));

	javax.persistence.Query q = getEntityManager().createQuery(cq);
	q.setMaxResults(range[1] - range[0]);
	q.setFirstResult(range[0]);

	return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<T> findRange(int[] range, CriteriaQuery<T> query) {
	javax.persistence.Query q = getEntityManager().createQuery(query);
	q.setMaxResults(range[1] - range[0]);
	q.setFirstResult(range[0]);

	return q.getResultList();
    }

    public CriteriaBuilder getCriteriaBuilder() {
	return getEntityManager().getCriteriaBuilder();
    }

    public int count() {
	CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
	javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
	cq.select(getEntityManager().getCriteriaBuilder().count(rt));

	javax.persistence.Query q = getEntityManager().createQuery(cq);

	return ((Long) q.getSingleResult()).intValue();
    }
}
