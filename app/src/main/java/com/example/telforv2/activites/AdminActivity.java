package com.example.telforv2.activites;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telforv2.Model.Model;
import com.example.telforv2.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {


    //Se necesitan globales, mas accesible

    private RecyclerView list;

    //Base de datos
    private DatabaseReference reference;
    private ProgressDialog loader;
    private String key = "";
    private String task;
    private String description;
    private String uid;
    FirebaseDatabase firebaseDatabase;
    SharedPreferences sharedPreferences;

    public static final String PREFERENCES = "prefKey";
    public static final  String IsLogIn = "islogin";
    private int seconds;
    private boolean running;
    public boolean wasRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home_activity);
        correrTiempo();

        //Boton para agregar tareas
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        list = findViewById(R.id.list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        list.setHasFixedSize(true);
        list.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);
        firebaseDatabase = getInstance();
        uid= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor pref = sharedPreferences.edit();
        pref.putBoolean(IsLogIn, true);
        pref.commit();

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        String onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Tareas").child(onlineUserID);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });
    }
    private void addTask() {
        //Metodo para agregar tareas
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.input_file,null);
        myDialog.setView(myView);

        AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final EditText task = myView.findViewById(R.id.task);
        final EditText description = myView.findViewById(R.id.description);
        TextView tiempo = findViewById(R.id.tiempo);
        Button save = myView.findViewById(R.id.saveBtn);
        Button cancel = myView.findViewById(R.id.cancelBtn);

        //Boton para cancelar la agregacion de una nueva tarea
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //Con esto se guarda la tarae dentro de la app y de la bd
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mTask = task.getText().toString().trim();
                String mDescription = description.getText().toString().trim();
                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());
                String mTime = tiempo.getText().toString().trim();

                //Verificacion para ver si la tarea esta vacia o no
                if (TextUtils.isEmpty(mTask)) {
                    task.setError("Tarea requerida");
                    return;
                }
                if (TextUtils.isEmpty(mDescription)) {
                    description.setError("Descripción requerida ");
                    return;
                } else {
                    loader.setMessage("Añadiendo los datos");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    Model model = new Model(mTask, mDescription, id, date, mTime);
                    reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(AdminActivity.this, "La actividad ha sido registrada satisfactoriamente", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(AdminActivity.this, "Algo salió mal: " + error, Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(reference, Model.class)
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
                        updateTask();

                    }
                });
            }
            @NonNull
            @Override
            public HomeActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrived_layout,parent,false);
                return new HomeActivity.MyViewHolder(view);
            }
        };

        list.setAdapter(adapter);
        adapter.startListening();
    }

    public void correrTiempo(){
        TextView tiempo = findViewById(R.id.tiempo);
        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600)/60;
                int secs= seconds % 60;
                String time = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes,secs);

                tiempo.setText(time);
                if(running){
                    seconds+=1;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void onIniciar(View view){
        running = true;
    }

    public void onDetener(View view){
        running = false;
    }

    public void onReiniciar(View view){
        running = false;
        seconds = 0;
    }

    protected void onPausa(){
        onPausa();
        wasRunning = running;
        running = false;
    }

    public void onReanudar(){
        onReanudar();
        if(wasRunning){
            running = true;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setTask(String task){
            TextView taskTextView = mView.findViewById(R.id.taskTv);
            taskTextView.setText(task);
        }
        public void setDescription(String desc){
            TextView descTextView = mView.findViewById(R.id.descriptionTv);
            descTextView.setText(desc);
        }
        public void setDate(String date){
            TextView dateTextView = mView.findViewById(R.id.dateTv);
            dateTextView.setText(date);
        }
        public void setNewTime(String time){
            TextView timeTextView = mView.findViewById(R.id.timeTv);
            timeTextView.setText(time);
        }
    }

    private void updateTask(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_data, null);
        myDialog.setView(view);

        AlertDialog dialog = myDialog.create();


        EditText mTask = view.findViewById(R.id.mEditTextTask);
        EditText mDescription = view.findViewById(R.id.mEditTextDescription);
        TextView mTime = findViewById(R.id.tiempo);


        mTask.setText(task);
        mTask.setSelection(task.length());
        mDescription.setText(description);
        mDescription.setSelection(description.length());


        Button deleteBtn = view.findViewById(R.id.btnDelete);
        Button updateBtn = view.findViewById(R.id.btnUpdate);

        //Boton para actualizar la tarea
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = mTask.getText().toString().trim();
                String description = mDescription.getText().toString().trim();
                String date = DateFormat.getDateInstance().format(new Date());
                String tiempov2 = mTime.getText().toString().trim();

                Model model = new Model(task, description, key, date, tiempov2);

                //Comprobacion de tarea vacia
                if(TextUtils.isEmpty(task)){
                    mTask.setError("Tarea requerida");
                    return;
                }if(TextUtils.isEmpty(description)){
                    mDescription.setError("Tarea requerida");
                    return;
                }else {
                    //Verifica en la bd
                    reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Si todo sale bien, se muestra un mensaje exitoso
                            if (task.isSuccessful()) {
                                Toast.makeText(AdminActivity.this, "Los datos se han actualizado correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                String err = task.getException().toString();
                                Toast.makeText(AdminActivity.this, "Los datos no se han actualizado correctamente" + err, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        //Boton para eliminar tarea
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Con esta linea se elimina dentro de la bd
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AdminActivity.this, "Tarea borrada correctamente", Toast.LENGTH_SHORT).show();
                        }else{
                            String err = task.getException().toString();
                            Toast.makeText(AdminActivity.this, "Tarea no se ha borrado correctamente" + err, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_admin,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //de esta forma se cierra sesion de la aplicacion.

        if(item.getItemId() == R.id.consultarDatos){
            Intent intent = new Intent(AdminActivity.this,ActivityNewUserList.class);
            startActivity(intent);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        }
        if (item.getItemId() == R.id.logoutAdmin) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}