package de.sharknoon.officesense.models

import de.sharknoon.officesense.R

enum class Sensors(val sensorName: Int, val graph: Int, val graphColor: Int) {
    TEMPERATURE(R.string.temperature, R.id.temperatureGraph, R.color.colorTemperature),
    LIGHT(R.string.light, R.id.lightGraph, R.color.colorLight),
    HUMIDITY(R.string.humidity, R.id.humidityGraph, R.color.colorHumidity),
    NOISE(R.string.noise, R.id.noiseGraph, R.color.colorNoise)
}