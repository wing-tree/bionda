package wing.tree.bionda.data.source.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import wing.tree.bionda.data.database.dao.MidLandFcstDao
import wing.tree.bionda.data.database.dao.MidTaDao
import wing.tree.bionda.data.database.dao.VilageFcstDao
import wing.tree.bionda.data.model.MidLandFcst.Local as MidLandFcst
import wing.tree.bionda.data.model.MidTa.Local as MidTa
import wing.tree.bionda.data.model.VilageFcst.Local as VilageFcst

class WeatherDataSource(
    private val midLandFcstDao: MidLandFcstDao,
    private val midTaDao: MidTaDao,
    private val vilageFcstDao: VilageFcstDao
) {
    private val supervisorScope = CoroutineScope(Dispatchers.IO.plus(SupervisorJob()))

    suspend fun insert(midLandFcst: MidLandFcst) = midLandFcstDao.insert(midLandFcst)
    suspend fun insert(midTa: MidTa) = midTaDao.insert(midTa)
    suspend fun insert(vilageFcst: VilageFcst) = vilageFcstDao.insert(vilageFcst)

    suspend fun load(
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
}
