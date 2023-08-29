package wing.tree.bionda.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.data.core.PostProcessor
import wing.tree.bionda.data.source.local.WeatherDataSource

@Module
@InstallIn(SingletonComponent::class)
object ProcessorModule {
    @Provides
    fun providesPostProvider(localDataSource: WeatherDataSource): PostProcessor {
        return PostProcessor(localDataSource)
    }
}
