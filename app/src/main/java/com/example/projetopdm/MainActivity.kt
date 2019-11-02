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



class MainActivity : AppCompatActivity() {

    val CAMERA = 1
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

        this.btEnviar = findViewById(R.id.btMainEnviar)
        this.btCancelar = findViewById(R.id.btMainCancelar)
        this.etTitulo = findViewById(R.id.etMainTitulo)
        this.etInfo = findViewById(R.id.etMainInfo)
        this.etLocal = findViewById(R.id.etMainLocalizacao)
        this.ivCamera = findViewById(R.id.ivMainCamera)

        this.btEnviar.setOnClickListener{OnClickBotaoEnviar()}
        this.btCancelar.setOnClickListener{
            finish()
        }

        fab.setOnClickListener { view ->
            val itFoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(itFoto, CAMERA)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                val imagem = data?.extras?.get("data") as Bitmap
                this.ivCamera.setImageBitmap(imagem)
            }
        }
    }

    inner class OnClickBotaoEnviar: View.OnClickListener {
        override fun onClick(v: View?) {
            val titulo = this@MainActivity.etTitulo.text.toString()
            val descricao = this@MainActivity.etInfo.text.toString()
            val local = this@MainActivity.etLocal.text.toString()
            val imagem = this@MainActivity.ivCamera.drawable.toBitmap()

            val res = "${titulo} - ${descricao} - ${imagem}"
            val denuncia = Denuncia(titulo, descricao, "02/11/2019", "DER", "Aqui")
            val itResp = Intent()

            Log.i("APP_DENUNCIA", res)
            itResp.putExtra("DENUNCIA", res)
            setResult(Activity.RESULT_OK, itResp)
            finish()

        }

    }

}
