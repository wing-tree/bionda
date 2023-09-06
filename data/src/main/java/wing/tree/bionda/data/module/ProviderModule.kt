package wing.tree.bionda.data.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.data.database.AreaDatabase
import wing.tree.bionda.data.provider.AreaNoProvider
import wing.tree.bionda.data.provider.LocationProvider

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {
    @Provides
    fun providesAreaNoProvider(database: AreaDatabase): AreaNoProvider {
        return AreaNoProvider(database.dao)
    }

    @Provides
    fun providesLocationProvider(@ApplicationContext context: Context): LocationProvider {
        return LocationProvider(context)
    }
}
