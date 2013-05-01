package cz.ixi.fusionweb.drools.channels;

import java.util.Date;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;

import org.drools.runtime.Channel;

import cz.ixi.fusionweb.ejb.NotificationBean;
import cz.ixi.fusionweb.entities.Notification;
import cz.ixi.fusionweb.entities.NotificationSeverity;

/**
 * Channel for product search. Creates new notification.
 */
@ManagedBean
public class ProductSearchUnsuccsessfulChannel implements Channel {

    @EJB
    private NotificationBean notifications;

    @Override
    public void send(Object object) {
	String searchedText = (String) object;
	notifications.create(new Notification(NotificationSeverity.WARNING, new Date(),
		"There were too many product searches of '" + searchedText + "' with no results in the last hour. "));
    }
}
