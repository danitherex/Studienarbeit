import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.example.studienarbeit.presentation.datastore
import kotlinx.coroutines.launch

@Composable
fun RadiusSlider(
    radius: Double,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Slider(
        value = radius.toFloat(),
        onValueChange = { newRadius ->
            scope.launch {
                context.datastore.updateData {
                    it.copy(
                        radius = newRadius.toDouble()
                    )
                }
            }
        },
        valueRange = 120f..500f,
        modifier = Modifier
            .graphicsLayer {
                rotationZ = 270f
                transformOrigin = TransformOrigin(0f, 0f)
            }
            .layout { measurable, constraints ->
                val placeable = measurable.measure(
                    Constraints(
                        minWidth = constraints.minHeight,
                        maxWidth = constraints.maxHeight,
                        minHeight = constraints.minWidth,
                        maxHeight = constraints.maxHeight,
                    )
                )
                layout(placeable.height, placeable.width) {
                    placeable.place(-placeable.width, 0)
                }
            }
            .width(350.dp)
            .height(65.dp)
    )
}