package cz.ixi.fusionweb.entities;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.codec.binary.Base64;

/**
 * User entity.
 */
@Entity
@Table(name = "User")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @JoinTable(name = "UserRoles", joinColumns = @JoinColumn(name = "User_username"))
    private Set<Role> roles;

    @Id
    private String username;

    @NotNull
    @Size(min = 3, max = 100, message = "Length of the password must be 3-100.")
    private String password;

    public User() {
    }
    
    public User(Set<Role> roles, String username, String password) {
	super();
	this.roles = roles;
	this.username = username;
	setPassword(password);
    }

    public String getUsername() {
	return this.username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public void setPassword(String password) {
	try {
	    MessageDigest m;
	    m = MessageDigest.getInstance("SHA-256");
	    m.update(password.getBytes(), 0, password.length());
	    this.password = Base64.encodeBase64String(m.digest());
	    ;
	} catch (NoSuchAlgorithmException e) {
	    // TODO log
	}
    }

    public Set<Role> getRoles() {
	return this.roles;
    }

    public void setRoles(Set<Role> roles) {
	this.roles = roles;
    }

    public String getPassword() {
	return this.password;
    }

    
    @Override
    public boolean equals(Object object) {
	if (!(object instanceof User)) {
	    return false;
	}
	User other = (User) object;
	if (((this.username == null) && (other.username != null))
		|| ((this.username != null) && !this.username.equals(other.username))) {
	    return false;
	}
	return true;
    }
    
    @Override
    public int hashCode(){
	return username.hashCode();
    }

    @Override
    public String toString() {
	return "User[ username=" + username + " ]";
    }
}