package cz.ixi.fusionweb.entities;

/**
 * User roles.
 */
public enum Role {

    CUSTOMER("CUSTOMER"), ADMINISTRATOR("ADMINISTRATOR");

	private String string;

	private Role(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return this.string;
	}
    
}
