package cz.ixi.fusionweb.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Product entity.
 */
@Entity
@Table (name = "PRODUCT")
@NamedQueries({
    @NamedQuery(name = "Product.findAll",query = "SELECT p FROM Product p")
    , @NamedQuery(name = "Product.findById", query = "SELECT p FROM Product p WHERE p.id = :id")
    , @NamedQuery(name = "Product.findByName", query = "SELECT p FROM Product p WHERE p.name = :name")
    , @NamedQuery(name = "Product.findByPrice", query = "SELECT p FROM Product p WHERE p.price = :price")
    , @NamedQuery(name = "Product.findByDescription", query = "SELECT p FROM Product p WHERE p.description = :description")
    , @NamedQuery(name = "Product.findByImg", query = "SELECT p FROM Product p WHERE p.img = :img")
})
public class Product implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @JoinColumn(name = "PRODUCTCATEGORY_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false)
    private ProductCategory category;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @Basic(optional = false)
    @Size(min = 3, max = 1000, message = "Description must be between {min} and {max} characters.")
    @Column(name = "DESCRIPTION", nullable = false, length = 1000)
    private String description;
   
    @Size(min = 3, max = 45, message = "Image name must be between {min} and {max} value.")
    @Column(name = "IMG", length = 45)
    private String img;
    
    @Basic(optional = false)
    @Size(min = 3, max = 100, message = "=Name must be between {min} and {max} characters.")
    @Column(name = "NAME", nullable = false, length = 100)
    private String name;
   
    @Lob
    @Basic(fetch = FetchType.LAZY) //
    @Column(name = "IMG_SRC")
    @XmlTransient
    private byte[] imgSrc;
    
    @Basic(optional = false)
    @DecimalMax(value = "9999.99", message = "Prices must be lower than 1,000.00.")
    @Column(name = "PRICE", nullable = false)
    private double price;

    public Product() {
    }

    public Product(Integer id) {
        this.id = id;
    }
    
    public Product(ProductCategory category, String name, double price, String description) {
	this.category = category;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public Product(Integer id, String name, double price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String simg) {
        this.img = simg;
    }

    public byte[] getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(byte[] imgSrc) {
        this.imgSrc = imgSrc;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += ((id != null) ? id.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Product)) {
            return false;
        }

        Product other = (Product) object;

        if (((this.id == null) && (other.id != null))
                || ((this.id != null) && !this.id.equals(other.id))) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Product[id=" + id + "]";
    }
}
