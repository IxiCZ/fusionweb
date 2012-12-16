package cz.ixi.fusionweb.web;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import cz.ixi.fusionweb.ejb.ProductCategoryBean;
import cz.ixi.fusionweb.entities.ProductCategory;
import cz.ixi.fusionweb.web.util.AbstractPaginationHelper;
import cz.ixi.fusionweb.web.util.JsfUtil;
import cz.ixi.fusionweb.web.util.PageNavigation;

@ManagedBean(name = "categoryController")
@SessionScoped
public class CategoryController implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String BUNDLE = "/Bundle";

    @EJB
    private ProductCategoryBean ejbFacade;
    private AbstractPaginationHelper pagination;
    private ProductCategory current;
    private DataModel<ProductCategory> items = null;
    private int selectedItemIndex;

    public CategoryController() {
    }

    public ProductCategory getSelected() {
	if (current == null) {
	    current = new ProductCategory();
	    selectedItemIndex = -1;
	}

	return current;
    }

    private ProductCategoryBean getFacade() {
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
		public DataModel<ProductCategory> createPageDataModel() {
		    return new ListDataModel<ProductCategory>(getFacade().findRange(
			    new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }));
		}
	    };
	}

	return pagination;
    }

    public PageNavigation prepareList() {
	recreateModel();

	return PageNavigation.LIST;
    }

    public PageNavigation prepareView() {
	current = (ProductCategory) getItems().getRowData();
	selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();

	return PageNavigation.VIEW; // .getText();
    }

    public PageNavigation prepareCreate() {
	current = new ProductCategory();
	selectedItemIndex = -1;

	return PageNavigation.CREATE; // .getText();//"Create";
    }

    public PageNavigation create() {
	try {
	    getFacade().create(current);
	    JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("CategoryCreated"));
	    recreateModel();

	    return prepareCreate();
	} catch (Exception e) {
	    JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));

	    return null;
	}
    }

    public PageNavigation prepareEdit() {
	current = (ProductCategory) getItems().getRowData();
	selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();

	return PageNavigation.EDIT;
    }

    public PageNavigation update() {
	try {
	    getFacade().edit(current);
	    JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("CategoryUpdated"));

	    return PageNavigation.VIEW;
	} catch (Exception e) {
	    JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));

	    return null;
	}
    }

    public PageNavigation destroy() {
	current = (ProductCategory) getItems().getRowData();
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
	    JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("CategoryDeleted"));
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
    public DataModel<ProductCategory> getItems() {
	if (items == null) {
	    items = (DataModel<ProductCategory>) getPagination().createPageDataModel();
	}

	return items;
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

    @FacesConverter(forClass = ProductCategory.class)
    public static class CategoryControllerConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
	    if ((value == null) || (value.length() == 0)) {
		return null;
	    }

	    CategoryController controller = (CategoryController) facesContext.getApplication().getELResolver()
		    .getValue(facesContext.getELContext(), null, "categoryController");

	    return controller.ejbFacade.find(getKey(value));
	}

	java.lang.Integer getKey(String value) {
	    java.lang.Integer key;
	    key = Integer.valueOf(value);

	    return key;
	}

	String getStringKey(java.lang.Integer value) {
	    StringBuilder sb = new StringBuilder();
	    sb.append(value);

	    return sb.toString();
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
	    if (object == null) {
		return null;
	    }

	    if (object instanceof ProductCategory) {
		ProductCategory o = (ProductCategory) object;

		return getStringKey(o.getId());
	    } else {
		throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName()
			+ "; expected type: " + CategoryController.class.getName());
	    }
	}
    }
}
