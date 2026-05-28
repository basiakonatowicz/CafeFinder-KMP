package pl.konatowicz.cafefinder.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import pl.konatowicz.cafefinder.domain.model.Place

@Composable
fun PlaceListScreen(viewModel: PlaceListViewModel) {
    // Obserwujemy listę kawiarni z ViewModelu
    val places by viewModel.places.collectAsState()

    // LazyColumn to odpowiednik RecyclerView - ładuje elementy tylko gdy są widoczne (Lazy Loading)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(places) { place ->
            PlaceItem(place = place)
        }
    }
}

@Composable
fun PlaceItem(place: Place) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Asynchroniczne ładowanie obrazka z Twojego serwera (Kamel)
            KamelImage(
                resource = asyncPainterResource(data = place.imageUrl),
                contentDescription = place.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                onLoading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                onFailure = { exception ->
                    // To wypisze szczegóły błędu w terminalu na dole ekranu
                    println("BŁĄD ZDJĘCIA: ${exception.message}")
                    exception.printStackTrace()

                    // To wyświetli powód błędu bezpośrednio na szarym kafelku w aplikacji
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Błąd: ${exception.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Teksty pod zdjęciem
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = place.name, style = MaterialTheme.typography.titleLarge)
                Text(text = place.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = place.description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}