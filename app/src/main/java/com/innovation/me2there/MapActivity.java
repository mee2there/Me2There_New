package com.innovation.me2there;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;


public class MapActivity extends AbstractMapActivity  implements OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, OnMapClickListener, OnMapLongClickListener {
    private LatLng currentLocation;
    private static LatLng fromPosition = null;
    private static LatLng toPosition = null;
    private int zoomLevel = 7;
    private GoogleMap map;
    private Button pickLocationButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentLocation = (LatLng)getIntent().getParcelableExtra("currentLocation");
        toPosition = currentLocation;
        setContentView(R.layout.activity_map);
        pickLocationButton = (Button) findViewById(R.id.pick_location);
        pickLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("pick_location",toPosition);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
        if (readyToGo()) {

            MapFragment mapFrag=
                    (MapFragment)getFragmentManager().findFragmentById(R.id.map);
           map = mapFrag.getMap();
            if (map!=null){
                map.getUiSettings().setCompassEnabled(true);
                map.setTrafficEnabled(true);
                map.setMyLocationEnabled(true);


                // Move the camera instantly to defaultLatLng.
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));


                map.addMarker(new MarkerOptions().position(currentLocation)
                        .title("This is the title")
                        .snippet("This is the snippet within the InfoWindow")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


                map.setOnInfoWindowClickListener(this);
                map.setOnMarkerClickListener(this);
                map.setOnMarkerDragListener(this);


            }
            if (savedInstanceState == null) {
                mapFrag.getMapAsync(this);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(currentLocation);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        map.moveCamera(center);
        map.animateCamera(zoom);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
/*        Intent intent = new Intent(this, NewActivity.class);
        intent.putExtra("snippet", marker.getSnippet());
        intent.putExtra("title", marker.getTitle());
        intent.putExtra("position", marker.getPosition());
        startActivity(intent);*/
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("GoogleMapActivity", "onMarkerClick");
        Toast.makeText(getApplicationContext(),
                "Marker Clicked: " + marker.getTitle(), Toast.LENGTH_LONG)
                .show();
        return false;
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        // do nothing during drag
    }
    @Override
    public void onMarkerDragStart(Marker arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onMarkerDragEnd(Marker marker) {
        toPosition = marker.getPosition();
        Toast.makeText(
                getApplicationContext(),
                "Marker " + marker.getTitle() + " dragged from " + fromPosition
                        + " to " + toPosition, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub
        map.animateCamera(CameraUpdateFactory.newLatLng(arg0));
    }


    @Override
    public void onMapLongClick(LatLng arg0) {
        // TODO Auto-generated method stub

        //create new marker when user long clicks
        map.addMarker(new MarkerOptions()
                .position(arg0)
                .draggable(true));
    }
}
