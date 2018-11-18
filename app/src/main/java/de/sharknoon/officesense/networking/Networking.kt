package de.sharknoon.officesense.networking

import android.content.Context
import android.support.v7.preference.PreferenceManager
import android.widget.Toast
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import de.sharknoon.officesense.models.History
import java.nio.charset.StandardCharsets
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


private val parser = Gson()

fun <T> getSensorValue(context: Context, clazz: Class<T>, sensorValueConsumer: ((t: T) -> Unit)? = { }, onFailure: (() -> Unit)? = { }) {
    val baseUrl = PreferenceManager.getDefaultSharedPreferences(context).getString("temperatureURL", "")
            ?: ""
    executeGet(url = baseUrl, responseHandler = object : AsyncHttpResponseHandler() {
        override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
            val response = responseBody.toString(StandardCharsets.UTF_8)
            val sensorValue = parser.fromJson<T>(response, clazz)
            sensorValueConsumer?.invoke(sensorValue)
        }

        override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
            Toast.makeText(context, "Server unavailable", Toast.LENGTH_SHORT).show()
            onFailure?.invoke()
        }
    })
}

fun getSensorHistory(context: Context, sensorHistoryConsumer: ((h: History) -> Unit)? = {}, onFailure: (() -> Unit)? = {}) {
    val baseUrl = PreferenceManager.getDefaultSharedPreferences(context).getString("historyurl", "")
            ?: ""
    val dateInstance = SimpleDateFormat.getDateInstance(DateFormat.)
    val strDate = dateInstance.format(Calendar.getInstance().time)
    val pars = RequestParams()
    pars.put("dayId", strDate)

    executeGet("http://htwg.sharknoon.de/officesense/temperaturePerDay", pars, object : AsyncHttpResponseHandler() {
        override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
            val response = responseBody.toString(StandardCharsets.UTF_8)
            val sensorHistory = parser.fromJson<History>(response, History::class.java)
            sensorHistoryConsumer?.invoke(sensorHistory)
        }

        override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
            Toast.makeText(context, "Server unavailable", Toast.LENGTH_SHORT).show()
            onFailure?.invoke()
        }

    })
}

private val client = AsyncHttpClient()

fun executeGet(url: String, params: RequestParams? = null, responseHandler: AsyncHttpResponseHandler) {
    client.get(url, params, responseHandler)
}