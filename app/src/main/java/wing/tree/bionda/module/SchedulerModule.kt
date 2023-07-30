package wing.tree.bionda.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.scheduler.AlarmScheduler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SchedulerModule {
    @Provides
    fun providesAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler {
        return AlarmScheduler(context)
    }
}
