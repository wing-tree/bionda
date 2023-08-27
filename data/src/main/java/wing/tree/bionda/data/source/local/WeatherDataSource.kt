package wing.tree.bionda.data.source.local

import android.content.Context
import android.location.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import wing.tree.bionda.data.database.dao.AreaDao
import wing.tree.bionda.data.database.dao.LCRiseSetInfoDao
import wing.tree.bionda.data.database.dao.MidLandFcstDao
import wing.tree.bionda.data.database.dao.MidTaDao
import wing.tree.bionda.data.database.dao.UltraSrtNcstDao
import wing.tree.bionda.data.database.dao.VilageFcstDao
import wing.tree.bionda.data.database.sttKiller
import wing.tree.bionda.data.extension.double
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.radians
import wing.tree.bionda.data.extension.two
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.data.model.LatLon
import wing.tree.bionda.data.model.weather.FcstZoneCd
import wing.tree.bionda.data.model.weather.RegId
import wing.tree.bionda.data.service.RiseSetInfoService
import wing.tree.bionda.data.service.VilageFcstInfoService
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import wing.tree.bionda.data.model.weather.LCRiseSetInfo.Local as LCRiseSetInfo
import wing.tree.bionda.data.model.weather.MidLandFcst.Local as MidLandFcst
import wing.tree.bionda.data.model.weather.MidTa.Local as MidTa
import wing.tree.bionda.data.model.weather.UltraSrtNcst.Local as UltraSrtNcst
import wing.tree.bionda.data.model.weather.VilageFcst.Local as VilageFcst

class WeatherDataSource(
    private val context: Context,
    private val areaDao: AreaDao,
    private val midLandFcstDao: MidLandFcstDao,
    private val midTaDao: MidTaDao,
    private val lcRiseSetInfoDao: LCRiseSetInfoDao,
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

    suspend fun buildAreaDB() {
            val a = sttKiller().dropLast(1)
            for (i in a.withIndex()) {
                val b = i.value.split('/')

                val areaNo = b[0]
                val nx = b[1]
//                    try {
//                    b[1]
//                } catch (e: Exception) {
//                    println("wwwwwwiii:${i.index},${a.count()}")
//                    "---"
//                }
                val ny = b[2]
                val longitude = b[3]
                val latitude = b[4]
                //$nx,$ny,$longitude,$latitude
                areaDao.insert(
                    Area(
                        index = i.index.inc(),
                        no = areaNo.trim(),
                        nx = nx.trim().toInt(),
                        ny = ny.trim().toInt(),
                        longitude = longitude.trim().toDouble(),
                        latitude = latitude.trim().toDouble()
                    )
                )
            }
    }
}
