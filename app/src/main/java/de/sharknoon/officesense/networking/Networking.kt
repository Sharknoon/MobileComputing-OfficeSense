package de.sharknoon.officesense.networking

import android.content.Context
import android.support.v7.preference.PreferenceManager
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import de.sharknoon.officesense.models.History
import de.sharknoon.officesense.models.Sensors
import de.sharknoon.officesense.utils.localDateTimeDeserializer
import de.sharknoon.officesense.utils.localDateTimeSerializer
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import java.nio.charset.StandardCharsets

private val parser = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, localDateTimeSerializer)
        .registerTypeAdapter(LocalDateTime::class.java, localDateTimeDeserializer)
        .create()

fun <T> getSensorValue(context: Context, clazz: Class<T>, sensorValueConsumer: ((t: T) -> Unit) = { }, onFailure: (() -> Unit) = { }) {
    val baseUrl = PreferenceManager.getDefaultSharedPreferences(context).getString("temperatureURL", "")
            ?: ""
    executeGet(url = "http://htwg.sharknoon.de/officesense/home", responseHandler = object : AsyncHttpResponseHandler() {
        override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
            val response = responseBody.toString(StandardCharsets.UTF_8)
            val sensorValue = parser.fromJson(response, clazz)
            sensorValueConsumer.invoke(sensorValue)
        }

        override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
            Toast.makeText(context, "Server unavailable", Toast.LENGTH_SHORT).show()
            onFailure.invoke()
        }
    })
}

fun getSensorHistory(
        url: String,
        sensor: Sensors,
        sensorHistoryConsumer: ((h: History) -> Unit) = {},
        onFailure: ((Throwable) -> Unit) = {},
        dateRange: DateRanges = DateRanges.DAY) {

    val endpoint = "/${sensor.urlName}Per${dateRange.url}"
    val baseUrl = url + endpoint

    val pars = RequestParams()
    pars.put(dateRange.id, LocalDate.now().toString())

    executeGet(baseUrl, pars, object : AsyncHttpResponseHandler() {
        override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
            val response = responseBody.toString(StandardCharsets.UTF_8)
            val sensorHistory = parser.fromJson<History>(response, History::class.java)
            if (sensorHistory?.measurementValues == null) {
                onFailure(400, null, null, null)
            }
            sensorHistoryConsumer.invoke(sensorHistory)
        }

        override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
            onFailure.invoke(error ?: Exception())
        }

    })
}

enum class DateRanges(val url: String, val id: String) {
    DAY("Day", "dayId"), WEEK("Week", "weekId"), MONTH("Month", "monthId"), YEAR("Year", "yearId")
}

private val client = AsyncHttpClient()

fun executeGet(url: String, params: RequestParams? = null, responseHandler: AsyncHttpResponseHandler) {
    client.connectTimeout = 3
    client.get(url, params, responseHandler)
}
