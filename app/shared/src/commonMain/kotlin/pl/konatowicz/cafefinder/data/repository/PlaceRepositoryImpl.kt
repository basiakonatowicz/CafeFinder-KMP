package pl.konatowicz.cafefinder.data.repository

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import pl.konatowicz.cafefinder.data.datasource.HttpClientProvider
import pl.konatowicz.cafefinder.domain.model.Place
import pl.konatowicz.cafefinder.domain.repository.PlaceRepository

class PlaceRepositoryImpl : PlaceRepository {

    // Używamy naszego skonfigurowanego klienta
    private val client = HttpClientProvider.client

    override suspend fun getPlaces(page: Int, size: Int): List<Place> {
        return try {
            // Zapytanie pod adres bazowy + "/places" z parametrami stronicowania
            client.get("/places") {
                parameter("page", page)
                parameter("size", size)
            }.body() // Ktor zamienia zwrócony JSON na listę obiektów Place
        } catch (e: Exception) {
            println("Błąd pobierania danych z sieci: ${e.message}")
            emptyList() // Bezpieczny "fallback" w razie braku połączenia
        }
    }
}