package wing.tree.bionda.data.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.data.database.Database
import wing.tree.bionda.data.service.MidFcstInfoService
import wing.tree.bionda.data.service.RiseSetInfoService
import wing.tree.bionda.data.service.VilageFcstInfoService
import wing.tree.bionda.data.source.local.AlarmDataSource
import wing.tree.bionda.data.source.local.WeatherDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.WeatherDataSource as RemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    fun providesAlarmDataSource(
        database: Database
    ): AlarmDataSource {
        return AlarmDataSource(database.alarmDao())
    }

    @Provides
    fun providesLocalDataSource(
        @ApplicationContext context: Context,
        database: Database
    ): LocalDataSource {
        return LocalDataSource(
            context = context,
            midLandFcstDao = database.midLandFcstDao(),
            midTaDao = database.midTaDao(),
            lcRiseSetInfoDao = database.lcRiseSetInfoDao(),
            ultraSrtNcstDao = database.ultraSrtNcstDao(),
            vilageFcstDao = database.vilageFcstDao()
        )
    }

    @Provides
    fun providesRemoteDataSource(
        midFcstInfoService: MidFcstInfoService,
        riseSetInfoService: RiseSetInfoService,
        vilageFcstInfoService: VilageFcstInfoService
    ): RemoteDataSource {
        return RemoteDataSource(
            midFcstInfoService = midFcstInfoService,
            riseSetInfoService = riseSetInfoService,
            vilageFcstInfoService = vilageFcstInfoService
        )
    }
}
