package cz.ixi.fusionweb.web;

import cz.ixi.fusionweb.ejb.ProductBean;
import cz.ixi.fusionweb.entities.Product;
import cz.ixi.fusionweb.web.util.AbstractPaginationHelper;
import cz.ixi.fusionweb.web.util.JsfUtil;
import cz.ixi.fusionweb.web.util.PageNavigation;
import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Named;

@Named(value = "productController")
@SessionScoped
public class ProductController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String BUNDLE = "/Bundle";
    private static final String PRODUCT = "/product/";

    @EJB
    private ProductBean ejbFacade;
    private AbstractPaginationHelper pagination;
    private DataModel<Product> items = null;
    private Product current;
    private int categoryId;
    private int selectedItemIndex;

    // used for wizard
    private int step = 1;

    public Product getSelected() {
	if (current == null) {
	    current = new Product();
	    selectedItemIndex = -1;
	}

	return current;
    }

    public String showAll() {
	recreateModel();
	categoryId = 0; // show all products

	return "product/List";
    }

    private ProductBean getFacade() {
	return ejbFacade;
    }

    public AbstractPaginationHelper getPagination() {
	if (pagination == null) {
	    pagination = new AbstractPaginationHelper(AbstractPaginationHelper.DEFAULT_SIZE) {
		@Override
		public int getItemsCount() {
		    return getFacade().count();
		}

		@Override
		public DataModel<Product> createPageDataModel() {
		    if (categoryId != 0) {
			return new ListDataModel<Product>(getFacade().findByCategory(
				new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }, categoryId));
		    }

		    return new ListDataModel<Product>(getFacade().findRange(
			    new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }));
		}
	    };
	}

	return pagination;
    }

    public String prepareList() {
	// System.out.println("preparing list of category number: " +
	// categoryId);
	recreateModel();

	return  PRODUCT + PageNavigation.LIST;
    }

    public PageNavigation done() {
	recreateModel();
	setStep(1);
	current = null;

	return PageNavigation.INDEX;
    }

    public Product findById(int id) {
	return ejbFacade.find(id);
    }

    public PageNavigation prepareView() {
	current = getItems().getRowData();
	selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();

	return PageNavigation.VIEW;
    }

    public PageNavigation prepareCreate() {
	current = new Product();
	selectedItemIndex = -1;
	setStep(1);

	return PageNavigation.CREATE;
    }

    public PageNavigation nextStep() {
	setStep(getStep() + 1);

	return PageNavigation.CREATE;
    }

    public PageNavigation create() {
	try {
	    getFacade().create(current);
	    JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("ProductCreated"));

	    setStep(2);

	    return PageNavigation.CREATE;
	} catch (Exception e) {
	    JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));

	    return null;
	}
    }

    public PageNavigation prepareEdit() {
	current = getItems().getRowData();
	selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();

	return PageNavigation.EDIT;
    }

    public PageNavigation update() {
	try {
	    getFacade().edit(current);
	    JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("ProductUpdated"));

	    return PageNavigation.VIEW;
	} catch (Exception e) {
	    JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));

	    return null;
	}
    }

    public PageNavigation destroy() {
	current = getItems().getRowData();
	selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
	performDestroy();
	recreateModel();

	return PageNavigation.LIST;
    }

    public PageNavigation destroyAndView() {
	performDestroy();
	recreateModel();
	updateCurrentItem();

	if (selectedItemIndex >= 0) {
	    return PageNavigation.VIEW;
	} else {
	    // all items were removed - go back to list
	    recreateModel();

	    return PageNavigation.LIST;
	}
    }

    private void performDestroy() {
	try {
	    getFacade().remove(current);
	    JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("ProductDeleted"));
	} catch (Exception e) {
	    JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
	}
    }

    private void updateCurrentItem() {
	int count = getFacade().count();

	if (selectedItemIndex >= count) {
	    // selected index cannot be bigger than number of items:
	    selectedItemIndex = count - 1;

	    // go to previous page if last page disappeared:
	    if (pagination.getPageFirstItem() >= count) {
		pagination.previousPage();
	    }
	}

	if (selectedItemIndex >= 0) {
	    current = getFacade().findRange(new int[] { selectedItemIndex, selectedItemIndex + 1 }).get(0);
	}
    }

    @SuppressWarnings("unchecked")
    public DataModel<Product> getItems() {
	if (items == null) {
	    items = (DataModel<Product>) getPagination().createPageDataModel();
	}

	return (DataModel<Product>) getPagination().createPageDataModel();
    }

    private void recreateModel() {
	items = null;
    }

    public PageNavigation next() {
	getPagination().nextPage();
	recreateModel();

	return PageNavigation.LIST;
    }

    public PageNavigation previous() {
	getPagination().previousPage();
	recreateModel();

	return PageNavigation.LIST;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
	return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
	return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    /**
     * @return the categoryId
     */
    public int getCategoryId() {
	return categoryId;
    }

    /**
     * @param categoryId
     *            the categoryId to set
     */
    public void setCategoryId(int categoryId) {
	this.categoryId = categoryId;
    }

    public int getStep() {
	return step;
    }

    public void setStep(int step) {
	this.step = step;
    }

    @FacesConverter(forClass = Product.class)
    public static class ProductControllerConverter implements Converter {
	public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
	    if ((value == null) || (value.length() == 0)) {
		return null;
	    }

	    ProductController controller = (ProductController) facesContext.getApplication().getELResolver()
		    .getValue(facesContext.getELContext(), null, "productController");

	    return controller.ejbFacade.find(getKey(value));
	}

	java.lang.Integer getKey(String value) {
	    java.lang.Integer key;
	    key = Integer.valueOf(value);

	    return key;
	}

	String getStringKey(java.lang.Integer value) {
	    StringBuffer sb = new StringBuffer();
	    sb.append(value);

	    return sb.toString();
	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
	    if (object == null) {
		return null;
	    }

	    if (object instanceof Product) {
		Product o = (Product) object;

		return getStringKey(o.getId());
	    } else {
		throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName()
			+ "; expected type: " + ProductController.class.getName());
	    }
	}
    }
}
