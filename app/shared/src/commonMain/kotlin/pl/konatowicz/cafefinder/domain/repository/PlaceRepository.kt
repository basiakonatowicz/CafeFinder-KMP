package pl.konatowicz.cafefinder.domain.repository

import pl.konatowicz.cafefinder.data.datasource.HttpClientProvider.client
import pl.konatowicz.cafefinder.domain.model.Place
import io.ktor.client.request.put
import pl.konatowicz.cafefinder.data.datasource.BASE_URL
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface PlaceRepository {

    suspend fun getPlaces(page: Int, size: Int): List<Place>

    suspend fun markAsVisited(placeId: String, newState: Boolean) {
        client.put("$BASE_URL/places/$placeId/visit?state=$newState")
    }

    suspend fun addPlace(name: String, address: String, description: String, imageUrl: String) {
        val newPlace = Place(
            id = "",
            name = name,
            description = description,
            address = address,
            imageUrl = imageUrl,
            rating = 0.0,
            isVisited = false
        )

        client.post("$BASE_URL/places") {
            contentType(ContentType.Application.Json)
            setBody(newPlace)
        }
    }
}