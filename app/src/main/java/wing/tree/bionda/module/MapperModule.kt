package wing.tree.bionda.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.mapper.VilageFcstMapper

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {
    @Provides
    fun providesVilageFcstMapper(): VilageFcstMapper {
        return VilageFcstMapper()
    }
}
