package com.example.cognify

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase

//Por Bruno Dipietro 2023
//Este fragment lo vamos a utilizar para la creacion de nuevos usuarios

//Declaramos variables
private lateinit var passNuevoEditText: EditText
private lateinit var emailNuevoEditText: EditText
private lateinit var passConfirmEditText:EditText

private lateinit var botonRegistrar:Button

private lateinit var firebaseAuth:FirebaseAuth //Variable del firebase

private lateinit var database: DatabaseReference //Variable del firebase


class RegistroFragment : Fragment() {

    companion object {
        fun newInstance() = RegistroFragment()
    }

    private lateinit var viewModel: RegistroViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Convertimos el "return inflater..." en un val view, al final retornamos view
        val view = inflater.inflate(R.layout.fragment_registro2, container, false)

        firebaseAuth = FirebaseAuth.getInstance() //Instanciamos la base de datos
        //database = Firebase.database.reference

        emailNuevoEditText = view.findViewById(R.id.emailNuevoEditText)
        passNuevoEditText = view.findViewById(R.id.passNuevoEditText)
        passConfirmEditText = view.findViewById(R.id.passConfirmEditText)

        botonRegistrar = view.findViewById(R.id.boton_registrar)

        botonRegistrar.setOnClickListener {
            //Convertimos el texto del campo de texto a un String, para comparar
            val email = emailNuevoEditText.text.toString()
            val pass = passNuevoEditText.text.toString()
            val passCheck = passConfirmEditText.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && passCheck.isNotEmpty()){ //"Si no estan vacios..."
                if (pass == passCheck){ //Vemos si las contraseñas coinciden

                    //Sign up mediante una funcion de firebase
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) { //"Si es exitoso..."
                            findNavController().navigate(R.id.loginFragment) //Navegamos al login

                        }else { //Si algo sale mal nos da una exception
                            Toast.makeText(getActivity(), it.exception.toString(), Toast.LENGTH_LONG).show();
                        }
                    }

                }else{ //Si las contraseñas no coinciden
                    Toast.makeText(getActivity(), "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                }


                } else { //Si algun campo de texto está vacio
                Toast.makeText(getActivity(), "Complete todos los campos de texto", Toast.LENGTH_LONG).show();
            }


            }

        return view //Retornamos el view antes mencionado
    }


}