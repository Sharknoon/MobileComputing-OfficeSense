package de.sharknoon.officesense.networking

import com.google.gson.GsonBuilder
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import de.sharknoon.officesense.models.History
import de.sharknoon.officesense.models.Values
import de.sharknoon.officesense.utils.localDateTimeDeserializer
import de.sharknoon.officesense.utils.localDateTimeSerializer
import org.threeten.bp.LocalDate
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
        onFailure: ((String) -> Unit) = {}) {

    val baseUrl = "$url/home"

    executeGet(url = baseUrl, responseHandler = object : AsyncHttpResponseHandler() {
        override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
            val response = responseBody.toString(StandardCharsets.UTF_8)
            val sensorValues = parser.fromJson<Values>(response, Values::class.java)
            if (sensorValues == null) {
                val error = parser.fromJson<Error>(response, Error::class.java)
                onFailure.invoke(error.error)
                return
            }
            sensorValuesConsumer.invoke(sensorValues)
        }

        override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
            onFailure.invoke(error?.javaClass?.simpleName?.plus(": " + error.localizedMessage)
                    ?: "Unknown error")
        }

    })

}

fun getSensorsHistory(
        url: String,
        sensorHistoryConsumer: ((h: History) -> Unit) = {},
        onFailure: ((String) -> Unit) = {},
        dateRange: DateRanges = DateRanges.DAY,
        date: LocalDate) {

    val endpoint = "/historyPer${dateRange.getName()}"
    val baseUrl = url + endpoint

    val pars = RequestParams()
    pars.put(dateRange.getURLKey(), dateRange.getURLValue(date))

    executeGet(baseUrl, pars, object : AsyncHttpResponseHandler() {
        override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
            val response = responseBody.toString(StandardCharsets.UTF_8)
            val sensorHistory = parser.fromJson<History>(response, History::class.java)
            if (sensorHistory?.measurementValues == null) {
                val error = parser.fromJson<Error>(response, Error::class.java)
                onFailure.invoke(error.error)
                return
            }
            sensorHistoryConsumer.invoke(sensorHistory)
        }

        override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
            onFailure.invoke(error?.javaClass?.simpleName?.plus(": " + error.localizedMessage)
                    ?: "Unknown error")
        }

    })
}

enum class DateRanges(private val urlParameterGetter: (LocalDate) -> String) {
    DAY({ it.toString() }),
    WEEK({ it.toString() }),
    MONTH({ it.format(DateTimeFormatter.ofPattern("yyyy-MM")) }),
    YEAR({ it.year.toString() });

    fun getName() = name.toLowerCase().capitalize()
    fun getURLKey(): String = if (this == WEEK) DAY.getURLKey() else name.toLowerCase() + "Id"
    fun getURLValue(localDate: LocalDate) = urlParameterGetter.invoke(localDate)
}

class Error(val error: String = "Unknown error")

private val client = AsyncHttpClient()

fun executeGet(url: String, params: RequestParams? = null, responseHandler: AsyncHttpResponseHandler) {
    client.setMaxRetriesAndTimeout(1, 3)
    client.setTimeout(3)
    client.get(url, params, responseHandler)
}
