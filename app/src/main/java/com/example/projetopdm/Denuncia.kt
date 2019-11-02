package com.example.projetopdm

import java.io.Serializable
import java.util.*

class Denuncia : Serializable {
    var id: Int
    var titulo: String
    var descricao: String
    var data: String
    var orgao: String
    var localizacao: String
//    var foto: Bitmap

    constructor(titulo:String, descricao:String, data:String, orgao:String, localizacao:String) {
        this.id = -1
        this.titulo = titulo
        this.descricao = descricao
        this.data = data
        this.orgao = orgao
        this.localizacao = localizacao
    }

    constructor(id: Int, titulo:String, descricao:String, data:String, orgao:String, localizacao:String) {
        this.id = id
        this.titulo = titulo
        this.descricao = descricao
        this.data = data
        this.orgao = orgao
        this.localizacao = localizacao
    }

    override fun toString(): String {
        return this.titulo
    }
}