package com.example.earthquake.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;

import android.content.Intent;
import android.os.Bundle;
import android.text.style.MaskFilterSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.earthquake.Model.EarthQuake;
import com.example.earthquake.R;
import com.example.earthquake.Util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuakesActivity extends AppCompatActivity {

    private ArrayList<String> arrayList;
    private RequestQueue requestQueue;
    private ListView listView;
    private ArrayAdapter arrayAdapter;

    private List<EarthQuake> earthQuakeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quakes);

        requestQueue= Volley.newRequestQueue(this);
        listView=(ListView)findViewById(R.id.listView);
        earthQuakeList=new ArrayList<>();
        arrayList=new ArrayList<>();


        getAllQuakes(Constants.URL);
    }

    public void getAllQuakes(String url){
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                EarthQuake earthQuake=new EarthQuake();
                try {
                    JSONArray features=response.getJSONArray("features");

                    for (int i = 0; i< Constants.LIMIT; i++){
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


                        arrayList.add(earthQuake.getPlace());
                        }
                    arrayAdapter=new ArrayAdapter<>(QuakesActivity.this,android.R.layout.simple_list_item_1,android.R.id.text1,arrayList);

                    listView.setAdapter(arrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            int updatePositon=position+1;
                            Toast.makeText(getApplicationContext(), "Clicked : "+updatePositon, Toast.LENGTH_SHORT).show();
                        }
                    });
                    arrayAdapter.notifyDataSetChanged();
                    }



                catch (JSONException e) {
                    e.printStackTrace();
                } }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}