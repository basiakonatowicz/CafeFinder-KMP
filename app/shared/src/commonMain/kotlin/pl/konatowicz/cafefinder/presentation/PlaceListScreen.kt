package pl.konatowicz.cafefinder.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import pl.konatowicz.cafefinder.domain.model.Place

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceListScreen(viewModel: PlaceListViewModel) {
    val places by viewModel.places.collectAsState()
    val listState = rememberLazyListState()

    // Scaffold to "rusztowanie" ekranu, idealne do dodania paska górnego
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cafe Finder", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Respektuje miejsce zajęte przez górny pasek
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Równe odstępy między kartami
        ) {
            items(places) { place ->
                PlaceItem(place = place)
            }
        }
    }
}

@Composable
fun PlaceItem(place: Place) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp), // Mocniejsze, nowoczesne zaokrąglenia
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            KamelImage(
                resource = asyncPainterResource(data = "${pl.konatowicz.cafefinder.data.datasource.BASE_URL}${place.imageUrl}"),
                contentDescription = place.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)), // Obrazek zaokrąglony tylko od góry
                contentScale = ContentScale.Crop,
                onLoading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                onFailure = { exception ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Brak zdjęcia", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = place.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary // Wyróżniony kolor adresu
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = place.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}