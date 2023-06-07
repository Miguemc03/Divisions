package com.miguelangelmoreno.divisions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.divisions.R;
import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends AppCompatActivity {
    static final String APIKEY = "c6196c01e7c1d93932590f42beec9ef8";
    List<String> listaEquiposString = new ArrayList<>();
    List<Equipos> listaEquipos = new ArrayList<>();
    List<String> listaLigasString = new ArrayList<>();
    List<Ligas> listaLigas = new ArrayList<>();
    ListView listViewEquipos;
    TextView saludoUsuario;
    Spinner spinnerLigas;
    ArrayList<Ligas> arrayLigas;
    ArrayList<Equipos> arrayEquipos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        getSupportActionBar().hide();
        listViewEquipos = findViewById(R.id.ListViewEquipos);
        spinnerLigas = findViewById(R.id.SpinnerLigaEquipos);
        saludoUsuario = findViewById(R.id.textViewNombreSaludo);
        SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        String usuario = sharedPreferences.getString("usuario", "null");
        saludoUsuario.setText(usuario);
        arrayLigas = new ArrayList<>();
        arrayEquipos = new ArrayList<>();
        DescargarLigas descargarLigas = new DescargarLigas();
        descargarLigas.execute();
//        AdaptadorLigas adaptadorLigas=new AdaptadorLigas(this,R.layout.equipos,listaLigasString);
//        spinnerLigas.setAdapter(adaptadorLigas);
//        AdaptadorEquipos adaptadorEquipos=new AdaptadorEquipos(this,R.layout.equipos,listaEquiposString);
//        listViewEquipos.setAdapter(adaptadorEquipos);
        spinnerLigas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DescargarEquipos equipos = new DescargarEquipos();
                equipos.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        listViewEquipos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("equipo", listaEquipos.get(position).getIdEquipo());
                editor.commit();
                GuardarEquipo guardarEquipo = new GuardarEquipo();
                guardarEquipo.execute(listaEquipos.get(position).getIdEquipo());

            }
        });
    }

    private class GuardarEquipo extends AsyncTask<String, Void, Void> {
        ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(TeamActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Cargando");
            progreso.setProgress(0);
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            String idequipo = strings[0];
            SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
            String usuario=sharedPreferences.getString("usuario",null);
            Log.v("usuario",usuario);
            Log.v("equipo",idequipo);
            String ruta = "https://miguedb.000webhostapp.com/GuardarEquipo.php?nick="+usuario+"&equipo="+idequipo;
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(ruta);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
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
            Intent main = new Intent(TeamActivity.this, MainActivity.class);
            startActivity(main);
        }
    }

    private class DescargarEquipos extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObject;
        String todo;
        ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(TeamActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Cargando");
            progreso.setProgress(0);
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String ruta = "https://apiclient.besoccerapps.com/scripts/api/api.php?key=" + APIKEY + "&format=json&req=tables&league=" + listaLigas.get(spinnerLigas.getSelectedItemPosition()).getIdLiga();

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
                        todo += linea + "\n";

                    }
                    String palabra = "";
                    for (int i = 0; i < 4; i++) {
                        palabra += String.valueOf(todo.charAt(i));
                    }


                    if (palabra.compareTo("null") == 0) {
                        todo = todo.substring(4);
                    }

                    Log.v("palabra", palabra);
                    Log.v("todo", todo);

                    jsonObject = new JSONObject(todo);


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
            listaEquipos.clear();
            listaEquiposString.clear();
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("table");
                Log.v("jsonObject", jsonArray.length() + "");
                String nombre = "", idEquipo = "", escudo = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.getJSONObject(i).getString("team") != null) {
                        nombre = jsonArray.getJSONObject(i).getString("team");
                    }
                    if (jsonArray.getJSONObject(i).getString("id") != null) {
                        idEquipo = jsonArray.getJSONObject(i).getString("id");
                    }
                    if (jsonArray.getJSONObject(i).getString("shield") != null) {
                        escudo = jsonArray.getJSONObject(i).getString("shield");

                    }
                    Log.v("Nombre", nombre + " " + idEquipo + " " + escudo);
                    Equipos equipos = new Equipos(idEquipo, nombre, escudo);
                    listaEquiposString.add(nombre);
                    listaEquipos.add(equipos);
                    arrayEquipos.add(equipos);


                }
                AdaptadorEquipos adaptadorEquipos = new AdaptadorEquipos(TeamActivity.this, R.layout.equipos, listaEquiposString);
                listViewEquipos.setAdapter(adaptadorEquipos);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            progreso.dismiss();
        }
    }

    private class DescargarLigas extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObject;
        String todo;
        ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(TeamActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Cargando");
            progreso.setProgress(0);
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String ruta = "https://apiclient.besoccerapps.com/scripts/api/api.php?key=" + APIKEY + "&tz=Europe/Madrid&req=categories&filter=my_leagues&format=json";

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
                        todo += linea + "\n";

                    }
                    String palabra = "";
                    for (int i = 0; i < 4; i++) {
                        palabra += String.valueOf(todo.charAt(i));
                    }


                    if (palabra.compareTo("null") == 0) {
                        todo = todo.substring(4);
                    }


                    jsonObject = new JSONObject(todo);


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
            listaLigas.clear();
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("category");
                Log.v("jsonObject", jsonArray.length() + "");
                String nombre = "", idLiga = "", icono = "";
                for (int i = 0; i < 6; i++) {
                    if (jsonArray.getJSONObject(i).getString("year").compareTo("2023") == 0) {
                        if (jsonArray.getJSONObject(i).getString("shortName") != null) {
                            nombre = jsonArray.getJSONObject(i).getString("shortName");
                        }
                        if (jsonArray.getJSONObject(i).getString("id") != null) {
                            idLiga = jsonArray.getJSONObject(i).getString("id");
                        }
                        if (jsonArray.getJSONObject(i).getString("logo") != null) {
                            icono = jsonArray.getJSONObject(i).getString("logo");

                        }
                        Log.v("Nombre", nombre + " " + idLiga + " " + icono);
                        Ligas ligas = new Ligas(idLiga, nombre, icono);
                        listaLigasString.add(nombre);
                        listaLigas.add(ligas);
                        arrayLigas.add(ligas);
                    }

                }
                AdaptadorLigas adaptadorParaActividades = new AdaptadorLigas(TeamActivity.this, R.layout.ligas, listaLigasString);
                spinnerLigas.setAdapter(adaptadorParaActividades);


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            progreso.dismiss();
        }
    }

    private class AdaptadorEquipos extends ArrayAdapter<String> {

        public AdaptadorEquipos(@NonNull Context context, int resource, List<String> lista) {
            super(context, resource, lista);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View miFila = inflater.inflate(R.layout.equipos, parent, false);
            TextView nombre = miFila.findViewById(R.id.textView2);
            ImageView logo = miFila.findViewById(R.id.imageView2);
            Equipos equipos = listaEquipos.get(position);

            nombre.setText(equipos.getNombre());
            Picasso.get().load(equipos.getEscudo()).into(logo);
            return miFila;
        }
    }

    private class AdaptadorLigas extends ArrayAdapter<String> {

        public AdaptadorLigas(@NonNull Context context, int resource, List<String> lista) {
            super(context, resource, lista);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View miFila = inflater.inflate(R.layout.ligas, parent, false);
            TextView nombre = miFila.findViewById(R.id.textView3);
            ImageView logo = miFila.findViewById(R.id.imageView3);
            Ligas ligas = listaLigas.get(position);

            nombre.setText(ligas.getNombre());
            Picasso.get().load(ligas.getLogo()).into(logo);
            return miFila;
        }


    }

    class Ligas {
        String idLiga;
        String nombre;
        String logo;

        public Ligas(String idLiga, String nombre, String logo) {
            this.idLiga = idLiga;
            this.nombre = nombre;
            this.logo = logo;
        }

        public String getIdLiga() {
            return idLiga;
        }

        public String getNombre() {
            return nombre;
        }

        public String getLogo() {
            return logo;
        }
    }

    class Equipos {
        String idEquipo;
        String nombre;
        String escudo;

        public Equipos(String idEquipo, String nombre, String escudo) {
            this.idEquipo = idEquipo;
            this.nombre = nombre;
            this.escudo = escudo;
        }

        public String getIdEquipo() {
            return idEquipo;
        }

        public String getNombre() {
            return nombre;
        }

        public String getEscudo() {
            return escudo;
        }
    }
}