package cz.ixi.fusionweb.drools.functions;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cz.ixi.fusionweb.drools.model.CategoryNavigationEvent;

/**
 * Implementation of accumulate function to find out most visited category event.
 */
public class MostVisitedCategoryFunction implements org.drools.base.accumulators.AccumulateFunction {

    protected static class MaxData implements Externalizable {

	public Map<Integer, Integer> eventsCount = new HashMap<Integer, Integer>();
	public Map<Integer, CategoryNavigationEvent> events = new HashMap<Integer, CategoryNavigationEvent>();

	public MaxData() {
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	    eventsCount = (Map<Integer, Integer>) in.readObject();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
	    out.writeObject(eventsCount);
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
	MaxData data = (MaxData) context;
	data.events.clear();
	data.eventsCount.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.drools.base.accumulators.AccumulateFunction#accumulate(java.lang.
     * Object, java.lang.Object)
     */
    public void accumulate(Serializable context, Object value) {
	MaxData data = (MaxData) context;
	CategoryNavigationEvent event = (CategoryNavigationEvent) value;

	if (!data.eventsCount.containsKey(event.getProductCategoryId())) {
	    data.eventsCount.put(event.getProductCategoryId(), 1);
	    data.events.put(event.getProductCategoryId(), event);
	} else {
	    int i = data.eventsCount.get(event.getProductCategoryId());
	    data.eventsCount.put(event.getProductCategoryId(), i + 1);
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
	MaxData data = (MaxData) context;
	if (data.events.isEmpty()) {
	    return new CategoryNavigationEvent("", -1, "");
	}

	int maxValue = 0;
	int maxId = -1;
	for (Integer i : data.eventsCount.keySet()) {
	    if (data.eventsCount.get(i) > maxValue) {
		maxValue = data.eventsCount.get(i);
		maxId = i;
	    }
	}
	
	return data.events.get(maxId);
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
	return CategoryNavigationEvent.class;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }
}