package de.sharknoon.officesense.models

import de.sharknoon.officesense.R
import org.joda.time.LocalDateTime

enum class Sensors(val sensorName: Int, val graph: Int, val graphColor: Int, val valueGetter: (Value) -> Float) {
    TEMPERATURE(R.string.temperature, R.id.temperatureGraph, R.color.colorTemperature, { it.temperature.toFloat() }),
    LIGHT(R.string.light, R.id.lightGraph, R.color.colorLight, { 0.0F }),
    HUMIDITY(R.string.humidity, R.id.humidityGraph, R.color.colorHumidity, { 0.0F }),
    NOISE(R.string.noise, R.id.noiseGraph, R.color.colorNoise, { 0.0F })
}

//Ignores the date to parse a lot faster
data class Value(val id: LocalDateTime, val temperature: Double)