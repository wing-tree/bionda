package wing.tree.bionda.data.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.data.database.AreaDatabase
import wing.tree.bionda.data.database.Database

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun providesAreaDatabase(@ApplicationContext context: Context): AreaDatabase {
        return AreaDatabase.getInstance(context)
    }

    @Provides
    fun providesDatabase(@ApplicationContext context: Context): Database {
        return Database.getInstance(context)
    }
}
