package cz.ixi.fusionweb.drools.model;

import java.util.Date;

/**
 * Class representing general user action event.
 */
public class GeneralUserActionEvent implements Comparable<GeneralUserActionEvent> {

    private String username;
    private Date time = new Date();

    public GeneralUserActionEvent(String username) {
	super();
	this.username = username;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public Date getTime() {
	return time;
    }

    @Override
    public int compareTo(GeneralUserActionEvent o) {
	return o.getTime().compareTo(time);
    }
}
