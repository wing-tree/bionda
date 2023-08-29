package wing.tree.bionda.data.repository

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import wing.tree.bionda.data.extension.negativeOne
import wing.tree.bionda.data.model.Alarm
import wing.tree.bionda.data.core.State.Complete
import wing.tree.bionda.data.source.local.AlarmDataSource

class AlarmRepository(private val alarmDataSource: AlarmDataSource) {
    private val ioDispatcher = Dispatchers.IO

    fun load(): Flow<Complete<ImmutableList<Alarm>>> = alarmDataSource.load()
        .map<List<Alarm>, Complete<ImmutableList<Alarm>>> {
            Complete.Success(it.toImmutableList())
        }.catch {
            emit(Complete.Failure(it))
        }.flowOn(ioDispatcher)

    suspend fun add(alarm: Alarm): Long {
        return with(alarmDataSource) {
            if (isExists(alarm.hour, alarm.minute)) {
                Long.negativeOne
            } else {
                alarmDataSource.insert(alarm)
            }
        }
    }

    suspend fun update(alarm: Alarm) {
        alarmDataSource.update(alarm)
    }

    suspend fun updateAll(alarms: List<Alarm>) {
        alarmDataSource.updateAll(alarms)
    }

    suspend fun delete(alarm: Alarm) {
        alarmDataSource.delete(alarm)
    }

    suspend fun deleteAll(alarms: List<Alarm>) {
        alarmDataSource.deleteAll(alarms)
    }

    suspend fun get(id: Long): Alarm? {
        return alarmDataSource.get(id)
    }
}
