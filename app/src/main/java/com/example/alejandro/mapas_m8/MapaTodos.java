package com.example.alejandro.mapas_m8;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.ArrayList;

public class MapaTodos extends AppCompatActivity implements OnMapReadyCallback{


    private GoogleMap mapa;
    ultimaTodos uTodos = new ultimaTodos();
    LatLng[] posiciones;
    ArrayList<String> matricula, fecha;


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


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
    }


    /**
     *
     * Declaramos el mapa que sea de GoogleMap y ejecutamos la ultima posicion de todos los buses
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        uTodos.execute();

    }

    /**
     *
     * Como siempre declaramos la classe interna con un constructor vacío y que extienda de asyncTask
     *
     * Haciendo uso de métodos ya deprecated declaramos  un cliente Http para la conexion via web
     * y otro para obetener la informacion de  dicha web. Marcamos que los datos seran de tipo json
     *
     * debido a que este enlace te devuelve un json de todos los buses cuya informacion sea la posicion
     * del ultimo bus lo almacenamos en un JsonArray
     *
     * Pasamos por cada objeto y lo vamos recogiendo en variables qe mas adelante utilizamos para hacer el marker
     * del mapa
     *
     */
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
                    matricula.add(pos.getString("matricula"));
                    double latitud = pos.getDouble("latitud"), altitud = pos.getDouble("altitud");
                    fecha.add(pos.getString("fecha"));
                    posiciones[i] = new LatLng(latitud, altitud);
                    mapa.addMarker(new MarkerOptions().position(posiciones[i]).title(matricula.get(i)).snippet(fecha.get(i)));

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