package com.punkmkt.beacons.databases;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.property.Property;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by DaniPunk on 04/05/16.
 */
@Table(database = AppDatabase.class,name="PhoneModel")

public class PhoneModel extends BaseModel {

    public static final Property<String> bid = new Property<String>(BeaconModel.class, "beaconid");
    public static final Property<Integer> bregistered = new Property<Integer>(BeaconData.class, "readed");
    //public static final Property<Long> idregister = new Property<Long>(BeaconData.class, "id");


    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    int phoneid;



    public long getId() {
        return id;
    }


    public int getPhoneId() {
        return phoneid;
    }

    public void setPhoneid(int beaconid) {
        this.phoneid = phoneid;
    }

}
