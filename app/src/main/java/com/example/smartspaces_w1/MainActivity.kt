package com.example.smartspaces_w1

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

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    // mutableState forces refresh on a change (kinda like React)
    private var isSensorActive by mutableStateOf(false)
    private var accelSensor: Sensor? = null
    private var sensorValue by mutableStateOf("Press the button to start.")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

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

    }

    @Composable
    fun SensorUI() {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Start the sensor
            Button(onClick = {
                isSensorActive = !isSensorActive

                if (isSensorActive) {
                    sensorValue = "Listening for sensor..."
                    // Listener being registered is equivalent to turning it on
                    accelSensor?.also { acceleration ->
                        sensorManager.registerListener(this@MainActivity, acceleration, SensorManager.SENSOR_DELAY_NORMAL)
                    }
                } else {
                    sensorValue = "Sensor is off"
                    sensorManager.unregisterListener(this@MainActivity)
                }
            }) {
                Text(if (isSensorActive) "Turn Off Sensor" else "Turn On Sensor")
            }
            Text(text = sensorValue, modifier = Modifier.padding(top = 16.dp))
        }
    }
}