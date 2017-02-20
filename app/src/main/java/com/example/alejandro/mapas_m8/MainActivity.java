package com.example.alejandro.mapas_m8;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnConcreto;
    Button btnTodos;
    EditText etMatricula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConcreto = (Button) findViewById(R.id.concreto);
        btnTodos = (Button) findViewById(R.id.todos);
        etMatricula = (EditText) findViewById(R.id.matricula);


    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case (R.id.concreto):
                if(verificarVacio()) {

                        Intent intent1 = new Intent(this, MapaConcreto.class);
                        intent1.putExtra("matricula", etMatricula.getText().toString().trim());
                        startActivity(intent1);
                    } else {
                        Toast t2 = Toast.makeText(getApplicationContext(), "No coincide con ninguna matricula de la BBDD", Toast.LENGTH_SHORT);
                        t2.show();
                         break;}

            case (R.id.todos):
                Intent intent = new Intent(this, MapaTodos.class);
                startActivity(intent);


        }
    }


    private boolean verificarVacio() {

        String matricula = etMatricula.getText().toString().trim();

        if(matricula.isEmpty() || matricula.length() == 0 || matricula.equals("") || matricula == null)
        {
            Toast t1 = Toast.makeText(getApplicationContext(),"El campo de matricula está vacío", Toast.LENGTH_SHORT);
            t1.show();
            return false;
        }
        else
        {
            return true;
        }
    }
}
