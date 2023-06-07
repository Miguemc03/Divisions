package com.example.divisions.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.divisions.EditarBaseFragment;
import com.example.divisions.MainActivity;
import com.example.divisions.R;
import com.example.divisions.RegisterActivity;
import com.example.divisions.StartActivity;
import com.example.divisions.TeamActivity;
import com.example.divisions.databinding.FragmentHomeBinding;
import com.example.divisions.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    Button buttonUsername, buttonPassword, buttonEmail, buttonLogOut;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonUsername = view.findViewById(R.id.buttonUsername);
        buttonPassword = view.findViewById(R.id.buttonPassword);
        buttonEmail = view.findViewById(R.id.buttonEmail);
        buttonLogOut = view.findViewById(R.id.buttonLogOut);
        Log.v("Carga","Carga");


        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("inicio", false);
                editor.putString("usuario", "");
                editor.commit();
                Intent inicio = new Intent(getContext(), StartActivity.class);
                startActivity(inicio);
            }
        });
        buttonUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().
                        replace(R.id.Cambio2Layout, EditarBaseFragment.newInstance("Usuario")).
                        addToBackStack(null).
                        commit();
            }
        });
        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().
                        replace(R.id.Cambio2Layout, EditarBaseFragment.newInstance("Email")).
                        addToBackStack(null).
                        commit();
            }
        });
        buttonPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().
                        replace(R.id.Cambio2Layout, EditarBaseFragment.newInstance("Contraseña")).
                        addToBackStack(null).
                        commit();
            }
        });

    }
}