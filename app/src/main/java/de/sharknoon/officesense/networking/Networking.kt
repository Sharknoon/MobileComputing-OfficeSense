package de.sharknoon.officesense.networking

import com.google.gson.GsonBuilder
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import de.sharknoon.officesense.models.History
import de.sharknoon.officesense.models.Sensors
import de.sharknoon.officesense.models.Values
import de.sharknoon.officesense.utils.localDateTimeDeserializer
import de.sharknoon.officesense.utils.localDateTimeSerializer
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.nio.charset.StandardCharsets

private val parser = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, localDateTimeSerializer)
        .registerTypeAdapter(LocalDateTime::class.java, localDateTimeDeserializer)
        .create()

fun getSensorValues(
        url: String,
        sensorValuesConsumer: ((v: Values) -> Unit) = {},
        onFailure: ((Throwable) -> Unit) = {}) {

    val baseUrl = "$url/home"

    executeGet(url = baseUrl, responseHandler = object : AsyncHttpResponseHandler() {
        override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
            val response = responseBody.toString(StandardCharsets.UTF_8)
            val sensorValues = parser.fromJson<Values>(response, Values::class.java)
            sensorValuesConsumer.invoke(sensorValues)
        }

        override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
            onFailure.invoke(error ?: Exception())
        }

    })

}

fun getSensorHistory(
        url: String,
        sensor: Sensors,
        sensorHistoryConsumer: ((h: History) -> Unit) = {},
        onFailure: ((Throwable) -> Unit) = {},
        dateRange: DateRanges = DateRanges.DAY) {

    val endpoint = "/${sensor.getURLName()}Per${dateRange.getName()}"
    val baseUrl = url + endpoint

    val pars = RequestParams()
    pars.put(dateRange.getID(), dateRange.getUrlParameter(LocalDateTime.now()))

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

enum class DateRanges(private val urlParameterGetter: (LocalDateTime) -> String) {
    DAY({ it.toLocalDate().toString() }),
    WEEK({ "" }),
    MONTH({ it.format(DateTimeFormatter.ofPattern("yyyy-MM")) }),
    YEAR({ it.year.toString() });

    fun getName() = name.toLowerCase().capitalize()
    fun getID() = name.toLowerCase() + "Id"
    fun getUrlParameter(localDateTime: LocalDateTime) = urlParameterGetter.invoke(localDateTime)
}

private val client = AsyncHttpClient()

fun executeGet(url: String, params: RequestParams? = null, responseHandler: AsyncHttpResponseHandler) {
    client.connectTimeout = 3
    client.get(url, params, responseHandler)
}
