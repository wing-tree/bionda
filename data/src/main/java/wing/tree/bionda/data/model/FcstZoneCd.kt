package wing.tree.bionda.data.model

import kotlinx.serialization.Serializable
import wing.tree.bionda.data.core.Response

@Serializable
data class FcstZoneCd(val response: Response<Item>) {
    val items: List<Item> = response.items

    @Serializable
    data class Item(
        val regId: String,
        val lat: Double,
        val lon: Double,
        val regEn: String,
        val regName: String,
        val regSp: String,
        val regUp: String,
        val seq: Int,
        val stnF3: Int,
        val tmEd: Long,
        val tmSt: Long
    )

    companion object {
        const val FILE_NAME = "FcstZoneCd.json"
    }
}
