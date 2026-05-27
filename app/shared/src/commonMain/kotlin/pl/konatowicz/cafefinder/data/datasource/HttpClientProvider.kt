package pl.konatowicz.cafefinder.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// Konfiguracja adresu Twojego serwera.
// "10.0.2.2" to specjalny adres dla emulatora Androida, by widział "localhost" Twojego komputera.
// Dla Desktopu powinieneś używać "http://localhost:8080".
const val BASE_URL = "http://10.0.2.2:8080"

expect fun provideEngine(): HttpClientEngineFactory<*>
expect fun HttpClientConfig<*>.platformConfig()

object HttpClientProvider {
    val client: HttpClient by lazy {
        HttpClient(provideEngine()) {

            // Automatyczne parsowanie JSON z serwera na nasz model Place
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true // Ignoruje nieznane pola zgodnie z wymogiem prowadzącego
                })
            }

            // Ustawienie domyślnego adresu URL dla każdego zapytania
            defaultRequest {
                url(BASE_URL)
            }

            // Dodatkowa konfiguracja specyficzna dla platformy
            platformConfig()
        }
    }
}