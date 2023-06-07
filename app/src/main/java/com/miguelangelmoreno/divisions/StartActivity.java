package com.miguelangelmoreno.divisions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.divisions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class StartActivity extends AppCompatActivity {
Button buttonRegister,buttonLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();
        registrarDispositivo();
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
    private void registrarDispositivo() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        SharedPreferences sharedPreferences = getSharedPreferences("Mis preferencias", Context.MODE_PRIVATE);
                        String tokenGuardado = sharedPreferences.getString("DEVICEID", null);
                        Log.v("token",token);
                        Log.v("tokenGuardado",tokenGuardado+"");
                        if (token != null) {
                            if (tokenGuardado == null || !token.equals(tokenGuardado)) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("DEVICEID", token);
                                editor.commit();
                            }
                        }
                        // Log and toast
                        Log.d(ContentValues.TAG, token);

                    }
                });
    }
}