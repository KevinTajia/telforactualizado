package com.example.telforv2.Activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telforv2.Adapter.MyAdapter;
import com.example.telforv2.Model.Model;
import com.example.telforv2.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Userlist extends AppCompatActivity {

    private String key = "";
    private String task;
    private String description;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);
        //Variables


        FirebaseDatabase firebaseDatabase;
        MyAdapter myAdapter;
        ArrayList<User> list;
        String uid;

        //Buscamos el recyclerview dentro del dise;o
        recyclerView = findViewById(R.id.userList);
        firebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        String onlineUserID = mUser.getUid();

        //databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        myAdapter = new MyAdapter(this, list);
        recyclerView.setAdapter(myAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tareas").child(onlineUserID);
    }


        @Override
        protected void onStart () {
            super.onStart();
            databaseReference = FirebaseDatabase.getInstance().getReference("Tareas");
            FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                    .setQuery(databaseReference, Model.class)
                    .build();
            FirebaseRecyclerAdapter<Model, HomeActivity.MyViewHolder> adapter = new FirebaseRecyclerAdapter<Model, HomeActivity.MyViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull HomeActivity.MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Model model) {
                    holder.setDate(model.getDate());
                    holder.setTask(model.getTask());
                    holder.setDescription(model.getDescription());
                    holder.setNewTime(model.getNew_time());

                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            key = getRef(position).getKey();
                            task = model.getTask();
                            description = model.getDescription();


                        }
                    });
                }

                @NonNull
                @Override
                public HomeActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrived_layout, parent, false);
                    return new HomeActivity.MyViewHolder(view);
                }
            };
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }

    @Override
    public boolean onCreateOptionsMenu (@NonNull Menu menu){
        getMenuInflater().inflate(R.menu.main_menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item){
        //de esta forma se cierra sesion de la aplicacion.
        if (item.getItemId() == R.id.logoutAdmin) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Userlist.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}


