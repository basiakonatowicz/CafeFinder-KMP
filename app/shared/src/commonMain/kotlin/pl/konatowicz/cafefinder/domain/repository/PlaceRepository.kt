package pl.konatowicz.cafefinder.domain.repository

import pl.konatowicz.cafefinder.domain.model.Place

interface PlaceRepository {
    // Metoda asynchroniczna do pobierania listy z serwera z obsługą paginacji
    suspend fun getPlaces(page: Int, size: Int): List<Place>
}