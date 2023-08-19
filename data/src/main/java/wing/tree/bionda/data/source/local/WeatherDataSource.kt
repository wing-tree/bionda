package wing.tree.bionda.data.source.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import wing.tree.bionda.data.database.dao.MidTaDao
import wing.tree.bionda.data.database.dao.VilageFcstDao
import wing.tree.bionda.data.model.VilageFcst.Local as VilageFcst
import wing.tree.bionda.data.model.MidTa.Local as MidTa

class WeatherDataSource(
    private val midTaDao: MidTaDao,
    private val vilageFcstDao: VilageFcstDao
) {
    private val supervisorScope = CoroutineScope(Dispatchers.IO.plus(SupervisorJob()))

    suspend fun insert(midTa: MidTa) {
        midTaDao.insert(midTa)
    }

    suspend fun insert(vilageFcst: VilageFcst) {
        vilageFcstDao.insert(vilageFcst)
    }

    suspend fun load(regId: String, tmFc: String): MidTa? =
        midTaDao.load(regId = regId, tmFc = tmFc)

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
