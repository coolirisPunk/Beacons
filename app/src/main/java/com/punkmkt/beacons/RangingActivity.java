package com.punkmkt.beacons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.text.format.Time;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.EditText;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.punkmkt.beacons.databases.BeaconData;
import com.punkmkt.beacons.databases.BeaconModel;
import com.punkmkt.beacons.utils.AuthRequest;
import com.punkmkt.beacons.utils.NetworkUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

public class RangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    File file;
    final String filename = "myfile.txt";
    Time fechaevento = new Time(Time.getCurrentTimezone());
    UploadBeaconData upload_data;
    Calendar c = Calendar.getInstance();
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String HORA_INICIO_RANGING = "";
    private Calendar fromTime;
    private Calendar toTime;
    private Calendar currentTime;
    JsonObjectRequest request;
    private String RALLY_MAYA_BEACONSREGISTER_JSON_API_URL = "http://rallymaya.punklabs.ninja/api/beacons/registrobeacons/";
    SharedPreferences userDetails;
    String Ukey;
    String UId;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        verifyBluetooth();
        userDetails = getApplicationContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        Ukey = userDetails.getString("hasKey", "");
        UId = userDetails.getString("Userid", "");
        Log.d("PREFERENCE", Ukey);
        Log.d("PREFERENCE", UId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            //verifyStoragePermissions(this);
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
            if(this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs storage access");
                builder.setMessage("Please grant storage access so this app can write the results in background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(PERMISSIONS_STORAGE,
                                REQUEST_EXTERNAL_STORAGE);
                    }

                });
                builder.show();
            }
        }

        Date today = Calendar.getInstance().getTime();
        final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
        Date initial;
//        for(int i=1; i<=30;i++){
//            initial = addMinutesToDate(15, today);
//            Log.d("timeafter", initial.toString());
//            //BeaconModel b = new BeaconModel();
//            //b.setBeaconid(i);
//            //b.save();
//        }




        //reset_data = new ResetBeaconData();
      //  reset_data.execute((Void) null);


         upload_data = new UploadBeaconData();
            upload_data.execute((Void) null);
        //file = new File(Environment.getExternalStorageDirectory(),filename);

        beaconManager.bind(this);
    }

    @Override 
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override 
    protected void onPause() {
        super.onPause();
        //upload_data.cancel(false);
        Log.d("state", "onpause");
            if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
            //RangingBeacon();
    }

    @Override 
    protected void onResume() {
        super.onResume();
        Log.d("state", "onresume");
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.d("current-beacon", beacons.toString());
                    //beacons.iterator().next().//unicamente  Los siguientes
                    Beacon firstBeacon = beacons.iterator().next();
                    Log.d("current-beacon", firstBeacon.getId2().toString());

                    String beaconid = firstBeacon.getId2().toString();

                    Date today = Calendar.getInstance().getTime();
                    String current_month = (String) android.text.format.DateFormat.format("MM", today);
                    String current_day = (String) android.text.format.DateFormat.format("dd", today);
                    String current_time = (String) android.text.format.DateFormat.format("HH:mm", today);
                    Log.d("current", current_time);
                    Log.d("current", current_month);
                    Log.d("current", current_day);

                    String event_month = "05";
                    String range_time = "10:00-23:00";
                    List<String> event_days = Arrays.asList("10", "11", "12", "13");

                    final BeaconData beacon_readed = SQLite.select().from(BeaconData.class).where(BeaconData.bid.eq(beaconid)).querySingle();


                    //      cada beacon. debera leeer a un cierto tiempo.
                    // horas ideales de lectura del evento.   listo
                    // fecha y hora de lectura por beacon para precaucion.
                    boolean bandera_entrada_beacon = false;

                    if(checkTime(range_time)){
                        Log.d("fuck", "my life");
                    }

                    if ((current_month.equals(event_month)) && (event_days.contains(current_day)) && checkTime(range_time)) {
                        Log.d("current", "FECHA CORRECTA");
                        if (beacon_readed == null) {
                            bandera_entrada_beacon = true;
                        } else if (beacon_readed.getReaded() == 0) {
                            bandera_entrada_beacon = true;
                        }
                        if (bandera_entrada_beacon) {
                            Log.d("current", "BEACON READED OK");
                            Long tsLong = System.currentTimeMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                            Date resultdate = new Date(tsLong);
                            String ts = sdf.format(resultdate);
                            fechaevento.setToNow();
                            Date date = Calendar.getInstance().getTime();
                            String fechaevento = sdf.format(date);
                            Log.d("fechaevento", fechaevento);
                            logToDisplay("\n\n" + ts + " The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away. " + fechaevento + "Time");
                            //si la hora del telefono con la hora del registro del beacon. definir una hora de rango de diferencia de un beacon en un punto con el mismo beacon en otro punto.

                            //o resetear la base de datos a media noche e ir sacando los dias del evento.

                            BeaconData newbeacondata = new BeaconData();
                            newbeacondata.setBeaconid(Integer.parseInt(beaconid));
                            newbeacondata.setUserid(UId);
                            newbeacondata.setDate(fechaevento);
                            newbeacondata.setDistance(firstBeacon.getDistance());
                            newbeacondata.setUploaded(0);
                            newbeacondata.setReaded(1);
                            newbeacondata.save();
                            Log.d("current", "GUARDADO TERMINADO");
                        //SaveInFile(getApplicationContext(), "\n\n" + ts + " Beacon is " + firstBeacon.getDistance() + " mt.");
                        } else {
                                Log.d("current", "BEACON READED NO");
                        }
                    } else {
                            Log.d("current", "DATE TIME Noooo");
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }


    public class UploadBeaconData extends AsyncTask<Void, Void, Boolean> {

        public  Boolean uploaded = false;

        UploadBeaconData() {}

        @Override
        protected Boolean doInBackground(Void... params) {
            while(!this.isCancelled()){
                if(NetworkUtils.haveNetworkConnection(getApplicationContext())){
                    //Log.d("internet", "jajajaja");
                    List<BeaconData> beaconData = SQLite.select().from(BeaconData.class).where(BeaconData.buploaded.eq(0)).queryList();
                    Log.d("current-BeaconDataList", beaconData.toString());
                    try {
                        for (BeaconData beaconitem: beaconData){
                            final BeaconData bi = new Select().from(BeaconData.class).where(BeaconData.idregister.eq(beaconitem.getId())).querySingle();
                            final String userid = beaconitem.getUserid();
                            final String beaconid = String.valueOf(beaconitem.getBeaconid());
                            final String uploadedbeaconitem = String.valueOf(beaconitem.getUploaded());
                            final String distance = String.valueOf(beaconitem.getDistance());
                            final String date = String.valueOf(bi.getDate());
                            Log.d("queryfieldgetId", String.valueOf(bi.getId()));
                            Log.d("queryfieldgetBeaconid", String.valueOf(bi.getBeaconid()));
                            Log.d("queryfieldgetUserid", String.valueOf(bi.getUserid()));
                            Log.d("queryfieldgetDistance", String.valueOf(bi.getDistance()));
                            Log.d("queryfield", bi.getDate());
                            Log.d("queryfield", String.valueOf(beaconitem.getUploaded()));

                            JSONObject js = new JSONObject();

                            try {

                                js.put("userid", userid);
                                js.put("beaconid", beaconid);
                                js.put("uploaded", uploadedbeaconitem);
                                js.put("distance", distance);
                                js.put("date", date);
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("js", js.toString());

                            request = new JsonObjectRequest(Request.Method.POST,RALLY_MAYA_BEACONSREGISTER_JSON_API_URL,js, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("Response",response.toString());
                                    try {
                                        bi.setUploaded(1);
                                        bi.update();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        //alert con error
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("request", error.toString());
                                    //alert con error
                                }
                            }){
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String,String> params = new HashMap<String, String>();
                                    params.put("Content-Type","application/json; charset=utf-8");
                                    Log.d("log", Ukey);
                                    params.put("Authorization", "Token " + Ukey);
                                    return params;
                                }

                            };
                            request.setRetryPolicy(new DefaultRetryPolicy(
                                    9000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            BeaconReferenceApplication.getInstance().addToRequestQueue(request);

                        }
                        Thread.sleep(9000);

                    }
                    catch (InterruptedException ie) {
                        //Handle exception
                    }

                }
                else{
                    List<BeaconData> beaconData = SQLite.select().from(BeaconData.class).where(BeaconData.buploaded.eq(0)).queryList();
                    Log.d("uploaded", beaconData.toString());
                    Log.d("internet", "jijijiji");
                }
                Date today = Calendar.getInstance().getTime();
                String range_time_middle_night = "23:59-04:00";
                if(checkTime(range_time_middle_night)){
                    Log.d("current_time", "Ya es media noche");

                    List<BeaconData> beaconData = SQLite.select().from(BeaconData.class).queryList();
                    Log.d("current-beacondata",beaconData.toString());
                    try {
                        for (BeaconData beaconitem : beaconData) {
                            beaconitem.delete();
                            Log.d("beaconitem",beaconitem.toString());
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Log.d("current_time","Aun no es media noche");
                }

            }
            // TODO: register the new account here.
            return uploaded;  // cambiar a true cuando el resultado sea satisfactorio
        }

        @Override
        protected void onPostExecute(final Boolean success) {

        }

        @Override
        protected void onCancelled() {

        }
    }

    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView editText = (TextView) RangingActivity.this.findViewById(R.id.rangingText);
                editText.setMovementMethod(new ScrollingMovementMethod());
                editText.append(line + "\n");
            }
        });
    }
    private void SaveInFile(Context context,final String line) {

        Log.d("FilesDir", Environment.getExternalStorageDirectory().toString());

        FileOutputStream outputStream;
        try {
        //    file.createNewFile();
            outputStream = new FileOutputStream(file,true);
            outputStream.write(line.getBytes());
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }
    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }



    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "storage location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since storage access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
    public void RangingBeacon(){

    }


    public boolean checkTime(String time) {
        try {
            String[] times = time.split("-");
            String[] from = times[0].split(":");
            String[] until = times[1].split(":");

            fromTime = Calendar.getInstance();
            fromTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(from[0]));
            fromTime.set(Calendar.MINUTE, Integer.valueOf(from[1]));

            toTime = Calendar.getInstance();
            toTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(until[0]));
            toTime.set(Calendar.MINUTE, Integer.valueOf(until[1]));

            currentTime = Calendar.getInstance();
            currentTime.setTime(new Date()); //
            if(currentTime.after(fromTime) && currentTime.before(toTime)){
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }




    @Override
    public void onBackPressed() {
    }
}
