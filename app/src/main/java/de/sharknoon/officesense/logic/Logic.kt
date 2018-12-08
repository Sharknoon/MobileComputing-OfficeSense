package de.sharknoon.officesense.logic

import de.sharknoon.officesense.models.Sensors
import de.sharknoon.officesense.models.Values

fun checkForRoomClimate(v: Values): Map<Sensors, Adjustments> {
    val results = mutableMapOf<Sensors, Adjustments>()
    enumValues<Sensors>().forEach { it ->
        if (it.currentValueGetter.invoke(v) > it.maxValue) {
            results[it] = Adjustments.TOO_HIGH
        } else if (it.currentValueGetter.invoke(v) < it.minValue) {
            results[it] = Adjustments.TOO_LOW
        }
    }
    return results
}

enum class Adjustments {
    TOO_HIGH,
    TOO_LOW,

}