package wing.tree.bionda.data.qualifier

import javax.inject.Qualifier

object Qualifier {
    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class MidFcstInfoService

    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class VilageFcstInfoService
}
