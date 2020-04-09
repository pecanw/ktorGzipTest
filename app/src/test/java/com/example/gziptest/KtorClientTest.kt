package com.example.gziptest

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.compression.ContentEncoding
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpStatement
import io.ktor.http.isSuccess
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.junit.Test

@Serializable
data class ToDo(
    @SerialName("completed")
    val completed: Boolean,
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("userId")
    val userId: Int
)

class KtorClientTest {
    @Test
    // Runs OK
    fun log_info_with_gzip() = getJson(LogLevel.INFO)

    @Test
    // Runs OK
    fun log_all_without_gzip() = getJson(LogLevel.ALL, false)

    @Test
    // Fails with kotlinx.coroutines.JobCancellationException: Parent job is Completed
    // but only when called as the only test
    fun log_all_with_gzip() = getJson(LogLevel.ALL)

    private fun getJson(logLevel: LogLevel, useCompression: Boolean = true) = runBlocking {
        val httpClient = HttpClient(Apache) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }

            if (useCompression) {
                install(ContentEncoding) {
                    gzip()
                    deflate()
                }
            }

            install(Logging) { level = logLevel }
        }

        val url = "https://jsonplaceholder.typicode.com/todos"

        httpClient.get<HttpStatement>(url).execute { response ->
            val result = response.receive<List<ToDo>>()
            assert(response.status.isSuccess())
            assert(result.isNotEmpty())
            assert(result.first().id == 1)
        }
    }
}