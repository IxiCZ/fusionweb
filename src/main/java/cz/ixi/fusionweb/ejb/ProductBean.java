package cz.ixi.fusionweb.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import cz.ixi.fusionweb.entities.Product;
import cz.ixi.fusionweb.entities.ProductCategory;

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
     * Returns list of products in given category by range.
     * 
     * @param range
     * @param categoryId
     * @return list of products in given category by range
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

    /**
     * Returns how many order items is associated with this product.
     * 
     * @param productCategoryId
     *            given product
     * @return how many order items is associated the product.
     */
    public long orderItemsOfProduct(int productId) {
	Query createQuery = this.em.createQuery("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :id");
	createQuery.setParameter("id", productId);

	return (Long) createQuery.getSingleResult();
    }

    /**
     * Returns count of products in given category.
     * 
     * @param categoryId
     *            category id
     * @return count of products in given category
     */
    public long countInCategory(int categoryId) {
	Query createQuery = this.em.createQuery("SELECT COUNT(p) FROM Product p WHERE p.category.id = :id");
	createQuery.setParameter("id", categoryId);

	return (Long) createQuery.getSingleResult();
    }

    /**
     * Returns list of products by range, where product's name contains given
     * keyword.
     * 
     * @param range
     *            range
     * @param keyword
     *            to be contained in product's name
     * @return list of products by range, where product's name contains given
     *         keyword
     */
    @SuppressWarnings("unchecked")
    public List<Product> searchProduct(int[] range, String keyword) {
	Query query = this.em.createQuery("SELECT p FROM Product p WHERE UPPER(p.name) LIKE :keyword");
	query.setParameter("keyword", "%" + keyword.toUpperCase() + "%");
	query.setMaxResults(range[1] - range[0]);
	query.setFirstResult(range[0]);

	return (List<Product>) query.getResultList();
    }

    /**
     * Returns number of products, where product's name contains given keyword.
     * 
     * @param keyword
     *            to be contained in product's name
     * @return number of products, where product's name contains given keyword.
     */
    public long searchProductCount(String keyword) {
	Query query = this.em.createQuery("SELECT COUNT(p) FROM Product p WHERE UPPER(p.name) LIKE :keyword");
	query.setParameter("keyword", "%" + keyword.toUpperCase() + "%");

	return (Long) query.getSingleResult();
    }

}
