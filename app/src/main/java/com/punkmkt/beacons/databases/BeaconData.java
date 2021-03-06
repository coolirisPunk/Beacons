package com.punkmkt.beacons.databases;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.property.Property;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by DaniPunk on 02/03/16.
 */
@Table(database = AppDatabase.class,name="BeaconData")

public class BeaconData extends BaseModel {

    public static final Property<String> bid = new Property<String>(BeaconData.class, "beaconid");
    public static final Property<Integer> buploaded = new Property<Integer>(BeaconData.class, "uploaded");
    public static final Property<Long> idregister = new Property<Long>(BeaconData.class, "id");


    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    String userid;

    @Column
    int beaconid;


    @Column
    int readed;

    @Column
    int uploaded;

    @Column
    double distance;

    @Column
    String date;

    public long getId() {
        return id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getBeaconid() {
        return beaconid;
    }

    public void setBeaconid(int beaconid) {
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

    public int getReaded() {
        return readed;
    }

    public void setReaded(int readed) {
        this.readed = readed;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
