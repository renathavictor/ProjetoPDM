package com.example.projetopdm

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity(), SensorEventListener  {

    val CAMERA = 1
    val FORMULARIO = 2
    private lateinit var dao: DenunciaDAO
    private lateinit var btEnviar: Button
    private lateinit var btVoltar: Button
    private lateinit var etTitulo: EditText
    private lateinit var etInfo: EditText
    private lateinit var tvLocal: TextView
    private lateinit var ivCamera: ImageView

    // sensor
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    private lateinit var root: View
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        this.dao = DenunciaDAO(this)

        this.btEnviar = findViewById(R.id.btMainEnviar)
        this.btVoltar = findViewById(R.id.btMainVoltar)
        this.etTitulo = findViewById(R.id.etMainTitulo)
        this.etInfo = findViewById(R.id.etMainInfo)
        this.tvLocal = findViewById(R.id.etMainLocalizacao)
        this.ivCamera = findViewById(R.id.ivMainCamera)

        display_img.visibility = View.INVISIBLE
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        this.btVoltar.setOnClickListener{
            val it = Intent(this, ListActivity::class.java)
            startActivity(it)
            finish()
        }

        fab.setOnClickListener {
            val itFoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(itFoto, CAMERA)
        }

        val denuncia = intent.getSerializableExtra("DENUNCIA")
        if (denuncia != null){
            this.etTitulo.text.append((denuncia as Denuncia).titulo)
            this.etInfo.text.append((denuncia as Denuncia).descricao)
            this.tvLocal.text = ((denuncia as Denuncia).localizacao)
            this.ivCamera.setImageBitmap((BitmapFactory.decodeByteArray(((denuncia as Denuncia).foto), 0, ((denuncia as Denuncia).foto).size)) as Bitmap)
            this.btEnviar.setOnClickListener{
                val denunciaEdit = Denuncia(
                    this.etTitulo.text.toString(),
                    this.etInfo.text.toString(),
                    denuncia.data,
                    "DER",
                    this.tvLocal.text.toString(),
                    toByteArrayImg(this.ivCamera.drawable.toBitmap())
                )
                val intent = Intent(this, ListActivity::class.java)
                intent.putExtra("DENUNCIA_EDIT", denunciaEdit)

                this.dao.update(denuncia.id, denunciaEdit)
                startActivity(intent)
            }
        }else {
            getLastLocation()
            this.btEnviar.setOnClickListener{onClickEnviar(it)}
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickEnviar(view: View) {
        val titulo = this@MainActivity.etTitulo.text.toString()
        val descricao = this@MainActivity.etInfo.text.toString()
        val local = this@MainActivity.tvLocal.text.toString()
        val imagem = this@MainActivity.ivCamera.drawable.toBitmap()
        val simpleData = SimpleDateFormat("dd-MM-yyyy")
        val data = Date()
        val dataFormatada = simpleData.format(data)

        val denuncia = Denuncia(titulo, descricao, dataFormatada, "DER", local, toByteArrayImg(imagem))

        val itResp = Intent(this, ListActivity::class.java)
        itResp.putExtra("DENUNCIA", denuncia)
//        onClickEmail(denuncia)
        this.dao.insert(denuncia)
        startActivityForResult(itResp, FORMULARIO)
    }


    fun onClickEmail(denuncia: Denuncia){
        Toast.makeText(this, "Chegou aqui", Toast.LENGTH_SHORT).show()
        val uri = Uri.parse("mailto:renatha.victor@academico.ifpb.edu.br")
        val it = Intent(Intent.ACTION_SENDTO, uri)
        it.putExtra(Intent.EXTRA_SUBJECT, "${denuncia.titulo}")
        it.putExtra(Intent.EXTRA_TEXT, "Descricao da denuncia: ${denuncia.descricao} \n Local: ${denuncia.localizacao} \n Data: ${denuncia.data}")
        startActivity(it)
    }

    fun toByteArrayImg(img: Bitmap): ByteArray {
        var byteImg: ByteArrayOutputStream = ByteArrayOutputStream()
        img.compress(Bitmap.CompressFormat.JPEG, 100, byteImg)
        return byteImg.toByteArray()
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

    // SENSOR

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
    var isRunning = false

    override fun onSensorChanged(event: SensorEvent?) {
        try {
            if (event!!.values[0] == 0F && isRunning == false) {
                var value = event.values[0]
                isRunning = true
                display_img.visibility = View.VISIBLE
            } else {
                isRunning = false
                display_img.visibility = View.INVISIBLE
            }
        } catch (e: IOException) {

        }
    }


    //LOCALIZAÇÃO

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData()
//                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
//                    var location: Location? = task.result
//                    if (location == null) {
//                        requestNewLocationData()
//                    } else {
//                        this.tvLocal.setText("Latitude: "+ location.latitude.toString()+" Longitude: "+location.longitude.toString())
//                    }
//                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            this@MainActivity.tvLocal.setText("Latitude: "+mLastLocation.latitude.toString()+" Longitude: "+mLastLocation.longitude.toString())
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

}
