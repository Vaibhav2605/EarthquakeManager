package com.example.vaibhav.earthquakemanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.vaibhav.earthquakemanager.Model.EarthQuake;
import com.example.vaibhav.earthquakemanager.UI.CustomInfoWindow;
import com.example.vaibhav.earthquakemanager.Util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private RequestQueue queue;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        queue= Volley.newRequestQueue(this);

        getEarthQuakes();


    }


    //get all earthquakes object
    public void getEarthQuakes() {

        final EarthQuake earthQuake=new EarthQuake();
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, Constants.URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features=response.getJSONArray("features");
                            for(int i =0; i <Constants.LIMIT;i++){
                            JSONObject properites=features.getJSONObject(i).getJSONObject("properties");

                            //get geometry object
                             JSONObject geometry=features.getJSONObject(i).getJSONObject("geometry");

                             //get coordinates array
                             JSONArray coordinates=geometry.getJSONArray("coordinates");
                             double lon=coordinates.getDouble(0);
                             double lat=coordinates.getDouble(1);

                             earthQuake.setPlace(properites.getString("place"));
                             earthQuake.setType(properites.getString("type"));
                             earthQuake.setTime(properites.getLong("time"));
                             earthQuake.setLat(lat);
                             earthQuake.setLon(lon);
                             earthQuake.setMagnitude(properites.getDouble("mag"));
                             earthQuake.setDetailLink(properites.getString("detail"));
                             java.text.DateFormat dateFormat = java.text.DateFormat.getInstance();
                             String formattedDate = dateFormat.format(new Date(Long.valueOf(properites.getLong("time"))).getTime());

                            MarkerOptions markerOptions=new MarkerOptions();
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                            markerOptions.title(earthQuake.getPlace());
                            markerOptions.position(new LatLng(lat,lon));
                            markerOptions.snippet("Magnitude : "+
                            earthQuake.getMagnitude() +"\n"+
                            "Date :"+ formattedDate);

                            //add circle to marker with mag>2
                             if(earthQuake.getMagnitude()>2.0){
                                 CircleOptions circleOptions=new CircleOptions();
                                 circleOptions.center(new LatLng(earthQuake.getLat(),earthQuake.getLon()));
                                 circleOptions.radius(30000);
                                 circleOptions.strokeWidth(3.6f);
                                 circleOptions.fillColor(Color.RED);
                                 markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                 mMap.addCircle(circleOptions);
                             }

                            Marker marker=mMap.addMarker(markerOptions);
                            marker.setTag(earthQuake.getDetailLink());

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),2));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));

        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(Build.VERSION.SDK_INT<23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        getQuakeDetails(marker.getTag().toString());

    }

    private void getQuakeDetails(String url) {
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String detailsUrl="";
                try {
                    JSONObject properites=response.getJSONObject("properties");
                    JSONObject products=response.getJSONObject("products");
                    JSONArray geoserve=products.getJSONArray("gerserve");

                    for(int i=0;i<geoserve.length();i++){
                        JSONObject geoserveObj=geoserve.getJSONObject(i);
                        JSONObject contentObj=geoserveObj.getJSONObject("contents");
                        JSONObject geojsonObj=contentObj.getJSONObject("geoserve.json");
                        detailsUrl = geojsonObj.getString("url");

                    }
                    getMoreDetails(detailsUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);

    }

    public void getMoreDetails(String url){
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                dialogBuilder= new AlertDialog.Builder(MapsActivity.this);
                View view = getLayoutInflater().inflate(R.layout.popup,null);

                Button dismissButton=(Button)view.findViewById(R.id.dismisspop);
                Button dismissButtonTop=(Button)view.findViewById(R.id.dismisspopTop);
                TextView popList=(TextView)view.findViewById(R.id.popList);
                WebView htmlPop=(WebView)view.findViewById(R.id.htmlWebview);
                StringBuilder stringBuilder=new StringBuilder();


                try {
                    JSONArray cities= response.getJSONArray("cities");
                    for(int i =0;i<cities.length();i++){
                        JSONObject citiesObj=cities.getJSONObject(i);
                        stringBuilder.append("City:"+ citiesObj.getString("city")+ "\n"
                        + "Distance:"+citiesObj.getString("distance") + "\n" +
                        "Population:" + citiesObj.getString("population"));

                        stringBuilder.append("\n\n");

                    }
                    popList.setText(stringBuilder);

                    dismissButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dismissButtonTop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialogBuilder.setView(view);
                    dialog=dialogBuilder.create();
                    dialog.show();



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
