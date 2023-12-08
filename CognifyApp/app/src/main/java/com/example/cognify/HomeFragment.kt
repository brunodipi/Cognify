package com.example.cognify

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import clases.Pacientes
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

// Por Bruno Dipietro 2023
// Este fragment es para seleccionar un paciente

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    private lateinit var botonAgregarPaciente: Button

    private lateinit var agregarNuevoPaciente:EditText

    private lateinit var easterEgg1_Agradecimientos: Button

    private var flagX = false


    //Variables Recycler View
    private var listapacientes: MutableList<Pacientes> = mutableListOf()
    private lateinit var recycler: RecyclerView

    //Firebase cositas
    private lateinit var firebaseAuth: FirebaseAuth //Variable del firebase
    val db = FirebaseFirestore.getInstance() //Instanciamos la base de datos
    private lateinit var database: FirebaseDatabase
    private lateinit var usersReference: DatabaseReference



    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Convertimos el "return inflater..." en un val view, al final retornamos view
        val view = inflater.inflate(R.layout.fragment_home3, container, false)

        database = FirebaseDatabase.getInstance()

        //Asociamos variables
        botonAgregarPaciente = view.findViewById(R.id.botonAgregarPaciente)
        easterEgg1_Agradecimientos = view.findViewById(R.id.easterEgg1_Agradecimientos)
        recycler = view.findViewById(R.id.listaRecycle)

        agregarNuevoPaciente = view.findViewById(R.id.agregarNuevoPaciente)


        /* Forma IMPROVISADA CRUDA MAL MAL de añadir items al recycler. Cambiar por firebase
           listapacientes.add(Pacientes("Carlos Mandú"))
           listapacientes.add(Pacientes("PEPE Mandú"))

           listapacientes.add(Pacientes("MATEO Mandú"))
           listapacientes.add(Pacientes("Juan Mandú"))

           listapacientes.add(Pacientes("BARBAS BARBAAAAS"))

           HECHO :)) */


        easterEgg1_Agradecimientos.setOnClickListener {
            findNavController().navigate(R.id.facuFragment)
        }


        //RECYCLER CON REALTIME DATABASE (todo PF)
        botonAgregarPaciente.setOnClickListener {
            agregarNuevoPaciente.visibility = View.VISIBLE
            agregarNuevoPaciente.requestFocus()

            botonAgregarPaciente.text = "Confirmar"

            if (agregarNuevoPaciente.text.isNotEmpty()) {

                val nombreIngresado = agregarNuevoPaciente.text.toString()

                if (listapacientes.contains(Pacientes(nombreIngresado))) {
                    Toast.makeText(getActivity(), "Ya existe ese paciente", Toast.LENGTH_LONG)
                        .show();
                } else {

                    //listapacientes.add(Pacientes(nombreIngresado))
                    val nodoRefUsuarios =
                        database.getReference("UsersData").child("yYNof0hM5jMPTdJOD7dyndwUepb2")
                            .child("datos").child(nombreIngresado)

                    val datoUsuario = mapOf("0" to "")

                    // Subir los datos al nodo en la base de datos
                    nodoRefUsuarios.setValue(datoUsuario)

                    establecerAdapter()
                    Toast.makeText(getActivity(), "Se agregó paciente", Toast.LENGTH_LONG).show();

                    agregarNuevoPaciente.visibility = View.GONE
                    botonAgregarPaciente.text = "Agregar paciente"

                }
            }
            else {
                Toast.makeText(getActivity(), "Complete el campo de texto", Toast.LENGTH_LONG).show();
            }

            //Toast.makeText(getActivity(), "Agregue: $nombreIngresado", Toast.LENGTH_LONG).show();
            //Toast.makeText(getActivity(), "LISTA: ${listapacientes.size}", Toast.LENGTH_LONG).show();

            agregarNuevoPaciente.text.clear()

        }

        usersReference = database.reference.child("UsersData")
        val datosPersonalesReference = usersReference.child("yYNof0hM5jMPTdJOD7dyndwUepb2").child("datos")

        datosPersonalesReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (userSnapshot in dataSnapshot.children) { //Mostrar los datos
                    //Obtenemos los valores ENTRENAMIENTO
                    //val datoTiempo = userSnapshot.child("tiempo").getValue(String::class.java)


                    if (!flagX) {
                        val listaPacientesFiltrada = mutableListOf<String>()
                        val nombreUsuario = userSnapshot.key

                        if (nombreUsuario != null) {
                            if (!listapacientes.contains(Pacientes(nombreUsuario))) {
                                listapacientes.add(Pacientes("$nombreUsuario"))

                            }
                        }

                        //Toast.makeText(context, "$listaPacientes", Toast.LENGTH_LONG).show();

                    }
                }
                establecerAdapter()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show();
            }
        })



        //RECYCLER CON FIRESTORE (todo DAP)
        /*
        botonAgregarPaciente.setOnClickListener {
            agregarNuevoPaciente.visibility = View.VISIBLE
            agregarNuevoPaciente.requestFocus()

            botonAgregarPaciente.text = "Confirmar"

            if (agregarNuevoPaciente.text.isNotEmpty()) {

                val nombreIngresado = agregarNuevoPaciente.text.toString()

                if (listapacientes.contains(Pacientes(nombreIngresado))) {
                    Toast.makeText(getActivity(), "Ya existe ese paciente", Toast.LENGTH_LONG)
                        .show();
                } else {

                    //listapacientes.add(Pacientes(nombreIngresado))
                    val usuariosCollection = db.collection("Pacientes")

                    val nuevoUsuario = hashMapOf(
                        "nombre" to nombreIngresado
                    )

                    usuariosCollection
                        .add(nuevoUsuario) // Puedes usar .set() si deseas especificar un ID personalizado.
                        .addOnSuccessListener { documentReference ->
                            // Los datos se subieron con éxito.
                            //Toast.makeText(getActivity(), "$idDelNuevoDocumento", Toast.LENGTH_LONG).show();
                            println("Documento subido con ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            // Maneja cualquier error que pueda ocurrir durante la subida.
                            println("Error al subir datos a Firestore: $e")
                        }

                    listapacientes.add(Pacientes(nombreIngresado))
                    establecerAdapter()
                    Toast.makeText(getActivity(), "Se agregó paciente", Toast.LENGTH_LONG).show();

                    agregarNuevoPaciente.visibility = View.GONE
                    botonAgregarPaciente.text = "Agregar paciente"

                }
            }
            else {
                Toast.makeText(getActivity(), "Complete el campo de texto", Toast.LENGTH_LONG).show();
            }

            //Toast.makeText(getActivity(), "Agregue: $nombreIngresado", Toast.LENGTH_LONG).show();
            //Toast.makeText(getActivity(), "LISTA: ${listapacientes.size}", Toast.LENGTH_LONG).show();

            agregarNuevoPaciente.text.clear()

        }

        //Accedemos al documento a leer
        val docRef = db.collection("Pacientes")//.document("Paciente 1")
        docRef.get().addOnSuccessListener { documents -> //"Si todo esta bien"
            for (document in documents){ //"Si el documento existe"
                //textoNombre.text = "Nombre: ${document.get("nombre") as String}" //MOSTRAR DATO PARTICULAR
                //textoDatos.text = document.data.toString() MOSTRAR TODOS LOS DATOS

                if (!flagX) {
                    val listaPacientesFiltrada = mutableListOf<String>()
                    val nombreUsuario = document.get("nombre") as String

                    if (!listapacientes.contains(Pacientes(nombreUsuario))) {
                        listapacientes.add(Pacientes(nombreUsuario))

                        //Toast.makeText(context, listapacientes.toString(), Toast.LENGTH_LONG).show();
                    }

                }

            }
            establecerAdapter()
        }
            .addOnFailureListener() { exception ->
                Log.d("TAG", "Error al obtener $exception") //Otro error
            }

        establecerAdapter()

         */



        return view //Retornamos el view antes mencionado
    }


    private fun establecerAdapter() {
        recycler.layoutManager = LinearLayoutManager(getActivity())
        recycler.adapter = context?.let { RecyclePacientes(it, listapacientes) }
    }

}





