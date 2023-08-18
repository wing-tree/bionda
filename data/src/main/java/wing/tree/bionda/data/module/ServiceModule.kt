package wing.tree.bionda.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import wing.tree.bionda.data.service.ForecastService

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    fun providesForecastService(
        retrofit: Retrofit
    ): ForecastService {
        return retrofit.create(ForecastService::class.java)
    }
}
