package de.sharknoon.officesense.utils

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import de.sharknoon.officesense.R

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