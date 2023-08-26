package wing.tree.bionda.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import wing.tree.bionda.data.qualifier.Qualifier
import wing.tree.bionda.data.service.MidFcstInfoService
import wing.tree.bionda.data.service.RiseSetInfoService
import wing.tree.bionda.data.service.VilageFcstInfoService

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    fun providesMidFcstInfoService(
        @Qualifier.MidFcstInfoService retrofit: Retrofit
    ): MidFcstInfoService {
        return retrofit.create(MidFcstInfoService::class.java)
    }

    @Provides
    fun providesRiseSetInfoService(
        @Qualifier.RiseSetInfoService retrofit: Retrofit
    ): RiseSetInfoService {
        return retrofit.create(RiseSetInfoService::class.java)
    }

    @Provides
    fun providesVilageFcstInfoService(
        @Qualifier.VilageFcstInfoService retrofit: Retrofit
    ): VilageFcstInfoService {
        return retrofit.create(VilageFcstInfoService::class.java)
    }
}
