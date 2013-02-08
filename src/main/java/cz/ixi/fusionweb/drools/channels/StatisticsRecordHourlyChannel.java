package cz.ixi.fusionweb.drools.channels;

import java.util.Date;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;

import org.drools.runtime.Channel;

import cz.ixi.fusionweb.ejb.StatisticsRecordBean;
import cz.ixi.fusionweb.entities.StatisticsFrequency;
import cz.ixi.fusionweb.entities.StatisticsRecord;

/**
 * Channel which creates a new notification statistics record.
 */
@ManagedBean
public class StatisticsRecordHourlyChannel implements Channel {

    @EJB
    private StatisticsRecordBean statistics;

    @Override
    public void send(Object object) {
	statistics.create(new StatisticsRecord(StatisticsFrequency.HOURLY, new Date(), object.toString()));
    }
}
