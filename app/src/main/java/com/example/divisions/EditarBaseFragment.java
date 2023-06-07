package com.example.divisions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.divisions.ui.dashboard.SettingsFragment;
import com.example.divisions.ui.home.PartidosFragment;

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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarBaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarBaseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;


    public EditarBaseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment EditarBaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditarBaseFragment newInstance(String param1) {
        EditarBaseFragment fragment = new EditarBaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editar_base, container, false);
    }

    EditText editTextCambiarDatos;
    Button buttonCambiarDatos;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextCambiarDatos = view.findViewById(R.id.editTextCambiarDatos);
        buttonCambiarDatos = view.findViewById(R.id.buttonCambiarDatos);
        editTextCambiarDatos.setHint(mParam1);
        buttonCambiarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mParam1) {
                    case "Usuario":
                        CambiarUser cambiarUser = new CambiarUser();
                        cambiarUser.execute();
                        break;
                    case "Email":
                        CambiarEmail cambiarEmail = new CambiarEmail();
                        cambiarEmail.execute();
                        break;
                    default:
                        CambiarPassword cambiarPassword = new CambiarPassword();
                        cambiarPassword.execute();
                        break;
                }
            }
        });
    }

    private class CambiarPassword extends AsyncTask<Void, Void, Void> {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        String usuario = sharedPreferences.getString("usuario", "null");
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
            Log.v("usuario", usuario);

            String ruta = "https://miguedb.000webhostapp.com/CambiarPassword.php?password=" + editTextCambiarDatos.getText() + "&usuario=" + usuario;
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(ruta);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String todo = "";
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String linea = "";
                    while ((linea = br.readLine()) != null) {
                        todo += linea + "\n";

                    }
                    Log.v("todo", todo);
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
            progreso.dismiss();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().
                    replace(R.id.Cambio2Layout, new SettingsFragment()).
                    addToBackStack(null).
                    commit();
        }
    }

    private class CambiarUser extends AsyncTask<Void, Void, Void> {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        String correo = sharedPreferences.getString("correo", "null");
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
            String ruta = "https://miguedb.000webhostapp.com/CambiarUsuario.php?password=" + editTextCambiarDatos.getText() + "&correo=" + correo;
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(ruta);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    String todo = "";
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String linea = "";
                    while ((linea = br.readLine()) != null) {
                        todo += linea + "\n";

                    }
                    Log.v("todo", todo);
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
            progreso.dismiss();
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("inicio", true);
            editor.putString("usuario", editTextCambiarDatos.getText().toString());
            editor.commit();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().
                    replace(R.id.Cambio2Layout, new SettingsFragment()).
                    addToBackStack(null).
                    commit();
        }
    }

    private class CambiarEmail extends AsyncTask<Void, Void, Void> {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        String usuario = sharedPreferences.getString("usuario", "null");
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
            String ruta = "https://miguedb.000webhostapp.com/CambiarEmail.php?password=" + editTextCambiarDatos.getText() + "&usuario=" + usuario;
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url = new URL(ruta);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String todo = "";
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String linea = "";
                    while ((linea = br.readLine()) != null) {
                        todo += linea + "\n";

                    }
                    Log.v("todo", todo);

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
            progreso.dismiss();
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("inicio", true);
            editor.putString("correo", editTextCambiarDatos.getText().toString());
            editor.commit();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().
                    replace(R.id.Cambio2Layout, new SettingsFragment()).
                    addToBackStack(null).
                    commit();
        }
    }

}