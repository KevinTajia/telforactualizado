package com.example.telforv2.activites;

import android.os.Bundle;

import androidx.annotation.NonNull;


public class Cronometro extends HomeActivity {

    public int seconds;
    public boolean running;
    public boolean wasRunning;

    protected void onPausa(){
        super.onPausa();
        wasRunning = running;
        running = false;
    }

    public void onReanudar(){
        super.onReanudar();
        if(wasRunning){
            running = true;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("segundos",seconds);
        outState.putBoolean("corriendo",running);
        outState.putBoolean("Estaba corriendo",wasRunning);
    }

}
