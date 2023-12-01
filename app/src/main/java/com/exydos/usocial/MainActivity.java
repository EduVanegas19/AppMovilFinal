package com.exydos.usocial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button loginBTN, registrarBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TOOLBAR
        TextView title = findViewById(R.id.toolbar_title);
        ImageView leftIcon = findViewById(R.id.left_icon);
        leftIcon.setVisibility(View.GONE);
        title.setText("USOcial");
        //TOOLBAR

        loginBTN = findViewById(R.id.LoginBTN);
        registrarBTN = findViewById(R.id.RegistrarBTN);

        CambioDeLetra();

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        registrarBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Registro.class));
            }
        });
    }

    private void CambioDeLetra(){
        //FUENTE DE LA LETRA
        String ubicacion = "fuentes/sans_medio.ttf";
        Typeface tf = Typeface.createFromAsset(MainActivity.this.getAssets(),ubicacion);
        //FUENTE DE LA LETRA

        loginBTN.setTypeface(tf);
        registrarBTN.setTypeface(tf);
    }
}