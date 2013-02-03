package cz.ixi.fusionweb.drools.model;

/**
 * Class representing category navigation event.
 */
public class CategoryNavigationEvent extends GeneralUserActionEvent {

    private Integer productCategoryId;
    private String categoryName;

    public CategoryNavigationEvent(String username, Integer productCategoryId, String categoryName) {
	super(username);
	this.productCategoryId = productCategoryId;
	this.categoryName = categoryName;
    }

    public Integer getProductCategoryId() {
	return productCategoryId;
    }

    public void setProductCategoryId(Integer productCategoryId) {
	this.productCategoryId = productCategoryId;
    }

    public String getCategoryName() {
	return categoryName;
    }

    public void setCategoryName(String categoryName) {
	this.categoryName = categoryName;
    }

    @Override
    public String toString() {
	if (getUsername() != null) {
	    return "User " + getUsername() + " visited category " + categoryName + "(" + productCategoryId + ").";
	}
	return "Anonymous User visited category " + categoryName + "(" + productCategoryId + ").";
    }
}
