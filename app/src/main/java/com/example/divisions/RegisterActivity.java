package com.example.divisions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {
    EditText editextUsername,editextEmail,editTextPassword;
    Button buttonContinue;
    static final String SERVIDOR = "https://miguedb.000webhostapp.com/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editextUsername=findViewById(R.id.editTextUsername);
        editextEmail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        buttonContinue=findViewById(R.id.buttonContinueLogin);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editextUsername.getText().length()==0){
                    Toast.makeText(RegisterActivity.this,"El nick no puede estar vacio",Toast.LENGTH_SHORT).show();
                }
                else if (editextEmail.getText().length()==0){
                    Toast.makeText(RegisterActivity.this,"El correo no puede estar vacio",Toast.LENGTH_SHORT).show();
                }
                else if (editTextPassword.getText().length()==0){
                    Toast.makeText(RegisterActivity.this,"La contraseña no puede estar vacia",Toast.LENGTH_SHORT).show();
                }
                else{
                    DescargarJSON descargarJSON = new DescargarJSON();
                    descargarJSON.execute(SERVIDOR+"comprobarNick.php?dato="+editextUsername.getText(),SERVIDOR+"comprobarCorreo.php?dato="+editextEmail.getText());
                }


            }
        });
    }
    private class DescargarJSON extends AsyncTask<String,Void,Void>{
        String todo="";
        String todo2="";
        JSONArray jsonArray;
        JSONArray jsonArray2;
        ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso= new ProgressDialog(RegisterActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Cargando");
            progreso.setProgress(0);
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            String nick=strings[0];
            String correo=strings[1];

            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url=new URL(nick);
                httpURLConnection= (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode()== HttpURLConnection.HTTP_OK){
                    Log.d("hola","hola");
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                    String linea = "";
                    while ((linea = br.readLine()) != null) {
                        todo += linea+"\n";
                    }
                    Log.d("mensaje", todo);
                    jsonArray=new JSONArray(todo);
                    br.close();
                    inputStream.close();
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                url=new URL(correo);
                httpURLConnection= (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode()== HttpURLConnection.HTTP_OK){
                    Log.d("hola","hola");
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                    String linea = "";
                    while ((linea = br.readLine()) != null) {
                        todo2 += linea+"\n";
                    }
                    Log.d("mensaje", todo2);
                    jsonArray2=new JSONArray(todo2);
                    br.close();
                    inputStream.close();
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {


            if (jsonArray.length()==0 && jsonArray2.length()==0 && editTextPassword.getText().length()!=0){
                Insertar insertar = new Insertar();
                insertar.execute(SERVIDOR+"insertar.php?nick="+editextUsername.getText().toString()+"&correo="+editextEmail.getText().toString()+"&password="+editTextPassword.getText().toString());

            }
            else {
                if (jsonArray.length()!=0){
                    Toast.makeText(RegisterActivity.this,"El nombre de usuario ya existe",Toast.LENGTH_SHORT).show();
                }
                if (jsonArray2.length()!=0){
                    Toast.makeText(RegisterActivity.this,"El correo ya existe",Toast.LENGTH_SHORT).show();
                }

            }
            progreso.dismiss();
        }
    }
    private class Insertar extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            String ruta = strings[0];
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url=new URL(ruta);
                httpURLConnection= (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode()== HttpURLConnection.HTTP_OK){
                    Log.v("conexion","correcta");
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("inicio",true);
            editor.putString("usuario",editextUsername.getText().toString());
            editor.putString("correo", editextEmail.getText().toString());
            editor.commit();
            Intent main = new Intent(RegisterActivity.this,TeamActivity.class);
            startActivity(main);
        }
    }
}