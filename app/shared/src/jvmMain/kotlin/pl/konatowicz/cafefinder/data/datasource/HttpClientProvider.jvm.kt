package pl.konatowicz.cafefinder.data.datasource

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

// Dostarczamy silnik OkHttp dla platformy JVM/Desktop
actual fun provideEngine(): HttpClientEngineFactory<*> = OkHttp

actual fun HttpClientConfig<*>.platformConfig() {
    // Tutaj można dodać np. logowanie dla Desktopu, na ten moment zostawiamy puste
}