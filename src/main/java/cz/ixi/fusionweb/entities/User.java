package cz.ixi.fusionweb.entities;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * User entity.
 */
@Entity
@Table(name = "User")
@NamedQueries({
    @NamedQuery(name = "User.findAll",query = "SELECT p FROM User p")
    , @NamedQuery(name = "User.findByUsername", query = "SELECT p FROM User p WHERE p.username = :username")
    , @NamedQuery(name = "User.findByFirstname", query = "SELECT p FROM User p WHERE p.firstname = :firstname")
    , @NamedQuery(name = "User.findByLastname", query = "SELECT p FROM User p WHERE p.lastname = :lastname")
    , @NamedQuery(name = "User.findByEmail", query = "SELECT p FROM User p WHERE p.email = :email")
    , @NamedQuery(name = "User.findByAddress", query = "SELECT p FROM User p WHERE p.address = :address")
    , @NamedQuery(name = "User.findByCity", query = "SELECT p FROM User p WHERE p.city = :city")
})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "UserRoles", joinColumns =
    @JoinColumn(name = "User_username"))
    private Set<Role> roles;
    
    @Id
    @Basic(optional = false)
    @Column(name = "USERNAME")
    @Size(min = 3, max = 100, message = "{person.username}")
    private String username;
    
    @NotNull
    @Size(min = 3, max = 100, message = "{person.password}")
    private String password;
    
    @Basic(optional = false)
    @Size(min = 3, max = 45, message = "{person.address}")
    @Column(name = "ADDRESS")
    private String address;
    
    @Basic(optional = false)
    @Size(min = 3, max = 45, message = "{person.city}")
    @Column(name = "CITY")
    private String city;
    
    @Pattern(regexp = ".+@.+\\.[a-z]+", message = "{person.email}")
    @Size(min = 3, max = 45, message = "{person.email}")
    @Basic(optional = false)
    @Column(name = "EMAIL")
    private String email;
    
    @Basic(optional = false)
    @Size(min = 3, max = 50, message = "{person.firstname}")
    @Column(name = "FIRSTNAME")
    private String firstname;
    
    @Basic(optional = false)
    @Size(min = 3, max = 100, message = "{person.lastname}")
    @Column(name = "LASTNAME")
    private String lastname;

    public User() {
    }

    public User(Set<Role> roles, String username, String password) {
        this.roles = roles;
        this.username = username;
        setPassword(password);
    }
    
     public User(Set<Role> roles, String username, String password, String firstname, String lastname, String email, String address, String city) {
        this.roles = roles;
        this.username = username;
        setPassword(password);
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.address = address;
        this.city = city;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return "User[ username=" + username + " ]";
    }
}