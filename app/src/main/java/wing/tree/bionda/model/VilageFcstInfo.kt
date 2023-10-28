package wing.tree.bionda.model

import wing.tree.bionda.data.core.State

data class VilageFcstInfo(
    val ultraSrtNcst: State<UltraSrtNcst>,
    val vilageFcst: State<VilageFcst>
) {
    companion object {
        val initialValue = VilageFcstInfo(
            ultraSrtNcst = State.Loading,
            vilageFcst = State.Loading
        )
    }
}
