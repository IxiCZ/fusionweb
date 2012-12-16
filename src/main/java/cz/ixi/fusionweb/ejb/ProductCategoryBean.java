package cz.ixi.fusionweb.ejb;

import cz.ixi.fusionweb.entities.ProductCategory;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


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
}
