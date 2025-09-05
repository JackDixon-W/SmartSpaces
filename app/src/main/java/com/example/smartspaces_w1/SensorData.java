package com.example.smartspaces_w1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SensorData {
    private long time;
    private float value;
    private float longitude;
    private float latitude;

    public SensorData(long time, float value, float longitude, float latitude) {
        this.time = time;
        this.value = value;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void writeData(Context context) {
        String val = Float.toString(this.value);
        String time = Long.toString(this.time);
        String longitude = Float.toString(this.longitude);
        String latitude = Float.toString(this.latitude);
        String finalWrite = "Time = " + time + "\n" +
                            "Accel = " + val + "\n" +
                            "Longitude = " + longitude + "\n" +
                            "Latitude = " + latitude + "\n" +
                            "-\n";
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("anomalies.txt", Context.MODE_APPEND));
            outputStreamWriter.write(finalWrite);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
