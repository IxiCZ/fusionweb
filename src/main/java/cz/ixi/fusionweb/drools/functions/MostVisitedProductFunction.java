package cz.ixi.fusionweb.drools.functions;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cz.ixi.fusionweb.drools.model.ProductNavigationEvent;

/**
 * Implementation of accumulate function to find out most visited product event.
 */
public class MostVisitedProductFunction implements org.drools.base.accumulators.AccumulateFunction {

    protected static class MaxData implements Externalizable {

	public Map<Integer, Integer> eventsCount = new HashMap<Integer, Integer>();
	public Map<Integer, ProductNavigationEvent> events = new HashMap<Integer, ProductNavigationEvent>();

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
	ProductNavigationEvent event = (ProductNavigationEvent) value;

	if (!data.eventsCount.containsKey(event.getProductId())) {
	    data.eventsCount.put(event.getProductId(), 1);
	    data.events.put(event.getProductId(), event);
	} else {
	    int i = data.eventsCount.get(event.getProductId());
	    data.eventsCount.put(event.getProductId(), i + 1);
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
	    return new ProductNavigationEvent("", -1, "");
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
	return ProductNavigationEvent.class;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }
}