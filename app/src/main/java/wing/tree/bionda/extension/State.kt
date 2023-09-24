package wing.tree.bionda.extension

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import wing.tree.bionda.data.core.State
import wing.tree.bionda.data.core.isSuccess
import wing.tree.bionda.data.core.map
import wing.tree.bionda.data.extension.isNull
import wing.tree.bionda.data.model.LCRiseSetInfo
import wing.tree.bionda.data.model.MidLandFcst.Local.LandFcst
import wing.tree.bionda.data.model.MidLandFcstTa
import wing.tree.bionda.data.model.MidLandFcstTa.BothSuccess
import wing.tree.bionda.data.model.MidTa.Local.Ta
import wing.tree.bionda.mapper.LandFcstMapper
import wing.tree.bionda.mapper.TaMapper
import wing.tree.bionda.model.VilageFcst

fun State<VilageFcst>.insertLCRiseSetInfo(
    lcRiseSetInfo: State<ImmutableList<LCRiseSetInfo.Local>>
) = map {
    with(lcRiseSetInfo) {
        if (isSuccess()) {
            it.insertLCRiseSetInfo(value)
        } else {
            it
        }
    }
}

fun State<MidLandFcstTa>.prependVilageFcst(
    vilageFcst: State<VilageFcst>
) = map {
    if (isSuccess()) {
        fun <T> Pair<T?, T?>.dayAfterTomorrow() = second
        fun <T> Pair<T?, T?>.tomorrow() = first

        val items = with(vilageFcst) {
            if (isSuccess()) {
                value.items.ifEmpty {
                    return@map it
                }
            } else {
                return@map it
            }
        }

        val tomorrow = items.tomorrow()
        val dayAfterTomorrow = items.dayAfterTomorrow()

        val landFcst = with(LandFcstMapper) {
            Pair(
                toPresentationModel(tomorrow),
                toPresentationModel(dayAfterTomorrow)
            )
        }

        val ta = with(TaMapper) {
            Pair(
                toPresentationModel(tomorrow),
                toPresentationModel(dayAfterTomorrow)
            )
        }

        when (val value = value) {
            is BothSuccess -> {
                fun LandFcst?.toItem(ta: Ta?): BothSuccess.Item? = ta?.let {
                    if (this.isNull()) {
                        return null
                    }

                    BothSuccess.Item(
                        n = n,
                        landFcst = this,
                        ta = ta
                    )
                }

                val builder = persistentListOf<BothSuccess.Item>()
                    .builder()

                landFcst.dayAfterTomorrow()
                    .toItem(ta.dayAfterTomorrow())
                    ?.let { item ->
                        landFcst.tomorrow()
                            .toItem(ta.tomorrow())
                            ?.let(builder::add)

                        builder.add(item)
                    }

                value.copy(prefix = builder.build())
            }

            is MidLandFcstTa.OneOfSuccess -> when(value) {
                is MidLandFcstTa.OneOfSuccess.MidLandFcst -> with(landFcst) {
                    val prefix = persistentListOf<LandFcst>().apply {
                        tomorrow()?.let { landFcst ->
                            add(landFcst)
                        }

                        dayAfterTomorrow()?.let { landFcst ->
                            add(landFcst)
                        } ?: return@map it
                    }

                    value.copy(prefix = prefix)
                }

                is MidLandFcstTa.OneOfSuccess.MidTa -> with(ta) {
                    val prefix = persistentListOf<Ta>().apply {
                        tomorrow()?.let { ta ->
                            add(ta)
                        }

                        dayAfterTomorrow()?.let { ta ->
                            add(ta)
                        } ?: return@map it
                    }

                    value.copy(prefix = prefix)
                }
            }

            else -> it
        }
    } else {
        it
    }
}
