package csc.mobility.openevents.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;
import com.facebook.FacebookException;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PlacePickerFragment;

import csc.mobility.openevents.R;


public class PickerPlaceActivity extends FragmentActivity {
	private PlacePickerFragment placePickerFragment;
    private LocationListener locationListener;
    private static final int SEARCH_RADIUS_METERS = 1000;
    private static final int SEARCH_RESULT_LIMIT = 50;
    private static final String SEARCH_TEXT = "Restaurant";
    private static final int LOCATION_CHANGE_THRESHOLD = 50; // meters
  
    
    private static final Location HOCHIMINH_LOCATION = new Location("") {{
            setLatitude(10.769444);
            setLongitude(106.681944);
    }};
    
    // A helper to simplify life for callers who want to populate a Bundle with the necessary
    // parameters. A more sophisticated Activity might define its own set of parameters; our needs
    // are simple, so we just populate what we want to pass to the PlacePickerFragment.
    public static void populateParameters(Intent intent, Location location, String searchText) {
        intent.putExtra(PlacePickerFragment.LOCATION_BUNDLE_KEY, location);
        intent.putExtra(PlacePickerFragment.SEARCH_TEXT_BUNDLE_KEY, searchText);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        FragmentManager fm = getSupportFragmentManager();
        placePickerFragment = (PlacePickerFragment) fm.findFragmentById(R.id.place_picker_fragment);
        placePickerFragment.setShowPictures(true);
        placePickerFragment.setShowTitleBar(true);
        //placePickerFragment.setshowsearchbox;
        
        if (savedInstanceState == null) {
            // If this is the first time we have created the fragment, update its properties based on
            // any parameters we received via our Intent.
            placePickerFragment.setSettingsFromBundle(getIntent().getExtras());
        }

        // We finish the activity when either the Done button is pressed or when a place is
        // selected (since only a single place can be selected).
        placePickerFragment.setOnSelectionChangedListener(new PickerFragment.OnSelectionChangedListener() {
            @Override
            public void onSelectionChanged(PickerFragment<?> fragment) {
                if (placePickerFragment.getSelection() != null) {
                    finishActivity(); // call finish since you can only pick one place
                }
            }
        });
        placePickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
            @Override
            public void onDoneButtonClicked(PickerFragment<?> fragment) {
                finishActivity();
            }
        });
                    
        placePickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
            @Override
            public void onError(PickerFragment<?> fragment, FacebookException error) {
                PickerPlaceActivity.this.onError(error);
            }
        });       
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        try {
            // Load data, unless a query has already taken place.
            // placePickerFragment.loadData(false);
        	Location location = null;
            String bestProvider = "";
            
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
 
            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
 
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            	Toast.makeText(getApplicationContext(), "No location provider", Toast.LENGTH_LONG).show();
            } else {                
                // First get location from Network Provider
                if (isNetworkEnabled) {                   
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  
                        bestProvider = LocationManager.NETWORK_PROVIDER;
                    }
                }
                // if GPS Enabled get location using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {                      
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);    
                            bestProvider = LocationManager.GPS_PROVIDER;
                        }
                    }
                }
                               
                if (locationListener == null) {
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            float distance = location.distanceTo(placePickerFragment.getLocation());
                            if (distance >= LOCATION_CHANGE_THRESHOLD) {
                                placePickerFragment.setLocation(location);
                                placePickerFragment.loadData(true);
                            }
                        }
                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {
                        }
                        @Override
                        public void onProviderEnabled(String s) {
                        }
                        @Override
                        public void onProviderDisabled(String s) {
                        }
                    };
                    locationManager.requestLocationUpdates(bestProvider, 1, LOCATION_CHANGE_THRESHOLD,
                            locationListener, Looper.getMainLooper());
                }
                
                if (location == null) {
                    String model = Build.MODEL;
                    if (model.equals("sdk") || model.equals("google_sdk") || model.contains("x86")) {
                        // this may be the emulator, pretend we're in an exotic place
                        location = HOCHIMINH_LOCATION;
                    }
                }
                if (location != null) {
                    placePickerFragment.setLocation(location);
                    placePickerFragment.setRadiusInMeters(SEARCH_RADIUS_METERS);
                    placePickerFragment.setSearchText(SEARCH_TEXT);
                    placePickerFragment.setResultsLimit(SEARCH_RESULT_LIMIT);
                    placePickerFragment.loadData(false);
                } else {
                    onError(getResources().getString(R.string.no_location_error), true);
                }
            } 
 
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (locationListener != null) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(locationListener);
            locationListener = null;
        }
    }
    
    private void finishActivity() {
        // We just store our selection in the Application for other activities to look at.
        OpenEventsApplication app = (OpenEventsApplication) getApplication();
        if (placePickerFragment != null) {
            app.setSelectedPlace(placePickerFragment.getSelection());
        }
        setResult(RESULT_OK, null);
        finish();
    }
      
    private void onError(Exception error) {
        String text = getString(R.string.exception, error.getMessage());
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void onError(String error, final boolean finishActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error_dialog_title).
                setMessage(error).
                setPositiveButton(R.string.error_dialog_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (finishActivity) {
                            finishActivity();
                        }
                    }
                });
        builder.show();
    }
    
    
}
