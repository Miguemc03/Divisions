package com.miguelangelmoreno.divisions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.Toast;

import com.example.divisions.R;
import com.miguelangelmoreno.divisions.ui.home.ClasificacionFragment;
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

public class JugadoresFragment extends Fragment {
    String idEquipo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jugadores, container, false);
    }

    static final String APIKEY = "c6196c01e7c1d93932590f42beec9ef8";
    List<String> listaEquiposString = new ArrayList<>();
    List<Jugador> listaEquipos = new ArrayList<>();
    ListView listViewEquipos;
    ArrayList<Jugador> arrayEquipos = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewEquipos = view.findViewById(R.id.listViewJugadores);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        idEquipo = sharedPreferences.getString("equipo", null);
        idEquipo=idEquipo.split(" ")[0];
        DescargarEquipos descargarEquipos = new DescargarEquipos();
        descargarEquipos.execute();
    }

    private class DescargarEquipos extends AsyncTask<Void, Void, Void> {
        JSONArray jsonArray;
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
            String ruta = "https://apiclient.besoccerapps.com/scripts/api/api.php?key=" + APIKEY + "&tz=Europe/Madrid&format=json&req=team&id=" + idEquipo.split(" ")[0] + "&ext=png";

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

                    Log.v("todo", todo);
                    Log.v("palabra", palabra);
                    Log.v("todo", todo);

                    JSONObject jsonObject2 = new JSONObject(todo);
                    jsonArray = jsonObject2.getJSONObject("team").getJSONArray("squad");
                    Log.v("jsonArray", jsonArray.toString());
                    listaEquipos.clear();
                    listaEquiposString.clear();
                    try {
                        if (jsonArray.length() != 0) {


                            String nombre = "", imagen = "", numero = "";
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (jsonArray.getJSONObject(i).getString("nick") != null) {
                                    nombre = jsonArray.getJSONObject(i).getString("nick");
                                }
                                if (jsonArray.getJSONObject(i).getString("image") != null) {
                                    imagen = jsonArray.getJSONObject(i).getString("image");

                                }
                                if (jsonArray.getJSONObject(i).get("squadNumber") != null) {
                                    numero = jsonArray.getJSONObject(i).get("squadNumber") + "";

                                }
                                Jugador equipos = new Jugador(nombre, imagen, numero);
                                listaEquiposString.add(nombre);
                                listaEquipos.add(equipos);
                                arrayEquipos.add(equipos);


                            }
                        }

                    } catch (JSONException ex) {
                        throw new RuntimeException(ex);
                    }
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


            if (jsonArray.length() > 0) {
                AdaptadorEquipos adaptadorEquipos = new AdaptadorEquipos(getContext(), R.layout.jugadores, listaEquiposString);
                listViewEquipos.setAdapter(adaptadorEquipos);
            } else {
                Toast.makeText(getContext(), "No hay jugadores", Toast.LENGTH_SHORT).show();
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
            View miFila = inflater.inflate(R.layout.jugadores, parent, false);
            ImageView imagen = miFila.findViewById(R.id.imageView5);
            TextView nombre = miFila.findViewById(R.id.textView15);
            TextView numero = miFila.findViewById(R.id.textView16);

            Jugador jugador = listaEquipos.get(position);
            nombre.setText(jugador.getNombre());
            numero.setText(jugador.getNumero());
            Picasso.get().load(jugador.getImagen()).into(imagen);

            return miFila;
        }
    }

    class Jugador {
        String nombre;
        String imagen;
        String numero;


        public Jugador(String nombre, String imagen, String numero) {
            this.nombre = nombre;
            this.imagen = imagen;
            this.numero = numero;
        }

        public String getNombre() {
            return nombre;
        }

        public String getImagen() {
            return imagen;
        }

        public String getNumero() {
            return numero;
        }
    }

}