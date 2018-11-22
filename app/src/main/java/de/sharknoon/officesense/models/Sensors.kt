package de.sharknoon.officesense.models

import de.sharknoon.officesense.R

enum class Sensors(val sensorName: Int,
                   val graph: Int,
                   val graphColor: Int,
                   val valueGetter: (History.Value) -> Float,
                   val valuesGetter:(Values)->Double,
                   val textView:Int,
                   val unit: Int) {
    TEMPERATURE(
            R.string.temperature,
            R.id.temperatureGraph,
            R.color.colorTemperature,
            { it.temperature.toFloat() },
            { it.temperature },
            R.id.textViewTemperature,
            R.string.unit_temperature
    ),
    LIGHT(
            R.string.light,
            R.id.lightGraph,
            R.color.colorLight,
            { it.light.toFloat() },
            { it.light },
            R.id.textViewLight,
            R.string.unit_light
    ),
    HUMIDITY(
            R.string.humidity,
            R.id.humidityGraph,
            R.color.colorHumidity,
            { it.humidity.toFloat() },
            { it.humidity },
            R.id.textViewHumidity,
            R.string.unit_humidity
    ),
    NOISE(
            R.string.noise,
            R.id.noiseGraph,
            R.color.colorNoise,
            { it.noise.toFloat() },
            { it.noise },
            R.id.textViewNoise,
            R.string.unit_noise
    );

    fun getURLName() = name.toLowerCase()
}
