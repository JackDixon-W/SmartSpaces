package com.example.smartspaces_w1

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Toast


class MainActivity : ComponentActivity(), SensorEventListener {
    private var isGood = false
    private lateinit var sensorManager: SensorManager
    // mutableState forces refresh on a change (kinda like React)

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isSensorActive by mutableStateOf(false)
    private var accelSensor: Sensor? = null
    private var sensorValue by mutableStateOf("Press the button to start.")

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineGranted || coarseGranted) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()
        setContent {
            SensorUI()
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }

    override fun onSensorChanged(event: SensorEvent) {
        val acceleration = event.values[2]

        sensorValue = "Acceleration (m/s^2): $acceleration"
        Log.i("SensorData", "Acceleration (m/s^2): $acceleration")

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    val lat = location?.latitude?.toFloat() ?: 0f
                    val lon = location?.longitude?.toFloat() ?: 0f

                    val newSensorData = SensorData(
                        System.currentTimeMillis(),
                        acceleration,
                        lat,
                        lon
                    )
                    newSensorData.writeData(this)
                }
                .addOnFailureListener { exception ->
                    Log.e("LocationError", "Failed to get location: ${exception.message}")

                    // Write sensor data without location
                    val newSensorData = SensorData(
                        System.currentTimeMillis(),
                        acceleration,
                        0f,
                        0f
                    )
                    newSensorData.writeData(this)
                }
        } else {
            Log.w("PermissionCheck", "Location permission not granted, saving without location")

            // No location permission → write only accelerometer data
            val newSensorData = SensorData(
                System.currentTimeMillis(),
                acceleration,
                0f,
                0f
            )
            newSensorData.writeData(this)
        }
    }

    private fun checkLocationPermission() {
        val fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineLocation != PackageManager.PERMISSION_GRANTED &&
            coarseLocation != PackageManager.PERMISSION_GRANTED) {
            // Launch permission request
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @Composable
    fun SensorUI() {
        // Context has to be grabbed inside these functions
        val context = LocalContext.current
        // Remember is needed to keep the state when the UI is redrawn
        // (Not really necessary for our purpose but it throws an error if I don't)
        var readContent by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Start the sensor
            Button(onClick = {
                isSensorActive = !isSensorActive

                if (isSensorActive) {
                    sensorValue = "Listening for sensor..."
                    // Listener being registered is equivalent to turning it on
                    accelSensor?.also { acceleration ->
                        sensorManager.registerListener(
                            this@MainActivity,
                            acceleration,
                            SensorManager.SENSOR_DELAY_NORMAL
                        )
                    }
                } else {
                    sensorValue = "Sensor is off"
                    sensorManager.unregisterListener(this@MainActivity)
                }
            }) {
                Text(if (isSensorActive) "Turn Off Sensor" else "Turn On Sensor")
            }
            Text(text = sensorValue, modifier = Modifier.padding(top = 16.dp))

            // Read stored data (persists between runs)
            Button(onClick = {
                if (!isSensorActive) {
                    // Data should not be read while sensor is active
                    val reader = SensorDataManager()
                    readContent = reader.readFromFile(context)
                }
            }) {
                Text(if (isSensorActive) "Turn Off Sensor to Read" else "Read Sensor Data")
            }

            if (readContent.isNotEmpty()) {
                Text(text = readContent)
            }
        }
    }
}