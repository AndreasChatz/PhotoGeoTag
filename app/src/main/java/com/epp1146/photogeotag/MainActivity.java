package com.epp1146.photogeotag;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private DBHandler dbHandlerObject;
    double locPalio, locKenourio = 0;


    private Location mLocation;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Boolean mRequestingLocationUpdates = false;


    int i, j, k;
    Button btn7,btnPhoto;


    private static String TAG = "GeoTag";

    Intent mediaIntent;

    public static final int MEDIA_TYPE_IMAGE = 1;
    static final int SELECT_IMAGE = 100;
    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 200;

    File imageFile;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UIInitialize();
        GooglePlayServicesInit();
        createLocationRequest();
    }

    private void GooglePlayServicesInit() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void UIInitialize() {
        btnPhoto = (Button) findViewById(R.id.btn1);
        btnPhoto.setBackgroundColor(Color.RED);
        btnPhoto.setOnClickListener(this);
        btnPhoto.setClickable(false);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn7).setOnClickListener(this);
        findViewById(R.id.btn8).setOnClickListener(this);
    }

    private Location findLastLocation(){
        return mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    private boolean isLocationRecentAndAccurate(Location loc, int accuracy, int mins) {
        if (loc == null) {
            return false;
        }
        return ((loc.getAccuracy() < accuracy && (System.currentTimeMillis() - loc.getTime()) < mins * 60 * 1000)) ? true : false;
    }

    private void findMyLocation() {
        if (isLocationRecentAndAccurate(mLastLocation, 100, 1)){
            Log.i(TAG, "findMyLocation: proto if");
            mLocation = mLastLocation;
        }else if (isLocationRecentAndAccurate(findLastLocation(), 100, 1)){
            Log.i(TAG, "findMyLocation: deutero if");
            mLocation = mLastLocation;
        }else{
            Log.i(TAG, "findMyLocation: trito if");
            startLocationUpdates();
            Log.i(TAG, "findMyLocation: false " + (System.currentTimeMillis() - mLastLocation.getTime())); 
        }
        if(mLocation!=null) {
            Toast.makeText(this, getString(R.string.vrethikeMeAkrivia) + mLocation.getAccuracy() + getString(R.string.metron), Toast.LENGTH_LONG).show();
            btnPhoto.setBackgroundColor(Color.GREEN);
            btnPhoto.setClickable(true);
            Log.i(TAG, "findMyLocation: " + "lat:" + mLocation.getLatitude() + " log:" + mLocation.getLongitude());
        }else{
            Log.i(TAG, "findMyLocation: einai null");
        }
    }

    @Override
    // Όταν πατηθεί κάποιο από τα 3 πλήκτρα
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:

                try {
                    if (isLocationRecentAndAccurate(mLastLocation, 100, 1) || isLocationRecentAndAccurate(mLocation, 100, 1)) {
                        takePhoto();
                    }else{
                        findMyLocation();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case R.id.btn2: // Αν πατήθηκε το πλήκρτο Add Tag
                // Δημιουργώ και ξεκινάω ένα Intent για να επιλέξει ο χρήστης μία
                // φωτογραφία

                Intent photoPickerIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(photoPickerIntent, SELECT_IMAGE);
                break;
            case R.id.btn3:
                DBHandler dbHandlerObject = new DBHandler(this);
                dbHandlerObject.updateNullPicturePlace(this);
                Intent i = new Intent(this, SelectFilters.class);
                startActivity(i);

                break;
            case R.id.btn7:
                findMyLocation();
                break;
            case R.id.btn8:
                this.deleteDatabase("MyDB.db");
                Toast.makeText(this, R.string.diagrafiVasis,
                        Toast.LENGTH_SHORT).show();
        }
    }

    public static String returnGeoPlace(Context context, Location location) {
        String place = ".";
        if (isWifiConnected(context)) {
            Geocoder geo = new Geocoder(context, Locale.getDefault());

            try {
                List<Address> address = geo.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 2);
                if (address.size() > 0) {
                    place = address.get(1).getLocality();
                } else {
                    Toast.makeText(context, R.string.denVerethikeTopothesia, Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return place;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Η onActivityResult καλείται μετά την κλίση της startActivityForResult
        super.onActivityResult(requestCode, resultCode, data);
        // Αν ο requestCode έχει τιμή SELECT_IMAGE τότε έχει κλιθεί το Intent
        // για την επιλογή φωτογραφίας
        // Αν ο χρήστης επέλεξε μία φωτογραφία ο resultCode έχει τιμή RESULT_OK

        switch (requestCode) {
            case SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    // Δημιουργώ μία Intent που θα ξεκινήσει την activity
                    // FaceDetection
                    Intent intent = new Intent(this, FaceDetection.class);
                    // Εισάγω το Uri της φωτογραφίας που επιλέχτηκε στην Intent με
                    // μορφή
                    // "κλειδί" "τιμή"
                    intent.putExtra("path", data.getData());
                    Log.i("image path send", data.getDataString());
                    // Ξεκινώ την Intent
                    startActivity(intent);
//                    Log.i("meta tin startActivity(intent)", "pige");
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, R.string.akirosiEpilogisEikonas,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String place = returnGeoPlace(this, this.mLocation);
                    addToGallery();
                    Toast.makeText(this, R.string.ProsthikiPhotoStinGallery,
                            Toast.LENGTH_LONG).show();

                    float locLat = new Float(mLocation.getLatitude());
                    float locLong = new Float(mLocation.getLongitude());

//				Log.i("LocLat", ""+locLat);
//				Log.i("LocLong", ""+locLong);

                    if (!(place == "" || place == null)) {
                        Log.i("Place", "" + place);
                        Toast.makeText(this, getString(R.string.Topotheias) + place, Toast.LENGTH_LONG).show();
                        dbHandlerObject = new DBHandler(this);
                        dbHandlerObject.setPhotoLocationAttributes(
                                Uri.fromFile(imageFile).toString(), locLat,
                                locLong, place);
                        Log.i("teliki", "topothsia arxiou"
                                + Uri.fromFile(imageFile).toString());
                        Log.i("teliki", "ok doulevei");
                    } else {
                        Log.i("place", "place null or empty");
                        Toast.makeText(this, "place null or empty", Toast.LENGTH_LONG).show();
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, R.string.akirosiPhotografisis,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    static boolean isWifiConnected(Context context) {
        NetworkInfo mWifi;
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnectedOrConnecting();
    }

    /**
     * Arxi kodika gia tis
     * fotografies!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */

    private void takePhoto() throws IOException {
        // TODO Auto-generated method stub

        mediaIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (checkAvailabilities()) {

            imageFile = createMediaFile(MEDIA_TYPE_IMAGE);
            if (imageFile != null) {
                mediaIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(imageFile));

                startActivityForResult(mediaIntent,
                        CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            } else {
                Toast.makeText(this, R.string.adinamiaDimiourgiasArxiou, Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createMediaFile(int type) throws IOException {
        // TODO Auto-generated method stub

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        Log.i("MyCameraApp", "timeStamp : " + timeStamp);
        File mediaFile = null;
        switch (type) {
            case MEDIA_TYPE_IMAGE:

                File mediaStorageDir = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "My_Photos");

                // Create the storage directory if it does not exist

                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdir()) {
                        Toast.makeText(this, R.string.adinamiaDimiourgiasArxiouEikonas, Toast.LENGTH_SHORT).show();
                        Log.d("MyCameraApp", "failed to create directory");
                    }
                }

                Log.i("MyCameraApp", "mediaStorageDir : " + mediaStorageDir);

                mediaFile = new File(mediaStorageDir.getAbsolutePath()
                        + File.separator + "IMG_" + timeStamp + ".jpg");

        }

        return mediaFile;
    }

    private boolean checkAvailabilities() {
        // TODO Auto-generated method stub

        if (!isCameraAvailable(this)) {
            Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT)
                    .show();
            Log.d("MyCameraApp", "Camera unavailable");
        }
        if (!isIntentAvailable(this, mediaIntent)) {
            Toast.makeText(this, "Intent unavailable", Toast.LENGTH_SHORT)
                    .show();
            Log.d("MyCameraApp", "Intent unavailable");
        }
        if (!isSDMounted()) {
            Toast.makeText(this, "SD unmounted", Toast.LENGTH_SHORT).show();
            Log.d("MyCameraApp", "SD unmounted");
        }
        return isCameraAvailable(this) && isIntentAvailable(this, mediaIntent)
                && isSDMounted();
    }

    private boolean isSDMounted() {
        // TODO Auto-generated method stub
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private boolean isCameraAvailable(Context context) {
        // TODO Auto-generated method stub
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    private boolean isIntentAvailable(Context context, Intent intent) {
        // TODO Auto-generated method stub
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void addToGallery() {
//        Log.i("mpike stin addtogallery?", "nai");
        Intent mediaScanIntent = new Intent(
                "android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        if (Uri.fromFile(imageFile).toString() == null) {
            Toast.makeText(this, R.string.sfalmaProsthikisStinGallery, Toast.LENGTH_SHORT).show();
        }
        mediaScanIntent.setData(Uri.fromFile(imageFile));
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * Telos kodika gia fotografies
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        findLastLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isLocationRecentAndAccurate(location, 100, 1)) {
            stopLocationUpdates();
            mLocation = location;
            Toast.makeText(this, getString(R.string.vrethikeMeAkrivia) + mLocation.getAccuracy() + getString(R.string.metron), Toast.LENGTH_LONG).show();
            btnPhoto.setBackgroundColor(Color.GREEN);
            btnPhoto.setClickable(true);
            Log.i(TAG, "onLocationChanged: vre8ike");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}

