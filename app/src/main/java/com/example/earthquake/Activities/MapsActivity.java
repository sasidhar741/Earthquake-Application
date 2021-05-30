package com.example.earthquake.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.earthquake.Model.EarthQuake;
import com.example.earthquake.R;
import com.example.earthquake.Ui.CustomInfoWindow;
import com.example.earthquake.Util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Objects;

public class
MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    public GoogleMap mMap;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private RequestQueue requestQueue;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private BitmapDescriptor[] markerColors;

    private Button showList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        showList=(Button)findViewById(R.id.showList_id);
        showList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, QuakesActivity.class));
            }
        });

        markerColors=new BitmapDescriptor[]{
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
        };

        requestQueue=Volley.newRequestQueue(this);

        getEarthQuakes();


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
        public void onMapReady (GoogleMap googleMap){
            mMap = googleMap;
            mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));
            mMap.setOnInfoWindowClickListener(this);
            mMap.setOnMarkerClickListener(this);


            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    //latLng = new LatLng(location.getLatitude(), location.getLongitude());

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
            if(Build.VERSION.SDK_INT < 23) {

                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,locationListener);
                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,locationListener);
            }
            else {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //Ask for permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                }
                else {
                    // we have permission!
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .title("Marker is in Current Location"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));

                }
            }
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }
    }



    private void getEarthQuakes() {

        final EarthQuake earthQuake=new EarthQuake();

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, Constants.URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray features=response.getJSONArray("features");

                    for (int i=0;i<Constants.LIMIT;i++){
                        JSONObject properties=features.getJSONObject(i).getJSONObject("properties");
                        //Get geometry object
                        JSONObject geometry=features.getJSONObject(i).getJSONObject("geometry");
                        //Get the coordinates from the array which is inside the geometry object
                        JSONArray coordinates=geometry.getJSONArray("coordinates");

                        double lon=coordinates.getDouble(0);
                        double lat=coordinates.getDouble(1);

                        earthQuake.setPlace(properties.getString("place"));
                        earthQuake.setMagnitude(properties.getDouble("mag"));
                        earthQuake.setTime(properties.getLong("time"));
                        earthQuake.setDetailLink(properties.getString("detail"));
                        earthQuake.setType(properties.getString("type"));
                        earthQuake.setLon(lon);
                        earthQuake.setLon(lat);

                        java.text.DateFormat dateFormat=java.text.DateFormat.getDateInstance();

                        String formattedDate=dateFormat.format(new Date(Long.valueOf(properties.getLong("time"))).getTime());



                        MarkerOptions markerOptions=new MarkerOptions();

                        markerOptions.title(properties.getString("place"));
                        markerOptions.icon(markerColors[Constants.randomcolor(markerColors.length,0)]);
                        markerOptions.alpha(1.0f);
                        markerOptions.position(new LatLng(lat,lon));
                        markerOptions.snippet("Magnitude : "+earthQuake.getMagnitude()+
                                "\n"+"Date : "+formattedDate);

                        //Add a circle if the magnitude is greater than 2.0

                        if (earthQuake.getMagnitude() >= 2.0){
                            CircleOptions circleOptions=new CircleOptions();
                            circleOptions.center(new LatLng(earthQuake.getLat(),earthQuake.getLon()));
                            circleOptions.radius(30000);
                            circleOptions.strokeWidth(5.0f);
                            circleOptions.fillColor(Color.RED);

                            circleOptions.strokeColor(Color.GREEN);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                            mMap.addCircle(circleOptions);
                        }

                        Marker marker=mMap.addMarker(markerOptions);
                        marker.setTag(earthQuake.getDetailLink());

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),1));


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
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

           //Toast.makeText(getApplicationContext(),marker.getTag().toString(),Toast.LENGTH_LONG).show();

           getQuakeDetails(Objects.requireNonNull(marker.getTag()).toString());


    }

    private void getQuakeDetails(String url) {

            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String detailsUrl="";
                    try {
                        JSONObject properties=response.getJSONObject("properties");
                        JSONObject products=properties.getJSONObject("products");

                        JSONArray nearbycities=products.getJSONArray("nearby-cities");

                        for (int i=0;i<nearbycities.length();i++){
                            JSONObject nearbycitiesObj=nearbycities.getJSONObject(i);
                            JSONObject contentsObj=nearbycitiesObj.getJSONObject("contents");

                            JSONObject nearbyCitiesJsonObj=contentsObj.getJSONObject("nearby-cities.json");

                            detailsUrl=nearbyCitiesJsonObj.getString("url");
                            //Toast.makeText(MapsActivity.this,"entered into the "+detailsUrl,Toast.LENGTH_LONG).show();//successfully executed till now
                            getMoreDetails(detailsUrl);



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
            requestQueue.add(jsonObjectRequest);
    }

    public void getMoreDetails(String url){



        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                alertDialogBuilder=new AlertDialog.Builder(MapsActivity.this);
                View view=getLayoutInflater().inflate(R.layout.popup,null);

                Button dismiss_top=(Button) view.findViewById(R.id.dismiss_popup_id);
                Button dismiss_below=(Button) view.findViewById(R.id.dismiss_popup_bottom);

                TextView popupTextview=(TextView) view.findViewById(R.id.pop_list);
                WebView webView=(WebView) view.findViewById(R.id.html_web_view);
                StringBuilder stringBuilder=new StringBuilder();


                try {

                   for (int i=0;i<response.length();i++){
                       JSONObject citiesjsonObject=response.getJSONObject(i);
                       stringBuilder.append("Name : "+citiesjsonObject.getString("name")+
                               "\n"+"Distance : "+citiesjsonObject.getString("distance")+ "\n"
                               +"Population : "+citiesjsonObject.getString("population"));


                       stringBuilder.append("\n\n");
                   }

                    popupTextview.setText(stringBuilder);
                    webView.loadUrl("https://www.usgs.gov/natural-hazards/earthquake-hazards/lists-maps-and-statistics");
                   dismiss_top.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           alertDialog.dismiss();
                       }
                   });

                   dismiss_below.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           alertDialog.dismiss();
                       }
                   });
                    alertDialogBuilder.setView(view);
                    alertDialog=alertDialogBuilder.create();
                    alertDialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}