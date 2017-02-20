package com.example.alejandro.mapas_m8;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MapaTodos extends AppCompatActivity implements OnMapReadyCallback{


    private GoogleMap mapa;
    ultimaTodos uTodos = new ultimaTodos();
    LatLng[] posiciones;
    String matricula, fecha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_todos);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        uTodos.execute();

    }

    private class ultimaTodos extends AsyncTask<Void, Void, Boolean> {

        public ultimaTodos() {
        }

        protected Boolean doInBackground(Void... params) {

            boolean vaBien = false;

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://192.168.1.37:8080/WebServiceYEISON/webresources/generic/ultima");
            get.setHeader("content-type", "application/json");

            try {
                String ejecutado = EntityUtils.toString(httpClient.execute(get).getEntity());
                JSONArray jsonPosiciones = new JSONArray(ejecutado);
                posiciones = new LatLng[jsonPosiciones.length()];

                for (int i = 0; i < jsonPosiciones.length(); i++) {
                    JSONObject pos = jsonPosiciones.getJSONObject(i);
                    matricula = pos.getString("matricula");
                    double latitud = pos.getDouble("latitud"), altitud = pos.getDouble("altitud");
                    fecha = pos.getString("fecha");
                    posiciones[i] = new LatLng(latitud, altitud);

                }
                if (!ejecutado.equals("true")) {
                    vaBien = true;
                }
            }catch (ClientProtocolException e) {
                e.printStackTrace();
                vaBien = false;
            } catch (IOException |JSONException e) {
                e.printStackTrace();
                vaBien = false;
            }
            return vaBien;
        }

    }
}