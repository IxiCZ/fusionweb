package cz.ixi.fusionweb.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Product category entity.
 */
@Entity
@Table(name = "PRODUCTCATEGORY")
@NamedQueries({
	@NamedQuery(name = "ProductCategory.findAll", query = "SELECT c FROM ProductCategory c"),
	@NamedQuery(name = "ProductCategory.findById", query = "SELECT c FROM ProductCategory c WHERE c.id = :id"),
	@NamedQuery(name = "ProductCategory.findByName", query = "SELECT c FROM ProductCategory c WHERE c.name = :name"),
	@NamedQuery(name = "ProductCategory.findByTags", query = "SELECT c FROM ProductCategory c WHERE c.tags = :tags") })
public class ProductCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    private List<Product> productList;

    @Basic(optional = false)
    @Size(min = 3, max = 45, message = "Name must be between {min} and {max} characters.")
    @Column(name = "NAME", nullable = false, length = 45)
    private String name;

    @Size(min = 3, max = 45, message = "Tags must be between {min} and {max} characters.")
    @Column(name = "TAGS", length = 45)
    private String tags;

    public ProductCategory() {
    }

    public ProductCategory(Integer id) {
	this.id = id;
    }

    public ProductCategory(String name) {
	super();
	this.name = name;
    }

    public ProductCategory(Integer id, String name) {
	this.id = id;
	this.name = name;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getTags() {
	return tags;
    }

    public void setTags(String tags) {
	this.tags = tags;
    }

    @XmlTransient
    public List<Product> getProductList() {
	return productList;
    }

    public void setProductList(List<Product> productList) {
	this.productList = productList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += ((id != null) ? id.hashCode() : 0);

	return hash;
    }

    @Override
    public boolean equals(Object object) {
	if (!(object instanceof ProductCategory)) {
	    return false;
	}

	ProductCategory other = (ProductCategory) object;

	if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
	    return false;
	}

	return true;
    }

    @Override
    public String toString() {
	return getName() + " [ID: " + id + "]";
    }
}
