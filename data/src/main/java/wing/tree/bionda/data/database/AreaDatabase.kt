package wing.tree.bionda.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import wing.tree.bionda.data.database.dao.AreaDao
import wing.tree.bionda.data.model.Area

@androidx.room.Database(
    entities = [Area::class],
    exportSchema = true,
    version = 1
)
abstract class AreaDatabase : RoomDatabase() {
    abstract val areaDao: AreaDao

    companion object {
        private const val DATABASE_FILE_PATH = "area.db"
        private const val NAME = "area-database"

        @Volatile
        private var instance: AreaDatabase? = null

        fun getInstance(context: Context): AreaDatabase {
            synchronized(this) {
                return instance ?: run {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AreaDatabase::class.java,
                        NAME
                    )
                        .createFromAsset(DATABASE_FILE_PATH)
                        .build()
                        .also {
                            instance = it
                        }
                }
            }
        }
    }
}
