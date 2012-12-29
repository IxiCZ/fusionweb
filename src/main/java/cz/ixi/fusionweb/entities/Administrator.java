package cz.ixi.fusionweb.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

/**
 * Administrator entity.
 */
@Entity
//@Access(AccessType.FIELD)
public class Administrator extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    public Administrator(){
	super();
    }
    
    public Administrator(Set<Role> roles, String username, String password) {
	super(roles, username, password);
    }
    
    public Administrator(String username, String password) {
        super(new HashSet<Role>(Arrays.asList(new Role[]{Role.ADMINISTRATOR})), username, password);
    }
    
    public Administrator(String username,  String password, String firstname, String lastname, String email, String address, String city) {
         super(new HashSet<Role>(Arrays.asList(new Role[]{Role.ADMINISTRATOR})), username, password, firstname, lastname, email, address, city);   
    }
    
    @Override
    public String toString() {
        return "Administrator[username=" + getUsername() + "]";
    }
}
