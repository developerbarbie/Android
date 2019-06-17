package com.isaacson.josie.jisaacsonlab8;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST = 1;
    private int mTotalMarkers;
    ArrayList<MarkerOptions> mMapMarkerOptions;
    ArrayList<Marker> mMapMarkers;
    private float mZoomLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(savedInstanceState != null){
            mZoomLevel = savedInstanceState.getFloat("mapZoom");
            mMapMarkerOptions = (ArrayList) savedInstanceState.getSerializable("markerList");
            mMapMarkers = new ArrayList<>();
            mTotalMarkers = savedInstanceState.getInt("totalMarkers");
        }else{
            mZoomLevel = 13;
            mMapMarkerOptions = new ArrayList<>();
            mMapMarkers = new ArrayList<>();
            mTotalMarkers = 0;
        }
        Button changeButton = findViewById(R.id.button_mark);

        changeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onMarkClick(v);
            }
        });

        changeButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!mMapMarkers.isEmpty()){
                    onLongMarkClick(v);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Toast.makeText(this,
                    "Lab 8, Winter 2019, Josie Isaacson",
                    Toast.LENGTH_SHORT)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initMap();
    }

    @Override
    public void onRequestPermissionsResult(int rqst, String perms[], int[] res){
        if (rqst == PERMISSION_REQUEST) {
            // if the request is cancelled, the result arrays are empty.
            if (res.length>0 && res[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted! We can now init the map
                initMap() ;
            } else {
                Toast.makeText(this, "This app is useless without loc permissions",
                        Toast.LENGTH_SHORT).show();
            } }
    }

    public void initMap() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // we don’t yet have permission, so request it and return
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST); return;
            //return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = getLocation() ;
                if (location==null) {
                    String provider = getProvider(locMgr, Criteria.ACCURACY_FINE, locMgr.GPS_PROVIDER);
                    try {
                        location = locMgr.getLastKnownLocation(provider) ;
                    } catch(SecurityException e) {
                        Log.e("ERROR", "Security Exception: "+e.getMessage());
                    }
                }
                if (location!=null)
                    moveToLocation(location) ;
                else
                    Toast.makeText(getApplicationContext(),
                            "No Location, try the zoom to button", Toast.LENGTH_SHORT).show();
            } };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 200);

        if(!mMapMarkerOptions.isEmpty()){
            for(MarkerOptions m : mMapMarkerOptions){
                mMapMarkers.add(mMap.addMarker(m));
            }
        }

    }

    private String getProvider(LocationManager locMgr, int locAccuracy, String
            defProvider) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(locAccuracy);
        // get best provider regardless of whether it is enabled
        String providerName = locMgr.getBestProvider(criteria, false);
        if (providerName == null)
            providerName = defProvider;
        // if neither that nor the default are enabled, prompt user to change settings
        if (!locMgr.isProviderEnabled(providerName)) {
            View parent = findViewById(R.id.mapLinearLay);
            Snackbar snack = Snackbar.make(parent,
                    "Location Provider Not Enabled: Goto Settings?", Snackbar.LENGTH_LONG);
            snack.setAction("Confirm", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            snack.show();
        }
        return providerName;
    }

    public Location getLocation(){
        return mMap.getMyLocation();
    }

    public void moveToLocation(Location location){
        LatLng coords = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUp = CameraUpdateFactory.newLatLngZoom(coords, mZoomLevel);
        mMap.moveCamera(cameraUp);
    }

    public void onChangeTypeClick(View v){
        if(mMap.getMapType() == MAP_TYPE_NORMAL){
            mMap.setMapType(MAP_TYPE_SATELLITE);
        }else if(mMap.getMapType() == MAP_TYPE_SATELLITE){
            mMap.setMapType(MAP_TYPE_TERRAIN);
        }else{
            mMap.setMapType(MAP_TYPE_NORMAL);
        }
    }

    public void onMarkClick(View v){
        LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = getLocation() ;
        if (location==null) {
            String provider = getProvider(locMgr, Criteria.ACCURACY_FINE, locMgr.GPS_PROVIDER);
            try {
                location = locMgr.getLastKnownLocation(provider) ;
            } catch(SecurityException e) {
                Log.e("ERROR", "Security Exception: "+e.getMessage());
            }
        }
        LatLng coords = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(coords);
        mTotalMarkers++;
        markerOptions.title(String.valueOf("Mark " + mTotalMarkers));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
        Marker markerAdded = mMap.addMarker(markerOptions);
        mMapMarkers.add(markerAdded);
        mMapMarkerOptions.add(markerOptions);
    }

    public void onLongMarkClick(View v){
        mMapMarkers.get(mMapMarkers.size() - 1).remove();
        mTotalMarkers--;
        mMapMarkers.remove(mMapMarkers.size() - 1);
        mMapMarkerOptions.remove(mMapMarkerOptions.size() - 1);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putSerializable("markerList", mMapMarkerOptions);
        outState.putFloat("mapZoom", mMap.getCameraPosition().zoom);
        outState.putInt("totalMarkers", mTotalMarkers);
        super.onSaveInstanceState(outState);
    }
}
