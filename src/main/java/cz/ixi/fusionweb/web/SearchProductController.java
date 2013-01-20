package cz.ixi.fusionweb.web;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import cz.ixi.fusionweb.ejb.ProductBean;
import cz.ixi.fusionweb.entities.Product;
import cz.ixi.fusionweb.web.util.AbstractPaginationHelper;
import cz.ixi.fusionweb.web.util.PageNavigation;

@ManagedBean(name = "searchProductController")
@SessionScoped
public class SearchProductController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String PRODUCT = "/product/";

    @EJB
    private ProductBean ejbFacade;
    private AbstractPaginationHelper pagination;
    private DataModel<Product> items = null;
    private String searchedKeyword = "";

    private ProductBean getFacade() {
	return ejbFacade;
    }

    public String getSearchedKeyword() {
	return searchedKeyword;
    }

    public void setSearchedKeyword(String searchedKeyword) {
	this.searchedKeyword = searchedKeyword;
    }

    public AbstractPaginationHelper getPagination() {
	if (pagination == null) {
	    pagination = new AbstractPaginationHelper(AbstractPaginationHelper.DEFAULT_SIZE) {
		@Override
		public int getItemsCount() {
		    return (int) getFacade().searchProductCount(searchedKeyword);
		}

		@Override
		public DataModel<Product> createPageDataModel() {
		    return new ListDataModel<Product>(getFacade().searchProduct(
			    new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }, searchedKeyword));
		}
	    };
	}

	return pagination;
    }

    public String prepareList() {
	pagination = null;
	recreateModel();

	return PRODUCT + PageNavigation.SEARCH;
    }

    public Product findById(int id) {
	return ejbFacade.find(id);
    }

    @SuppressWarnings("unchecked")
    public DataModel<Product> getItems() {
	if (items == null) {
	    items = (DataModel<Product>) getPagination().createPageDataModel();
	}

	return items;
    }

    private void recreateModel() {
	items = null;
    }

    public PageNavigation next() {
	getPagination().nextPage();
	recreateModel();

	return PageNavigation.SEARCH;
    }

    public PageNavigation previous() {
	getPagination().previousPage();
	recreateModel();

	return PageNavigation.SEARCH;
    }
}
