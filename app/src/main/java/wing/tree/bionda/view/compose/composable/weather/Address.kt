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

@Composable
fun Address(
    address: Address?,
    modifier: Modifier = Modifier
) {
    val thoroughfare = address?.thoroughfare

    if (thoroughfare.isNotNull()) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = thoroughfare)
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null
            )
        }
    }
}
