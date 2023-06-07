package com.example.divisions.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.divisions.LeagueFragment;
import com.example.divisions.R;
import com.example.divisions.databinding.FragmentHomeBinding;
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

public class HomeFragment extends Fragment {
    static final String APIKEY = "c6196c01e7c1d93932590f42beec9ef8";
    List<String> listaLigasString = new ArrayList<>();
    List<Ligas> listaLigas = new ArrayList<>();
    ArrayList<Ligas> arrayLigas = new ArrayList<>();
    ListView listViewligas;
    private FragmentHomeBinding binding;
    public static String LigaEscogida;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        String equipos = sharedPreferences.getString("equipo", "null");
        listViewligas = view.findViewById(R.id.listViewLigas);
        listViewligas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                LigaEscogida=listaLigas.get(position).getIdLiga();
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.gotoleagueFragment);
/*
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                fragmentManager.beginTransaction()
                        .replace(R.id.LinearReemplazar,LeagueFragment.newInstance(listaLigas.get(position).getIdLiga(), ""))
                        .addToBackStack(null)
                        .commit();

 */
            }
        });
        DescargarLigas descargarLigas = new DescargarLigas();
        descargarLigas.execute();

    }
    private class DescargarLigas extends AsyncTask<Void, Void, Void> {
        JSONObject jsonObject;
        String todo;
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
            arrayLigas.clear();
            listaLigasString.clear();
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
                AdaptadorLigas adaptadorParaActividades = new AdaptadorLigas(getContext(), R.layout.ligas, listaLigasString);
                listViewligas.setAdapter(adaptadorParaActividades);
                progreso.dismiss();

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
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


}