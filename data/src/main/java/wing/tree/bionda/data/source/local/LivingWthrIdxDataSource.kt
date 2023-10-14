package wing.tree.bionda.data.source.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import wing.tree.bionda.data.database.dao.AirDiffusionIdxDao
import wing.tree.bionda.data.database.dao.UVIdxDao
import wing.tree.bionda.data.model.LivingWthrIdx

class LivingWthrIdxDataSource(
    private val airDiffusionIdxDao: AirDiffusionIdxDao,
    private val uvIdxDao: UVIdxDao
) {
    private val supervisorScope = CoroutineScope(Dispatchers.IO.plus(SupervisorJob()))

    fun cache(airDiffusionIdx: LivingWthrIdx.AirDiffusionIdx.Local) {
        supervisorScope.launch {
            airDiffusionIdxDao.cacheInTransaction(airDiffusionIdx)
        }
    }

    fun cache(uvIdx: LivingWthrIdx.UVIdx.Local) {
        supervisorScope.launch {
            uvIdxDao.cacheInTransaction(uvIdx)
        }
    }

    suspend fun loadAirDiffusionIdx(areaNo: String, time: String): LivingWthrIdx.AirDiffusionIdx.Local? {
        return airDiffusionIdxDao.load(areaNo = areaNo, time = time)
    }

    suspend fun loadUVIdx(areaNo: String, time: String): LivingWthrIdx.UVIdx.Local? {
        return uvIdxDao.load(areaNo = areaNo, time = time)
    }
}
