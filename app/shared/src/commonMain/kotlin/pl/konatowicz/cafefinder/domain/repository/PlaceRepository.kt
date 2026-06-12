package pl.konatowicz.cafefinder.domain.repository

import pl.konatowicz.cafefinder.data.datasource.HttpClientProvider.client
import pl.konatowicz.cafefinder.domain.model.Place
import io.ktor.client.request.put
import pl.konatowicz.cafefinder.data.datasource.BASE_URL

interface PlaceRepository {
    // Metoda asynchroniczna do pobierania listy z serwera z obsługą paginacji
    suspend fun getPlaces(page: Int, size: Int): List<Place>
    suspend fun markAsVisited(placeId: String, newState: Boolean) {
        // Zwróć uwagę na doklejone ?state=$newState na samym końcu!
        client.put("$BASE_URL/places/$placeId/visit?state=$newState")
    }
}
