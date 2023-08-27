package wing.tree.bionda.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import wing.tree.bionda.data.database.dao.AlarmDao
import wing.tree.bionda.data.database.dao.AreaDao
import wing.tree.bionda.data.database.dao.LCRiseSetInfoDao
import wing.tree.bionda.data.database.dao.MidLandFcstDao
import wing.tree.bionda.data.database.dao.MidTaDao
import wing.tree.bionda.data.database.dao.UltraSrtNcstDao
import wing.tree.bionda.data.database.dao.VilageFcstDao
import wing.tree.bionda.data.database.type.converters.AlarmConverters
import wing.tree.bionda.data.database.type.converters.LCRiseSetInfoConverters
import wing.tree.bionda.data.database.type.converters.MidLandFcstConverters
import wing.tree.bionda.data.database.type.converters.MidTaConverters
import wing.tree.bionda.data.database.type.converters.UltraSrtNcstConverters
import wing.tree.bionda.data.database.type.converters.VilageFcstConverters
import wing.tree.bionda.data.model.Alarm
import wing.tree.bionda.data.model.Area
import wing.tree.bionda.data.model.weather.LCRiseSetInfo.Local as LCRiseSetInfo
import wing.tree.bionda.data.model.weather.MidLandFcst.Local as MidLandFcst
import wing.tree.bionda.data.model.weather.MidTa.Local as MidTa
import wing.tree.bionda.data.model.weather.UltraSrtNcst.Local as UltraSrtNcst
import wing.tree.bionda.data.model.weather.VilageFcst.Local as VilageFcst

@androidx.room.Database(
    entities = [
        Area::class,
        Alarm::class,
        LCRiseSetInfo::class,
        MidLandFcst::class,
        MidTa::class,
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
    UltraSrtNcstConverters::class,
    VilageFcstConverters::class
)
abstract class Database : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun areaDao(): AreaDao
    abstract fun midLandFcstDao(): MidLandFcstDao
    abstract fun midTaDao(): MidTaDao
    abstract fun lcRiseSetInfoDao(): LCRiseSetInfoDao
    abstract fun ultraSrtNcstDao(): UltraSrtNcstDao
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
