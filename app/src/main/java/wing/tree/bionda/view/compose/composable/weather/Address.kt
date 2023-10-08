package wing.tree.bionda.view.compose.composable.weather

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wing.tree.bionda.data.core.Address
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.view.compose.composable.core.HorizontalSpacer

@Composable
fun Address(
    address: Address?,
    modifier: Modifier = Modifier
) {
    val thoroughfare = address?.thoroughfare

    if (thoroughfare.isNotNull()) {
        val imageVector = Icons.Default.LocationOn

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalSpacer(width = imageVector.defaultWidth)
            Text(text = thoroughfare)
            Icon(
                imageVector = imageVector,
                contentDescription = null
            )
        }
    }
}
