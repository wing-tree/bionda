package wing.tree.bionda.data.module.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.data.service.LivingWthrIdxService
import wing.tree.bionda.data.service.MidFcstInfoService
import wing.tree.bionda.data.service.RiseSetInfoService
import wing.tree.bionda.data.service.VilageFcstInfoService
import wing.tree.bionda.data.source.remote.LivingWthrIdxDataSource
import wing.tree.bionda.data.source.remote.WeatherDataSource

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    fun providesLivingWthrIdxDataSource(
        livingWthrIdxService: LivingWthrIdxService
    ): LivingWthrIdxDataSource {
        return LivingWthrIdxDataSource(
            livingWthrIdxService = livingWthrIdxService
        )
    }

    @Provides
    fun providesWeatherDataSource(
        livingWthrIdxService: LivingWthrIdxService,
        midFcstInfoService: MidFcstInfoService,
        riseSetInfoService: RiseSetInfoService,
        vilageFcstInfoService: VilageFcstInfoService
    ): WeatherDataSource {
        return WeatherDataSource(
            livingWthrIdxService = livingWthrIdxService,
            midFcstInfoService = midFcstInfoService,
            riseSetInfoService = riseSetInfoService,
            vilageFcstInfoService = vilageFcstInfoService
        )
    }
}
