package cz.ixi.fusionweb.drools.channels;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;

import org.drools.runtime.Channel;

import cz.ixi.fusionweb.ejb.NotificationBean;
import cz.ixi.fusionweb.entities.Notification;

/**
 * Channel which saves the sent notification.
 */
@ManagedBean
public class NotificationsGeneralChannel implements Channel {

    @EJB
    private NotificationBean notifications;

    @Override
    public void send(Object object) {
	notifications.create((Notification) object);
    }
}
