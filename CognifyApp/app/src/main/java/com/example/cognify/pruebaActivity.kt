package com.example.cognify

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar

//Por Bruno Dipietro 2023
//Activity de prueba para ver si puedo cambiar de pantalla
//Obsoleto, queda por las dudas

class pruebaActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prueba)

        //Pruebo si me llegan datos
        val keyPaciente = intent.getStringExtra("paqueteKeyPaciente")

        Snackbar.make(findViewById<View>(android.R.id.content),
            "Lei: $keyPaciente", Snackbar.LENGTH_SHORT).show()


    }
}