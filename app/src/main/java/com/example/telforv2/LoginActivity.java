package com.example.telforv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    public static final String PREFERENCES = "prefKey";


    SharedPreferences sharedPreferences;
    StorageReference reference;
    FirebaseFirestore firebaseFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        reference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //Se realiza una comprobacion a la bd para verificar que exista un usuario
                FirebaseUser user = mAuth.getCurrentUser();
                //Si el usuario es encontrado, se va a ejecutar una nueva actividad.
                if (user!= null){
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        //Si el usuario no tiene una cuenta, le da click al edit text y lo llevara a la otra actividad
        findViewById(R.id.no_tienes_acceso).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        //variables para el boton de login, textview en caso de olvidar contrase;a
        //edit text del usuario y la contrase;a
        Button loginBtn;
        TextView forgetPassword;
        EditText userEmail, userPassword;

        userEmail = findViewById(R.id.user_email);
        userPassword = findViewById(R.id.user_password);
        loginBtn = findViewById(R.id.login_btn);
        forgetPassword = findViewById(R.id.forget_password);
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();
                //Se realiza comprobacion de que los campos no esten vacios, si lo estan
                //mostraran un mensaje.
                if (TextUtils.isEmpty(email)) {
                    userEmail.setError("Por favor, llene los campos");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    userPassword.setError("Por favor, llene los campos");
                    return;
                } else {
                    //Primero se realiza una verificacion a la bd para ver si el usuario existe.
                    // Si el usuario existe dentro de la bd, entrara a la nueva actividad e iniciara sesion, ademas mostrara un mensaje.
                    //Si no, mostrara un mensaje de usuario no encontrado.
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "!Bienvenido!", Toast.LENGTH_SHORT).show();
                                startActivity((new Intent(LoginActivity.this, HomeActivity.class)));
                                verifyEmail();
                            }if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                //Esto lo que hace es verificar que el usuario ingrese un correo elecctronico valido.
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                }else{
                    Toast.makeText(LoginActivity.this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Es para el textview de olvidar contrase;a, si le da click lo manda a la actividad correspondiente
        forgetPassword.findViewById(R.id.forget_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });
    }

    private void verifyEmail() {
        //Este es el formato que se le da a los datos dentro de la bd
        String Name = "nameKey";
        String Email = "emailKey";
        String Password = "passwordKey";
        String Matricula = "matriculaKey";
        FirebaseUser user = mAuth.getCurrentUser();

        assert user != null;
        if(user.isEmailVerified()){
            //Si el usuario esta verificado, se relacionan los datos.
            String name = sharedPreferences.getString(Name, null);
            String password = sharedPreferences.getString(Password, null);
            String email = sharedPreferences.getString(Email, null);
            String matricula = sharedPreferences.getString(Matricula, null);

            //Si los datos no estan vacios, se mandan directamente a la base de datos
            if(name !=null && password !=null && email !=null && matricula!=null){
                String uid = mAuth.getUid();
                Map<String,String> map = new HashMap<>();
                map.put("name",name);
                map.put("email",email);
                map.put("password",password);
                map.put("matricula",matricula);
                map.put("uid",uid);

                //Los usuarios se van a almacenar dentro de la bd como "Usuarios"
                firebaseFirestore.collection("Usuarios")
                        .document(uid)
                        .set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            //Si todo sale bien, va a haber un login al sistema., si no el sistema mandara un mensaje diciendo que los datos no guardados.
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    finish();

                                } else {
                                    Toast.makeText(LoginActivity.this, "Datos no guardados", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }

            startActivity(new Intent(LoginActivity.this, HomeActivity.class ));
            finish();

        }else{
            mAuth.signOut();
            Toast.makeText(LoginActivity.this, "Por favor, verifica tu correo", Toast.LENGTH_SHORT).show();
        }
    }
}