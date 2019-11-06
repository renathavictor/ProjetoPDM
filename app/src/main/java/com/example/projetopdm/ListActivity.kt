package com.example.projetopdm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    private lateinit var lvDenuncia: ListView
    private lateinit var dao: DenunciaDAO
    private lateinit var lista: ArrayList<Denuncia>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        this.dao = DenunciaDAO(this)
        this.lista = this.dao.get()

        this.lvDenuncia = findViewById(R.id.lvListDenuncia)
        this.lvDenuncia.adapter = DenunciaAdapter(this, this.lista)


        fabList.setOnClickListener{
            val it = Intent(this, MainActivity::class.java)
            startActivity(it)
            finish()
        }

    }
}
