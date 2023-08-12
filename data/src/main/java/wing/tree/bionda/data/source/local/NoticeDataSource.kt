package wing.tree.bionda.data.source.local

import wing.tree.bionda.data.database.dao.NoticeDao
import wing.tree.bionda.data.model.Notice

class NoticeDataSource(private val noticeDao: NoticeDao) {
    fun load() = noticeDao.load()

    suspend fun insert(notice: Notice): Long {
        return noticeDao.insert(notice)
    }

    suspend fun delete(notice: Notice) {
        noticeDao.delete(notice)
    }

    suspend fun update(notice: Notice) {
        noticeDao.update(notice)
    }

    suspend fun get(id: Long): Notice? {
        return noticeDao.get(id)
    }

    suspend fun isExists(hour: Int, minute: Int): Boolean {
        return noticeDao.isExists(hour, minute)
    }
}
