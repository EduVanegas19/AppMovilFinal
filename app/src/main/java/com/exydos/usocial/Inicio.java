package com.exydos.usocial;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Inicio extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Button CerrarSesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        /*
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; //AFIRMAMOS QUE EL TITULO NO ES NULO
        actionBar.setTitle("Inicio");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
         */
        
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        CerrarSesion = findViewById(R.id.CerrarSesion);

        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CREAMOS UN METODO PARA CERRAR SESION
                CerrarSesion();
            }
        });
    }

    //LO QUE HAREMOS A COTINUACION SERA DELARAR LA CLASE "INICIO COMO LA PRINCIPAL"
    //PERO COMO BIEN SABEMOS LA PRIMERA ACTIVIDAD EN EJECUTAR ES LA PANTALLA DE CARGA
    @Override
    protected void onStart(){
        //AQUI LLAMAMOS AL METODO PARA QUE SE EJECUTE CUANDO INICIE LA ACTIVIDAD
        VerificacionInicioSesion();
        super.onStart();
    }
    
    //CREAMOS UN METODO QUE NOS PERMITA VERIFICAR SI EL USUARIO YA HA INICIADO SESION PREVIAMENTE
    private void VerificacionInicioSesion(){
        //SI EL USUARIO HA INICIADO SESION NOS DIRIGE DIRECTAMENTE A ESTA ACTIVIDAD
        if (firebaseUser != null){
            Toast.makeText(this, "Se ha iniciado sesion", Toast.LENGTH_SHORT).show();
        }
        //CASO CONTRARIO NOS DIRIGE AAL MAIN ACTIVITY
        else {
            startActivity(new Intent(Inicio.this, MainActivity.class));
            finish();
        }
    }

    //METODO CERRAR SESION
    private void CerrarSesion(){
        firebaseAuth.signOut(); // CIERRA SESION DEL USUARIO ACTIVO ACTUALMENTE EN NUESTRA APP
        Toast.makeText(this, "Ha cerrado sesion", Toast.LENGTH_SHORT).show();
        //LUEGO DE CERRAR SESION QUE NOS DIRIGA AL MAIN ACTIVITY
        startActivity(new Intent(Inicio.this, MainActivity.class));
    }
}