package wing.tree.bionda.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import wing.tree.bionda.data.database.dao.AlarmDao
import wing.tree.bionda.data.database.dao.LCRiseSetInfoDao
import wing.tree.bionda.data.database.dao.MidLandFcstDao
import wing.tree.bionda.data.database.dao.MidTaDao
import wing.tree.bionda.data.database.dao.UVIdxDao
import wing.tree.bionda.data.database.dao.UltraSrtNcstDao
import wing.tree.bionda.data.database.dao.VilageFcstDao
import wing.tree.bionda.data.database.type.converters.AlarmConverters
import wing.tree.bionda.data.database.type.converters.LCRiseSetInfoConverters
import wing.tree.bionda.data.database.type.converters.MidLandFcstConverters
import wing.tree.bionda.data.database.type.converters.MidTaConverters
import wing.tree.bionda.data.database.type.converters.UVIdxConverters
import wing.tree.bionda.data.database.type.converters.UltraSrtNcstConverters
import wing.tree.bionda.data.database.type.converters.VilageFcstConverters
import wing.tree.bionda.data.model.Alarm
import wing.tree.bionda.data.model.LCRiseSetInfo.Local as LCRiseSetInfo
import wing.tree.bionda.data.model.MidLandFcst.Local as MidLandFcst
import wing.tree.bionda.data.model.MidTa.Local as MidTa
import wing.tree.bionda.data.model.UVIdx.Local as UVIdx
import wing.tree.bionda.data.model.UltraSrtNcst.Local as UltraSrtNcst
import wing.tree.bionda.data.model.VilageFcst.Local as VilageFcst

@androidx.room.Database(
    entities = [
        Alarm::class,
        LCRiseSetInfo::class,
        MidLandFcst::class,
        MidTa::class,
        UVIdx::class,
        UltraSrtNcst::class,
        VilageFcst::class
    ],
    exportSchema = true,
    version = 1
)
@androidx.room.TypeConverters(
    AlarmConverters::class,
    LCRiseSetInfoConverters::class,
    MidLandFcstConverters::class,
    MidTaConverters::class,
    UVIdxConverters::class,
    UltraSrtNcstConverters::class,
    VilageFcstConverters::class
)
abstract class Database : RoomDatabase() {
    abstract val alarmDao: AlarmDao
    abstract val midLandFcstDao: MidLandFcstDao
    abstract val midTaDao: MidTaDao
    abstract val lcRiseSetInfoDao: LCRiseSetInfoDao
    abstract val uvIdxDao: UVIdxDao
    abstract val ultraSrtNcstDao: UltraSrtNcstDao
    abstract val vilageFcstDao: VilageFcstDao

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
