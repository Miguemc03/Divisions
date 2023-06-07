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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PartidosFragment extends Fragment {
    String idLiga;
    int jornadaActual;
    int jornadasTotales;
    ImageButton anterior, siguiente;
    TextView textViewJornada;
    Spinner spinnerFechas;
    static final String APIKEY = "c6196c01e7c1d93932590f42beec9ef8";
    List<String> listaPartidosString = new ArrayList<>();
    List<Partidos> listaPartidos = new ArrayList<>();
    ListView listViewPartidos;
    ArrayList<Partidos> arrayPartidos = new ArrayList<>();
    List<String> listaFechas = new ArrayList<>();

    public PartidosFragment(String idLiga) {
        this.idLiga = idLiga;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        anterior = view.findViewById(R.id.imageButton);
        siguiente = view.findViewById(R.id.imageButton2);
        textViewJornada = view.findViewById(R.id.textViewJornada);
        listViewPartidos = view.findViewById(R.id.listViewPartidos);
        spinnerFechas = view.findViewById(R.id.spinnerFechas);
        DescargarJornada descargarJornada = new DescargarJornada();
        descargarJornada.execute();
        anterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((jornadaActual - 1) > 0) {
                    jornadaActual = jornadaActual - 1;
                    textViewJornada.setText(jornadaActual + "");
                    DescargarFechas descargarFechas = new DescargarFechas();
                    descargarFechas.execute();
                }

            }
        });
        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((jornadaActual + 1) <= jornadasTotales) {
                    jornadaActual = jornadaActual + 1;
                    textViewJornada.setText(jornadaActual + "");
                    DescargarFechas descargarFechas = new DescargarFechas();
                    descargarFechas.execute();
                }
            }
        });
        spinnerFechas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DescargarPartidos descargarPartidos = new DescargarPartidos();
                descargarPartidos.execute(position + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_partidos, container, false);

    }

    private class DescargarFechas extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String ruta = "https://apiclient.besoccerapps.com/scripts/api/api.php?key=" + APIKEY + "&format=json&req=matchs&league=" + idLiga + "&tz=Europe/Madrid&round=" + jornadaActual;

            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(ruta);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String linea = "";
                    String todo = "";
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
                    JSONObject jsonObject = new JSONObject(todo);
                    JSONArray matchs = jsonObject.getJSONArray("match");
                    listaFechas.clear();
                    String date;
                    for (int i = 0; i < matchs.length(); i++) {

                        if (!listaFechas.contains(matchs.getJSONObject(i).getString("date"))) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                            String fecha = simpleDateFormat.format(new Date());

                            if (fecha.compareTo(matchs.getJSONObject(i).getString("date")) == 0) {
                                date = "Hoy";
                            } else {
                                date = matchs.getJSONObject(i).getString("date");
                            }
                            listaFechas.add(date);

                        }


                    }
                    for (int i = 0; i < listaFechas.size(); i++) {
                        Log.v("fechas", listaFechas.get(i));

                    }
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
            spinnerFechas.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, listaFechas));
            DescargarPartidos descargarPartidos = new DescargarPartidos();
            descargarPartidos.execute("0");
        }


    }

    private class DescargarJornada extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String ruta = "https://apiclient.besoccerapps.com/scripts/api/api.php?key=" + APIKEY + "&tz=Europe/Madrid&format=json&req=competition_info&competitions=" + idLiga;

            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(ruta);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String linea = "";
                    String todo = "";
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

                    JSONObject jsonObject = new JSONObject(todo);
                    JSONArray competitions = jsonObject.getJSONArray("competitions");
                    JSONObject competitionsObject = competitions.getJSONObject(0);
                    JSONArray phases = competitionsObject.getJSONArray("phases");
                    JSONObject phasesObject = phases.getJSONObject(0);
                    jornadaActual = Integer.parseInt(phasesObject.getString("current_round"));
                    jornadasTotales = Integer.parseInt(phasesObject.getString("total_rounds"));
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
            textViewJornada.setText(jornadaActual + "");
            DescargarFechas descargarFechas = new DescargarFechas();
            descargarFechas.execute();
            Log.v("Jornadas", jornadasTotales + "");
        }


    }

    private class DescargarPartidos extends AsyncTask<String, Void, Void> {
        ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso= new ProgressDialog(getContext());
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Cargando");
            progreso.setProgress(0);
            progreso.setCancelable(false);
            progreso.show();
        }
        @Override
        protected Void doInBackground(String... strings) {
            String posFecha = strings[0];
            String fecha = listaFechas.get(Integer.parseInt(posFecha));
            String ruta = "https://apiclient.besoccerapps.com/scripts/api/api.php?key=" + APIKEY + "&format=json&req=matchs&league=" + idLiga + "&tz=Europe/Madrid&round=" + jornadaActual;

            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(ruta);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String linea = "";
                    String todo = "";
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

                    JSONObject jsonObject = new JSONObject(todo);
                    JSONArray matchs = jsonObject.getJSONArray("match");
                    listaPartidos.clear();
                    listaPartidosString.clear();

                    String equipo1, equipo2, idPartido, escudo1, escudo2, resultado, color;
                    for (int i = 0; i < matchs.length(); i++) {
                        if (fecha.compareTo("Hoy") == 0) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                            fecha = simpleDateFormat.format(new Date());
                            Log.v("Fecha", fecha);


                        }
                        if (matchs.getJSONObject(i).getString("date").compareTo(fecha) == 0) {
                            idPartido = matchs.getJSONObject(i).getString("id");
                            equipo1 = matchs.getJSONObject(i).getString("local");
                            equipo2 = matchs.getJSONObject(i).getString("visitor");
                            escudo1 = matchs.getJSONObject(i).getString("local_shield_png");
                            escudo2 = matchs.getJSONObject(i).getString("visitor_shield_png");
                            if (matchs.getJSONObject(i).getString("status").compareTo("-1") == 0) {
                                resultado = matchs.getJSONObject(i).getString("hour") + ":" + matchs.getJSONObject(i).getString("minute");
                            } else {
                                resultado = matchs.getJSONObject(i).getString("result");
                            }
                            color = matchs.getJSONObject(i).getString("status");
                            Partidos partidos = new Partidos(idPartido, equipo1, equipo2, escudo1, escudo2, resultado, color);
                            listaPartidosString.add(idPartido);
                            listaPartidos.add(partidos);
                            arrayPartidos.add(partidos);
                        }


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
            super.onPostExecute(unused);
            AdaptadorPartidos adaptadorPartidos = new AdaptadorPartidos(getContext(), R.layout.partidos, listaPartidosString);
            listViewPartidos.setAdapter(adaptadorPartidos);
            progreso.dismiss();
        }
    }

    private class AdaptadorPartidos extends ArrayAdapter<String> {

        public AdaptadorPartidos(@NonNull Context context, int resource, List<String> lista) {
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
            View miFila = inflater.inflate(R.layout.partidos, parent, false);
            TextView nombre1 = miFila.findViewById(R.id.textViewEquipo1);
            TextView nombre2 = miFila.findViewById(R.id.textViewEquipo2);
            ImageView escudo1 = miFila.findViewById(R.id.imageViewEquipo1);
            ImageView escudo2 = miFila.findViewById(R.id.imageViewEquipo2);
            TextView resultado = miFila.findViewById(R.id.textViewResultado);

            Partidos partidos = listaPartidos.get(position);
            Picasso.get().load(partidos.getEscudo1()).into(escudo1);
            Picasso.get().load(partidos.getEscudo2()).into(escudo2);

            nombre1.setText(partidos.getNombreEquipo1());
            nombre2.setText(partidos.getNombreEquipo2());
            resultado.setText(partidos.getResultado());
            switch (partidos.getColor()) {
                case "-1":
                    resultado.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    break;
                case "0":
                    resultado.setTextColor(ContextCompat.getColor(getContext(), R.color.verde));
                    break;
                default:
                    resultado.setTextColor(ContextCompat.getColor(getContext(), R.color.rojo));
                    break;
            }


            return miFila;
        }
    }


    class Partidos {
        String idPartido;
        String nombreEquipo1;
        String nombreEquipo2;
        String escudo1;
        String escudo2;
        String resultado;
        String color;

        public Partidos(String idPartido, String nombreEquipo1, String nombreEquipo2, String escudo1, String escudo2, String resultado, String color) {
            this.idPartido = idPartido;
            this.nombreEquipo1 = nombreEquipo1;
            this.nombreEquipo2 = nombreEquipo2;
            this.escudo1 = escudo1;
            this.escudo2 = escudo2;
            this.resultado = resultado;
            this.color = color;
        }

        public String getIdPartido() {
            return idPartido;
        }

        public String getNombreEquipo1() {
            return nombreEquipo1;
        }

        public String getNombreEquipo2() {
            return nombreEquipo2;
        }

        public String getEscudo1() {
            return escudo1;
        }

        public String getEscudo2() {
            return escudo2;
        }

        public String getResultado() {
            return resultado;
        }

        public String getColor() {
            return color;
        }
    }
}
