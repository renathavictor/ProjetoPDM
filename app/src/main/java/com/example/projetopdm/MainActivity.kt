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
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity(), SensorEventListener  {

    val CAMERA = 1
    val FORMULARIO = 2
    private lateinit var dao: DenunciaDAO
    private lateinit var btEnviar: Button
    private lateinit var btVoltar: Button
//    private lateinit var btCancelar: Button
    private lateinit var etTitulo: EditText
    private lateinit var etInfo: EditText
    private lateinit var etLocal: EditText
    private lateinit var ivCamera: ImageView

    // sensor
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    private lateinit var root: View


    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        this.dao = DenunciaDAO(this)

        this.btEnviar = findViewById(R.id.btMainEnviar)
//        this.btCancelar = findViewById(R.id.btMainCancelar)
        this.btVoltar = findViewById(R.id.btMainVoltar)
        this.etTitulo = findViewById(R.id.etMainTitulo)
        this.etInfo = findViewById(R.id.etMainInfo)
        this.etLocal = findViewById(R.id.etMainLocalizacao)
        this.ivCamera = findViewById(R.id.ivMainCamera)


        display_img.visibility = View.INVISIBLE
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

//        this.btCancelar.setOnClickListener{
//            this.etTitulo.text = null
//            this.etLocal.text = null
//            this.etInfo.text = null
//            this.ivCamera.setImageBitmap(null)
//        }
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
            this.etLocal.text.append((denuncia as Denuncia).localizacao)
            this.ivCamera.setImageBitmap((BitmapFactory.decodeByteArray(((denuncia as Denuncia).foto), 0, ((denuncia as Denuncia).foto).size)) as Bitmap)
            this.btEnviar.setOnClickListener{
                val denunciaEdit = Denuncia(
                    this.etTitulo.text.toString(),
                    this.etInfo.text.toString(),
                    "02/11/2019",
                    "DER",
                    this.etLocal.text.toString(),
                    toByteArrayImg(this.ivCamera.drawable.toBitmap())
                )
                val intent = Intent(this, ListActivity::class.java)
                intent.putExtra("DENUNCIA_EDIT", denunciaEdit)

                this.dao.update(denuncia.id, denunciaEdit)
                startActivity(intent)
            }
        }else {
            this.btEnviar.setOnClickListener{onClickEnviar(it)}
        }
        getLastLocation()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    fun onClickEnviar(view: View) {
        val titulo = this@MainActivity.etTitulo.text.toString()
        val descricao = this@MainActivity.etInfo.text.toString()
        val local = this@MainActivity.etLocal.text.toString()
        val imagem = this@MainActivity.ivCamera.drawable.toBitmap()

        val denuncia = Denuncia(titulo, descricao, "02/11/2019", "DER", local, toByteArrayImg(imagem))

        val itResp = Intent(this, ListActivity::class.java)
        itResp.putExtra("DENUNCIA", denuncia)
//        onClickEmail(denuncia)
        this.dao.insert(denuncia)
        Log.i("APP_DENUNCIA", "denuncia ${denuncia} AQUIIIIIIIII")

//            setResult(Activity.RESULT_OK, itResp)
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

    // SENSOR

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
    var isRunning = false

    override fun onSensorChanged(event: SensorEvent?) {
        try {
            if (event!!.values[0] < 40 && isRunning == false) {
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

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        this.etLocal.setText("Latitude: "+ location.latitude.toString()+" Longitude: "+location.longitude.toString())
//                        findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
//                        findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                    }
                }
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
            this@MainActivity.etLocal.setText("Latitude: "+mLastLocation.latitude.toString()+" Longitude: "+mLastLocation.longitude.toString())
//            findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
//            findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
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
