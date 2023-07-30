package wing.tree.bionda.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import wing.tree.bionda.data.database.dao.ForecastDao
import wing.tree.bionda.data.database.dao.NoticeDao
import wing.tree.bionda.data.database.type.converters.TypeConverters
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.model.forecast.local.Forecast

@androidx.room.Database(
    entities = [Forecast::class, Notice::class],
    exportSchema = true,
    version = 1
)
@androidx.room.TypeConverters(TypeConverters::class)
abstract class Database: RoomDatabase() {
    abstract fun forecastDao(): ForecastDao
    abstract fun noticeDao(): NoticeDao

    companion object {
        private const val NAME = "database"

        @Volatile
        private var instance: Database? = null

        fun getInstance(context: Context): Database {
            synchronized(this) {
                return instance ?: run {
                    Room.databaseBuilder(
                        context.applicationContext,
                        Database::class.java,
                        NAME
                    )
                        .build()
                        .also {
                            instance = it
                        }
                }
            }
        }
    }
}
