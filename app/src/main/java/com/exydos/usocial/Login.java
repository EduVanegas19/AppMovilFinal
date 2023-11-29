package com.exydos.usocial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    EditText correoLogin,passwordLogin;
    Button INGRESAR, INGRESARGOOGLE;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;

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
        INGRESARGOOGLE = findViewById(R.id.INGRESARGOOGLE);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(Login.this); //INICIALIZAMOS EL PROGRESSDIALOG
        dialog = new Dialog(Login.this);// INICIAMOS EL DIALOG

        /* PASO 1.  CREAMOS LA SOLICITUD */
        crearSolicitud();

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

        // EVENTO AL PRESIONAR EN BOTON DE LOGIN GOOGLE
        INGRESARGOOGLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singIn();
            }
        });

    }

    /* SOLICITUD */
    private void crearSolicitud(){
        //CONFIGURAMOS EL INICIO DE SESION DE GOOGLE
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //CREAMOS UN GOOGLESIGINCLIENT CON LAS OPCIONES ESPECIFICADAS POR GSO
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    //PASO 2 -- Crear la pantalla de google
    private void singIn(){
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //resultado devuelto al iniciar la intencion desde googleSignInApp.getSignIntent
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //el inicio de sesion fue exitoso, autentifique con FIREBASE
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AutentificacionFirebase(account);// aqui se ejecuta el metodo para logearnos con Google
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //METODO PARA AUTENTIFICAR CON FIREBASE - GOOGLE
    private void AutentificacionFirebase(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //SI INICIO CORRECTAMENTE
                            FirebaseUser user = firebaseAuth.getCurrentUser(); // OBTENEMOS AL USUARIO, EL CUAL QUIERE INICIAR SESION

                            if (task.getResult().getAdditionalUserInfo().isNewUser()){
                                String uid = user.getUid();
                                String correo = user.getEmail();
                                String nombre = user.getDisplayName();

                                // AQUI PASAMOS LOS PARAMETROS
                                HashMap<Object, String> DatosUsuario = new HashMap<>();

                                DatosUsuario.put("uid", uid);
                                DatosUsuario.put("correo", correo);
                                //DatosUsuario.put("pass", pass); //Comentamos contrasena ya que se agregar con Google
                                DatosUsuario.put("nombres", nombre);
                                //DatosUsuario.put("apellidos", apellidos);
                                DatosUsuario.put("edad", "");
                                DatosUsuario.put("telefono", "");
                                DatosUsuario.put("direccion", "");

                                //LA IMAGE DE MOMENTO ESTARA VACIO
                                DatosUsuario.put("imagen", "");

                                //INICIALIZAMOS LA INSTANCIA A LA BASE DE DATOS DE FIREBASE
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                //CREAMOS LA BASE DE DATOS
                                DatabaseReference reference = database.getReference("USUARIOS_DE_APP");
                                //EL NOMBRE DE LA BASE DE DATOS "NO RELACIONAL ES USUARIO_DE_APP"
                                reference.child(uid).setValue(DatosUsuario);
                            }

                            // AHORA NOS DIRIGE A LA ACTIVIDAD "INICIO"
                            startActivity(new Intent(Login.this, Inicio.class));
                        }
                        else{
                            Dialog_No_Inicio();
                        }
                    }
                });
    }

    //METODO PARA LOGUEAR USUARIO
    private void LOGINUSUARIO(String correo, String pass) {
        progressDialog.setTitle("Ingresando");
        progressDialog.setMessage("Espere por favor...");
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