package com.example.alejandro.mapas_m8;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class MapaConcreto extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mapa;

    ArrayList<LatLng> arrayPosiciones;
    String matricula, fecha;
    Ubicaciones ubi = new Ubicaciones();


    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_todos);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        matricula = getIntent().getStringExtra("matricula");
        ubi.execute();

    }


    public void onMapReady(GoogleMap gMap) {
        mapa = gMap;
        ubi.execute();
    }




    private class Ubicaciones extends AsyncTask<Void, Void, Boolean> {


        protected Boolean doInBackground(Void... params) {

            boolean vaBien = false;

            HttpClient httpC = new DefaultHttpClient();

            HttpGet httpG = new HttpGet("http://192.168.1.37:8080/WebServiceYEISON/webresources/ultima/" + matricula);

            httpG.setHeader("content-type", "application/json");

            try {

                String ejecutado = EntityUtils.toString(httpC.execute(httpG).getEntity());

                JSONArray matriculas = new JSONArray(ejecutado);
                arrayPosiciones = new ArrayList<>();

                for (int i = 0; i < matriculas.length(); i++) {
                    double latitud , altitud ;

                    JSONObject pos = matriculas.getJSONObject(i);
                    matricula = pos.getString("matricula");
                    latitud = pos.getDouble("latitud");
                    altitud = pos.getDouble("altitud");
                    fecha = pos.getString("data");
                    arrayPosiciones.add(new LatLng(latitud, altitud));

                    PolylineOptions polylineOptions = new PolylineOptions().add(new LatLng(latitud, altitud));
                    mapa.addPolyline(polylineOptions);


                }

                    vaBien = true;

            } catch (ClientProtocolException e) {
                e.printStackTrace();
                vaBien = false;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                vaBien = false;
            }
            return vaBien;
        }


    }
}