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
    private float latitude;
    private float longitude;

    public SensorData(long time, float value, float latitude, float longitude) {
        this.time = time;
        this.value = value;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void writeData(Context context) {
        String val = Float.toString(this.value);
        String time = Long.toString(this.time);
        String lat = Float.toString(this.latitude);
        String longt = Float.toString(this.longitude);
        String finalWrite = "Time=" + time + "\n" +
                            "Accel=" + val + "\n" +
                            "Latitude=" + lat + "\n" +
                            "Longitude=" + longt + "\n" +
                            "-\n";
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("data.txt", Context.MODE_APPEND));
            outputStreamWriter.write(finalWrite);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
