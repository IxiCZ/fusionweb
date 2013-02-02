package cz.ixi.fusionweb.drools.model;

/**
 * Class representing category navigation event.
 */
public class CategoryNavigationEvent extends NavigationEvent {

    public CategoryNavigationEvent(Integer id, String username) {
	super(id, username);
    }

    @Override
    public String toString() {
	return "Category[" + getId() + "] navigation event";
    }

}
