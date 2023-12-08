package com.example.cognify

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import clases.Pacientes
import com.google.firebase.firestore.FirebaseFirestore

//Por Bruno Dipietro 2023
//Nuestro recycleView de pacientes

//Creamos el Recycle
class RecyclePacientes (var context:Context, var listaPacientes:MutableList<Pacientes>): RecyclerView.Adapter<RecyclePacientes.miHolder>() {


    inner class miHolder(itemView:View):RecyclerView.ViewHolder(itemView){ //Creamos el holder

        lateinit var labelPacientes:TextView

        init {
            labelPacientes = itemView.findViewById(R.id.labelPacientes)


            itemView.setOnClickListener {

                //val nombreTocado = listaPacientes[position]
                //cambio pantalla todo DAP
                /*
                val db = FirebaseFirestore.getInstance()
                val usuariosCollection = db.collection("Pacientes")

                usuariosCollection
                    .whereEqualTo("nombre", labelPacientes.text)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            // Debería haber un único documento con ese nombre.
                            val document = querySnapshot.documents[0]
                            val idDelDocumento = document.id
                            //Toast.makeText(context, "ID: $idDelDocumento", Toast.LENGTH_LONG).show();
                            println("ID del documento: $idDelDocumento")

                            // Al tocar te manda a otra pantalla:
                            var position: Int = getAdapterPosition()
                            val context = itemView.context
                            val intent = Intent(context, datosPacienteActivity::class.java)

                            intent.putExtra("paqueteKeyPaciente", labelPacientes.text)
                            intent.putExtra("paqueteKeyID", idDelDocumento)
                            itemView.context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "No se encuentra el doc", Toast.LENGTH_LONG)
                                .show();
                            println("No se encontró ningún documento con el nombre: $labelPacientes")
                        }
                    }
                    .addOnFailureListener { e ->
                        // Maneja cualquier error que pueda ocurrir durante la consulta.
                        println("Error al consultar Firestore: $e")
                    }

                 */




                // Al tocar te manda a otra pantalla todo PF

                var position: Int = getAdapterPosition()
                val context = itemView.context
                val intent = Intent(context, datosPacienteActivity::class.java)

                intent.putExtra("paqueteKeyPaciente", labelPacientes.text)
                itemView.context.startActivity(intent)


                //Para crear un snackbar:
                //Toast.makeText(context, "ID: $idDelNuevoDocumento", Toast.LENGTH_LONG).show();




            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): miHolder {
       var itemView = LayoutInflater.from(context).inflate(R.layout.carta_pacientes, parent, false)
        return miHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listaPacientes.size
    }

    override fun onBindViewHolder(holder: miHolder, position: Int) {
        var pacientes = listaPacientes [position]
        holder.labelPacientes.text = pacientes.nombre_pacientes
    }


}