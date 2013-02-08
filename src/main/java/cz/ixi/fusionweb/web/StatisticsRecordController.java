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

import cz.ixi.fusionweb.ejb.StatisticsRecordBean;
import cz.ixi.fusionweb.entities.StatisticsFrequency;
import cz.ixi.fusionweb.entities.StatisticsRecord;
import cz.ixi.fusionweb.web.util.AbstractPaginationHelper;
import cz.ixi.fusionweb.web.util.JsfUtil;
import cz.ixi.fusionweb.web.util.PageNavigation;

@ManagedBean(name = "statisticsRecordController")
@SessionScoped
public class StatisticsRecordController implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String BUNDLE = "Bundle";
    private static final int PAGE_SIZE = 15;

    @EJB
    private StatisticsRecordBean ejbFacade;

    private AbstractPaginationHelper pagination;
    private DataModel<StatisticsRecord> items = null;
    private StatisticsFrequency currentFrequency = null;

    public StatisticsRecordController() {
    }

    private StatisticsRecordBean getFacade() {
	return ejbFacade;
    }

    public StatisticsFrequency getCurrentFrequency() {
	return currentFrequency;
    }

    public AbstractPaginationHelper getPagination() {
	if (pagination == null) {
	    pagination = new AbstractPaginationHelper(PAGE_SIZE) {
		@Override
		public int getItemsCount() {
		    if (currentFrequency == null) {
			return getFacade().count();
		    }
		    return getFacade().count(currentFrequency);
		}

		@Override
		public DataModel<StatisticsRecord> createPageDataModel() {
		    if (currentFrequency == null) {
			return new ListDataModel<StatisticsRecord>(getFacade().findRange(
				new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }));
		    }
		    return new ListDataModel<StatisticsRecord>(getFacade().findRange(
			    new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }, currentFrequency));
		}
	    };
	}

	return pagination;
    }

    public PageNavigation prepareList() {
	recreateModel();

	return PageNavigation.LIST;
    }

    public PageNavigation prepareAllList() {
	currentFrequency = null;
	return prepareList();
    }

    public PageNavigation prepareHourlyList() {
	currentFrequency = StatisticsFrequency.HOURLY;
	return prepareList();
    }

    public PageNavigation prepareDailyList() {
	currentFrequency = StatisticsFrequency.DAILY;
	return prepareList();
    }

    public String prepareAllListOutside() {
	recreateModel();
	currentFrequency = null;
	return "/administrator/statisticsrecord/List";
    }

    public PageNavigation destroy() {
	try {
	    getFacade().remove((StatisticsRecord) getItems().getRowData());
	    JsfUtil.addSuccessMessage(ResourceBundle.getBundle(BUNDLE).getString("StatisticsRecordDeleted"));
	} catch (Exception e) {
	    JsfUtil.addErrorMessage(e, ResourceBundle.getBundle(BUNDLE).getString("PersistenceErrorOccured"));
	}
	recreateModel();

	return PageNavigation.LIST;
    }

    @SuppressWarnings("unchecked")
    public DataModel<StatisticsRecord> getItems() {
	if (items == null) {
	    items = (DataModel<StatisticsRecord>) getPagination().createPageDataModel();
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

    @FacesConverter(forClass = StatisticsRecord.class)
    public static class StatisticsRecirdConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
	    if ((value == null) || (value.length() == 0)) {
		return null;
	    }

	    StatisticsRecordController controller = (StatisticsRecordController) facesContext.getApplication()
		    .getELResolver().getValue(facesContext.getELContext(), null, "statisticsRecordController");

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

	    if (object instanceof StatisticsRecord) {
		StatisticsRecord o = (StatisticsRecord) object;

		return getStringKey(o.getId());
	    } else {
		throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName()
			+ "; expected type: " + StatisticsRecordController.class.getName());
	    }
	}
    }
}
