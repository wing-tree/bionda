package wing.tree.bionda.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import wing.tree.bionda.data.database.dao.MidTaDao
import wing.tree.bionda.data.database.dao.NoticeDao
import wing.tree.bionda.data.database.dao.VilageFcstDao
import wing.tree.bionda.data.database.type.converters.MidTaConverters
import wing.tree.bionda.data.database.type.converters.NoticeConverters
import wing.tree.bionda.data.database.type.converters.VilageFcstConverters
import wing.tree.bionda.data.model.Notice
import wing.tree.bionda.data.model.MidTa.Local as MidTa
import wing.tree.bionda.data.model.VilageFcst.Local as VilageFcst

@androidx.room.Database(
    entities = [MidTa::class, Notice::class, VilageFcst::class],
    exportSchema = true,
    version = 1
)
@androidx.room.TypeConverters(
    MidTaConverters::class,
    NoticeConverters::class,
    VilageFcstConverters::class
)
abstract class Database: RoomDatabase() {
    abstract fun midTaDao(): MidTaDao
    abstract fun noticeDao(): NoticeDao
    abstract fun vilageFcstDao(): VilageFcstDao

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
