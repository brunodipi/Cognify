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

//Por Bruno Dipietro 2023
//Intuitivamente, este es el fragement de login. Es la primer pantalla


//Declaracion variables
private lateinit var botonLogin: Button
private lateinit var botonCrearUser: Button
private lateinit var passwordTextView: EditText
private lateinit var usuarioTextView: EditText
class LoginFragment : Fragment() {
    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var firebaseAuth: FirebaseAuth //Variable del firebase

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Convertimos el "return inflater..." en un val view, al final retornamos view
        val view = inflater.inflate(R.layout.fragment_login2, container, false)


        firebaseAuth = FirebaseAuth.getInstance() //Instanciamos la base de datos

        //Asociamos variables
        botonLogin = view.findViewById(R.id.botonLogin)
        botonCrearUser = view.findViewById(R.id.boton_crear_usuario)
        passwordTextView = view.findViewById(R.id.passwordPlainText)
        usuarioTextView = view.findViewById(R.id.usuarioPlainText)

        botonLogin.setOnClickListener {

            //Convertimos el texto del campo de texto a un String, para comparar
            val email = usuarioTextView.text.toString()
            val pass = passwordTextView.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) { //"Si no estan vacios..."

                //Sign in mediante una funcion de firebase
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) { //"Si es exitoso"
                        findNavController().navigate(R.id.homeFragment) //Navegamos a la home

                    } else { //Si algo sale mal nos da una exception
                        Toast.makeText(getActivity(), it.exception.toString(), Toast.LENGTH_LONG).show();
                    }
                }

            } else { //Si algun campo de texto está vacio
                Toast.makeText(getActivity(), "Complete todos los campos de texto", Toast.LENGTH_LONG).show();
            }


        }


        botonCrearUser.setOnClickListener {
            findNavController().navigate(R.id.registroFragment) //Navegamos al fragment de registro

        }

        return view //Retornamos el view antes mencionado
    }
}




