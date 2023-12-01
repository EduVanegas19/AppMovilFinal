package com.exydos.usocial.Opciones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.exydos.usocial.CambiarPassword.CambiarPassword;
import com.exydos.usocial.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Mis_Datos extends AppCompatActivity {

    ImageView imagenDato;
    TextView uidDato, nombresDato, apellidosDato, correoDato, passwordDato, edadDato, direccionDato, telefonoDato;
    TextView misDatosTXT, uidDatoTXT, nombresDatoTXT, apellidosDatoTXT, correoDatoTXT, passwordDatoTXT, edadDatoTXT, direccionDatoTXT, telefonoDatoTXT;
    Button actualizar, actualizarPass;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    DatabaseReference BASE_DE_DATOS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_datos);

        //TOOLBAR
        ImageView leftIcon = findViewById(R.id.left_icon);
        TextView title = findViewById(R.id.toolbar_title);
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title.setText("Mi Perfil");
        //TOOLBAR

        misDatosTXT = findViewById(R.id.MisDatosTXT);
        imagenDato = findViewById(R.id.ImagenDato);
        uidDato = findViewById(R.id.UidDato);
        nombresDato = findViewById(R.id.NombresDato);
        apellidosDato = findViewById(R.id.ApellidoDato);
        correoDato = findViewById(R.id.CorreoDato);
        passwordDato = findViewById(R.id.PasswordDato);
        edadDato = findViewById(R.id.EdadDato);
        direccionDato = findViewById(R.id.DireccionDato);
        telefonoDato = findViewById(R.id.TelefonoDato);
        //TXT
        uidDatoTXT = findViewById(R.id.UidDatoTXT);
        nombresDatoTXT = findViewById(R.id.NombresDatoTXT);
        apellidosDatoTXT = findViewById(R.id.ApellidoDatoTXT);
        correoDatoTXT = findViewById(R.id.CorreoDatoTXT);
        passwordDatoTXT = findViewById(R.id.PasswordDatoTXT);
        edadDatoTXT = findViewById(R.id.EdadDatoTXT);
        direccionDatoTXT = findViewById(R.id.DireccionDatoTXT);
        telefonoDatoTXT = findViewById(R.id.TelefonoDatoTXT);

        actualizar = findViewById(R.id.Actualizar);
        actualizarPass = findViewById(R.id.ActualizarPass);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        BASE_DE_DATOS = FirebaseDatabase.getInstance().getReference("USUARIOS_DE_APP");

        CambioDeLetra(); // LLAMAMOS EL PROCEDIMIENTO PARA CAMBIAR LA FUENTE DE LETRA

        //OBTENEMOS LOS DATOS DEL USUARIO
        BASE_DE_DATOS.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //SI EL USUARIO EXISTE
                if (snapshot.exists()){

                    //OBTENEMOS LOS DATOS DE FIREBASE
                    //LOS DATOS SE RESCANTAN TAL CUAL FUERON REGISTRASOS
                    String uid = ""+snapshot.child("uid").getValue();
                    String nombres = ""+snapshot.child("nombres").getValue();
                    String apellidos = ""+snapshot.child("apellidos").getValue();
                    String correo = ""+snapshot.child("correo").getValue();
                    String password = ""+snapshot.child("pass").getValue();
                    String direccion = ""+snapshot.child("direccion").getValue();
                    String edad = ""+snapshot.child("edad").getValue();
                    String telefono = ""+snapshot.child("telefono").getValue();
                    String imagen = ""+snapshot.child("imagen").getValue();

                    //AHORA SETEAMOS LOS DATOS EN LOS TEXTIEW E IMAGEVIEW
                    uidDato.setText(uid);
                    nombresDato.setText(nombres);
                    apellidosDato.setText(apellidos);
                    correoDato.setText(correo);
                    passwordDato.setText(password);
                    direccionDato.setText(direccion);
                    edadDato.setText(edad);
                    telefonoDato.setText(telefono);

                    //PARA OBTENER IMAGEN HACEMOS LO SIGUIENTE
                    try {
                        // SI EXISTE IMAGEN
                        Picasso.get().load(imagen).placeholder(R.drawable.img_perfil).into(imagenDato);
                    } catch (Exception e){
                        //SI NO EXISTE IMAGEN
                        Picasso.get().load(R.drawable.img_perfil).into(imagenDato);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        actualizarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NOS MANDARA A CAMBIAR LA CONTRASENA
                startActivity(new Intent(Mis_Datos.this, CambiarPassword.class));
            }
        });
    }

    private void CambioDeLetra(){
        //FUENTE DE LA LETRA
        String ubicacion = "fuentes/sans_medio.ttf";
        Typeface tf = Typeface.createFromAsset(Mis_Datos.this.getAssets(),ubicacion);
        //FUENTE DE LA LETRA

        misDatosTXT.setTypeface(tf);
        uidDatoTXT.setTypeface(tf);
        nombresDatoTXT.setTypeface(tf);
        apellidosDatoTXT.setTypeface(tf);
        correoDatoTXT.setTypeface(tf);
        passwordDatoTXT.setTypeface(tf);
        edadDatoTXT.setTypeface(tf);
        direccionDatoTXT.setTypeface(tf);
        telefonoDatoTXT.setTypeface(tf);
        uidDato.setTypeface(tf);
        nombresDato.setTypeface(tf);
        apellidosDato.setTypeface(tf);
        correoDato.setTypeface(tf);
        passwordDato.setTypeface(tf);
        edadDato.setTypeface(tf);
        direccionDato.setTypeface(tf);
        telefonoDato.setTypeface(tf);

        actualizar.setTypeface(tf);
        actualizarPass.setTypeface(tf);
    }

    //HABILITAMOS LA ACCION PARA RETROCEDER (IR A LA ACTIVIDAD ANTERIOR)
    @Override
    public boolean onSupportNavigateUp(){
        getSupportActionBar();
        return super.onSupportNavigateUp();
    }
}