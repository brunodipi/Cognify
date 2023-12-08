package com.example.cognify

import android.app.PendingIntent.getActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

//Por Bruno Dipietro 2023
//Se muestra el splashscreen por 2 segundos y no pasa mucho ademas de ser el host
//de la mitad de la aplicación

class MainActivity : AppCompatActivity() {
    //vvvvv

    override fun onCreate(savedInstanceState: Bundle?) {
        //El tema default del main activity es el de la splash screen (ver manifest)
        Thread.sleep(2000) //Entonces por 2000 milisegundos (2 segundos) no se hace nada
        setTheme(R.style.Theme_Cognify) //Volvemos a poner el tema de la app
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}