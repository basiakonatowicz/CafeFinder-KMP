package pl.konatowicz.cafefinder.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import pl.konatowicz.cafefinder.domain.model.Place
import pl.konatowicz.cafefinder.data.datasource.BASE_URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceListScreen(viewModel: PlaceListViewModel) {
    val places by viewModel.places.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val listState = rememberLazyListState()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cafe Finder", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Szukaj kawiarni lub adresu...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(places) { place ->
                    PlaceItem(
                        place = place,
                        onVisitClick = { viewModel.toggleVisitStatus(place.id, place.isVisited) }
                    )
                }
            }
        }

        if (showAddDialog) {
            var newName by remember { mutableStateOf("") }
            var newAddress by remember { mutableStateOf("") }
            var newDescription by remember { mutableStateOf("") }
            var newImageUrl by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Dodaj nową kawiarnię") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Nazwa") })
                        OutlinedTextField(value = newAddress, onValueChange = { newAddress = it }, label = { Text("Adres") })
                        OutlinedTextField(value = newDescription, onValueChange = { newDescription = it }, label = { Text("Krótki opis") })
                        OutlinedTextField(value = newImageUrl, onValueChange = { newImageUrl = it }, label = { Text("Link do zdjęcia (np. /static/costa.jpg)") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (newName.isNotBlank() && newAddress.isNotBlank()) {
                            viewModel.addPlace(newName, newAddress, newDescription, newImageUrl)
                            showAddDialog = false
                        }
                    }) {
                        Text("Zapisz")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Anuluj")
                    }
                }
            )
        }
    }
}

@Composable
fun PlaceItem(place: Place, onVisitClick: () -> Unit) {

    val animatedButtonColor by animateColorAsState(
        targetValue = if (place.isVisited) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 500)
    )

    val fullImageUrl = if (place.imageUrl.startsWith("http")) {
        place.imageUrl
    } else {
        "$BASE_URL${place.imageUrl}"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            KamelImage(
                resource = asyncPainterResource(data = fullImageUrl),
                contentDescription = place.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop,
                onLoading = { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } },
                onFailure = { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Brak zdjęcia", color = MaterialTheme.colorScheme.error) } }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = place.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = place.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = place.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onVisitClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = animatedButtonColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (place.isVisited) "Odwiedzono! (Kliknij, by cofnąć) ↩" else "Oznacz jako odwiedzone ✓")
                }
            }
        }
    }
}