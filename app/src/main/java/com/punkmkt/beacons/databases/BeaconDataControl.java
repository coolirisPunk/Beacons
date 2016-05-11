package com.punkmkt.beacons.databases;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.sql.language.property.Property;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by DaniPunk on 04/05/16.
 */
public class BeaconDataControl extends BaseModel {

    public static final Property<String> pid = new Property<String>(BeaconData.class, "participanteid");
    public static final Property<Integer> buploaded = new Property<Integer>(BeaconData.class, "uploaded");
    public static final Property<Long> idregister = new Property<Long>(BeaconData.class, "id");


    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    String phoneid;

    @Column
    int participanteid;

    @Column
    int beaconid;

    @Column
    int uploaded;

    @Column
    double distance;

    @Column
    String date;

    public long getId() {
        return id;
    }

    public String setPhoneId() {
        return phoneid;
    }

    public void setPhoneId(String phoneid) {
        this.phoneid = phoneid;
    }

    public int getParticipanteId() {
        return participanteid;
    }

    public void setParticipanteId(int participanteid) {
        this.participanteid = participanteid;
    }

    public int getBeaconId() {
        return beaconid;
    }

    public void setBeaconId(int beaconid) {
        this.beaconid = beaconid;
    }


    public int getUploaded() {
        return uploaded;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
