package pl.konatowicz.cafefinder.data.datasource

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual val HOST_IP: String = "localhost"

actual fun provideEngine(): HttpClientEngineFactory<*> = OkHttp
actual fun HttpClientConfig<*>.platformConfig() { }