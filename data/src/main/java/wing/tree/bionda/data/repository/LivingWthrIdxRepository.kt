package wing.tree.bionda.data.repository

import android.location.Location
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.extension.time
import wing.tree.bionda.data.model.Decorator
import wing.tree.bionda.data.model.LivingWthrIdx
import wing.tree.bionda.data.source.local.AreaDataSource
import wing.tree.bionda.data.top.level.baseCalendar
import wing.tree.bionda.data.source.local.LivingWthrIdxDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.LivingWthrIdxDataSource as RemoteDataSource

class LivingWthrIdxRepository(
    private val areaDataSource: AreaDataSource,
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun getAirDiffusionIdx(location: Location): State.Complete<LivingWthrIdx.AirDiffusionIdx.Local> {
        return try {
            val areaNo = areaDataSource.getAreaNo(location)
            val time = baseCalendar(Decorator.Calendar.UvIdx).time()

            val uvIdx = localDataSource.loadAirDiffusionIdx(
                areaNo = areaNo,
                time = time
            ) ?: remoteDataSource.getAirDiffusionIdx(
                areaNo = areaNo,
                time = time
            ).toLocal(
                areaNo = areaNo,
                time = time
            ).also {
                localDataSource.cache(it)
            }

            State.Complete.Success(uvIdx)
        } catch (throwable: Throwable) {
            State.Complete.Failure(throwable)
        }
    }

    suspend fun getUVIdx(location: Location): State.Complete<LivingWthrIdx.UVIdx.Local> {
        return try {
            val areaNo = areaDataSource.getAreaNo(location)
            val time = baseCalendar(Decorator.Calendar.UvIdx).time()

            val uvIdx = localDataSource.loadUVIdx(
                areaNo = areaNo,
                time = time
            ) ?: remoteDataSource.getUVIdx(
                areaNo = areaNo,
                time = time
            ).toLocal(
                areaNo = areaNo,
                time = time
            ).also {
                localDataSource.cache(it)
            }

            State.Complete.Success(uvIdx)
        } catch (throwable: Throwable) {
            State.Complete.Failure(throwable)
        }
    }
}
