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
@Table(name = "STATISTICS_RECORD")
public class StatisticsRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "SEVERITY")
    private StatisticsFrequency frequency;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "DATE_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    
    @Basic(optional = false)
    @Size(min = 3, max = 1000, message = "Description must be between {min} and {max} characters.")
    @Column(name = "DESCRIPTION", nullable = false, length = 1000)
    private String description;
    
    public StatisticsRecord() {
	super();
    }

    public StatisticsRecord(Integer id) {
	super();
	this.id = id;
    }
    
    public StatisticsRecord(StatisticsFrequency frequency, Date dateCreated, String description) {
	super();
	this.frequency = frequency;
	this.dateCreated = dateCreated;
	this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StatisticsFrequency getFrequency() {
        return frequency;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getDescription() {
        return description;
    }

    public void setFrequency(StatisticsFrequency frequency) {
        this.frequency = frequency;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
