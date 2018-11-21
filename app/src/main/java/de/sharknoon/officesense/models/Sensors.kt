package de.sharknoon.officesense.models

import de.sharknoon.officesense.R
import org.joda.time.LocalDateTime

enum class Sensors(val sensorName: Int,
                   val urlName: String,
                   val graph: Int,
                   val graphColor: Int,
                   val valueGetter: (Value) -> Float,
                   val graphTimeButtons: Int) {
    TEMPERATURE(
            R.string.temperature,
            "temperature",
            R.id.temperatureGraph,
            R.color.colorTemperature,
            { it.temperature.toFloat() },
            R.id.radioButtonsTemperature
    ),
    LIGHT(
            R.string.light,
            "light",
            R.id.lightGraph,
            R.color.colorLight,
            { 0.0F },
            R.id.radioButtonsLight
    ),
    HUMIDITY(
            R.string.humidity,
            "humidity",
            R.id.humidityGraph,
            R.color.colorHumidity,
            { 0.0F },
            R.id.radioButtonsHumidity
    ),
    NOISE(
            R.string.noise,
            "noise",
            R.id.noiseGraph,
            R.color.colorNoise,
            { 0.0F },
            R.id.radioButtonsNoise
    )
}

//Ignores the date to parse a lot faster
data class Value(val id: LocalDateTime, val temperature: Double)