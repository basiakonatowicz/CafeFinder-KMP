package pl.konatowicz.cafefinder.data.datasource

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual val HOST_IP: String = "10.0.2.2"

actual fun provideEngine(): HttpClientEngineFactory<*> = OkHttp
actual fun HttpClientConfig<*>.platformConfig() { }