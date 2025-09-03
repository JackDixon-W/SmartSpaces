package com.example.smartspaces_w1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDataDao {
    @Query("SELECT * FROM sensor_data")
    fun getAll(): Flow<List<SensorData>>

    @Query("SELECT * FROM sensor_data where sensor_value > :minValue")
    fun getSensorDataMoreThan(minValue: Int): Flow<List<SensorData>>

    @Query("SELECT * FROM sensor_data where sensor_value < :maxValue")
    fun getSensorDataLessThan(maxValue: Int): Flow<List<SensorData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensorData(sensorData: SensorData)

    // These probably won't be used but are good to have
    @Update
    suspend fun updateSensorData(sensorData: SensorData)

    @Delete
    suspend fun deleteSensorData(sensorData: SensorData)
}