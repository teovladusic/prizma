package com.prizma_distribucija.prizma.feature_track_location.presentation.track_location

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.*
import com.prizma_distribucija.prizma.feature_track_location.domain.GoogleMapManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class BearingCalculator(context: Context) : SensorEventListener {

    private var sensorManager: SensorManager =
        context.getSystemService(SENSOR_SERVICE) as SensorManager


    companion object {
        var currentBearing = 0f
    }

    private var accelerometerValues = FloatArray(3)
    private var magneticValues = FloatArray(3)


    override fun onSensorChanged(p0: SensorEvent?) {
        p0?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = it.values.clone()
            } else {
                magneticValues = it.values.clone()
            }

            val r = FloatArray(9)
            val values = FloatArray(3)

            SensorManager.getRotationMatrix(r, null, accelerometerValues, magneticValues)
            SensorManager.getOrientation(r, values)

            currentBearing = Math.toDegrees(values[0].toDouble()).toFloat()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    fun registerListener() {
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun unregisterListener() {
        sensorManager.unregisterListener(this)
    }
}