package cz.ixi.fusionweb.ejb;

import cz.ixi.fusionweb.entities.ProductCategory;
import cz.ixi.fusionweb.entities.Product;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * EJB stateless bean handling products.
 */
@Stateless
public class ProductBean extends AbstractFacade<Product> {

    private static final Logger logger = Logger.getLogger(ProductBean.class.getCanonicalName());

    @PersistenceContext(unitName = "fusionWebPrimaryDB")
    private EntityManager em;

    public ProductBean() {
	super(Product.class);
    }

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    /**
     * Example usage of JPA CriteriaBuilder. You can also use NamedQueries
     * 
     * @param range
     * @param categoryId
     * @return
     */
    public List<Product> findByCategory(int[] range, int categoryId) {
	ProductCategory cat = new ProductCategory();
	cat.setId(categoryId);

	CriteriaBuilder qb = em.getCriteriaBuilder();
	CriteriaQuery<Product> query = qb.createQuery(Product.class);
	Root<Product> product = query.from(Product.class);
	query.where(qb.equal(product.get("category"), cat));

	List<Product> result = this.findRange(range, query);

	logger.finest("Product List size: " + result.size());

	return result;
    }
}
