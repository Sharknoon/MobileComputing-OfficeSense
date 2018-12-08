package de.sharknoon.officesense.models

import de.sharknoon.officesense.R
import de.sharknoon.officesense.utils.cut

enum class Sensors(val sensorName: Int,
                   val graph: Int,
                   val graphColor: Int,
                   val historyValueGetter: (History.Value) -> Float,
                   val currentValueGetter: (Values) -> Double,
                   val currentValueStringGetter: (Values) -> String,
                   val textView: Int,
                   val icon: Int,
                   val unit: Int,
                   val minValue: Double,
                   val maxValue: Double) {
    TEMPERATURE(
            R.string.temperature,
            R.id.temperatureGraph,
            R.color.colorTemperature,
            { it.temperature.toFloat() },
            { it.temperature },
            { it.temperature.cut(2).toString() },
            R.id.textViewTemperature,
            R.drawable.ic_temperature_filled,
            R.string.unit_temperature,
            15.0,
            28.0
    ),
    LIGHT(
            R.string.light,
            R.id.lightGraph,
            R.color.colorLight,
            { it.light.toFloat() },
            { it.light },
            { it.light.toInt().toString() },
            R.id.textViewLight,
            R.drawable.ic_light_filled,
            R.string.unit_light,
            40.0,
            300.0
    ),
    HUMIDITY(
            R.string.humidity,
            R.id.humidityGraph,
            R.color.colorHumidity,
            { it.humidity.toFloat() },
            { it.humidity },
            { it.humidity.cut(2).toString() },
            R.id.textViewHumidity,
            R.drawable.ic_humidity_filled,
            R.string.unit_humidity,
            40.0,
            70.0

    ),
    NOISE(
            R.string.noise,
            R.id.noiseGraph,
            R.color.colorNoise,
            { it.noise.toFloat() },
            { it.noise },
            { it.noise.cut(2).toString() },
            R.id.textViewNoise,
            R.drawable.ic_noise_filled,
            R.string.unit_noise,
            20.0,
            60.0
    );

    fun getURLName() = name.toLowerCase()
}
