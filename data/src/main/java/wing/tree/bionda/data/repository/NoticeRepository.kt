package wing.tree.bionda.data.repository

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.source.local.NoticeDataSource

class NoticeRepository(private val noticeDataSource: NoticeDataSource) {
    private val ioDispatcher = Dispatchers.IO

    fun load(): Flow<Complete<ImmutableList<Notice>>> = noticeDataSource.load()
        .map<List<Notice>, Complete<ImmutableList<Notice>>> {
            Complete.Success(it.toImmutableList())
        }.catch {
            emit(Complete.Failure(it))
        }.flowOn(ioDispatcher)

    suspend fun add(notice: Notice): Long {
        return noticeDataSource.insert(notice)
    }

    suspend fun update(notice: Notice) {
        noticeDataSource.update(notice)
    }

    suspend fun delete(notice: Notice) {
        noticeDataSource.delete(notice)
    }

    suspend fun get(id: Long): Notice? {
        return noticeDataSource.get(id)
    }
}
