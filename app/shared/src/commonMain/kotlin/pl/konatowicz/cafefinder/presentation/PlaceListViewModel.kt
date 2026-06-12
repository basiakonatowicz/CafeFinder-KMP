package pl.konatowicz.cafefinder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.konatowicz.cafefinder.domain.model.Place
import pl.konatowicz.cafefinder.domain.repository.PlaceRepository

class PlaceListViewModel(private val repository: PlaceRepository = pl.konatowicz.cafefinder.data.repository.PlaceRepositoryImpl()) : ViewModel() {

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places

    private var allPlaces = listOf<Place>()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadPlaces()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterList()
    }

    private fun filterList() {
        val query = _searchQuery.value
        if (query.isBlank()) {
            _places.value = allPlaces
        } else {
            _places.value = allPlaces.filter {
                it.name.contains(query, ignoreCase = true) || it.address.contains(query, ignoreCase = true)
            }
        }
    }

    private fun loadPlaces() {
        viewModelScope.launch {
            val result = repository.getPlaces(1, 20)
            allPlaces = result
            filterList()
        }
    }

    fun toggleVisitStatus(placeId: String, currentState: Boolean) {
        viewModelScope.launch {
            try {
                val newState = !currentState
                repository.markAsVisited(placeId, newState)
                loadPlaces()
            } catch (e: Exception) {
                println("Błąd podczas aktualizacji: ${e.message}")
            }
        }
    }
}