package cz.ixi.fusionweb.drools.functions;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cz.ixi.fusionweb.drools.model.NavigationEvent;

/**
 * Implementation of accumulate function to find out most visited event. 
 */
public class MostVisitedFunction implements org.drools.runtime.rule.AccumulateFunction {

    protected static class MaxData implements Externalizable {
        
        private static Map<Integer, Integer> navigationEvents = new HashMap<Integer, Integer>();
        
        public MaxData() {}

	public static Map<Integer, Integer> getNavigationEvents() {
	    return navigationEvents;
	}

	public static void setNavigationEvents(Map<Integer, Integer> navigationEvents) {
	    MaxData.navigationEvents = navigationEvents;
	}
        
        @SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            navigationEvents = (Map<Integer, Integer>) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(navigationEvents);
        }
    }

    /* (non-Javadoc)
     * @see org.drools.base.accumulators.AccumulateFunction#createContext()
     */
    public Serializable createContext() {
        return new MaxData();
    }

    /* (non-Javadoc)
     * @see org.drools.base.accumulators.AccumulateFunction#init(java.lang.Object)
     */
    public void init(Serializable context) throws Exception {
    }

    /* (non-Javadoc)
     * @see org.drools.base.accumulators.AccumulateFunction#accumulate(java.lang.Object, java.lang.Object)
     */
    public void accumulate(Serializable context, Object value) {
        NavigationEvent event = (NavigationEvent) value;
        
        if (!MaxData.getNavigationEvents().containsKey(event.getId())){
            MaxData.getNavigationEvents().put(event.getId(), 1);
        } else {
            int i = MaxData.getNavigationEvents().get(event.getId());
            MaxData.getNavigationEvents().put(event.getId(), i+1);
        }
    }

    public void reverse(Serializable context,
                        Object value) throws Exception {
    }

    /* (non-Javadoc)
     * @see org.drools.base.accumulators.AccumulateFunction#getResult(java.lang.Object)
     */
    public Object getResult(Serializable context) throws Exception {
	int maxValue = 0;
	int maxId = 0; 
	for (Integer i : MaxData.getNavigationEvents().keySet()){
	    if (MaxData.getNavigationEvents().get(i) > maxValue){
		maxValue = MaxData.getNavigationEvents().get(i);
		maxId = i;
	    }
	}
	
	return maxId;
    }

    /* (non-Javadoc)
     * @see org.drools.base.accumulators.AccumulateFunction#supportsReverse()
     */
    public boolean supportsReverse() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Class< ? > getResultType() {
        return Number.class;
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }
}