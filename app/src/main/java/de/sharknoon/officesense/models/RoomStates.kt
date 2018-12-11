package de.sharknoon.officesense.models

import de.sharknoon.officesense.R

enum class RoomStates(val icon: Int, val status: String) {
    HORRIBLE(R.drawable.ic_emoji_dead, "The room climate is very bad \u2639\uFE0F"),
    BAD(R.drawable.ic_emoji_bad, "The room climate is bad \uD83E\uDD28"),
    OKAY(R.drawable.ic_emoji_okay, "The room climate is okay \uD83D\uDC4D"),
    GOOD(R.drawable.ic_emoji_good, "Hurray, the room climate is good \uD83D\uDC4C"),
    GREAT(R.drawable.ic_emoji_great, "Wow, the room climate is excellent \uD83D\uDE4C")
}