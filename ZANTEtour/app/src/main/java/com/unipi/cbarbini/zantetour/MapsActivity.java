package com.unipi.cbarbini.zantetour;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unipi.cbarbini.zantetour.Models.Attraction;

import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener {

    private GoogleMap mMap;
    private int Value,SetCamera ;
    private Button btn_museums,btn_restaurants,btn_hotels,btn_mylocation;
    String[] location ;
    float latitude;
    float longitude;
    Marker marker;

    Boolean mylocationisOn=false;
    Boolean MarkerAdded=false;

    //Firebase
    private DatabaseReference dbref;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        btn_museums=findViewById(R.id.button1);
        btn_restaurants=findViewById(R.id.button2);
        btn_hotels=findViewById(R.id.button3);
        btn_mylocation=findViewById(R.id.btn_mylocation);

        //Initialize  button's color
        ChangeLayoutButtonColor(btn_museums,btn_hotels,btn_restaurants);

        //Initialize vairable
        InitializeValues();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_museums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Value=0;
                ChangeLayoutButtonColor(btn_museums,btn_hotels,btn_restaurants);
                SetMarkersOnMap();

            }
        });
        btn_restaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Value=1;
                ChangeLayoutButtonColor(btn_restaurants,btn_museums,btn_hotels );
                SetMarkersOnMap();

            }
        });
        btn_hotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Value=2;
                ChangeLayoutButtonColor(btn_hotels,btn_restaurants,btn_museums);
                SetMarkersOnMap();

            }
        });
        btn_mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mylocationisOn==false)startGps();
                else stopGps();
            }
        });
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

        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //Add Markers
        SetMarkersOnMap();
    }
    private String SetMarkersOnMap()
    {
        mMap.clear();
        final float zoom=15;

        if      (Value==0) dbref=FirebaseDatabase.getInstance().getReference().child("Attraction").child("museums");
        else if (Value==1) dbref=FirebaseDatabase.getInstance().getReference().child("Attraction").child("restaurants");
        else if (Value==2) dbref=FirebaseDatabase.getInstance().getReference().child("Attraction").child("hotels");


        dbref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Attraction newAttraction =dataSnapshot.getValue(Attraction.class);
                location =newAttraction.getLocation().split(",");
                latitude=Float.valueOf(location[0]);
                longitude=Float.valueOf(location[1]);
                LatLng locationOnMap= new LatLng(latitude, longitude);
                marker=mMap.addMarker(new MarkerOptions().position(locationOnMap).title(newAttraction.getTitle()));

                //Set camera zoom to the first marker
                if(SetCamera==0)
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationOnMap, zoom));
                    SetCamera=1;
                }



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return "";
    }
    private void ChangeLayoutButtonColor(Button button1,Button button2,Button button3)
    {
        button1.setBackgroundColor(Color.parseColor("#FF8FB1DB"));
        button1.setTypeface(null, Typeface.BOLD);
        button2.setBackgroundColor(Color.parseColor("#FFE0DDDD"));
        button2.setTypeface(null, Typeface.NORMAL);
        button3.setBackgroundColor(Color.parseColor("#FFE0DDDD"));
        button3.setTypeface(null, Typeface.NORMAL);

    }

    private void InitializeValues()
    {
       SetCamera=0;
       Value=0;
       location=null;
       latitude= (float) 0.0;
       longitude=(float) 0.0;
    }


    public void startGps()
    {   //check if gps service is enabled
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==true)
        {
            //user permission ACCESS FINE LOCATION because we use gps provider
            //if user has already given this permission Call onlocationChnaged via location manager else call requestStoragePermission();
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mylocationisOn=true;
                btn_mylocation.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_blue));
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);   }
            else requestStoragePermission();}

        else  Toast.makeText(this, "OPEN GPS LOCATION SERVICES FIRST", Toast.LENGTH_SHORT).show();
    }

    public void stopGps ()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {
            //stop updates but first check for user's permissions
            mylocationisOn=false;
            locationManager.removeUpdates(this);
            btn_mylocation.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorPrimaryDark));
            MarkerAdded=false;

        }
    }

    private void requestStoragePermission() {
        //ask for permissions
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {warnings();}
        else {ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1); }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        //runtime permissions call back
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults [0]==PackageManager.PERMISSION_GRANTED)
        {  if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED )
        {   Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            btn_mylocation.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_blue));
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);}
        }
        else Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onLocationChanged(Location location) {
        double newLat = location.getLatitude();
        double newLon = location.getLongitude();
        LatLng mylocation= new LatLng(newLat, newLon);
        if(!MarkerAdded)
        {   MarkerAdded=true;
            marker=mMap.addMarker(new MarkerOptions().position(mylocation).title("My location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 15));
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
    public void warnings()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setCancelable(true);
        builder.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setMessage("We need this permission to begin !");

        AlertDialog alertbuilder = builder.create();
        alertbuilder.show();
    }
    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Settings:
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                break;
            case R.id.Help:
                startActivity(new Intent(getApplicationContext(),HelpActivity.class));
                break;
            case R.id.Logout:
                LogOut();
                break;
        }
        return false;
    }
    //signout
    private void LogOut()
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }

}
