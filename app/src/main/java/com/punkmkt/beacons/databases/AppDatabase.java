package com.punkmkt.beacons.databases;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by DaniPunk on 02/03/16.
 */

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION, generatedClassSeparator = "_")
public class AppDatabase {

    public static final String NAME = "AppDatabase";

    public static final int VERSION = 1;


}