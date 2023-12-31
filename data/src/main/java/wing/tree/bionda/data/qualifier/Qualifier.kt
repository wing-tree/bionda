package wing.tree.bionda.data.qualifier

import javax.inject.Qualifier

object Qualifier {
    @Retention
    @Qualifier
    annotation class LivingWthrIdxService

    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class MidFcstInfoService

    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class RiseSetInfoService

    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class VilageFcstInfoService
}
