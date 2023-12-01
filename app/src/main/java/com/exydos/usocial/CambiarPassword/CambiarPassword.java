package com.exydos.usocial.CambiarPassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exydos.usocial.Login;
import com.exydos.usocial.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class CambiarPassword extends AppCompatActivity {

    TextView misCredencialesTXT, correoActualTXT, correoActual, passActualTXT, passActual;
    EditText actualPassET, nuevoPassET;
    Button cambiarPASSBTN;
    DatabaseReference USUARIOS_DE_APP;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_password);

        //TOOLBAR
        ImageView leftIcon = findViewById(R.id.left_icon);
        TextView title = findViewById(R.id.toolbar_title);
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title.setText("Cambiar Contrasena");
        //TOOLBAR

        misCredencialesTXT = findViewById(R.id.MisCredencialesTXT);
        correoActualTXT = findViewById(R.id.CorreoActualTXT);
        correoActual = findViewById(R.id.CorreoActual);
        passActualTXT = findViewById(R.id.PassActualTXT);
        passActual = findViewById(R.id.PassActual);
        actualPassET = findViewById(R.id.ActualPassET);
        nuevoPassET = findViewById(R.id.NuevoPassET);
        cambiarPASSBTN = findViewById(R.id.CAMBIARPSSBTN);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        USUARIOS_DE_APP = FirebaseDatabase.getInstance().getReference("USUARIOS_DE_APP");

        progressDialog = new ProgressDialog(CambiarPassword.this);

        CambioDeLetra();

        //CONSULTAREMOS EL CORREO Y CONTRASENA DEL USUARIO
        Query query = USUARIOS_DE_APP.orderByChild("correo").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    //OBTENEMOS LOS VALORES
                    String correo = ""+ds.child("correo").getValue();
                    String pass = ""+ds.child("pass").getValue();

                    //SETEMOS LOS DATOS EN LOS TEXTVIEW
                    correoActual.setText(correo);
                    passActual.setText(pass);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // CREAMOS EL EVENTO PARA CAMBIAR CONTRASENA
        cambiarPASSBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PASS_ANTERIOR = actualPassET.getText().toString().trim();
                String NUEVO_PASS = nuevoPassET.getText().toString().trim();

                //CREAMOS LOS SIGUIENTES CONDICIONES
                if (TextUtils.isEmpty(PASS_ANTERIOR)){
                    Toast.makeText(CambiarPassword.this, "El campo contrasena actual esta vacio", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(NUEVO_PASS)){
                    Toast.makeText(CambiarPassword.this, "El campo nueva contrasena esta vacio", Toast.LENGTH_SHORT).show();
                }
                if (!NUEVO_PASS.equals("") &&  NUEVO_PASS.length()>=6){
                    //SE EJECUTA EL METODO PARA ACTUALIZAR PASSWORD, EL CUAL RECIBE DOS PARAMETROS
                    Cambio_De_Password(PASS_ANTERIOR, NUEVO_PASS);
                } else {
                    nuevoPassET.setError("La contrasena debe ser mayor a 6 caracteristicas");
                    nuevoPassET.setFocusable(true);
                }
            }
        });
    }

    //METODO PARA CMABIAR DE CONTRASENA
    private void Cambio_De_Password(String passAnterior, String nuevoPass) {
        progressDialog.show();
        progressDialog.setTitle("Actualizando");
        progressDialog.setMessage("Espere por favor");
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), passAnterior);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user.updatePassword(nuevoPass)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        String value = nuevoPassET.getText().toString().trim();
                                        HashMap<String, Object> result = new HashMap<>();
                                        result.put("pass", value);
                                        USUARIOS_DE_APP.child(user.getUid()).updateChildren(result)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(CambiarPassword.this, "Contrasena cambiada", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                                //LUEGO SE CERRARA SESION
                                                firebaseAuth.signOut();
                                                startActivity(new Intent(CambiarPassword.this, Login.class));
                                                finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CambiarPassword.this, "La contrasena actual no es la correcta", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //PARA RETROCEDER A LA ACTIVIDAD ANTERIOR
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void CambioDeLetra(){
        //FUENTE DE LA LETRA
        String ubicacion = "fuentes/sans_medio.ttf";
        Typeface tf = Typeface.createFromAsset(CambiarPassword.this.getAssets(),ubicacion);
        //FUENTE DE LA LETRA

        misCredencialesTXT.setTypeface(tf);
        correoActualTXT.setTypeface(tf);
        correoActual.setTypeface(tf);
        passActualTXT.setTypeface(tf);
        passActual.setTypeface(tf);
        actualPassET.setTypeface(tf);
        nuevoPassET.setTypeface(tf);
        cambiarPASSBTN.setTypeface(tf);
        //FUENTE DE LETRA
    }
}