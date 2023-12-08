
package com.example.cognify

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.github.mikephil.charting.data.Entry
import kotlin.properties.Delegates

//Por Bruno Dipietro 2023
//Como dice el nombre, aca se muestran los datos de paciente seleccionado

private lateinit var database: FirebaseDatabase
private lateinit var usersReference: DatabaseReference

@SuppressLint("StaticFieldLeak")
lateinit var textoDato1:TextView

@SuppressLint("StaticFieldLeak")
lateinit var textoDato2:TextView

@SuppressLint("StaticFieldLeak")
lateinit var textoDato3:TextView

@SuppressLint("StaticFieldLeak")
lateinit var textView33:TextView

private lateinit var botonInicioEntrenamiento:Button
private lateinit var botonGraf:Button
private lateinit var botonBorrar:Button

private lateinit var textTiempoIngresado:EditText

private lateinit var firebaseAuth: FirebaseAuth //Variable del firebase

@SuppressLint("StaticFieldLeak")
val db = FirebaseFirestore.getInstance() //Instanciamos la base de datos



class datosPacienteActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_paciente)

        val keyPaciente = intent.getStringExtra("paqueteKeyPaciente")
        val keyID = intent.getStringExtra("paqueteKeyID")
        var flagBotonBorrar:Boolean = false

        botonInicioEntrenamiento = findViewById(R.id.botonInicioEntrenamiento)
        botonGraf = findViewById(R.id.botonGraf)
        botonBorrar = findViewById(R.id.botonBorrar)

        //Toast.makeText(this, "Tocaste: $datoRecibido", Toast.LENGTH_LONG).show();

        textView33 = findViewById(R.id.textView3)
        textView33.text = "Ultimos resultados de: $keyPaciente"

        database = FirebaseDatabase.getInstance()


        textoDato1 = findViewById(R.id.textViewDato1)
        textoDato2 = findViewById(R.id.textViewDato2)
        textoDato3 = findViewById(R.id.textViewDato3)

        textTiempoIngresado = findViewById(R.id.textTiempoIngresado)

        botonGraf.setOnClickListener {
            flagBotonBorrar = false
            val intent = Intent(this, graficoActivity::class.java)
            intent.putExtra("clave", keyPaciente)
            startActivity(intent)
        }


        /*
         //todo Borrar un paciente DAP
        botonBorrar.setOnClickListener {
            if (!flagBotonBorrar) {
                Toast.makeText(
                    this, "¿Está seguro que quiere borrar " +
                            "$keyPaciente?", Toast.LENGTH_LONG
                ).show();
                flagBotonBorrar = true

            } else {
                val documentoAEliminar = db.collection("Pacientes").document(keyID.toString())
                // Borra el documento utilizando el método delete().
                documentoAEliminar
                    .delete()
                    .addOnSuccessListener {
                        // El documento se eliminó con éxito.
                        Toast.makeText(
                            this,
                            "$keyPaciente fue eliminado, reiniciando aplicación",
                            Toast.LENGTH_LONG
                        ).show();
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        // Maneja cualquier error que pueda ocurrir durante la eliminación.
                        println("Error al eliminar el documento: $e")
                    }
            }
        }
        */

        //todo Borrar paciente PF
        botonBorrar.setOnClickListener {
            if (!flagBotonBorrar) {
                Toast.makeText(this, "¿Está seguro que quiere borrar " +
                        "$keyPaciente?", Toast.LENGTH_LONG).show();
                flagBotonBorrar = true
            } else {
                usersReference = database.reference.child("UsersData")
                val referenciaNodo =
                    usersReference.child("yYNof0hM5jMPTdJOD7dyndwUepb2").child("datos")
                        .child(keyPaciente.toString())

                // Elimina el nodo llamando a removeValue().
                referenciaNodo
                    .removeValue()
                    .addOnSuccessListener {
                        // El nodo se eliminó con éxito.
                        Toast.makeText(
                            this,
                            "$keyPaciente fue eliminado, reiniciando aplicación",
                            Toast.LENGTH_LONG
                        ).show();
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        // Maneja cualquier error que pueda ocurrir durante la eliminación.
                        println("Error al eliminar el nodo: $e")
                    }
            }
        }

        botonInicioEntrenamiento.setOnClickListener {
            flagBotonBorrar = false
            textTiempoIngresado.visibility = View.VISIBLE
            val tiempoEntrenamiento = textTiempoIngresado.text.toString()
            botonInicioEntrenamiento.text = "Confirmar nuevo entrenamiento"
            if (tiempoEntrenamiento.isNotEmpty()) {
                //Lo convierto en INT
                val tiempoEntrenamiento = tiempoEntrenamiento.toInt()

                if (tiempoEntrenamiento > 0) {

                    // Obtén una referencia a un nodo específico en la base de datos
                    val miNodoRef = database.getReference("usuarios")


                    // Crear un mapa de datos
                    val datos = mapOf(
                        "paciente" to keyPaciente,
                        "flagInicio" to true,
                        "tiempoEntrenamiento" to tiempoEntrenamiento
                        //"nombre" to "Ejemplo", //--> Asignas a "nombre" el texto "Ejemplo"
                        //"edad" to 25 --> Asignas a "edad" el valor 25
                    )

                    // Subir los datos al nodo en la base de datos
                    miNodoRef.setValue(datos)
                    Toast.makeText(this, "Se cargaron los datos", Toast.LENGTH_LONG).show();
                    textTiempoIngresado.text.clear()
                    botonInicioEntrenamiento.text = "Iniciar nuevo entrenamiento"
                    textTiempoIngresado.visibility = View.INVISIBLE


                } else {
                    Toast.makeText(
                        this,
                        "El tiempo tiene que ser mayor a 0",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        }


        leer()

    }


    private fun leer() {
        val keyPaciente = intent.getStringExtra("paqueteKeyPaciente")
        val keyPacienteString = keyPaciente.toString()

        //Snackbar.make(findViewById<View>(android.R.id.content),
           // "Leido: $keyPacienteString", Snackbar.LENGTH_SHORT).show()


        usersReference = database.reference.child("UsersData")

        //val datosPersonalesReference = usersReference.child("yYNof0hM5jMPTdJOD7dyndwUepb2").child("pacientes").child(keyPacienteString).child("entrenamientos")
        val datosPersonalesReference = usersReference.child("yYNof0hM5jMPTdJOD7dyndwUepb2").child("datos").child(keyPacienteString)


        val query = datosPersonalesReference.orderByKey().limitToLast(1)

        //val datosPersonalesReference = usersReference.child("yYNof0hM5jMPTdJOD7dyndwUepb2").child("datos")
        //val query = datosPersonalesReference.orderByKey().limitToLast(1)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (userSnapshot in dataSnapshot.children) { //Mostrar los datos

                    //Obtenemos los valores ENTRENAMIENTO
                    val datoTiempo = userSnapshot.child("tiempo").getValue(String::class.java)
                    val datoPresiones = userSnapshot.child("presiones").getValue(String::class.java)
                    val datoCorrectas = userSnapshot.child("presiones correctas").getValue(String::class.java)

                    textoDato1.text = "Tiempo mensaje:  $datoTiempo"
                    textoDato2.text = "Presiones: $datoPresiones"
                    textoDato3.text = "Correctas: $datoCorrectas"

                }

                //mostrarGrafico()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Snackbar.make(findViewById<View>(android.R.id.content),
                    "Error al leer datos: ${databaseError.message}", Snackbar.LENGTH_SHORT).show()
            }
        })
    }





}
