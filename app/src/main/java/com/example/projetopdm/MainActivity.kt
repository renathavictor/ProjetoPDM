package com.example.projetopdm

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.MicrophoneDirection
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap

import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Build
import androidx.annotation.RequiresApi


class MainActivity : AppCompatActivity() {

    val CAMERA = 1
    val FORMULARIO = 2
    private lateinit var dao: DenunciaDAO
    private lateinit var btEnviar: Button
    private lateinit var btCancelar: Button
    private lateinit var etTitulo: EditText
    private lateinit var etInfo: EditText
    private lateinit var etLocal: EditText
    private lateinit var ivCamera: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        this.dao = DenunciaDAO(this)

        this.btEnviar = findViewById(R.id.btMainEnviar)
        this.btCancelar = findViewById(R.id.btMainCancelar)
        this.etTitulo = findViewById(R.id.etMainTitulo)
        this.etInfo = findViewById(R.id.etMainInfo)
        this.etLocal = findViewById(R.id.etMainLocalizacao)
        this.ivCamera = findViewById(R.id.ivMainCamera)

        this.btEnviar.setOnClickListener{onClickEnviar(it)}
        this.btCancelar.setOnClickListener{
            this.etTitulo.text = null
            this.etLocal.text = null
            this.etInfo.text = null
            this.ivCamera.setImageBitmap(null)
        }

        fab.setOnClickListener {
            val itFoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(itFoto, CAMERA)
        }
    }

    fun onClickEnviar(view: View) {
        val titulo = this@MainActivity.etTitulo.text.toString()
        val descricao = this@MainActivity.etInfo.text.toString()
        val local = this@MainActivity.etLocal.text.toString()
        val imagem = this@MainActivity.ivCamera.drawable.toBitmap()

        val denuncia = Denuncia(titulo, descricao, "02/11/2019", "DER", local)

        val itResp = Intent(this, ListActivity::class.java)
        itResp.putExtra("DENUNCIA", denuncia)
        this.dao.insert(denuncia)
        Log.i("APP_DENUNCIA", "denuncia ${denuncia} AQUIIIIIIIII")

//            setResult(Activity.RESULT_OK, itResp)
        startActivityForResult(itResp, FORMULARIO)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                val imagem = data?.extras?.get("data") as Bitmap
                this.ivCamera.setImageBitmap(imagem)
            }
//            else if (requestCode == FORMULARIO) {
//                val denuncia = (data?.extras?.get("data")) as Denuncia
//                this.dao.insert(denuncia)
//                Log.i("APP_DENUNCIA", "Denuncia: ")
//
//            }
        }
    }

//    inner class OnClickBotaoEnviar: View.OnClickListener {
//        override fun onClick(v: View?) {
//            val titulo = this@MainActivity.etTitulo.text.toString()
//            val descricao = this@MainActivity.etInfo.text.toString()
//            val local = this@MainActivity.etLocal.text.toString()
//            val imagem = this@MainActivity.ivCamera.drawable.toBitmap()
//
//            val denuncia = Denuncia(titulo, descricao, "02/11/2019", "DER", local)
//
//            val itResp = Intent()
//
//            itResp.putExtra("DENUNCIA", denuncia)
////            setResult(Activity.RESULT_OK, itResp)
//            startActivityForResult(itResp, FORMULARIO)
//            finish()
//        }
//
//    }

}
