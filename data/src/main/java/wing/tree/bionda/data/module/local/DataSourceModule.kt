package wing.tree.bionda.data.module.local

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.data.database.AreaDatabase
import wing.tree.bionda.data.database.Database
import wing.tree.bionda.data.source.local.AlarmDataSource
import wing.tree.bionda.data.source.local.AreaDataSource
import wing.tree.bionda.data.source.local.LivingWthrIdxDataSource
import wing.tree.bionda.data.source.local.WeatherDataSource

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    fun providesAlarmDataSource(
        database: Database
    ): AlarmDataSource {
        return AlarmDataSource(database.alarmDao)
    }

    @Provides
    fun providesAreaDataSource(
        database: AreaDatabase
    ): AreaDataSource {
        return AreaDataSource(
            dao = database.dao
        )
    }

    @Provides
    fun providesLivingWthrIdxDataSource(
        database: Database
    ): LivingWthrIdxDataSource {
        return LivingWthrIdxDataSource(
            airDiffusionIdxDao = database.airDiffusionIdxDao,
            uvIdxDao = database.uvIdxDao
        )
    }

    @Provides
    fun providesWeatherDataSource(
        @ApplicationContext context: Context,
        database: Database
    ): WeatherDataSource {
        return with(database) {
            WeatherDataSource(
                context = context,
                midLandFcstDao = midLandFcstDao,
                midTaDao = midTaDao,
                lcRiseSetInfoDao = lcRiseSetInfoDao,
                tmnDao = tmnDao,
                tmxDao = tmxDao,
                ultraSrtFcstDao = ultraSrtFcstDao,
                ultraSrtNcstDao = ultraSrtNcstDao,
                vilageFcstDao = vilageFcstDao
            )
        }
    }
}
