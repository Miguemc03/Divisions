package com.miguelangelmoreno.divisions.ui.notifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.divisions.R;
import com.example.divisions.databinding.FragmentTeamBinding;
import com.google.android.material.tabs.TabLayout;
import com.miguelangelmoreno.divisions.MyViewPagerAdapter;
import com.miguelangelmoreno.divisions.PagerAdapter;
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

public class TeamFragment extends Fragment {

    private FragmentTeamBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTeamBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    PagerAdapter myViewPagerAdapter;
    static final String APIKEY = "c6196c01e7c1d93932590f42beec9ef8";
    String idEquipo;
    TextView textViewNombre, textViewPais;
    ImageView imageViewEscudo;
    ConstraintLayout constraintLayout;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.tabLayout2);
        viewPager2 = view.findViewById(R.id.viewPager2);
        textViewNombre = view.findViewById(R.id.textView13);
        textViewPais = view.findViewById(R.id.textView14);
        imageViewEscudo = view.findViewById(R.id.imageView4);
        constraintLayout = view.findViewById(R.id.constraintLayout2);
        myViewPagerAdapter = new PagerAdapter(getActivity());
        viewPager2.setAdapter(myViewPagerAdapter);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        idEquipo = sharedPreferences.getString("equipo", "369");
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
        DescargarDatos descargarDatos = new DescargarDatos();
        descargarDatos.execute();
    }

    private class DescargarDatos extends AsyncTask<Void, Void, Void> {
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
            String ruta = "https://apiclient.besoccerapps.com/scripts/api/api.php?key=" + APIKEY + "&tz=Europe/Madrid&format=json&req=team&id=" + idEquipo + "&ext=png";

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
                    jsonObject =jsonObject2.getJSONObject("team");

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

            try {
                String nombre = "", pais = "", escudo = "", color1 = "", color2 = "";

                if (jsonObject.getString("fullName") != null) {
                    nombre = jsonObject.getString("fullName");
                }
                Log.v("nombre",nombre);
                if (jsonObject.getString("shield") != null) {
                    escudo = jsonObject.getString("shield");

                }
                if (jsonObject.getString("Name") != null) {
                    pais = jsonObject.getString("Name");

                }
                if (jsonObject.getString("color1") != null) {

                    color1 = jsonObject.getString("color1");

                }
                if (jsonObject.getString("color2") != null) {
                    color2 = jsonObject.getString("color2");

                }
                if (color1.split("#").length!=2){
                    color1="#"+color1;
                }
                if (color2.split("#").length!=2){
                    color2="#"+color2;
                }
                constraintLayout.setBackgroundColor(Color.parseColor(color1));
                textViewNombre.setText(nombre);
                textViewNombre.setTextColor(Color.parseColor(color2));
                textViewPais.setText(pais);
                textViewPais.setTextColor(Color.parseColor(color2));
                Picasso.get().load(escudo).into(imageViewEscudo);


            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }


            progreso.dismiss();
        }


    }


}