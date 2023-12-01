package com.exydos.usocial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PantallaDeCarga extends AppCompatActivity {

    TextView app_name, desarrolladorestxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_de_carga);

        //TOOLBAR
        TextView title = findViewById(R.id.toolbar_title);
        ImageView leftIcon = findViewById(R.id.left_icon);
        leftIcon.setVisibility(View.GONE);
        title.setText("USOcial");
        //TOOLBAR

        app_name = findViewById(R.id.app_name);
        desarrolladorestxt = findViewById(R.id.desarrolladortxt);

        CambioDeLetra();

        //Esto se representa en segundos, que demoraraa la pantalla de carga
        final int Duracion = 2500;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Esto se ejecutara pasado los segundos que hemos establecido
                Intent intent = new Intent(PantallaDeCarga.this, Inicio.class);
                startActivity(intent);
                finish();
                // Esto nos dirige de esta actividad, al main activity
            }
        }, Duracion);
    }

    private void CambioDeLetra(){
        //FUENTE DE LA LETRA
        String ubicacion = "fuentes/sans_medio.ttf";
        Typeface tf = Typeface.createFromAsset(PantallaDeCarga.this.getAssets(),ubicacion);
        //FUENTE DE LA LETRA

        app_name.setTypeface(tf);
        desarrolladorestxt.setTypeface(tf);
    }
}