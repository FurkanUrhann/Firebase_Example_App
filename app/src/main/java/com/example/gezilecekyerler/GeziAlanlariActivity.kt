package com.example.gezilecekyerler

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import com.example.gezilecekyerler.adapter.GeziAlanlariAdapter
import com.example.gezilecekyerler.models.GeziAlani
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class GeziAlanlariActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var geziAlanlariListView: ListView
    private lateinit var geziAlanlariAdapter: GeziAlanlariAdapter
    private lateinit var geziAlanlariList: MutableList<GeziAlani>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gezi_alanlari)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        geziAlanlariListView = findViewById(R.id.geziAlanlariListView)
        geziAlanlariList = mutableListOf()
        geziAlanlariAdapter = GeziAlanlariAdapter(this, geziAlanlariList)
        geziAlanlariListView.adapter = geziAlanlariAdapter

        geziAlanlariListView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { _, _, position, _ ->
                val selectedGeziAlani = geziAlanlariList[position]
                showDeleteConfirmationDialog(selectedGeziAlani)
                true
            }

        fetchGeziAlanlari()

        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            val baslikEditText = findViewById<EditText>(R.id.baslikEditText)
            val sehirEditText = findViewById<EditText>(R.id.sehirEditText)
            val notlarEditText = findViewById<EditText>(R.id.notlarEditText)

            val baslik = baslikEditText.text.toString()
            val sehir = sehirEditText.text.toString()
            val notlar = notlarEditText.text.toString()

            saveGeziAlani(baslik, sehir, notlar)

            // Animasyon uygulama
            val animation = AnimationUtils.loadAnimation(this, R.anim.animasyon)
            addButton.startAnimation(animation)
        }

    }

    private fun fetchGeziAlanlari() {
        val currentUser: FirebaseUser? = auth.currentUser
        val userId: String = currentUser?.uid ?: ""
        val ref: DatabaseReference = database.getReference("gezi_alanlari/$userId")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val newList = mutableListOf<GeziAlani>() // Yeni bir liste oluştur
                for (snapshot in dataSnapshot.children) {
                    val geziAlani = snapshot.getValue(GeziAlani::class.java)
                    geziAlani?.let {
                        newList.add(it)
                    }
                }
                geziAlanlariList.clear() // Eski listeyi temizle
                geziAlanlariList.addAll(newList) // Yeni listeyi ekle
                geziAlanlariAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@GeziAlanlariActivity,
                    "Veriler alınamadı!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showDeleteConfirmationDialog(geziAlani: GeziAlani) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Silmek İster Misiniz?")
        builder.setPositiveButton("Evet") { _, _ ->
            deleteGeziAlani(geziAlani)
        }
        builder.setNegativeButton("Hayır", null)
        builder.create().show()

    }

    private fun deleteGeziAlani(geziAlani: GeziAlani) {
        val currentUser: FirebaseUser? = auth.currentUser
        val userId: String = currentUser?.uid ?: ""
        val ref: DatabaseReference =
            database.getReference("gezi_alanlari/$userId/${geziAlani.geziId}")
        ref.removeValue()
    }

    private fun saveGeziAlani(baslik: String, sehir: String, notlar: String) {
        val currentUser: FirebaseUser? = auth.currentUser
        val userId: String = currentUser?.uid ?: ""
        val ref: DatabaseReference = database.getReference("gezi_alanlari/$userId")

        val geziAlaniId = ref.push().key
        if (geziAlaniId != null) {
            val geziAlani = GeziAlani(geziAlaniId, baslik, sehir, notlar)
            ref.child(geziAlaniId).setValue(geziAlani)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Gezi Alanı başarıyla kaydedildi.",
                            Toast.LENGTH_SHORT
                        ).show()
                        fetchGeziAlanlari() // Gezi alanlarını yeniden al
                        clearInputFields()
                    } else {
                        Toast.makeText(
                            this,
                            "Gezi Alanı kaydedilirken bir hata oluştu.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun clearInputFields() {
        val baslikEditText = findViewById<EditText>(R.id.baslikEditText)
        val sehirEditText = findViewById<EditText>(R.id.sehirEditText)
        val notlarEditText = findViewById<EditText>(R.id.notlarEditText)
        baslikEditText.text.clear()
        sehirEditText.text.clear()
        notlarEditText.text.clear()
    }
}

