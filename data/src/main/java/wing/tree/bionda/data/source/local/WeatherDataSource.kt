package wing.tree.bionda.data.source.local

import android.content.Context
import android.location.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import wing.tree.bionda.data.core.LatLon
import wing.tree.bionda.data.database.dao.AreaDao
import wing.tree.bionda.data.database.dao.LCRiseSetInfoDao
import wing.tree.bionda.data.database.dao.MidLandFcstDao
import wing.tree.bionda.data.database.dao.MidTaDao
import wing.tree.bionda.data.database.dao.UVIdxDao
import wing.tree.bionda.data.database.dao.UltraSrtFcstDao
import wing.tree.bionda.data.database.dao.UltraSrtNcstDao
import wing.tree.bionda.data.database.dao.VilageFcstDao
import wing.tree.bionda.data.extension.double
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.radians
import wing.tree.bionda.data.extension.two
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.data.model.FcstZoneCd
import wing.tree.bionda.data.model.RegId
import wing.tree.bionda.data.service.RiseSetInfoService
import wing.tree.bionda.data.service.VilageFcstInfoService
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import wing.tree.bionda.data.model.LCRiseSetInfo.Local as LCRiseSetInfo
import wing.tree.bionda.data.model.MidLandFcst.Local as MidLandFcst
import wing.tree.bionda.data.model.MidTa.Local as MidTa
import wing.tree.bionda.data.model.UVIdx.Local as UVIdx
import wing.tree.bionda.data.model.UltraSrtFcst.Local as UltraSrtFcst
import wing.tree.bionda.data.model.UltraSrtNcst.Local as UltraSrtNcst
import wing.tree.bionda.data.model.VilageFcst.Local as VilageFcst

class WeatherDataSource(
    private val context: Context,
    private val areaDao: AreaDao,
    private val midLandFcstDao: MidLandFcstDao,
    private val midTaDao: MidTaDao,
    private val lcRiseSetInfoDao: LCRiseSetInfoDao,
    private val uvIdxDao: UVIdxDao,
    private val ultraSrtFcstDao: UltraSrtFcstDao,
    private val ultraSrtNcstDao: UltraSrtNcstDao,
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

    private var areas: List<Area>? = null

    fun getRegId(location: Location, regId: RegId): String {
        val item = fcstZoneCd.items.minBy {
            location.haversine(LatLon(lat = it.lat, lon = it.lon))
        }

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

    fun cache(lcRiseSetInfo: LCRiseSetInfo) {
        supervisorScope.launch {
            lcRiseSetInfoDao.clearAndInsert(lcRiseSetInfo)
        }
    }

    fun cache(midTa: MidTa) {
        supervisorScope.launch {
            midTaDao.clearAndInsert(midTa)
        }
    }

    fun cache(uvIdx: UVIdx) {
        supervisorScope.launch {
            uvIdxDao.clearAndInsert(uvIdx)
        }
    }

    fun cache(ultraSrtFcst: UltraSrtFcst) {
        supervisorScope.launch {
            ultraSrtFcstDao.clearAndInsert(ultraSrtFcst)
        }
    }

    fun cache(ultraSrtNcst: UltraSrtNcst) {
        supervisorScope.launch {
            ultraSrtNcstDao.clearAndInsert(ultraSrtNcst)
        }
    }

    fun cache(vilageFcst: VilageFcst) {
        supervisorScope.launch {
            vilageFcstDao.clearAndInsert(vilageFcst)
        }
    }

    suspend fun getAreaNo(location: Location): String {
        val areas = areas ?: areaDao.load().also {
            areas = it
        }

        return areas.minBy {
            location.haversine(LatLon(lat = it.latitude, lon = it.longitude))
        }
            .no
    }

    suspend fun loadLCRiseSetInfo(
        params: RiseSetInfoService.Params
    ): LCRiseSetInfo? = with(params) {
        lcRiseSetInfoDao.load(
            locdate = locdate,
            longitude = longitude,
            latitude = latitude
        )
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

    suspend fun loadUVIdx(areaNo: String, time: String): UVIdx? {
        return uvIdxDao.load(areaNo, time)
    }

    suspend fun loadUltraSrtFcst(
        params: VilageFcstInfoService.Params,
        minute: Int,
    ): UltraSrtFcst? = with(params) {
        ultraSrtFcstDao.load(
            baseDate,
            baseTime,
            nx,
            ny,
            minute
        )
    }

    suspend fun loadUltraSrtNcst(
        params: VilageFcstInfoService.Params,
        minute: Int
    ): UltraSrtNcst? = with(params) {
        ultraSrtNcstDao.load(
            baseDate,
            baseTime,
            nx,
            ny,
            minute
        )
    }

    suspend fun loadVilageFcst(params: VilageFcstInfoService.Params): VilageFcst? = with(params) {
        vilageFcstDao.load(
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
