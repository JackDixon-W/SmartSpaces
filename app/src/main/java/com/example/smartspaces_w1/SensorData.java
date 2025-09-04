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

    public SensorData(long time, float value) {
        this.time = time;
        this.value = value;
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

    public void writeData(Context context) {
        String val = Float.toString(this.value);
        String time = Long.toString(this.time);
        String finalWrite = "Time=" + time + "\n" +
                            "Accel=" + val + "\n" +
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
