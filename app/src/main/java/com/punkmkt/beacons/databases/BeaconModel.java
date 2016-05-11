package com.punkmkt.beacons.databases;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.property.Property;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by DaniPunk on 25/04/16.
 */
@Table(database = AppDatabase.class,name="BeaconModel")

public class BeaconModel extends BaseModel {

    public static final Property<String> bid = new Property<String>(BeaconModel.class, "beaconid");
    public static final Property<Integer> bregistered = new Property<Integer>(BeaconData.class, "readed");
    //public static final Property<Long> idregister = new Property<Long>(BeaconData.class, "id");


    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    int beaconid;

    @Column
    int readed;

    @Column
    String range_days;

    @Column
    String range_hour;

    public long getId() {
        return id;
    }


    public int getBeaconid() {
        return beaconid;
    }

    public void setBeaconid(int beaconid) {
        this.beaconid = beaconid;
    }

    public int getReaded() {
        return readed;
    }

    public void setReaded(int readed) {
        this.readed = readed;
    }

    public String getRange_hour() {
        return range_hour;
    }

    public void setRange_hour(String range_hour) {
        this.range_hour = range_hour;
    }

    public String getRange_days() {
        return range_days;
    }

    public void setRange_days(String range_days) {
        this.range_days = range_days;
    }


}
