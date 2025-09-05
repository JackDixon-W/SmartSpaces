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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.widget.Toast

class MainActivity : ComponentActivity(), SensorEventListener {

    // Sensor Variables
    private lateinit var sensorManager: SensorManager
    // mutableState forces refresh on a change (kinda like React)
    private var isSensorActive by mutableStateOf(false)
    private var accelSensor: Sensor? = null
    private var sensorValue by mutableStateOf("Press the button to start.")

    // Chart variables
    // Entry is a data type that the chart will understand, so it should be worked with throughout
    private val chartEntries = mutableStateListOf<Entry>()
    private var isChartVisible by mutableStateOf(false)
    private var startTime: Long = 0

    // Location Variables
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    companion object {
        const val fineLoc = Manifest.permission.ACCESS_FINE_LOCATION
        const val coarseLoc = Manifest.permission.ACCESS_COARSE_LOCATION

        const val highPri = com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
    }

    // Filter variables
    private lateinit var filter: SensorFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locPermChecker()
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
        val acceleration = event.values[0]
        sensorValue = "Acceleration (m/s^2): $acceleration"
        Log.i("SensorData", "Acceleration (m/s^2): $acceleration")

        //val newSensorData = SensorData(System.currentTimeMillis(),acceleration)
        //newSensorData.writeData(this)
        val currentTime = (System.currentTimeMillis() - startTime) / 1000

        // This is where our filtering takes place
        filter.addToBuffer(acceleration)
        val chartVal = filter.findAverage()

        chartEntries.add(Entry(currentTime.toFloat(), chartVal))
        if (chartEntries.size > 1000) {
            chartEntries.removeAt(0)
        }

        // Location Data collection
        if (ContextCompat.checkSelfPermission(
                this,
                fineLoc
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.getCurrentLocation(
                highPri,
                null
            ).addOnSuccessListener { location ->
                val lat = location?.latitude?.toFloat()
                val lon = location?.longitude?.toFloat()
            }
        }
    }

    private val locPermRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineGranted = permissions[fineLoc] ?: false
            val coarseGranted = permissions[coarseLoc] ?: false

            if (fineGranted || coarseGranted) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun locPermChecker() {
        val fineLocation =
            ContextCompat.checkSelfPermission(this, fineLoc)
        val coarseLocation =
            ContextCompat.checkSelfPermission(this, coarseLoc)
        if (fineLocation != PackageManager.PERMISSION_GRANTED &&
            coarseLocation != PackageManager.PERMISSION_GRANTED) {
            // Launch permission request
            locPermRequest.launch(
                arrayOf(
                    fineLoc,
                    coarseLoc
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

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Start the sensor
            Button(onClick = {
                isSensorActive = !isSensorActive

                if (isSensorActive) {
                    startTime = System.currentTimeMillis()
                    sensorValue = "Listening for sensor..."
                    // Listener being registered is equivalent to turning it on
                    accelSensor?.also { acceleration ->
                        sensorManager.registerListener(this@MainActivity, acceleration, SensorManager.SENSOR_DELAY_NORMAL)
                    }
                    isChartVisible = true
                } else {
                    sensorValue = "Sensor is off"
                    sensorManager.unregisterListener(this@MainActivity)
                }
            }) {
                Text(if (isSensorActive) "Turn Off Sensor" else "Turn On Sensor")
            }
            Text(text = sensorValue, modifier = Modifier.padding(top = 16.dp))

            /* THIS IS THE PART YOU ADD AFTER THE OTHERS
            Button(onClick = {
                if (!isSensorActive) {
                    val clearer = SensorDataManager()
                    clearer.clearFile(context)
                }
            }) {
                Text("Clear data.txt")
            } */
            if (isChartVisible) {
                LineChartComposable(entries = chartEntries)
            }

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

    @Composable
    fun LineChartComposable(entries: List<Entry>) {
        // Context has to be grabbed inside these functions
        val context = LocalContext.current

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            // Init the Chart
            factory = { context ->
                LineChart(context).apply {
                    description.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    setPinchZoom(true)
                }
            },
            // On an update
            update = { chart ->
                val dataSet = LineDataSet(entries, "m/s^2").apply {
                    setDrawCircles(false)
                    setDrawValues(true)
                    lineWidth = 5f
                    color = context.getColor(android.R.color.holo_blue_light)
                }

                chart.axisLeft.axisMinimum = -10f
                chart.axisLeft.axisMaximum = 10f
                chart.axisRight.isEnabled = false

                val lineData = LineData(dataSet)
                chart.data = lineData

                // Refresh the chart
                chart.invalidate()
            }
        )
    }
}