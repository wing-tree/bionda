package wing.tree.bionda.data.model

import kotlinx.serialization.Serializable

sealed interface MidLandFcst {
    @Serializable
    data class Remote(val response: Response<Item>): MidLandFcst

    @Serializable
    data class Item(
        val regId: String,
        val rnSt3Am: Int,
        val rnSt3Pm: Int,
        val rnSt4Am: Int,
        val rnSt4Pm: Int,
        val rnSt5Am: Int,
        val rnSt5Pm: Int,
        val rnSt6Am: Int,
        val rnSt6Pm: Int,
        val rnSt7Am: Int,
        val rnSt7Pm: Int,
        val rnSt8: Int,
        val rnSt9: Int,
        val rnSt10: Int,
        val wf3Am: String,
        val wf3Pm: String,
        val wf4Am: String,
        val wf4Pm: String,
        val wf5Am: String,
        val wf5Pm: String,
        val wf6Am: String,
        val wf6Pm: String,
        val wf7Am: String,
        val wf7Pm: String,
        val wf8: String,
        val wf9: String,
        val wf10: String
    )
}
