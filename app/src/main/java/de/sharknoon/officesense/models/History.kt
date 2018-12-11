package de.sharknoon.officesense.models

import org.threeten.bp.LocalDateTime

data class History(val measurementValues: List<Value>) {
    data class Value(val id: LocalDateTime, val temperature: Double, val light: Double, val humidity: Double)
}

