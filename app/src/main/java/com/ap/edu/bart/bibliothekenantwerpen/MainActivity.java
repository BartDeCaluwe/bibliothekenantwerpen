package com.ap.edu.bart.bibliothekenantwerpen;

import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private LocationManager locationManager;
    private String data = "http://datasets.antwerpen.be/v4/gis/bibliotheekoverzicht.json";
    private RequestQueue mRequestQueue;
    SQLiteHelper helper;
    ArrayList<Bibliotheek> bibliotheeks = new ArrayList<Bibliotheek>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new SQLiteHelper(this);
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(18);

        mRequestQueue = Volley.newRequestQueue(this);
        setPreferences(true);
        if(true) {
            // A JSONObject to post with the request. Null is allowed and indicates no parameters will be posted along with request.
            JSONObject obj = null;
            // haal alle parkeerzones op

            JsonObjectRequest jr = new JsonObjectRequest(Request.Method.GET, "http://datasets.antwerpen.be/v4/gis/bibliotheekoverzicht.json", obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        helper.saveZones(response.getJSONArray("data"));
                        setPreferences(true);
                        bibliotheeks = helper.getAllBibliotheken();
                        Log.d("com.ap.edu", "Zones saved to DB");
                    }
                    catch (JSONException e) {
                        Log.e("edu.ap.mapsaver", e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("edu.ap.mapsaver", error.getMessage());
                }
            });
            mRequestQueue.add(jr);
        }
        else {
            bibliotheeks = helper.getAllBibliotheken();
            Log.d("edu.ap.mapsaver", "Zones retrieved from DB");
        }

    }

    private void setPreferences(boolean b) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("db_filled", b);
        editor.commit();
    }

    private boolean getPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("db_filled", false);
    }
}
