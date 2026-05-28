import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.konatowicz.cafefinder.presentation.PlaceListScreen
import pl.konatowicz.cafefinder.presentation.PlaceListViewModel

@Composable
fun App() {
    MaterialTheme {
        // Tworzymy instancję naszego ViewModelu
        val viewModel = remember { PlaceListViewModel() }

        // Wyświetlamy nasz ekran
        PlaceListScreen(viewModel = viewModel)
    }
}