package com.miguelangelmoreno.divisions.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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


public class ClasificacionFragment extends Fragment {
    String idLiga;
    static final String APIKEY = "c6196c01e7c1d93932590f42beec9ef8";
    List<String> listaEquiposString = new ArrayList<>();
    List<Clasificacion> listaEquipos = new ArrayList<>();
    ListView listViewEquipos;
    ArrayList<Clasificacion> arrayEquipos = new ArrayList<>();

    public ClasificacionFragment(String idLiga) {
        this.idLiga = idLiga;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clasificacion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewEquipos = view.findViewById(R.id.ListViewClasificacion);
        DescargarEquipos equipos = new DescargarEquipos();
        equipos.execute();
    }

    private class DescargarEquipos extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObject;
        String todo;

        ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(getContext());
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Cargando");
            progreso.setProgress(0);
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String ruta = "https://apiclient.besoccerapps.com/scripts/api/api.php?key=" + APIKEY + "&format=json&req=tables&league=" + idLiga;

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
            listaEquipos.clear();
            listaEquiposString.clear();
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("table");
                String nombre = "", pos = "", escudo = "", dG = "", puntos = "", pJ = "", mark = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.getJSONObject(i).getString("team") != null) {
                        nombre = jsonArray.getJSONObject(i).getString("team");
                    }
                    if (jsonArray.getJSONObject(i).getString("points") != null) {
                        puntos = jsonArray.getJSONObject(i).getString("points");
                    }
                    if (jsonArray.getJSONObject(i).getString("shield") != null) {
                        escudo = jsonArray.getJSONObject(i).getString("shield");

                    }
                    if (jsonArray.getJSONObject(i).getString("pos") != null) {
                        pos = jsonArray.getJSONObject(i).getString("pos");

                    }
                    if (jsonArray.getJSONObject(i).getString("avg") != null) {

                        dG = jsonArray.getJSONObject(i).getString("avg");

                    }
                    if (jsonArray.getJSONObject(i).getString("round") != null) {
                        pJ = jsonArray.getJSONObject(i).getString("round");

                    }
                    if (jsonArray.getJSONObject(i).get("mark") != null) {
                        mark = jsonArray.getJSONObject(i).get("mark") + "";

                    }
                    Clasificacion equipos = new Clasificacion(pos, nombre, escudo, pJ, dG, puntos, mark);
                    listaEquiposString.add(nombre);
                    listaEquipos.add(equipos);
                    arrayEquipos.add(equipos);


                }
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {

            AdaptadorEquipos adaptadorEquipos = new AdaptadorEquipos(getContext(), R.layout.clasificacion, listaEquiposString);
            listViewEquipos.setAdapter(adaptadorEquipos);

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
            View miFila = inflater.inflate(R.layout.clasificacion, parent, false);
            TextView posicion = miFila.findViewById(R.id.textViewPos);
            TextView nombre = miFila.findViewById(R.id.textViewNombreEquipo);
            ImageView logo = miFila.findViewById(R.id.imageViewEscudo);
            TextView pJ = miFila.findViewById(R.id.textViewJugados);
            TextView dG = miFila.findViewById(R.id.textViewDG);
            TextView puntos = miFila.findViewById(R.id.textViewPuntos);

            Clasificacion equipos = listaEquipos.get(position);

            nombre.setText(equipos.getNombre());
            posicion.setText(equipos.getPosicion());
            Log.v("Mark", equipos.getMark());
            switch (equipos.getMark()) {
                case "1":
                    posicion.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.azulOscuro));
                    posicion.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    break;
                case "2":
                    posicion.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.azul));
                    posicion.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    break;
                case "3":
                    posicion.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.naranja));
                    posicion.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    break;
                case "5":
                    posicion.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.verde));
                    posicion.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    break;
                case "6":
                    posicion.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.rojo));
                    posicion.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    break;
                case "7":
                    posicion.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.rojo));
                    posicion.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    break;
                default:
                    break;
            }

            pJ.setText(equipos.getPartidosJugados());
            dG.setText(equipos.getDiferenciaGoles());
            puntos.setText(equipos.getPuntos());
            Picasso.get().load(equipos.getEscudo()).into(logo);
            return miFila;
        }
    }

    class Clasificacion {
        String posicion;
        String nombre;
        String escudo;
        String partidosJugados;
        String diferenciaGoles;
        String puntos;
        String mark;

        public Clasificacion(String posicion, String nombre, String escudo, String partidosJugados, String diferenciaGoles, String puntos, String mark) {
            this.posicion = posicion;
            this.nombre = nombre;
            this.escudo = escudo;
            this.partidosJugados = partidosJugados;
            this.diferenciaGoles = diferenciaGoles;
            this.puntos = puntos;
            this.mark = mark;
        }

        public String getPosicion() {
            return posicion;
        }

        public String getNombre() {
            return nombre;
        }

        public String getEscudo() {
            return escudo;
        }

        public String getPartidosJugados() {
            return partidosJugados;
        }

        public String getDiferenciaGoles() {
            return diferenciaGoles;
        }

        public String getPuntos() {
            return puntos;
        }

        public String getMark() {
            return mark;
        }
    }
}