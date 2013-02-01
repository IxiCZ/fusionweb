package cz.ixi.fusionweb.drools.model;

public class CustomerRegistrationEvent {
    
    private String username;

    public CustomerRegistrationEvent() {
    }
    
    public CustomerRegistrationEvent(String username) {
	this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
	return "CustomerRegistrationEvent [username=" + username + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((username == null) ? 0 : username.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	CustomerRegistrationEvent other = (CustomerRegistrationEvent) obj;
	if (username == null) {
	    if (other.username != null)
		return false;
	} else if (!username.equals(other.username))
	    return false;
	return true;
    }    
}
