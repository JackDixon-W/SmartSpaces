package com.example.smartspaces_w1;

import java.util.ArrayList;
import java.util.List;

public class SensorFilter {
    private List<Float> dataBuffer = new ArrayList<>();
    private final int bufferSize = 20;
    private float total= 0;
    private int current_index= 0;
    private float avgVal = 0;
    private float threshold = 5;

    public float getAvgVal() {
        return avgVal;
    }

    public float movingAverage(float newVal) {
        if (dataBuffer.size() < bufferSize) {
            total += newVal;
            dataBuffer.add(newVal);
        } else {
            total = total - dataBuffer.get(current_index) + newVal;
            dataBuffer.set(current_index, newVal);
            current_index = (current_index + 1) % bufferSize;
        }
        avgVal = total / dataBuffer.size();
        return avgVal;
    }

    public boolean isAnomaly(float newVal){
        return Math.abs(newVal - avgVal) > threshold;
    }
}
