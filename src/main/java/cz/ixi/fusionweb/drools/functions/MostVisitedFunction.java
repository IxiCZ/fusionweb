package cz.ixi.fusionweb.drools.functions;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cz.ixi.fusionweb.drools.model.CategoryNavigationEvent;
import cz.ixi.fusionweb.drools.model.GeneralUserActionEvent;
import cz.ixi.fusionweb.drools.model.ProductNavigationEvent;

/**
 * Implementation of accumulate function to find out most visited event.
 */
public class MostVisitedFunction implements org.drools.runtime.rule.AccumulateFunction {

    protected static class MaxData implements Externalizable {

	private static Map<Integer, Integer> navigationEvents = new HashMap<Integer, Integer>();
	private static Map<Integer, GeneralUserActionEvent> events = new HashMap<Integer, GeneralUserActionEvent>();

	public MaxData() {
	}

	public static Map<Integer, Integer> getNavigationEvents() {
	    return navigationEvents;
	}

	public static void setNavigationEvents(Map<Integer, Integer> navigationEvents) {
	    MaxData.navigationEvents = navigationEvents;
	}

	public static Map<Integer, GeneralUserActionEvent> getEvents() {
	    return events;
	}

	public static void setEvents(Map<Integer, GeneralUserActionEvent> events) {
	    MaxData.events = events;
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	    navigationEvents = (Map<Integer, Integer>) in.readObject();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
	    out.writeObject(navigationEvents);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.base.accumulators.AccumulateFunction#createContext()
     */
    public Serializable createContext() {
	return new MaxData();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.base.accumulators.AccumulateFunction#init(java.lang.Object)
     */
    public void init(Serializable context) throws Exception {
	MaxData.setNavigationEvents(new HashMap<Integer, Integer>());
	MaxData.setEvents(new HashMap<Integer, GeneralUserActionEvent>());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.base.accumulators.AccumulateFunction#accumulate(java.lang.
     * Object, java.lang.Object)
     */
    public void accumulate(Serializable context, Object value) {
	if (value instanceof ProductNavigationEvent) {
	    ProductNavigationEvent event = (ProductNavigationEvent) value;

	    if (!MaxData.getNavigationEvents().containsKey(event.getProductId())) {
		MaxData.getNavigationEvents().put(event.getProductId(), 1);
		MaxData.getEvents().put(event.getProductId(), event);
	    } else {
		int i = MaxData.getNavigationEvents().get(event.getProductId());
		MaxData.getNavigationEvents().put(event.getProductId(), i + 1);
	    }
	} else if (value instanceof CategoryNavigationEvent) {
	    CategoryNavigationEvent event = (CategoryNavigationEvent) value;

	    if (!MaxData.getNavigationEvents().containsKey(event.getProductCategoryId())) {
		MaxData.getNavigationEvents().put(event.getProductCategoryId(), 1);
		MaxData.getEvents().put(event.getProductCategoryId(), event);
	    } else {
		int i = MaxData.getNavigationEvents().get(event.getProductCategoryId());
		MaxData.getNavigationEvents().put(event.getProductCategoryId(), i + 1);
	    }
	}
    }

    public void reverse(Serializable context, Object value) throws Exception {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.base.accumulators.AccumulateFunction#getResult(java.lang.Object
     * )
     */
    public Object getResult(Serializable context) throws Exception {
	int maxValue = 0;
	int maxId = -1;
	for (Integer i : MaxData.getNavigationEvents().keySet()) {
	    if (MaxData.getNavigationEvents().get(i) > maxValue) {
		maxValue = MaxData.getNavigationEvents().get(i);
		maxId = i;
	    }
	}
	return MaxData.getEvents().get(maxId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.base.accumulators.AccumulateFunction#supportsReverse()
     */
    public boolean supportsReverse() {
	return false;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getResultType() {
	return Number.class;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }
}