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

    

}