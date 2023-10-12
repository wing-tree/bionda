package wing.tree.bionda.model

import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.model.LivingWthrIdx.AirDiffusionIdx
import wing.tree.bionda.data.model.LivingWthrIdx.UVIdx

data class LivingWthrIdx(
    val airDiffusionIdx: State<AirDiffusionIdx>,
    val uvIdx: State<UVIdx>
) {
    companion object {
        val initialValue = LivingWthrIdx(
            airDiffusionIdx = State.Loading,
            uvIdx = State.Loading
        )
    }
}
