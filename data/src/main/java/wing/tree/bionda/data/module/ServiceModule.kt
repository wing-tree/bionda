package wing.tree.bionda.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import wing.tree.bionda.data.service.ForecastService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun providesForecastService(
        retrofit: Retrofit
    ): ForecastService {
        return retrofit.create(ForecastService::class.java)
    }
}
