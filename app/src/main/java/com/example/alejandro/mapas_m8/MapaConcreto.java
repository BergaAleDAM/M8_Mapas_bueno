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
import com.google.android.gms.maps.model.MarkerOptions;
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
    ArrayList<String> fecha;
    String matricula;
    Ubicaciones ubi = new Ubicaciones();


    private GoogleApiClient client;



    /**
     *
     * En el onCreate iniciamos el layout correspondiente y llama al fragment que le toca
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_todos);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        matricula = getIntent().getStringExtra("matricula");
        ubi.execute();

    }

    /**
     *
     * Declaramos el mapa que sea de GoogleMap y ejecutamos la ultima posicion de todos los buses
     *
     * @param googleMap
     */
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        ubi.execute();
    }



    /**
     *
     * Como siempre declaramos la classe interna con un constructor vacío y que extienda de asyncTask
     *
     * Haciendo uso de métodos ya deprecated declaramos  un cliente Http para la conexion via web
     * y otro para obetener la informacion de  dicha web. Marcamos que los datos seran de tipo json
     *
     * debido a que este enlace te devuelve un json de todos los buses cuya informacion sea las posiciones
     * que devuelva un bus
     *
     * Pasamos por cada objeto y lo vamos recogiendo en variables qe mas adelante utilizamos para hacer el marker
     * del mapa y dibujar una linea
     *
     */
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
                    matricula = (pos.getString("matricula"));
                    latitud = pos.getDouble("latitud");
                    altitud = pos.getDouble("altitud");
                    fecha.add(pos.getString("fecha"));
                    arrayPosiciones.add(new LatLng(latitud, altitud));


                    mapa.addMarker(new MarkerOptions().position(arrayPosiciones.get(i)).title(matricula).snippet(fecha.get(i)));
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