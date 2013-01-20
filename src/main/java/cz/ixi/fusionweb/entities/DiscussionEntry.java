package cz.ixi.fusionweb.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Discussion entry entity.
 */
@Entity
@Table(name = "DISCUSSION_ENTRY")
public class DiscussionEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Product product;

    @Basic(optional = false)
    @NotNull
    @Column(name = "DATE_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @JoinColumn(name = "USER_USERNAME", referencedColumnName = "USERNAME")
    @ManyToOne(optional = false)
    private User user;

    @Basic(optional = false)
    @Size(min = 3, max = 100, message = "Title must be between {min} and {max} characters.")
    @Column(name = "TITLE", nullable = false, length = 100)
    private String title;

    @Basic(optional = false)
    @Size(min = 3, max = 1000, message = "Text must be between {min} and {max} characters.")
    @Column(name = "TEXT", nullable = false, length = 1000)
    private String text;

    public DiscussionEntry(Product product, Date dateCreated, User user, String title, String text) {
	super();
	this.product = product;
	this.dateCreated = dateCreated;
	this.user = user;
	this.title = title;
	this.text = text;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public Product getProduct() {
	return product;
    }

    public void setProduct(Product product) {
	this.product = product;
    }

    public Date getDateCreated() {
	return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
	this.dateCreated = dateCreated;
    }

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }
}
