package de.sharknoon.officesense.models

data class History(val measurementValues: List<Value>) {

    //Ignores the date to parse a lot faster
    data class Value(/*val id: LocalDateTime, */val temperature: Double)
}

