package com.example.noteapp.noteapp;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.os.Handler;
import android.os.ResultReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;


public class FloatingActivity extends Activity implements ConnectionCallbacks,OnConnectionFailedListener {
    SharedPreferences Note_pref;
    public static Activity floatactiviy;
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";
    protected boolean mAddressRequested;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    protected String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating);
        floatactiviy = this;
        mAddressRequested = false;
        mResultReceiver = new AddressResultReceiver(new Handler());
        Intent intent = new Intent(this, CallDetectService.class);
        startService(intent);

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        Note_pref = getSharedPreferences("NoteAppPrefs", Context.MODE_PRIVATE);

        if (Note_pref.getString("EMAIL", "").isEmpty()) {
            Intent myIntent = new Intent(this, LoginActivity.class);
            this.startActivity(myIntent);
        } else {
            Intent floating = new Intent(this, FloatingBubbleService.class);
            startService(floating);

        }
        //startService(new Intent(this,LocationIntentService.class));

        updateValuesFromBundle(savedInstanceState);
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                //displayAddressOutput();
            }
        }
    }
    /**
     * Method to display the location on UI
     */
    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, "no geocoder available", Toast.LENGTH_LONG).show();
                return;
            }
            //Toast.makeText(this,latitude+" "+ longitude ,Toast.LENGTH_LONG).show();
            if (mAddressRequested) {
                startIntentService();
            }
            Log.e("Location", latitude + " " + longitude);// lblLocation.setText(latitude + ", " + longitude);

        }
    }

    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, LocationIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Log.e("GooglePlay", "resultcode!=success");
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Start", "onstart");
        if (mGoogleApiClient != null) {
            Log.e("Start", "mGoogleApiClient != null");
            mGoogleApiClient.connect();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("ConnectionFailed", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }
    protected void showToast(String text) {
       Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
   }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String Address;
            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            //displayAddressOutput();
            Log.e("OUTPUT", mAddressOutput);
            SharedPreferences.Editor edit = Note_pref.edit();
            edit.putString("CURRENT_ADDRESS", mAddressOutput);
            edit.commit();
            Address= Note_pref.getString("CURRENT_ADDRESS", "OOPS");
            Log.e("OOPS OUTPUT", Address);
            if(resultCode==Constants.FAILURE_RESULT)
            {
                showToast("oops"+" "+mAddressOutput);
            }
           // showToast("oops...."+" "+ mAddressOutput);

            // Show a toast message if an address was found.
           if (resultCode == Constants.SUCCESS_RESULT) {
              showToast(getString(R.string.address_found));
               showToast(mAddressOutput);
            }
            mAddressRequested = false;
        }
    }

}