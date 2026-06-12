package pl.konatowicz.cafefinder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.konatowicz.cafefinder.data.datasource.BASE_URL
import pl.konatowicz.cafefinder.data.repository.PlaceRepositoryImpl
import pl.konatowicz.cafefinder.domain.model.Place
import pl.konatowicz.cafefinder.domain.repository.PlaceRepository

class PlaceListViewModel : ViewModel() {

    // Inicjalizujemy nasze repozytorium
    private val repository: PlaceRepository = PlaceRepositoryImpl()

    // Stan (StateFlow), który będzie obserwowany przez interfejs użytkownika
    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    init {
        loadPlaces()
    }

    private fun loadPlaces() {
        viewModelScope.launch {
            // Paginacja: na start pobieramy pierwszą stronę, max 20 elementów (wymóg prowadzącego!)
            val result = repository.getPlaces(page = 1, size = 20)
            _places.value = result
        }
    }
    fun toggleVisitStatus(placeId: String, currentState: Boolean) {
        viewModelScope.launch {
            try {
                // Odwracamy obecny stan o 180 stopni (magia wykrzyknika)
                val newState = !currentState

                // Wysyłamy do repozytorium
                repository.markAsVisited(placeId, newState)

                // Odświeżamy listę
                loadPlaces()

            } catch (e: Exception) {
                println("Błąd podczas aktualizacji: ${e.message}")
            }
        }
    }

}