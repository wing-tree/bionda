package wing.tree.bionda.data.source.local

import wing.tree.bionda.data.database.dao.NoticeDao
import wing.tree.bionda.data.model.Notice

class NoticeDataSource(private val noticeDao: NoticeDao) {
    fun load() = noticeDao.load()

    suspend fun insert(notice: Notice) {
        noticeDao.insert(notice)
    }

    suspend fun delete(notice: Notice) {
        noticeDao.delete(notice)
    }

    suspend fun update(notice: Notice) {
        noticeDao.update(notice)
    }
}
