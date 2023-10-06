package wing.tree.bionda.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import wing.tree.bionda.data.database.dao.AreaDao
import wing.tree.bionda.data.model.Area

@androidx.room.Database(
    entities = [Area::class],
    exportSchema = true,
    version = 2
)
abstract class AreaDatabase : RoomDatabase() {
    abstract val dao: AreaDao

    companion object {
        private const val DATABASE_FILE_PATH = "area.db"
        private const val NAME = "area"

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE area ADD COLUMN favorited INTEGER NOT NULL DEFAULT 0")
            }
        }

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
                        .addMigrations(MIGRATION_1_2)
                        .build()
                        .also {
                            instance = it
                        }
                }
            }
        }
    }
}
