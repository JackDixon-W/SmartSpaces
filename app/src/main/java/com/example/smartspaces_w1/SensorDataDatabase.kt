package com.example.smartspaces_w1

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*
Had to put in loads of comments because I'd forget immediately how it worked otherwise
 */

// This expects an array of classes, do not change
// Update version if any changes are made to the schema
// Left schema export on true so we can later get the data
@Database(entities = [SensorData::class], version = 1, exportSchema = true)
abstract class SensorDataDatabase: RoomDatabase(){
    abstract fun sensorDataDao(): SensorDataDao

    // A singleton pattern is used to prevent multiple databases
    companion object {
        // The INSTANCE variable is visible to all threads
        @Volatile
        private var INSTANCE: SensorDataDatabase? = null

        fun getDatabase(context: Context): SensorDataDatabase
        {
            // Returns the existing db if it exists
            return INSTANCE ?: synchronized(this)
            {
                // If it doesn't exist, create it
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SensorDataDatabase::class.java,
                    "sensor_data_database",
                ).build()
                // Assign INSTANCE to a variable we can actually return
                INSTANCE = instance
                // Return the new instance
                instance
            }
        }
    }
}