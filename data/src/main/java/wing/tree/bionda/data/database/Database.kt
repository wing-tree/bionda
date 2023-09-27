package wing.tree.bionda.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Room
import androidx.room.RoomDatabase
import wing.tree.bionda.data.database.dao.AirDiffusionIdxDao
import wing.tree.bionda.data.database.dao.AlarmDao
import wing.tree.bionda.data.database.dao.LCRiseSetInfoDao
import wing.tree.bionda.data.database.dao.MidLandFcstDao
import wing.tree.bionda.data.database.dao.MidTaDao
import wing.tree.bionda.data.database.dao.TmnDao
import wing.tree.bionda.data.database.dao.TmxDao
import wing.tree.bionda.data.database.dao.UVIdxDao
import wing.tree.bionda.data.database.dao.UltraSrtFcstDao
import wing.tree.bionda.data.database.dao.UltraSrtNcstDao
import wing.tree.bionda.data.database.dao.VilageFcstDao
import wing.tree.bionda.data.database.type.converters.AlarmConverters
import wing.tree.bionda.data.database.type.converters.LCRiseSetInfoConverters
import wing.tree.bionda.data.database.type.converters.LivingWthrIdxConverters
import wing.tree.bionda.data.database.type.converters.MidLandFcstConverters
import wing.tree.bionda.data.database.type.converters.MidTaConverters
import wing.tree.bionda.data.database.type.converters.UltraSrtNcstConverters
import wing.tree.bionda.data.database.type.converters.VilageFcstConverters
import wing.tree.bionda.data.model.Alarm
import wing.tree.bionda.data.model.Tmn
import wing.tree.bionda.data.model.Tmx
import wing.tree.bionda.data.model.LCRiseSetInfo.Local as LCRiseSetInfo
import wing.tree.bionda.data.model.LivingWthrIdx.AirDiffusionIdx.Local as AirDiffusionIdx
import wing.tree.bionda.data.model.LivingWthrIdx.UVIdx.Local as UVIdx
import wing.tree.bionda.data.model.MidLandFcst.Local as MidLandFcst
import wing.tree.bionda.data.model.MidTa.Local as MidTa
import wing.tree.bionda.data.model.UltraSrtFcst.Local as UltraSrtFcst
import wing.tree.bionda.data.model.UltraSrtNcst.Local as UltraSrtNcst
import wing.tree.bionda.data.model.VilageFcst.Local as VilageFcst

@androidx.room.Database(
    entities = [
        AirDiffusionIdx::class,
        Alarm::class,
        LCRiseSetInfo::class,
        MidLandFcst::class,
        MidTa::class,
        Tmn::class,
        Tmx::class,
        UVIdx::class,
        UltraSrtFcst::class,
        UltraSrtNcst::class,
        VilageFcst::class
    ],
    exportSchema = true,
    version = 2,
    autoMigrations = [
        AutoMigration (from = 1, to = 2)
    ]
)
@androidx.room.TypeConverters(
    AlarmConverters::class,
    LCRiseSetInfoConverters::class,
    MidLandFcstConverters::class,
    MidTaConverters::class,
    LivingWthrIdxConverters::class,
    UltraSrtNcstConverters::class,
    VilageFcstConverters::class
)
abstract class Database : RoomDatabase() {
    abstract val airDiffusionIdxDao: AirDiffusionIdxDao
    abstract val alarmDao: AlarmDao
    abstract val midLandFcstDao: MidLandFcstDao
    abstract val midTaDao: MidTaDao
    abstract val lcRiseSetInfoDao: LCRiseSetInfoDao
    abstract val tmnDao: TmnDao
    abstract val tmxDao: TmxDao
    abstract val uvIdxDao: UVIdxDao
    abstract val ultraSrtFcstDao: UltraSrtFcstDao
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
