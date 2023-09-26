package wing.tree.bionda.model

import kotlinx.collections.immutable.ImmutableMap
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.string
import wing.tree.bionda.data.model.Category
import wing.tree.bionda.data.model.CodeValue

data class UltraSrtNcst(
    val baseDate: String,
    val baseTime: String,
    val codeValues: ImmutableMap<String, Double>
) {
    val t1h = codeValues[Category.T1H]
    val rn1 = codeValues[Category.RN1]
    val uuu = codeValues[Category.UUU]
    val vvv = codeValues[Category.VVV]
    val reh = codeValues[Category.REH]
    val pty: CodeValue.Pty get() = CodeValue.Pty(code = codeValues[Category.PTY]?.code)
    val vec = codeValues[Category.VEC]
    val wsd = codeValues[Category.WSD]

    private val Double.code: String get() = int.string
}
