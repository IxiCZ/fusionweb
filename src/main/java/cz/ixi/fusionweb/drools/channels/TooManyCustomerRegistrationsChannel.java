package cz.ixi.fusionweb.drools.channels;

import java.util.Date;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;

import org.drools.runtime.Channel;

import cz.ixi.fusionweb.ejb.NotificationBean;
import cz.ixi.fusionweb.entities.Notification;
import cz.ixi.fusionweb.entities.NotificationSeverity;

/**
 * Channel for situation where there is too many customer registrations. Creates
 * a new notification.
 */
@ManagedBean
public class TooManyCustomerRegistrationsChannel implements Channel {

    @EJB
    private NotificationBean notifications;

    @Override
    public void send(Object object) {
	notifications.create(new Notification(NotificationSeverity.WARNING, new Date(),
		"There were too many customer registrations in the last hour. "));
    }
}
