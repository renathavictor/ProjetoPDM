package com.example.projetopdm

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    private lateinit var lvDenuncia: ListView
    private lateinit var dao: DenunciaDAO
    private lateinit var lista: ArrayList<Denuncia>
    private lateinit var aviaoReceiver: AviaoReceiver
    var POSSICAO_EDIT = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        this.dao = DenunciaDAO(this)
        this.lista = this.dao.get()

        this.lvDenuncia = findViewById(R.id.lvListDenuncia)
        this.lvDenuncia.adapter = DenunciaAdapter(this, this.lista)
        this.lvDenuncia.setOnItemClickListener(ClickList())
        this.lvDenuncia.setOnItemLongClickListener(LongClickList())
        this.aviaoReceiver = AviaoReceiver()


        fabList.setOnClickListener{
            val it = Intent(this, MainActivity::class.java)
            startActivity(it)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val it = IntentFilter()
        it.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        registerReceiver(this.aviaoReceiver, it)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(this.aviaoReceiver)
    }

    fun atualizaLista(){
        this.lista.clear()
        this.lista.addAll(this.dao.get())
        (this.lvDenuncia.adapter as DenunciaAdapter).update()
    }

    inner class ClickList : AdapterView.OnItemClickListener{
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val denuncia = this@ListActivity.lista.get(position) as Denuncia
            val itMain = Intent(this@ListActivity, MainActivity::class.java)
            itMain.putExtra("DENUNCIA", denuncia)
            this@ListActivity.POSSICAO_EDIT = position
            Toast.makeText(this@ListActivity, denuncia.titulo+" - "+denuncia.descricao, Toast.LENGTH_SHORT).show()
            startActivity(itMain)
            finish()
        }
    }

    inner class LongClickList : AdapterView.OnItemLongClickListener{
        override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
            val denuncia = this@ListActivity.lvDenuncia.adapter.getItem(position) as Denuncia
            this@ListActivity.dao.delete(denuncia)
            this@ListActivity.atualizaLista()
            return true
        }
    }
}
