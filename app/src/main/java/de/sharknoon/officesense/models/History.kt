package de.sharknoon.officesense.models

import java.util.*

data class History(val measurementValues: List<Value>)

data class Value(val id: Calendar, val temperature: Double)