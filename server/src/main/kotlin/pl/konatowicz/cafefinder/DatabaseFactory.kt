package pl.konatowicz.cafefinder

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import java.io.File

// Definicja tabeli kawiarni w bazie danych (Mapowanie obiektu Place)
object PlacesTable : Table("places") {
    val id = varchar("id", 50)
    val name = varchar("name", 100)
    val description = varchar("description", 500)
    val address = varchar("address", 200)
    val imageUrl = varchar("image_url", 250)
    val rating = double("rating")
    val isVisited = bool("is_visited")

    override val primaryKey = PrimaryKey(id)
}

object DatabaseFactory {
    fun init() {
        // Tworzymy bazę danych w pliku "cafefinder.db" w katalogu głównym projektu.
        // Spełnia to wymóg relatywnej ścieżki i automatycznego tworzenia!
        Database.connect("jdbc:sqlite:./cafefinder.db", "org.sqlite.JDBC")

        transaction {
            // Jeśli tabela nie istnieje, Exposed ją utworzy automatycznie
            SchemaUtils.create(PlacesTable)

            // Weryfikacja (Seed): jeśli tabela jest pusta, dodajemy startowe kawiarnie
            if (PlacesTable.selectAll().count() == 0L) {
                PlacesTable.insert {
                    it[id] = "starbucks-central"
                    it[name] = "Starbucks Central"
                    it[description] = "Twoja ulubiona kawa w samym centrum miasta. Świetne miejsce do nauki i pracy z laptopem."
                    it[address] = "Marszałkowska 100, Warszawa"
                    it[imageUrl] = "http://localhost:8080/static/starbucks.jpg" // Ścieżka statyczna z serwera
                    it[rating] = 4.2
                    it[isVisited] = false
                }

                PlacesTable.insert {
                    it[id] = "kawiarnia-pod-amorem"
                    it[name] = "Kawiarnia Pod Amorem"
                    it[description] = "Przytulna, rzemieślnicza kawiarnia z domowymi wypiekami i alternatywnymi metodami parzenia."
                    it[address] = "Nowy Świat 25, Warszawa"
                    it[imageUrl] = "http://localhost:8080/static/amor.jpg"
                    it[rating] = 4.8
                    it[isVisited] = true
                }

                println("--- Baza danych była pusta. Została pomyślnie zasilona danymi startowymi (Seed)! ---")
            } else {
                println("--- Baza danych zawiera już dane. Pomijam zasiewanie. ---")
            }
        }
    }
}