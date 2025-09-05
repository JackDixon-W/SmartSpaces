package com.example.smartspaces_w1;

import java.util.ArrayList;
import java.util.List;

public class SensorFilter {
    private List<Float> dataBuffer = new ArrayList<>();
    private final float bufferSize = 10;

    public void addToBuffer(float x)
    {
        dataBuffer.add(x);
        if (dataBuffer.size() >= bufferSize)
        {
            // When buffer exceeds the allowed size, trim it
            dataBuffer.remove(0);
        }
    }

    public float findAverage()
    {
        float avg = 0;
        for (Float num : dataBuffer)
        {
            avg += num;
        }
        avg = avg / bufferSize;
        return avg;
    }
}
