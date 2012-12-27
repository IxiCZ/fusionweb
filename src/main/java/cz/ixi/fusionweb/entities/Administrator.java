package cz.ixi.fusionweb.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

/**
 * Administrator entity.
 */
@Entity
@Access(AccessType.FIELD)
public class Administrator extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    public Administrator(){
	super();
    }
    
    public Administrator(Set<Role> roles, String username, String password) {
	super(roles, username, password);
    }
}
