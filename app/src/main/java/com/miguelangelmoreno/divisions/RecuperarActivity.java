package com.miguelangelmoreno.divisions;

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

import com.example.divisions.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RecuperarActivity extends AppCompatActivity {
    EditText editextEmail, editTextPassword;
    Button buttonContinue;
    static final String SERVIDOR = "https://miguedb.000webhostapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_recuperar);
        editextEmail = findViewById(R.id.editTextEmailRecuperar);
        editTextPassword = findViewById(R.id.editTextPasswordRecuperar);
        buttonContinue = findViewById(R.id.buttonSiguiente);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editextEmail.getText().length() == 0) {
                    Toast.makeText(RecuperarActivity.this, "El correo no puede estar vacio", Toast.LENGTH_SHORT).show();
                } else if (editTextPassword.getText().length() == 0) {
                    Toast.makeText(RecuperarActivity.this, "La contraseña no puede estar vacia", Toast.LENGTH_SHORT).show();
                } else {

                    DescargarJSON descargarJSON = new DescargarJSON();
                    descargarJSON.execute(SERVIDOR + "comprobarCorreo.php?dato=" + editextEmail.getText(), SERVIDOR + "CambiarPasswordCorreo.php?password=" + editTextPassword.getText()+"&correo="+editextEmail.getText());
                }
            }
        });

    }


    private class DescargarJSON extends AsyncTask<String, Void, Void> {
        String todo = "";
        String todo2 = "";
        JSONArray jsonArray;
        JSONArray jsonArray2;
        String usuario;
        ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(RecuperarActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Cargando");
            progreso.setProgress(0);
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            String nick = strings[0];
            String correo = strings[1];


            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(nick);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.d("hola", "hola");
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String linea = "";
                    while ((linea = br.readLine()) != null) {
                        todo += linea + "\n";
                    }
                    Log.d("mensaje", todo);
                    jsonArray = new JSONArray(todo);
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
                url = new URL(correo);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.d("hola", "hola");
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String linea = "";
                    while ((linea = br.readLine()) != null) {
                        todo2 += linea + "\n";
                    }
                    Log.d("mensaje", todo2);
                    br.close();
                    inputStream.close();
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
            JSONObject jsonObject;


                if (jsonArray.length() != 0) {
                        ObtenerUsuario obtenerUsuario = new ObtenerUsuario();
                        obtenerUsuario.execute();

                } else if (jsonArray.length() == 0) {
                    Toast.makeText(RecuperarActivity.this, "El correo no existe", Toast.LENGTH_SHORT).show();
                }



            progreso.dismiss();
        }
    }

    private class ObtenerUsuario extends AsyncTask<Void, Void, Void> {
        String usuario = "";
        ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(RecuperarActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Cargando");
            progreso.setProgress(0);
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            String ruta = "https://miguedb.000webhostapp.com/GetUsername.php?correo=" + editextEmail.getText();
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(ruta);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String linea = "";
                    while ((linea = br.readLine()) != null) {
                        usuario += linea + "\n";

                    }
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
            super.onPostExecute(unused);
            SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("usuario", usuario);
            editor.commit();

            ObtenerEquipo obtenerEquipo = new ObtenerEquipo();
            obtenerEquipo.execute();


        }
    }

    private class ObtenerEquipo extends AsyncTask<Void, Void, Void> {
        String equipo = "";
        ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(RecuperarActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Cargando");
            progreso.setProgress(0);
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            String ruta = "https://miguedb.000webhostapp.com/GetTeam.php?correo=" + editextEmail.getText();
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(ruta);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String linea = "";
                    while ((linea = br.readLine()) != null) {
                        equipo += linea + "\n";

                    }
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
            super.onPostExecute(unused);
            Log.v("equipo",equipo);

            SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("equipo", equipo);
            editor.commit();

            ComprobarToken comprobarToken = new ComprobarToken();
            comprobarToken.execute();
            progreso.dismiss();

        }
    }

    private class ComprobarToken extends AsyncTask<Void, Void, Void> {
        String token = "";
        ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(RecuperarActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Cargando");
            progreso.setProgress(0);
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            String ruta = "https://miguedb.000webhostapp.com/comprobarToken.php?dato=" + editextEmail.getText();
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(ruta);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String linea = "";
                    while ((linea = br.readLine()) != null) {
                        token += linea + "\n";

                    }
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
            super.onPostExecute(unused);

            SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
            String tokenBueno = sharedPreferences.getString("DEVICEID", null);
            Log.v("tokenBueno", tokenBueno + "");
            if (tokenBueno.compareTo(token) != 0) {

                CambiarToken cambiarToken = new CambiarToken();
                cambiarToken.execute(tokenBueno);
            } else {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                token = sharedPreferences.getString("DEVICEID", null);
                editor.putBoolean("inicio", true);
                editor.putString("correo", editextEmail.getText().toString());
                editor.commit();
                progreso.dismiss();
                Intent main = new Intent(RecuperarActivity.this, MainActivity.class);
                startActivity(main);
            }


        }
    }

    private class CambiarToken extends AsyncTask<String, Void, Void> {

        ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(RecuperarActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Cargando");
            progreso.setProgress(0);
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            String token = strings[0];
            String ruta = "https://miguedb.000webhostapp.com/CambiarToken.php?correo=" + editextEmail.getText() + "&token=" + token;
            URL url;
            HttpURLConnection httpURLConnection;
            Log.v("token", token);

            try {
                url = new URL(ruta);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String linea = "";

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
            super.onPostExecute(unused);
            SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("inicio", true);
            editor.putString("correo", editextEmail.getText().toString());
            editor.commit();
            progreso.dismiss();
            Intent main = new Intent(RecuperarActivity.this, MainActivity.class);
            startActivity(main);


        }
    }


}