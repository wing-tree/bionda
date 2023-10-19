package wing.tree.bionda.data.source.local

import android.content.Context
import android.location.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import wing.tree.bionda.data.core.LatLon
import wing.tree.bionda.data.database.dao.LCRiseSetInfoDao
import wing.tree.bionda.data.database.dao.MidLandFcstDao
import wing.tree.bionda.data.database.dao.MidTaDao
import wing.tree.bionda.data.database.dao.TmnDao
import wing.tree.bionda.data.database.dao.TmxDao
import wing.tree.bionda.data.database.dao.UltraSrtFcstDao
import wing.tree.bionda.data.database.dao.UltraSrtNcstDao
import wing.tree.bionda.data.database.dao.VilageFcstDao
import wing.tree.bionda.data.extension.baseDate
import wing.tree.bionda.data.extension.haversine
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.model.Category
import wing.tree.bionda.data.model.FcstZoneCd
import wing.tree.bionda.data.model.RegId
import wing.tree.bionda.data.model.Tmn
import wing.tree.bionda.data.model.Tmx
import wing.tree.bionda.data.service.RiseSetInfoService
import wing.tree.bionda.data.service.VilageFcstInfoService
import wing.tree.bionda.data.top.level.today
import wing.tree.bionda.data.model.LCRiseSetInfo.Local as LCRiseSetInfo
import wing.tree.bionda.data.model.MidLandFcst.Local as MidLandFcst
import wing.tree.bionda.data.model.MidTa.Local as MidTa
import wing.tree.bionda.data.model.UltraSrtFcst.Local as UltraSrtFcst
import wing.tree.bionda.data.model.UltraSrtNcst.Local as UltraSrtNcst
import wing.tree.bionda.data.model.VilageFcst.Local as VilageFcst

class WeatherDataSource(
    private val context: Context,
    private val midLandFcstDao: MidLandFcstDao,
    private val midTaDao: MidTaDao,
    private val lcRiseSetInfoDao: LCRiseSetInfoDao,
    private val tmnDao: TmnDao,
    private val tmxDao: TmxDao,
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

    private fun cache(tmn: Tmn?, tmx: Tmx?) {
        supervisorScope.launch {
            tmn?.let {
                tmnDao.insert(it)
            }

            tmx?.let {
                tmxDao.insert(it)
            }
        }
    }

    private fun getRegId(item: FcstZoneCd.Item, regId: RegId): String {
        return if (item.regId in regId) {
            item.regId
        } else {
            fcstZoneCd.items.find {
                it.regId `is` item.regUp
            }?.let {
                getRegId(it, regId)
            } ?: regId.defaultValue
        }
    }

    fun getRegId(location: Location, regId: RegId): String {
        val item = fcstZoneCd.items.minBy {
            location.haversine(LatLon(lat = it.lat, lon = it.lon))
        }

        return getRegId(item, regId)
    }

    fun cache(midLandFcst: MidLandFcst) {
        supervisorScope.launch {
            midLandFcstDao.cacheInTransaction(midLandFcst)
        }
    }

    fun cache(params: RiseSetInfoService.Params, lcRiseSetInfo: LCRiseSetInfo) {
        supervisorScope.launch {
            lcRiseSetInfoDao.cacheInTransaction(
                params = params,
                lcRiseSetInfo = lcRiseSetInfo
            )
        }
    }

    fun cache(midTa: MidTa) {
        supervisorScope.launch {
            midTaDao.cacheInTransaction(midTa)
        }
    }

    fun cache(ultraSrtFcst: UltraSrtFcst) {
        supervisorScope.launch {
            ultraSrtFcstDao.cacheInTransaction(ultraSrtFcst)
        }
    }

    fun cache(ultraSrtNcst: UltraSrtNcst) {
        supervisorScope.launch {
            ultraSrtNcstDao.cacheInTransaction(ultraSrtNcst)
        }
    }

    fun cache(vilageFcst: VilageFcst) {
        supervisorScope.launch {
            vilageFcstDao.cacheInTransaction(vilageFcst)

            launch {
                vilageFcst.items.groupBy {
                    it.fcstDate
                }.forEach { (baseDate, items) ->
                    val tmn = items.find {
                        it.category `is` Category.TMN
                    }?.let {
                        Tmn(baseDate = baseDate, value = it.fcstValue)
                    }

                    val tmx = items.find {
                        it.category `is` Category.TMX
                    }?.let {
                        Tmx(baseDate = baseDate, value = it.fcstValue)
                    }

                    cache(tmn = tmn, tmx = tmx)
                }

                with(today) {
                    tmnDao.deleteBefore(baseDate)
                    tmxDao.deleteBefore(baseDate)
                }
            }
        }
    }

    suspend fun getTmn(baseDate: String) = tmnDao.get(baseDate = baseDate)
    suspend fun getTmx(baseDate: String) = tmxDao.get(baseDate = baseDate)

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

    suspend fun loadUltraSrtFcst(
        params: VilageFcstInfoService.Params,
        minute: Int? = null,
    ): UltraSrtFcst? = with(params) {
        minute?.let {
            ultraSrtFcstDao.load(
                baseDate,
                baseTime,
                nx,
                ny,
                it
            )
        } ?: ultraSrtFcstDao.load(
            baseDate,
            baseTime,
            nx,
            ny
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
}
