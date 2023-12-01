package com.exydos.usocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exydos.usocial.Opciones.Mis_Datos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Inicio extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference BASE_DE_DATOS;
    ImageView foto_perfil;
    TextView bienvenidoTXT, uidTXT, correoTXT, uidPerfil, correoPerfil, nombresPerfil, fecha;

    Button CerrarSesion, misDatosOpcion, crearPublicacionOpcion, publicacionesOpcion, usuarioOpcion, chatsOpcion;
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

        firebaseDatabase = FirebaseDatabase.getInstance(); // INICIALIZAMOS LA INSTANCIA
        BASE_DE_DATOS = firebaseDatabase.getReference("USUARIOS_DE_APP");

        fecha = findViewById(R.id.fecha);
        foto_perfil = findViewById(R.id.foto_perfil);
        uidPerfil = findViewById(R.id.uidPerfil);
        correoPerfil = findViewById(R.id.correoPerfil);
        nombresPerfil = findViewById(R.id.nombresPerfil);
        bienvenidoTXT = findViewById(R.id.Bienvenidotxt);
        uidTXT = findViewById(R.id.uidtxt);
        correoTXT = findViewById(R.id.correotxt);

        //OPCIONES
        misDatosOpcion = findViewById(R.id.MisDatosOpcion);
        crearPublicacionOpcion = findViewById(R.id.CrearPublicacionOpcion);
        publicacionesOpcion = findViewById(R.id.PublicacionesOpcion);
        usuarioOpcion = findViewById(R.id.UsuarioOpcion);
        chatsOpcion = findViewById(R.id.ChatsOpcion);
        CerrarSesion = findViewById(R.id.CerrarSesion);
        //OPCIONES

        CambioDeLetra();

        //FECHA
        Date date = new Date();
        SimpleDateFormat fechaC = new SimpleDateFormat("d 'de' MMMM 'del' yyyy");
        String sFecha = fechaC.format(date);
        fecha.setText(sFecha);
        //FECHA

        //EVENTO VER DATOS
        misDatosOpcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inicio.this, Mis_Datos.class);
                startActivity(intent);
                Toast.makeText(Inicio.this, "Nis Datos", Toast.LENGTH_SHORT).show();
            }
        });

        //EVENTO CERRAR SESION
        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CREAMOS UN METODO PARA CERRAR SESION
                CerrarSesion();
            }
        });
    }

    //Metodo para cambiar la letra
    private void CambioDeLetra(){
        //FUENTE DE LA LETRA
        String ubicacion = "fuentes/sans_medio.ttf";
        Typeface tf = Typeface.createFromAsset(Inicio.this.getAssets(),ubicacion);
        //FUENTE DE LA LETRA

        fecha.setTypeface(tf);
        bienvenidoTXT.setTypeface(tf);
        uidTXT.setTypeface(tf);
        correoTXT.setTypeface(tf);
        correoPerfil.setTypeface(tf);
        uidPerfil.setTypeface(tf);
        nombresPerfil.setTypeface(tf);

        //CAMBIO DE LETRA A BOTONES
        CerrarSesion.setTypeface(tf);
        misDatosOpcion.setTypeface(tf);
        crearPublicacionOpcion.setTypeface(tf);
        publicacionesOpcion.setTypeface(tf);
        usuarioOpcion.setTypeface(tf);
        chatsOpcion.setTypeface(tf);
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
            CargarDatos();
            Toast.makeText(this, "Se ha iniciado sesion", Toast.LENGTH_SHORT).show();
        }
        //CASO CONTRARIO NOS DIRIGE AAL MAIN ACTIVITY
        else {
            startActivity(new Intent(Inicio.this, MainActivity.class));
        }
    }

    // CREAMOS UN METODO PARA RECUERAR LOS DATOS DE FIREBASE, DEL USUARIO ACTUAL
    private void CargarDatos(){
        Query query = BASE_DE_DATOS.orderByChild("correo").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //RECORREMOS LOS UAURIOS REGISTRADOS EN LA BASE DE DATOS, HASTA ENCONTRAR EL USUARIO ACTUAL
                for (DataSnapshot ds : snapshot.getChildren()){

                    //OBTENEMOS LOS VALORES
                    String uid = ""+ds.child("uid").getValue();
                    String correo = ""+ds.child("correo").getValue();
                    String nombres = ""+ds.child("nombres").getValue();
                    String imagen = ""+ds.child("imagen").getValue();

                    // SETEMOS LOS DATOS EN NUESTRAS VIDAS
                    uidPerfil.setText(uid);
                    correoPerfil.setText(correo);
                    nombresPerfil.setText(nombres);

                    //DECLARAMOS UN TRY CATCH, PARA GESTIONAR NUESTRA FOTO DE PERFIL
                    try {
                        // SI EXISTE UNA IMAGEN EN LA BASE DE DATOS, DEL USUARIO ACTUAL
                        Picasso.get().load(imagen).placeholder(R.drawable.img_perfil).into(foto_perfil);
                    } catch (Exception e){
                        //SI EL USUARIO NO CUENTA CON UNA IMAGEN EN LA BASE DE DATOS
                        Picasso.get().load(R.drawable.img_perfil).into(foto_perfil);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //METODO CERRAR SESION
    private void CerrarSesion(){
        firebaseAuth.signOut(); // CIERRA SESION DEL USUARIO ACTIVO ACTUALMENTE EN NUESTRA APP
        Toast.makeText(this, "Ha cerrado sesion", Toast.LENGTH_SHORT).show();
        //LUEGO DE CERRAR SESION QUE NOS DIRIGA AL MAIN ACTIVITY
        startActivity(new Intent(Inicio.this, MainActivity.class));
        finish();
    }
}