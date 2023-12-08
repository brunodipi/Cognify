package com.example.cognify

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//Por Bruno Dipietro 2023
//Este fragment tiene la unica funcion de ser una prueba de lectura de datos
//Ya es innecesario, pero queda por las dudas
//Todo está hecho de forma rustica <--


class paciente1Fragment : Fragment() {

    companion object {
        fun newInstance() = paciente1Fragment()
    }

    private lateinit var viewModel: Paciente1ViewModel

    //Variables del firebase
    private lateinit var database: FirebaseDatabase
    private lateinit var usersReference: DatabaseReference

    //Campos de datos donde se van a mostrar los datos
    private lateinit var textoDatos: TextView
    private lateinit var textoNombre: TextView
    private lateinit var textoApellido: TextView
    private lateinit var textoEdad: TextView

    private lateinit var firebaseAuth: FirebaseAuth //Variable del firebase


    val db = FirebaseFirestore.getInstance() //Instanciamos la base de datos

    //var datos = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Convertimos el "return inflater..." en un val view, al final retornamos view
        val view = inflater.inflate(R.layout.fragment_paciente1, container, false)

        database = FirebaseDatabase.getInstance()
        textoDatos = view.findViewById(R.id.textViewShowData)

        textoNombre = view.findViewById(R.id.textViewShowName)
        textoApellido = view.findViewById(R.id.textViewShowLastName)
        textoEdad = view.findViewById(R.id.textViewShowAge)

        //usersReference = database.reference.child("usuarios")

        leer()

        //leerFirestore() //leemos los datos con una funcion


        return view //Retornamos el view antes mencionado
    }



    private fun leer() {

        // Accedemos al nodo "usuarios"
        /*
        usersReference = database.reference.child("usuarios")
        val datosPersonalesReference = usersReference.child("usuario2")
         */



        usersReference = database.reference.child("UsersData")
        val datosPersonalesReference = usersReference.child("yYNof0hM5jMPTdJOD7dyndwUepb2").child("datos").child("zaky")
        val query = datosPersonalesReference.orderByKey().limitToLast(1)


        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // Obtenemos los valores USUARIOS PRUBEA
                /*
                val nombreUsuario = dataSnapshot.child("numbre").getValue(String::class.java)
                val apellidoUsuario = dataSnapshot.child("apellido").getValue(String::class.java)
                val datoY = dataSnapshot.child("datoY").getValue(Int::class.java)

                 */


                for (userSnapshot in dataSnapshot.children) { //Mostrar los datos
                    //Obtenemos los valores ENTRENAMIENTO
                    val datoTiempo = userSnapshot.child("tiempo").getValue(String::class.java)
                    val datoPresiones = userSnapshot.child("presiones").getValue(String::class.java)
                    val datoCorrectas = userSnapshot.child("presiones correctas").getValue(String::class.java)

                    textoNombre.text = "Tiempo mensaje:  $datoTiempo"
                    textoApellido.text = "Presiones: $datoPresiones"
                    textoEdad.text = "Correctas: $datoCorrectas"
                    textoDatos.text = ""


                    /*
                    //Forma de mostrarlos 1
                    textoNombre.text = "Nombre1:  $nombreUsuario."
                    textoApellido.text= "Apellido: $apellidoUsuario"
                    textoEdad.text = "DatoY: ${datoY.toString()}"

                    //Forma de mostrarlos 2
                    Toast.makeText(getActivity(), "Nombre de usuario: $nombreUsuario", Toast.LENGTH_LONG).show();

                     */



                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(getActivity(), "Error al leer datos: ${databaseError.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}




//TODO TE AMO BRUNO DEL PASADO POR NO BORRAR ESTO
            /* FIRESTORE (BORRAR?)
            private fun leerFirestore() {
                //Accedemos al documento a leer
                val docRef = db.collection("Pacientes").document("Paciente 1")
                docRef.get().addOnSuccessListener { document -> //"Si todo esta bien"
                    if (document != null){ //"Si el documento existe"
                        textoNombre.text = "Nombre: ${document.get("nombre") as String}" //MOSTRAR DATO PARTICULAR
                        textoApellido.text = "Apellido: ${document.get("apellido") as String}" //MOSTRAR DATO PARTICULAR
                        textoEdad.text = "Edad: ${(document.get("Edad") as Number).toString()}"  //MOSTRAR DATO PARTICULAR
                        textoDatos.text = ""

                        //textoDatos.text = document.data.toString() MOSTRAR TODOS LOS DATOS


                    }
                    else{ //Si no hay documento
                        Log.d("TAG", "No se encuentra el documento")
                    }

                }
                    .addOnFailureListener() { exception ->
                        Log.d("TAG", "Error al obtener $exception") //Otro error
                    }


            }
           */




