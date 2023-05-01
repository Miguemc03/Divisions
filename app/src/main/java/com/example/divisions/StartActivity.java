package com.example.divisions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
Button buttonRegister,buttonLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();
        buttonRegister=findViewById(R.id.buttonRegister);
        buttonLogin=findViewById(R.id.buttonLogin);
        SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
        Boolean inicio = sharedPreferences.getBoolean("inicio",false);
        if (inicio){
            Intent intent = new Intent(StartActivity.this,MainActivity.class);
            startActivity(intent);
        }
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("inicio",true);
                editor.commit();*/
                Intent register = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(register);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("inicio",true);
                editor.commit();*/
                Intent login = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(login);
            }
        });
    }
}