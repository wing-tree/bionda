package wing.tree.bionda.data.repository

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.model.Result
import wing.tree.bionda.data.model.Result.Complete
import wing.tree.bionda.data.source.local.NoticeDataSource

class NoticeRepository(private val noticeDataSource: NoticeDataSource) {
    fun load(): Flow<Result<ImmutableList<Notice>>> = noticeDataSource.load().map {
        Complete.Success(it.toImmutableList())
    }.catch {
        Complete.Failure(it)
    }

    suspend fun add(notice: Notice) {
        noticeDataSource.insert(notice)
    }

    suspend fun update(notice: Notice) {
        noticeDataSource.update(notice)
    }

    suspend fun delete(notice: Notice) {
        noticeDataSource.delete(notice)
    }
}
