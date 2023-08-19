package wing.tree.bionda.data.source.local

import wing.tree.bionda.data.database.dao.AlarmDao
import wing.tree.bionda.data.model.Alarm

class AlarmDataSource(private val alarmDao: AlarmDao) {
    fun load() = alarmDao.load()

    suspend fun insert(alarm: Alarm): Long {
        return alarmDao.insert(alarm)
    }

    suspend fun delete(alarm: Alarm) {
        alarmDao.delete(alarm)
    }

    suspend fun deleteAll(alarms: List<Alarm>) {
        alarmDao.deleteAll(alarms)
    }

    suspend fun update(alarm: Alarm) {
        alarmDao.update(alarm)
    }

    suspend fun updateAll(alarms: List<Alarm>) {
        alarmDao.updateAll(alarms)
    }

    suspend fun get(id: Long): Alarm? {
        return alarmDao.get(id)
    }

    suspend fun isExists(hour: Int, minute: Int): Boolean {
        return alarmDao.isExists(hour, minute)
    }
}
