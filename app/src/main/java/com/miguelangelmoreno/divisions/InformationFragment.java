package com.miguelangelmoreno.divisions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class InformationFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_information, container, false);
    }

    Button buttonubi;
    TextView textViewEntrenador, textViewPresi, textViewEstadio, textViewFoundation, textViewLiga;
    ImageView imageViewLiga;
    String idEquipo;
    static final String APIKEY = "c6196c01e7c1d93932590f42beec9ef8";


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        idEquipo = sharedPreferences.getString("equipo", null);
        buttonubi = view.findViewById(R.id.buttonUbi);
        textViewEntrenador = view.findViewById(R.id.textViewEntrenador);
        textViewPresi = view.findViewById(R.id.textViewPresidente);
        textViewEstadio = view.findViewById(R.id.textViewNombreEstadio);
        textViewFoundation = view.findViewById(R.id.textViewFoundation);
        textViewLiga = view.findViewById(R.id.textViewLigaEquipo);
        imageViewLiga = view.findViewById(R.id.imageView6);
        DescargarDatos descargarDatos = new DescargarDatos();
        descargarDatos.execute();


    }

    private class DescargarDatos extends AsyncTask<Void, Void, Void> {
        JSONArray jsonArray;
        String todo;
        String ubi = "", ciudad = "",entrenador="",presi="",estadio="",fundado="",logo="",liga="";
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

                    JSONObject jsonObject2 = new JSONObject(todo);
                    jsonObject2 = jsonObject2.getJSONObject("team");

                    try {


                        if (jsonObject2.getString("address") != null) {
                            ubi = jsonObject2.getString("address");
                        }
                        if (jsonObject2.getString("city") != null) {
                            ciudad = jsonObject2.getString("city");
                        }
                        if (jsonObject2.getString("managerNow") != null) {
                            entrenador = jsonObject2.getString("managerNow");
                        }
                        if (jsonObject2.getString("chairman") != null) {
                            presi = jsonObject2.getString("chairman");
                        }
                        if (jsonObject2.getString("stadium") != null) {
                            estadio = jsonObject2.getString("stadium");
                        }if (jsonObject2.getString("yearFoundation") != null) {
                            fundado = jsonObject2.getString("yearFoundation");
                        }
                        if (jsonObject2.getJSONObject("category").getString("league_logo") != null) {
                            logo = jsonObject2.getJSONObject("category").getString("league_logo");
                        }
                        if (jsonObject2.getJSONObject("category").getString("completeNameEn") != null) {
                            liga = jsonObject2.getJSONObject("category").getString("completeNameEn");
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
            textViewEstadio.setText(estadio);
            textViewEntrenador.setText(entrenador);
            textViewPresi.setText(presi);
            textViewFoundation.setText(fundado);
            textViewLiga.setText(liga);
            Picasso.get().load(logo).into(imageViewLiga);


            buttonubi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + ubi + ", " + ciudad);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });


            progreso.dismiss();

        }


    }

}