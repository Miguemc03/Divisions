package com.miguelangelmoreno.divisions.ui.dashboard;

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
import androidx.navigation.fragment.NavHostFragment;

import com.example.divisions.databinding.FragmentSettingsBinding;
import com.miguelangelmoreno.divisions.EditarBaseFragment;
import com.example.divisions.R;
import com.miguelangelmoreno.divisions.StartActivity;
import com.miguelangelmoreno.divisions.ui.home.HomeFragment;

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
                Bundle bundle = new Bundle();
                bundle.putString("param","Usuario");
                NavHostFragment.findNavController(SettingsFragment.this).navigate(R.id.action_navigation_dashboard_to_editarBaseFragment,bundle);
            }
        });
        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("param","Email");
                NavHostFragment.findNavController(SettingsFragment.this).navigate(R.id.action_navigation_dashboard_to_editarBaseFragment,bundle);
            }
        });
        buttonPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("param","Contraseña");
                NavHostFragment.findNavController(SettingsFragment.this).navigate(R.id.action_navigation_dashboard_to_editarBaseFragment,bundle);
            }
        });

    }
}