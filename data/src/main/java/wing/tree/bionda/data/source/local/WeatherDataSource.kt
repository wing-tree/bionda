package wing.tree.bionda.data.source.local

import android.content.Context
import android.location.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import wing.tree.bionda.data.database.dao.MidLandFcstDao
import wing.tree.bionda.data.database.dao.MidTaDao
import wing.tree.bionda.data.database.dao.VilageFcstDao
import wing.tree.bionda.data.extension.double
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.radians
import wing.tree.bionda.data.extension.two
import wing.tree.bionda.data.model.FcstZoneCd
import wing.tree.bionda.data.model.LatLon
import wing.tree.bionda.data.model.RegId
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import wing.tree.bionda.data.model.MidLandFcst.Local as MidLandFcst
import wing.tree.bionda.data.model.MidTa.Local as MidTa
import wing.tree.bionda.data.model.VilageFcst.Local as VilageFcst

class WeatherDataSource(
    private val context: Context,
    private val midLandFcstDao: MidLandFcstDao,
    private val midTaDao: MidTaDao,
    private val vilageFcstDao: VilageFcstDao
) {
    private val fcstZoneCd: FcstZoneCd by lazy {
        val json = Json {
            allowStructuredMapKeys = true
        }

        context.assets.open(FcstZoneCd.FILE_NAME).use {
            val bytes = ByteArray(it.available())

            it.read(bytes)

            with(String(bytes, Charsets.UTF_8)) {
                json.decodeFromString(this)
            }
        }
    }

    private val supervisorScope = CoroutineScope(Dispatchers.IO.plus(SupervisorJob()))

    fun getRegId(location: Location, regId: RegId): String {
        val item = fcstZoneCd.items.minByOrNull {
            location.haversine(LatLon(lat = it.lat, lon = it.lon))
        } ?: return regId.default

        return getRegId(item, regId)
    }

    private fun getRegId(item: FcstZoneCd.Item, regId: RegId): String {
        return if (item.regId in regId) {
            item.regId
        } else {
            fcstZoneCd.items.find {
                it.regId `is` item.regUp
            }?.let {
                getRegId(it, regId)
            } ?: regId.default
        }
    }

    fun cache(midLandFcst: MidLandFcst) {
        supervisorScope.launch {
            midLandFcstDao.clearAndInsert(midLandFcst)
        }
    }

    fun cache(midTa: MidTa) {
        supervisorScope.launch {
            midTaDao.clearAndInsert(midTa)
        }
    }

    fun cache(vilageFcst: VilageFcst) {
        supervisorScope.launch {
            vilageFcstDao.clearAndInsert(vilageFcst)
        }
    }

    suspend fun loadMidLandFcst(regId: String, tmFc: String): MidLandFcst? {
        return midLandFcstDao.load(
            regId = regId,
            tmFc = tmFc
        )
    }

    suspend fun loadMidTa(regId: String, tmFc: String): MidTa? {
        return midTaDao.load(
            regId = regId,
            tmFc = tmFc
        )
    }

    suspend fun loadVilageFcst(
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int
    ): VilageFcst? {
        return vilageFcstDao.load(
            baseDate,
            baseTime,
            nx,
            ny
        )
    }

    suspend fun insert(midLandFcst: MidLandFcst) = midLandFcstDao.insert(midLandFcst)
    suspend fun insert(midTa: MidTa) = midTaDao.insert(midTa)
    suspend fun insert(vilageFcst: VilageFcst) = vilageFcstDao.insert(vilageFcst)

    private fun Location.haversine(latLon: LatLon): Double {
        val delta = latLon
            .delta(latitude, longitude)
            .radians()
            .half()

        val a = sin(delta.lat).pow(Int.two) +
                cos(latitude.radians) *
                cos(latLon.lon.radians) *
                sin(delta.lon).pow(Int.two)

        val c = atan2(sqrt(a), sqrt(Double.one.minus(a))).double

        return 6371.times(c)
    }
}
