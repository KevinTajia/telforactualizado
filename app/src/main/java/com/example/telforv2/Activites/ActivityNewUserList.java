package com.example.telforv2.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telforv2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.annotation.Nullable;

public class ActivityNewUserList extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle SavedInstance) {
        super.onCreate(SavedInstance);
        setContentView(R.layout.new_user_list);

        //Variables
        TextView lista;
        DatabaseReference databaseReference;

        //Busca el textView asociado
        lista = findViewById(R.id.listTv);

        //Referenciamos la bd y al nodo de la bd
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tareas");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Si existen datos va a obtener el valor y los hara una cadena de caracteres
                if(dataSnapshot.exists()){
                    String datos = dataSnapshot.getValue().toString();
                    //Pone la informacion en el textView llamado lista.
                    lista.setText(datos);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_admin,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //de esta forma se cierra sesion de la aplicacion.
        if (item.getItemId() == R.id.logoutAdmin) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ActivityNewUserList.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
