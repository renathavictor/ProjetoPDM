package com.example.projetopdm

import android.content.ContentValues
import android.content.Context

class DenunciaDAO {

    var bancoHelper: BancoHelper

    constructor(context: Context) {
        this.bancoHelper = BancoHelper(context)
    }

    fun insert(denuncia: Denuncia){
        val banco = this.bancoHelper.writableDatabase
        val cv = ContentValues()
        cv.put("titulo", denuncia.titulo)
        cv.put("descricao", denuncia.descricao)
        cv.put("data", denuncia.data)
        cv.put("orgao", denuncia.orgao)
        cv.put("localizacao", denuncia.localizacao)
//        cv.put("foto", denuncia.foto)
        banco.insert("denuncias", null, cv)
    }

    fun get(id:Int):Denuncia?{
        return null
    }

    fun get(): ArrayList<Denuncia> {
        val lista = arrayListOf<Denuncia>()
        val banco = this.bancoHelper.readableDatabase
        val colunas = arrayOf("id", "titulo", "descricao", "data", "orgao", "localizacao")
        val c = banco.query("denuncias", colunas, null, null, null, null, null)
        c.moveToFirst()
        do {
            val id = c.getInt(c.getColumnIndex("id")) //autoincrementável
            val titulo = c.getString(c.getColumnIndex("titulo"))
            val descricao = c.getString(c.getColumnIndex("descricao"))
            val data = c.getString(c.getColumnIndex("data"))
            val orgao = c.getString(c.getColumnIndex("orgao"))
            val localizacao = c.getString(c.getColumnIndex("localizacao"))
            val denuncia = Denuncia(id, titulo, descricao, data, orgao, localizacao)
            lista.add(denuncia)
        } while (c.moveToNext())
        return lista
    }

}