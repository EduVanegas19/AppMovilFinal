package com.exydos.usocial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class PantallaDeCarga extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_de_carga);

        //Esto se representa en segundos, que demoraraa la pantalla de carga
        final int Duracion = 2500;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Esto se ejecutara pasado los segundos que hemos establecido
                Intent intent = new Intent(PantallaDeCarga.this, Inicio.class);
                startActivity(intent);
                // Esto nos dirige de esta actividad, al main activity
            }
        }, Duracion);
    }
}