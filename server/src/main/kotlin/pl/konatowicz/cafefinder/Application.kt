package pl.konatowicz.cafefinder

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import pl.konatowicz.cafefinder.domain.model.Place

fun main() {
    // Uruchamiamy serwer Netty na porcie 8080
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {

        // 1. Inicjalizacja naszej bazy danych (oraz ewentualny Seed danych)
        DatabaseFactory.init()

        // 2. Wtyczka do automatycznego tłumaczenia obiektów Kotlin <-> JSON
        install(ContentNegotiation) {
            json()
        }

        // 3. GLOBALNA OBSŁUGA BŁĘDÓW (Wymóg prowadzącego!)
        install(StatusPages) {
            // Przechwytujemy sytuację, gdy np. szukany obiekt nie istnieje
            exception<IllegalArgumentException> { call, cause ->
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to (cause.message ?: "Zasób nie istnieje"))
                )
            }
            // Ogólny bezpiecznik dla nieznanych błędów serwera (zamiast wywalać aplikację)
            exception<Throwable> { call, cause ->
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Wystąpił nieoczekiwany błąd serwera")
                )
            }
        }

        // 4. Konfiguracja ścieżek (Routing)
        routing {

            // Serwowanie obrazków kawiarni z folderu resources/static
            staticResources("/static", "static")

            // Endpoint GET: Pobieranie wszystkich kawiarni z paginacją na żądanie
            get("/places") {
                // Odczytujemy parametry zapytania (np. /places?page=1&size=20)
                val page = call.request.queryParameters["page"]?.toLongOrNull() ?: 1L
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20

                val offset = (page - 1) * size

                val placesList = transaction {
                    PlacesTable
                        .selectAll()
                        .limit(size, offset = offset) // Paginacja, o której wspominał prowadzący
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

                // Serwer zwraca czysty, sformatowany JSON do klienta
                call.respond(placesList)
            }

            // Endpoint GET: Pobieranie szczegółów jednej kawiarni po ID
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
        }
    }.start(wait = true)
}