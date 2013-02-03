package cz.ixi.fusionweb.drools.model;

/**
 * Class representing category navigation event.
 */
public class CategoryNavigationEvent extends GeneralUserActionEvent {

    private Integer productCategoryId;

    public CategoryNavigationEvent(Integer productCategoryId, String username) {
	super(username);
	this.productCategoryId = productCategoryId;
    }

    public Integer getProductCategoryId() {
	return productCategoryId;
    }

    public void setProductCategoryId(Integer productCategoryId) {
	this.productCategoryId = productCategoryId;
    }

    @Override
    public String toString() {
	return "Category[" + getProductCategoryId() + "] navigation event";
    }
}
