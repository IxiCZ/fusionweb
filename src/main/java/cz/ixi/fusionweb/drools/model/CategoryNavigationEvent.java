package cz.ixi.fusionweb.drools.model;

/**
 * Class representing category navigation event.
 */
public class CategoryNavigationEvent extends NavigationEvent {

    public CategoryNavigationEvent(Integer id) {
	super(id);
    }

    @Override
    public String toString() {
	return "Category[" + getId() + "] navigation event";
    }

}
