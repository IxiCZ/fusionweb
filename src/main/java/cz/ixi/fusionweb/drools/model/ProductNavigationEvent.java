package cz.ixi.fusionweb.drools.model;

/**
 * Class representing product navigation event.
 */
public class ProductNavigationEvent extends NavigationEvent {

    public ProductNavigationEvent(Integer id) {
	super(id);
    }

    @Override
    public String toString() {
	return "Product[" + getId() + "] navigation event";
    }

}
