package mx.edu.itt.ladm_u3_practica2

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var baseremota = FirebaseFirestore.getInstance()
    var listaId=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        confirmar.setOnClickListener {
            InsertarPedido()
        }

        cargarpedidos()
    }


    private fun cargarpedidos() {
        baseremota.collection("restaurante")

            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null){
                    msj("Error, no hay conexión")
                    return@addSnapshotListener
                }



                var vect =Array<String>(200,{""})
                listaId=ArrayList<String>()
                var res = ""
                var it=0
                for(document in querySnapshot!!){

                    res = "ID: " + document.id+
                            "\nNombre: " + document.getString("nombre") +"\nDomicilio: " + document.getString("domicilio")+
                            "\nCelular: " + document.get("celular") +"\n_________PEDIDO______"+                            "\nDescripcion: " + document.get("pedido.descripcion") + "\nCantidad: " + document.get("pedido.cantidad")+ "\nPrecio: " + document.get("pedido.precio")
                    vect[it]=res
                    listaId.add(document.id)
                    it++
                }
                if (res.indexOf("null") >=0 ){
                    res = res.substring(0, res.indexOf("Conyugue"))
                }
                ListaP.adapter= ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,vect)
                ListaP.setOnItemClickListener { parent, view, position, id ->


                    AlertDialog.Builder(this).setTitle("¿Que deseas hacer?")

                        .setPositiveButton("Editar"){d,r-> cargarOtroActivity(listaId[position])}
                        .setNeutralButton("Cancelar"){d,r->}
                        .show()
                }

            }
    }
    private fun cargarOtroActivity(i:String) {

        val docRef = baseremota.collection("restaurante").document(i)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {




                    var intento = Intent(this,Main2Activity::class.java)

                    intento.putExtra("id",i)
                    intento.putExtra("nombre",document.data?.get("nombre").toString())
                    intento.putExtra("domicilio",document.data?.get("domicilio").toString())
                    intento.putExtra("celular",document.data?.get("celular").toString())

                    intento.putExtra("descripcion",document.get("pedido.descripcion").toString())
                    intento.putExtra("cantidad",document.get("pedido.cantidad").toString())
                    intento.putExtra("precio",document.get("pedido.precio").toString())
                    intento.putExtra("entregado",document.get("pedido.entregado").toString())

                    startActivityForResult(intento,0)
                } else {
                    msj( "No such document")
                }
            }
            .addOnFailureListener { exception ->
               msj("get failed with ")
            }




    }



    private fun InsertarPedido() {
        var entr ="False"
        if(entregado.isChecked==true){entr="True"}

        var data  =  hashMapOf(
            "nombre" to nombre.text.toString(),
            "domicilio" to domicilio.text.toString(),
            "celular" to celular.text.toString(),
            "pedido" to hashMapOf(
                "descripcion" to  descripcion.text.toString(),
                "precio" to precio.text.toString().toDouble(),
                "cantidad" to  cantidad.text.toString().toInt(),
                "entregado" to entr  //nombre.text.toString()

            )

        )

        baseremota.collection("restaurante")
            .add(data)
            .addOnSuccessListener {
            msj("Se inserto correctamete")
                descripcion.setText("")
                precio.setText("")
                cantidad.setText("")
                entregado.isChecked=false
                nombre.setText("")
                domicilio.setText("")
                celular.setText("")
            }
            .addOnFailureListener {
            msj("Error:no se pudo completar!")
            }
    }
    fun msj (i:String){
        Toast.makeText(this,i,Toast.LENGTH_LONG).show()
    }
}
