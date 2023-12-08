package com.example.cognify

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates

//Por Bruno Dipietro 2023
//Se muestra el historial del paciente seleccionado mediante un
//grafico del tipo LineChart

private lateinit var database: FirebaseDatabase
private lateinit var usersReference: DatabaseReference

private lateinit var firebaseAuth: FirebaseAuth //Variable del firebase
private lateinit var textoPaciente:TextView

@SuppressLint("StaticFieldLeak")
val db2 = FirebaseFirestore.getInstance() //Instanciamos la base de datos

class graficoActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grafico)

        val key = intent.getStringExtra("clave")
        val keyString = key.toString()

        textoPaciente = findViewById(R.id.textoPaciente)
        textoPaciente.text = "Historial de: $keyString"

        database = FirebaseDatabase.getInstance()

        leer(keyString)
    }


    private fun mostrarGrafico(listaDatosPresionesCorrectas: MutableList<Float>, listaDatosPresiones:MutableList<Float>) {
        val entriesPresiones = mutableListOf<Entry>() //Entradas de datos a graficar
        val entriesCorrectas = mutableListOf<Entry>()

        val lineChart = findViewById<LineChart>(R.id.lineChart)

        /*
        // Datos genéricos de ejemplo (puedes reemplazarlos por tus propios datos)
        entries.add(Entry(0f, 10f)) //el primero representa entrada, el segundo el dato
        entries.add(Entry(1f, 20f)) //f de float
        entries.add(Entry(2f, 20f)) //f de float
        entries.add(Entry(3f, 15.5f))
        entries.add(Entry(4f, 30f))
        entries.add(Entry(5f, 65f))

         */

        val tamanoLista = listaDatosPresiones.size //Para ver cuantos puntos graficar

        for (i in 0 until tamanoLista) {
            try { //Si lo puede hacer lo hace

                //Presiones TOTALES
                entriesPresiones.add(Entry(0f + i, listaDatosPresiones[i]))
                val dataSetPresiones = LineDataSet(entriesPresiones, "Presiones") //Describe cada linea
                val legend = lineChart.legend
                val descriptionPresiones = Description()

                legend.textSize = 15f //tamaño del texto de la descripcion de linea
                dataSetPresiones.valueTextSize = 15f //tamaño del texto de los datos
                dataSetPresiones.color = Color.BLUE //color de linea
                dataSetPresiones.valueTextColor = Color.RED //color de punto
                descriptionPresiones.textSize = 16f //tamaño del subtitulo
                lineChart.setBackgroundColor(Color.WHITE) // Cambia el color de fondo
                dataSetPresiones.lineWidth = 3f //anchura de la grafica


                //Presiones CORRECTAS
                entriesCorrectas.add(Entry(0f + i, listaDatosPresionesCorrectas[i]))
                val dataSetCorrectas = LineDataSet(entriesCorrectas, "Correctas") //Describe cada linea
                val legendCorrectas = lineChart.legend
                val descriptionCorrectas = Description()

                legendCorrectas.textSize = 15f //tamaño del texto de la descripcion de linea
                dataSetCorrectas.valueTextSize = 15f //tamaño del texto de los datos
                dataSetCorrectas.color = Color.RED //color de linea
                dataSetCorrectas.valueTextColor = Color.RED //color de punto
                descriptionCorrectas.textSize = 16f //tamaño del subtitulo

                dataSetCorrectas.lineWidth = 3f //anchura de la grafica


                //todo AJUSTES GENERICOS DE LA GRAF
                val lineData = LineData(dataSetCorrectas, dataSetPresiones) //graficas los dataSets
                lineChart.data = lineData

                // Configuración adicional del gráfico
                val description = Description()
                description.textSize = 18f //tamaño del subtitulo
                description.text = "Datos a traves del tiempo" //Subtitulo
                lineChart.description = description
                lineChart.invalidate() //crea la grafica


                /*
                //Todo PARA AGREGAR MUCHAS GRAFICAS
                //Reemplazar con los datos que hagan falta

                entriesB.add(Entry(0f + i, listaDatosB[i]))

                val dataSetB = LineDataSet(entriesB, "Correctas") //Describe cada linea
                val legendB = lineChart.legend
                val descriptionB = Description()

                legendB.textSize = 15f //tamaño del texto de la descripcion de linea
                dataSetB.valueTextSize = 15f //tamaño del texto de los datos
                dataSetB.color = Color.RED //color de linea
                dataSetB.valueTextColor = Color.RED //color de punto
                descriptionB.textSize = 16f //tamaño del subtitulo

                dataSetB.lineWidth = 3f //anchura de la grafica

                //todo IMPORTANTE: Agregar la nueva graph ABAJO
                //val lineData = LineData(dataB, dataX, dataY, dataETC)

                 */



            } catch (e: IndexOutOfBoundsException) { //Si male sal
                Snackbar.make(
                    findViewById<View>(android.R.id.content),
                    "El índice está fuera de los límites de la lista.", Snackbar.LENGTH_SHORT
                ).show()
                // Manejo de excepciones en caso de índice fuera de límites
                println("El índice está fuera de los límites de la lista.")
            }


        }

    }

    fun leer(keyString: String): MutableList<Float> {

        val listaDatosPresiones = mutableListOf<Float>()
        val listaDatosPresionesCorrectas = mutableListOf<Float>()

        usersReference = database.reference.child("UsersData")
        val datosPersonalesReference =
            usersReference.child("yYNof0hM5jMPTdJOD7dyndwUepb2").child("datos")
                .child(keyString)

        datosPersonalesReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (userSnapshot in dataSnapshot.children) { //Mostrar los datos
                    if (userSnapshot.hasChild("presiones")) {
                        // Obtiene el valor de "presiones" como un String
                        val presiones = userSnapshot.child("presiones").value as? String
                        if (presiones != null) {
                            listaDatosPresiones.add(presiones.toFloat())

                            mostrarGrafico(listaDatosPresiones, listaDatosPresionesCorrectas)

                        }

                    }

                    if (userSnapshot.hasChild("presiones correctas")) {
                        // Obtiene el valor de "presiones" como un String
                        val presionesCorrectas = userSnapshot.child("presiones correctas").value as? String
                        if (presionesCorrectas != null) {
                            listaDatosPresionesCorrectas.add(presionesCorrectas.toFloat())

                            mostrarGrafico(listaDatosPresionesCorrectas, listaDatosPresiones)

                        }

                    }
                    //mostrarGrafico(listaDatosPresionesCorrectas, listaDatosPresiones)
                    

                }

            }


            override fun onCancelled(databaseError: DatabaseError) {
                Snackbar.make(
                    findViewById<View>(android.R.id.content),
                    "Error al leer datos: ${databaseError.message}", Snackbar.LENGTH_SHORT
                ).show()
            }
        })
    return listaDatosPresiones //Innecesario creo, pero si lo saco trae mas problemas que si lo dejo :)
    }
}

