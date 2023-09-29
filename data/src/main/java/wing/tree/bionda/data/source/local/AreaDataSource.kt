package wing.tree.bionda.data.source.local

import wing.tree.bionda.data.database.dao.AreaDao
import wing.tree.bionda.data.model.Area

class AreaDataSource(private val dao: AreaDao) {
    private var loaded: List<Area>? = null

    suspend fun load(): List<Area> {
        val loaded = loaded ?: dao.load().also {
            loaded = it
        }

        return loaded
    }
}
