package pl.konatowicz.cafefinder.data.datasource

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

// Wersja mobilna również opiera się na stabilnym OkHttp
actual fun provideEngine(): HttpClientEngineFactory<*> = OkHttp

actual fun HttpClientConfig<*>.platformConfig() {
    // Specyficzna konfiguracja dla Androida (np. timeouty)
}