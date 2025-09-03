package com.example.smartspaces_w1

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow


/*
The purpose of this is to add an extra layer of abstraction from the database itself
Makes it easier to access and update data
 */
class SensorDataRepository(private val sensorDataDao: SensorDataDao) {
    val allSensorData: Flow<List<SensorData>> = sensorDataDao.getAll()

    @WorkerThread
    suspend fun insertSensorData(sensorData: SensorData)
    {
        sensorDataDao.insertSensorData(sensorData)
    }

    @WorkerThread
    suspend fun updateSensorData(sensorData: SensorData)
    {
        sensorDataDao.updateSensorData(sensorData)
    }
}