package de.sharknoon.officesense.utils

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import de.sharknoon.officesense.R
import org.threeten.bp.LocalDateTime


fun Double.cut(decimals: Int): Double {
    val multiplier = Math.pow(10.0, decimals.toDouble())
    return (this * multiplier).toInt() / multiplier
}

fun AppCompatActivity.openFragment(fragment: Fragment) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.container, fragment)
    transaction.addToBackStack(null)
    transaction.commit()
}

val localDateTimeSerializer: JsonSerializer<LocalDateTime> = JsonSerializer { src, _, _ -> if (src == null) null else JsonPrimitive(src.toString()) }

var localDateTimeDeserializer: JsonDeserializer<LocalDateTime> = JsonDeserializer { json, _, _ ->
    LocalDateTime.parse(json?.asString ?: "")
}