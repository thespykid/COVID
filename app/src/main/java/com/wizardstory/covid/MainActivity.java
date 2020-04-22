package com.wizardstory.covid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private final String url = "https://api.covid19api.com/summary";
    private RequestQueue queue;
    private JSONObject jsonCovid;
    private TextView globalNewConfirmed;
    private TextView globalTotalConfirmed;
    private TextView globalNewDeath;
    private TextView globalTotalDeath;
    private TextView globalNewRecovered;
    private TextView globalTotalRecovered;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText filterCountry;
    private JSONArray countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make relation UI
        videoView = findViewById(R.id.videoView);
        globalNewConfirmed = findViewById(R.id.globalNewConfirmed);
        globalTotalConfirmed = findViewById(R.id.globalTotalConfirmed);
        globalNewDeath = findViewById(R.id.globalNewDeath);
        globalTotalDeath = findViewById(R.id.globalTotalDeath);
        globalNewRecovered = findViewById(R.id.globalNewRecovered);
        globalTotalRecovered = findViewById(R.id.globalTotalRecovered);
        filterCountry = findViewById(R.id.filterCountry);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_1);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        // Get data
        queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jsonCovid = new JSONObject(response);
                    // 1. set global
                    JSONObject global = jsonCovid.getJSONObject("Global");
                    DecimalFormat decimalFormat = getDecimalFormat();
                    globalNewConfirmed.setText("New Confirmed: " + decimalFormat.format(global.getLong("NewConfirmed")));
                    globalTotalConfirmed.setText("Total Confirmed: " + decimalFormat.format(global.getLong("TotalConfirmed")));
                    globalNewDeath.setText("New Deaths: " + decimalFormat.format(global.getLong("NewDeaths")));
                    globalTotalDeath.setText("Total Deaths: " + decimalFormat.format(global.getLong("TotalDeaths")));
                    globalNewRecovered.setText("New Recovered: " + decimalFormat.format(global.getLong("NewRecovered")));
                    globalTotalRecovered.setText("Total Recovered: " + decimalFormat.format(global.getLong("TotalRecovered")));
                    Log.i("APP", "TEST JA");

                    // 2. set by country
                    countries = jsonCovid.getJSONArray("Countries");
                    mAdapter = new RecyclerAdapter(MainActivity.this, countries);
                    recyclerView.setAdapter(mAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("APP", "error");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        // Filter
        filterCountry.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String filter = filterCountry.getText().toString();
                if(filter.length() > 0){
                    for(int i = 0; i < countries.length(); i++){
                        try {
                            JSONObject country = (JSONObject) countries.get(i);
                            String countryString = country.getString("Country").toLowerCase();
                            if(countryString.contains(filter.toLowerCase())){
                                recyclerView.scrollToPosition(i);
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    recyclerView.scrollToPosition(0);
                }
                return false;
            }
        });
    }


    private DecimalFormat getDecimalFormat(){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        return decimalFormat;
    }
}
