package cz.ixi.fusionweb.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import cz.ixi.fusionweb.entities.ProductCategory;

/**
 * EJB stateless bean handling product categories.
 */
@Stateless
public class ProductCategoryBean extends AbstractFacade<ProductCategory> {

    @PersistenceContext(unitName = "fusionWebPrimaryDB")
    private EntityManager em;

    public ProductCategoryBean() {
	super(ProductCategory.class);
    }

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public long productCountInCategory(int productCategoryId) {
	Query createQuery = this.em.createQuery("SELECT COUNT(p) FROM Product p WHERE p.category.id = :id");
	createQuery.setParameter("id", productCategoryId);

	return (Long) createQuery.getSingleResult();
    }
}
