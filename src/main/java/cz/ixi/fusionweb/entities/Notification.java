package cz.ixi.fusionweb.entities;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Notification entity.
 */
@Entity
@Table(name = "NOTIFICATION")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "SEVERITY")
    private NotificationSeverity severity;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "DATE_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    
    @Basic(optional = false)
    @Size(min = 3, max = 1000, message = "Description must be between {min} and {max} characters.")
    @Column(name = "DESCRIPTION", nullable = false, length = 1000)
    private String description;
    
    public Notification() {
	super();
    }

    public Notification(Integer id) {
	super();
	this.id = id;
    }
    
    public Notification(NotificationSeverity severity, Date dateCreated, String description) {
	super();
	this.severity = severity;
	this.dateCreated = dateCreated;
	this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public NotificationSeverity getSeverity() {
        return severity;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getDescription() {
        return description;
    }

    public void setSeverity(NotificationSeverity severity) {
        this.severity = severity;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
