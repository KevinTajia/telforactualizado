package com.example.telforv2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telforv2.Model.Model;
import com.example.telforv2.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.myViewHolder> {


    Context context;

    ArrayList<Model> list;

    public MyAdapter(Context context, ArrayList<Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item,parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        Model model = list.get(position);

        holder.nombreEmpleado.setText(model.getId());
        holder.tituloTarea.setText(model.getTask());
        holder.descripcionTarea.setText(model.getDescription());
        holder.fechaTarea.setText(model.getDate());
        holder.tiempoTarea.setText(model.getNew_time());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder{

        TextView nombreEmpleado;
        TextView tituloTarea;
        TextView descripcionTarea;
        TextView fechaTarea;
        TextView tiempoTarea;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            nombreEmpleado = itemView.findViewById(R.id.nombreEmpleado);
            tituloTarea = itemView.findViewById(R.id.tituloTarea);
            descripcionTarea = itemView.findViewById(R.id.descripcionTarea);
            fechaTarea = itemView.findViewById(R.id.fechaTarea);
            tiempoTarea = itemView.findViewById(R.id.tiempoTarea);

        }
    }
}
