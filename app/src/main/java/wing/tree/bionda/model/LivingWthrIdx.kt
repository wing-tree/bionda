package wing.tree.bionda.model

import wing.tree.bionda.data.core.State

data class LivingWthrIdx(
    val airDiffusionIdx: State<wing.tree.bionda.data.model.LivingWthrIdx.AirDiffusionIdx>,
    val uvIdx: State<wing.tree.bionda.data.model.LivingWthrIdx.UVIdx>
) {
    companion object {
        val initialValue = LivingWthrIdx(
            airDiffusionIdx = State.Loading,
            uvIdx = State.Loading
        )
    }
}
