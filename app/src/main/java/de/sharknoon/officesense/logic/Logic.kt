package de.sharknoon.officesense.logic

import de.sharknoon.officesense.models.RoomStates
import de.sharknoon.officesense.models.RoomStates.*
import de.sharknoon.officesense.models.Sensors
import de.sharknoon.officesense.models.Values
import kotlin.math.roundToInt

fun checkForRoomClimateAdjustments(v: Values): Map<Sensors, Adjustments> {
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


fun checkForRoomClimatePoints(v: Values): Int {
    var allPoints = 0
    enumValues<Sensors>().forEach {
        val stateRanges = it.stateRanges
        val value = it.currentValueGetter.invoke(v)
        allPoints += when (value.toInt()) {
            in (stateRanges[GREAT]?.first ?: -1)..(stateRanges[GREAT]?.second
                    ?: -1) -> it.multiplierFactor * 10
            in (stateRanges[GOOD]?.first ?: -1)..(stateRanges[GOOD]?.second
                    ?: -1) -> (it.multiplierFactor * 7.5).roundToInt()
            in (stateRanges[BAD]?.first ?: -1)..(stateRanges[BAD]?.second
                    ?: -1) -> (it.multiplierFactor * 2.5).roundToInt()
            in (stateRanges[HORRIBLE]?.first ?: -1)..(stateRanges[HORRIBLE]?.second
                    ?: -1) -> it.multiplierFactor * 0
            else -> 0
        }
    }
    return allPoints
}

fun checkForRoomClimateState(points: Int): RoomStates {
    return when {
        points < 20 -> HORRIBLE
        points < 40 -> BAD
        points < 60 -> OKAY
        points < 80 -> GOOD
        else -> GREAT
    }
}

enum class Adjustments {
    TOO_HIGH,
    TOO_LOW,

}