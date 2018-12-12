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
                   val maxValue: Double,
                   val stateRanges: Map<RoomStates, Pair<Int, Int>>,
                   val multiplierFactor: Int) {
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
            19.0,
            25.0,
            mapOf(
                    RoomStates.HORRIBLE to (Int.MIN_VALUE to Int.MAX_VALUE),
                    RoomStates.BAD to (16 to 26),
                    RoomStates.GOOD to (19 to 25),
                    RoomStates.GREAT to (21 to 22)
            ),
            5
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
            300.0,
            1000.0,
            mapOf(
                    RoomStates.HORRIBLE to (Int.MIN_VALUE to Int.MAX_VALUE),
                    RoomStates.BAD to (100 to 2000),
                    RoomStates.GOOD to (300 to 1000),
                    RoomStates.GREAT to (500 to 750)
            ),
            2
    ),
    HUMIDITY(
            R.string.humidity,
            R.id.humidityGraph,
            R.color.colorHumidity,
            { it.humidity.toFloat() },
            { it.humidity },
            { it.humidity.toInt().toString() },
            R.id.textViewHumidity,
            R.drawable.ic_humidity_filled,
            R.string.unit_humidity,
            30.0,
            70.0,
            mapOf(
                    RoomStates.HORRIBLE to (Int.MIN_VALUE to Int.MAX_VALUE),
                    RoomStates.BAD to (20 to 80),
                    RoomStates.GOOD to (30 to 70),
                    RoomStates.GREAT to (40 to 60)
            ),
            3
    );
}
