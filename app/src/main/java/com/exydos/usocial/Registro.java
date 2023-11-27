package com.exydos.usocial;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registro extends AppCompatActivity {

    EditText Correo, Password, Nombres, Apellidos, Edad, Telefono, Direccion;
    Button RegistrarUsuario;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        /*
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; //AFIRMAMOS QUE EL TITULO NO ES NULO
        actionBar.setTitle("Registro");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
         */

        Correo = findViewById(R.id.CORREO);
        Password = findViewById(R.id.Password);
        Nombres = findViewById(R.id.NOMBRES);
        Apellidos = findViewById(R.id.APELLIDOS);
        Edad = findViewById(R.id.EDAD);
        Telefono = findViewById(R.id.TELEFONO);
        Direccion = findViewById(R.id.DIRECCION);
        RegistrarUsuario = findViewById(R.id.REGISTRARUSUARIO);

        firebaseAuth = FirebaseAuth.getInstance();

        RegistrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = Correo.getText().toString();
                String pass = Password.getText().toString();

                //validacion
                if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                    Correo.setError("Correo no valido");
                    Correo.setFocusable(true);
                } else if (pass.length()<6){
                    Password.setError("Contrasena debe ser mayor a 6");
                    Password.setFocusable(true);
                }else {
                    REGISTRAR(correo,pass);
                }
            }
        });

    }

    /*METODO  PARA REGISTRAR UN USUARIO*/
    private void REGISTRAR(String correo, String pass) {
        firebaseAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        /* si el registro es exitoso */
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //AQUI VAN LOS DATOS QUE QUEREMOS REGISTRAR
                            // DEBEMOS TOMAR EN CUENTA QUE LOS STRING DEBEN SER DIFERENTES A NUESTROS EDITTEXT

                            //PARA OBTENER EL UID
                            assert user != null; // AFIRMAMOS QUE EL USUARIO NO SEA NULO
                            String uid = user.getUid();
                            String correo = Correo.getText().toString();
                            String pass = Password.getText().toString();
                            String nombres = Nombres.getText().toString();
                            String apellidos = Apellidos.getText().toString();
                            String edad = Edad.getText().toString();
                            String telefono = Telefono.getText().toString();
                            String direccion = Direccion.getText().toString();

                            /*CREAMOS UN HASHMAP PARA MANDAR LOS DATOS A FIREBASE*/
                            HashMap<Object, String> DatosUsuario = new HashMap<>();

                            DatosUsuario.put("uid", uid);
                            DatosUsuario.put("correo", correo);
                            DatosUsuario.put("pass", pass);
                            DatosUsuario.put("nombres", nombres);
                            DatosUsuario.put("apellidos", apellidos);
                            DatosUsuario.put("edad", edad);
                            DatosUsuario.put("telefono", telefono);
                            DatosUsuario.put("direccion", direccion);

                            //LA IMAGE DE MOMENTO ESTARA VACIO
                            DatosUsuario.put("imagen", "");

                            //INICIALIZAMOS LA INSTANCIA A LA BASE DE DATOS DE FIREBASE
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //CREAMOS LA BASE DE DATOS
                            DatabaseReference reference = database.getReference("USUARIOS_DE_APP");
                            //EL NOMBRE DE LA BASE DE DATOS "NO RELACIONAL ES USUARIO_DE_APP"
                            reference.child(uid).setValue(DatosUsuario);
                            Toast.makeText(Registro.this, "Se registro exitosamente", Toast.LENGTH_SHORT).show();
                            //UNA VEZ QUE SE HA REGISTRADO, NOS MANDARA AL APARTADO INICIO
                            startActivity(new Intent(Registro.this, Inicio.class));
                        } else {
                            Toast.makeText(Registro.this, "Algo ha salido mal", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e){
                            Toast.makeText(Registro.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                });
    }

    //HABILITAMOS LA ACCION PARA RETROCEDER (IR A LA ACTIVIDAD ANTERIOR)
    @Override
    public boolean onSupportNavigateUp(){
        getSupportActionBar();
        return super.onSupportNavigateUp();
    }
}