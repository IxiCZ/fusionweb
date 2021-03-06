
package cz.ixi.fusionweb.entities;

public enum OrderStatus {
    
        NEW("NEW"), SENT("SENT"), PAID("PAID"), CANCELLED("CANCELLED");

	private String string;

	private OrderStatus(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return this.string;
	}
    
}
