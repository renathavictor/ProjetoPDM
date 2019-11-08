package com.example.projetopdm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.io.ByteArrayInputStream

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
        val iv = linha.findViewById<ImageView>(R.id.ivLvFotoDenuncia)
        tv.text = denuncia.titulo
        iv.setImageBitmap(toBitmapFromArray(denuncia.foto))


        if (position % 2 == 0) {
            linha.setBackgroundColor(Color.rgb(255, 230, 230))
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

    fun toBitmapFromArray(img: ByteArray): Bitmap? {
        var img : ByteArrayInputStream = ByteArrayInputStream(img)
        return BitmapFactory.decodeStream(img)
    }

}