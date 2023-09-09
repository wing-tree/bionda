package wing.tree.bionda.view.compose.composable

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.semantics
import kotlinx.collections.immutable.persistentListOf
import wing.tree.bionda.R
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.zero

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleChoiceSegmentedButtonRow(
    selectedSegmentedButtonIndex: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = persistentListOf(
        R.string.weather,
        R.string.alarm
    )

    androidx.compose.material3.SingleChoiceSegmentedButtonRow(
        modifier = modifier.semantics(false) {
            SemanticsProperties.SelectableGroup
        }
    ) {
        val colors = SegmentedButtonDefaults.colors(
            activeContainerColor = Color.Transparent,
            activeContentColor = MaterialTheme.colorScheme.primary,
            activeBorderColor = Color.Transparent,
            inactiveContainerColor = Color.Transparent,
            inactiveBorderColor = Color.Transparent
        )

        val rippleTheme = remember {
            object : RippleTheme {
                @Composable
                override fun defaultColor(): Color = Color.Red

                @Composable
                override fun rippleAlpha(): RippleAlpha = RippleAlpha(
                    draggedAlpha = Float.zero,
                    focusedAlpha = Float.zero,
                    hoveredAlpha = Float.zero,
                    pressedAlpha = Float.zero
                )

            }
        }

        CompositionLocalProvider(
            LocalRippleTheme provides rippleTheme
        ) {
            items.forEachIndexed { index, item ->
                SegmentedButton(
                    selected = index `is` selectedSegmentedButtonIndex,
                    onClick = {
                        onClick(index)
                    },
                    shape = CircleShape,
                    colors = colors
                ) {
                    Text(stringResource(id = item))
                }
            }
        }
    }
}
