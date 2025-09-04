package com.example.smartspaces_w1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import java.io.IOException;
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
        String finalWrite = "Time=" + time + "!" +
                            "Accel=" + val + "!" +
                            "Latitude=" + "!" +
                            "Longitude=" + "!";
        try {
            // Writing files in such a publicly accessible way is generally a security risk
            @SuppressLint("WorldReadableFiles")
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("data.txt", Context.MODE_WORLD_READABLE));
            // Data should be a string, will need to format this properly
            //outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
