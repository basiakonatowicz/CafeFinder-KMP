package pl.konatowicz.cafefinder

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.transactions.transaction
import pl.konatowicz.cafefinder.domain.model.Place
import java.util.UUID

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {

        DatabaseFactory.init()

        install(ContentNegotiation) {
            json()
        }

        install(StatusPages) {
            exception<IllegalArgumentException> { call, cause ->
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to (cause.message ?: "Zasób nie istnieje"))
                )
            }
            exception<Throwable> { call, cause ->
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Wystąpił nieoczekiwany błąd serwera")
                )
            }
        }

        routing {
            staticResources("/static", "static")

            get("/places") {
                val page = call.request.queryParameters["page"]?.toLongOrNull() ?: 1L
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                val offset = (page - 1) * size

                val placesList = transaction {
                    PlacesTable
                        .selectAll()
                        .limit(size, offset = offset)
                        .map {
                            Place(
                                id = it[PlacesTable.id],
                                name = it[PlacesTable.name],
                                description = it[PlacesTable.description],
                                address = it[PlacesTable.address],
                                imageUrl = it[PlacesTable.imageUrl],
                                rating = it[PlacesTable.rating],
                                isVisited = it[PlacesTable.isVisited]
                            )
                        }
                }
                call.respond(placesList)
            }

            get("/places/{id}") {
                val idParam = call.parameters["id"] ?: throw IllegalArgumentException("Brak identyfikatora ID")

                val place = transaction {
                    PlacesTable
                        .selectAll()
                        .where { PlacesTable.id eq idParam }
                        .map {
                            Place(
                                id = it[PlacesTable.id],
                                name = it[PlacesTable.name],
                                description = it[PlacesTable.description],
                                address = it[PlacesTable.address],
                                imageUrl = it[PlacesTable.imageUrl],
                                rating = it[PlacesTable.rating],
                                isVisited = it[PlacesTable.isVisited]
                            )
                        }.singleOrNull()
                }

                if (place == null) {
                    throw IllegalArgumentException("Kawiarnia o podanym ID nie została znaleziona")
                } else {
                    call.respond(place)
                }
            }

            put("/places/{id}/visit") {
                val placeId = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                val newState = call.request.queryParameters["state"]?.toBoolean() ?: true

                transaction {
                    PlacesTable.update({ PlacesTable.id eq placeId }) {
                        it[isVisited] = newState
                    }
                }
                call.respond(HttpStatusCode.OK, "Zaktualizowano status na $newState")
            }

            post("/places") {
                try {
                    val newPlace = call.receive<Place>()

                    transaction {
                        PlacesTable.insert {
                            it[id] = UUID.randomUUID().toString()
                            it[name] = newPlace.name
                            it[address] = newPlace.address
                            it[description] = newPlace.description
                            it[imageUrl] = newPlace.imageUrl
                            it[rating] = newPlace.rating
                            it[isVisited] = false
                        }
                    }
                    call.respond(HttpStatusCode.Created, "Dodano")
                } catch (e: Exception) {
                    application.log.error("Blad serwera przy POST: ${e.localizedMessage}", e)
                    call.respond(HttpStatusCode.InternalServerError, e.localizedMessage)
                }
            }
        }
    }.start(wait = true)
}