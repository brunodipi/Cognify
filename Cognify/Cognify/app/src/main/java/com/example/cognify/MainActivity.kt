package com.example.cognify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
//vvvvv

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        setTheme(R.style.Theme_Cognify)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }
}