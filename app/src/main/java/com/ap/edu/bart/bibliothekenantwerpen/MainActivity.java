package com.ap.edu.bart.bibliothekenantwerpen;

import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

//import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.*;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

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
    final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

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
        mapView.getController().setCenter(new GeoPoint(51.2244, 4.38566));
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
                        for(int i = 0; i < bibliotheeks.size(); i++){

                            Bibliotheek bib = bibliotheeks.get(i);
                            double lat = Double.parseDouble(bib.getlat());
                            double lng = Double.parseDouble(bib.getlng());
                            GeoPoint g = new GeoPoint(lat, lng);

                            OverlayItem myLocationOverlayItem = new OverlayItem("Here", "Current Position", g);
                            Drawable myCurrentLocationMarker = ResourcesCompat.getDrawable(getResources(), R.drawable.marker, null);
                            myLocationOverlayItem.setMarker(myCurrentLocationMarker);

                            items.add(myLocationOverlayItem);
                            DefaultResourceProxyImpl resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());

                            ItemizedIconOverlay<OverlayItem> currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
                                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                                            return true;
                                        }
                                        public boolean onItemLongPress(final int index, final OverlayItem item) {
                                            return true;
                                        }
                                    }, resourceProxy);
                            mapView.getOverlays().add(currentLocationOverlay);
                            mapView.invalidate();
                        }
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

    private void addMarker(GeoPoint g) {
        OverlayItem myLocationOverlayItem = new OverlayItem("Here", "Current Position", g);
        Drawable myCurrentLocationMarker = ResourcesCompat.getDrawable(getResources(), R.drawable.marker, null);
        myLocationOverlayItem.setMarker(myCurrentLocationMarker);

        items.add(myLocationOverlayItem);
        DefaultResourceProxyImpl resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());

        ItemizedIconOverlay<OverlayItem> currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        return true;
                    }
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return true;
                    }
                }, resourceProxy);
        this.mapView.getOverlays().add(currentLocationOverlay);
        this.mapView.invalidate();
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
