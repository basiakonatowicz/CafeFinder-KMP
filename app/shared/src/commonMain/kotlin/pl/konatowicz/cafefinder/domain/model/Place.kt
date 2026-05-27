package pl.konatowicz.cafefinder.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val id: String,
    val name: String,
    val description: String,
    val address: String,
    val imageUrl: String,
    val rating: Double,
    val isVisited: Boolean = false
)