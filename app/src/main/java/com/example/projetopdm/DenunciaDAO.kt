package com.example.projetopdm

import android.content.Context

class DenunciaDAO {

    var bancoHelper: BancoHelper

    constructor(context: Context) {
        this.bancoHelper = BancoHelper(context)
    }

}