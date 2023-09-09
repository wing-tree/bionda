package wing.tree.bionda.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import wing.tree.bionda.R
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.model.LivingWthrIdx
import wing.tree.bionda.data.model.LivingWthrIdx.AirDiffusionIdx

val LivingWthrIdx.H.level: String
    @Composable
    get() = when (h) {
        AirDiffusionIdx.Level.LOW -> stringResource(id = R.string.low)
        AirDiffusionIdx.Level.NORMAL -> stringResource(id = R.string.normal)
        AirDiffusionIdx.Level.HIGH -> stringResource(id = R.string.high)
        AirDiffusionIdx.Level.VERY_HIGH -> stringResource(id = R.string.very_high)
        else -> String.empty
    }
