package com.miguelangelmoreno.divisions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.divisions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class StartActivity extends AppCompatActivity {
    Button buttonRegister, buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();
        registrarDispositivo();
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonLogin = findViewById(R.id.buttonLogin);
        ComprobrarInternet comprobrarInternet = new ComprobrarInternet();
        comprobrarInternet.execute();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("inicio",true);
                editor.commit();*/
                Intent register = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(register);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("inicio",true);
                editor.commit();*/
                Intent login = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(login);
            }
        });
    }

    private void registrarDispositivo() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
                        String tokenGuardado = sharedPreferences.getString("DEVICEID", null);
                        Log.v("token", token);
                        Log.v("tokenGuardado", tokenGuardado + "");
                        if (token != null) {
                            if (tokenGuardado == null || !token.equals(tokenGuardado)) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("DEVICEID", token);
                                editor.commit();
                            }
                        }
                        // Log and toast
                        Log.d(ContentValues.TAG, token);

                    }
                });
    }

    private class ComprobrarInternet extends AsyncTask<Void, Void, Void> {
        ProgressDialog progreso;
        String texto;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(StartActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Comprobando internet");
            progreso.setProgress(0);
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            texto = comprobarInternet();
            Log.v("texto", texto);


            return null;


        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progreso.dismiss();
            if (texto != "Correcto") {
                new AlertDialog.Builder(StartActivity.this)
                        .setTitle(texto)
                        .setCancelable(false)
                        .setNegativeButton("Cerrar aplicacion", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ComprobrarInternet comprobrarInternet = new ComprobrarInternet();
                                comprobrarInternet.execute();
                            }
                        })
                        .show();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
                Boolean inicio = sharedPreferences.getBoolean("inicio", false);
                if (inicio) {
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }


        }
    }

    private String comprobarInternet() {
        ConnectivityManager cm;
        NetworkInfo ni;
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        boolean tipoConexion1 = false;
        boolean tipoConexion2 = false;

        if (ni != null) {
            ConnectivityManager connManager1 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            ConnectivityManager connManager2 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mWifi.isConnected()) {
                tipoConexion1 = true;
            }
            if (mMobile.isConnected()) {
                tipoConexion2 = true;
            }

            if (tipoConexion1 == true || tipoConexion2 == true) {
                return "Correcto";
            }
        } else {
            return "Fallo en conexion";
        }
        return "Fallo";
    }
}