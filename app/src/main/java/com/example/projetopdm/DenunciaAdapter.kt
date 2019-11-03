package com.example.projetopdm

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class DenunciaAdapter(var context: Context, var listDenuncias: ArrayList<Denuncia>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val linha: View
        val denuncia = this.listDenuncias.get(position)

        if (convertView == null) {
            var inflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            linha = inflater.inflate(R.layout.listview_denuncia, null)
        } else {
            linha = convertView
        }

        val tv = linha.findViewById<TextView>(R.id.tvLvTituloDenuncia)
        tv.text = denuncia.titulo

        if (position % 2 == 0) {
            linha.setBackgroundColor(Color.rgb(0, 255, 255))
        } else {
            linha.setBackgroundColor(Color.WHITE)
        }
        return linha
    }

    override fun getItem(position: Int): Any {
        return this.listDenuncias[position]
    }

    override fun getItemId(position: Int): Long {
        return -1
    }

    override fun getCount(): Int {
        return this.listDenuncias.count()
    }

    fun update(){
        notifyDataSetChanged()
    }

}