package com.exydos.usocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText correoLogin,passwordLogin;
    Button INGRESAR;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

/*
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; //AFIRMAMOS QUE EL TITULO NO ES NULO
        actionBar.setTitle("Login");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
         */

        correoLogin = findViewById(R.id.CorreoLogin);
        passwordLogin = findViewById(R.id.PasswordLogin);
        INGRESAR = findViewById(R.id.INGRESAR);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(Login.this); //INICIALIZAMOS EL PROGRESSDIALOG
        dialog = new Dialog(Login.this);// INICIAMOS EL DIALOG

        //ASIGNAMOS UN EVENTO AL BOTON INGRESAR
        INGRESAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CONVERTIMOS A STRING EL CORREO Y CONTRASENA
                String correo = correoLogin.getText().toString();
                String pass = passwordLogin.getText().toString();

                if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                    correoLogin.setError("Correo invalido");
                    correoLogin.setFocusable(true);
                } else if (pass.length()<6){
                    passwordLogin.setError("La contrasena debe ser mayor o igual  a 6 caracteres");
                    passwordLogin.setFocusable(true);
                } else {
                    //SE EJECUTA EL METODO
                    LOGINUSUARIO(correo, pass);
                }
            }
        });

    }

    //METODO PARA LOGUEAR USUARIO
    private void LOGINUSUARIO(String correo, String pass) {
        progressDialog.setCancelable(false);
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //SI SE INICIO SESION CORRECTAMENTE ENTONCES...
                        if (task.isSuccessful()){

                            progressDialog.dismiss();//el progress se cierra
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            //CUANDO INICIEMOS SESION NOS MANDE A LA ACTIVIDAD INICIO
                            startActivity(new Intent(Login.this, Inicio.class));
                            assert user != null; //Afirmamos que el usuario no es nulo, obtenemos su crreo electronico
                            Toast.makeText(Login.this, "Hola! Bienvenido(a)"+user.getEmail(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            progressDialog.dismiss();
                            //REEMPLAZAMOS EL TOAST POR EL DIALOG
                            Dialog_No_Inicio();
                            //Toast.makeText(Login.this, "Algo ha salido mal", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        //Que nos muestre el mensaje
                        Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //CREAMOS EL DIALOG PERSONALIZADO
    private void Dialog_No_Inicio(){
        Button ok_no_inicio;

        dialog.setContentView(R.layout.no_sesion); //HACEMOS LA CONEXION CON NUESTRA VISTA CREADA

        ok_no_inicio = dialog.findViewById(R.id.ok_no_inicio);

        // AL PRESIONAR EN OK, SE CERRARA EL CUADRO DE DIALOG
        ok_no_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);// AL PRESIONAR FUERA DE LA ANIMACION, ESTA  SEGUIRA MOSTRANDOSE, A MENOS QUE HAGAN CLICK EN OK;
        dialog.show();
    }

    //HABILITAMOS LA ACCION PARA RETROCEDER (IR A LA ACTIVIDAD ANTERIOR)
    @Override
    public boolean onSupportNavigateUp(){
        getSupportActionBar();
        return super.onSupportNavigateUp();
    }

    //VAMOS A CREAR UN MENSAJE PERSONALIZADO PARA CUANDO EL USUARIO NO PUEDA INICIAR SESION

}