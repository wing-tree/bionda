package wing.tree.bionda.model

import androidx.annotation.DrawableRes
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate
import wing.tree.bionda.data.core.Season
import wing.tree.bionda.data.core.TimesOfDay
import wing.tree.bionda.data.core.season
import wing.tree.bionda.data.extension.Builder.replaceAt
import wing.tree.bionda.data.extension.doubleOrNull
import wing.tree.bionda.data.extension.doubleOrZero
import wing.tree.bionda.data.extension.empty
import wing.tree.bionda.data.extension.ifZero
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.isNonNegative
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.extension.not
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.oneHundred
import wing.tree.bionda.data.extension.roundToOneDecimalPlace
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.model.Category
import wing.tree.bionda.data.model.CodeValue
import wing.tree.bionda.data.model.LCRiseSetInfo
import wing.tree.bionda.model.VilageFcst.Item.Type.RiseSet
import wing.tree.bionda.top.level.calculateHeatIndex
import wing.tree.bionda.top.level.calculateWindChill
import wing.tree.bionda.top.level.emptyPersistentMap

data class VilageFcst(val items: PersistentList<Item>) : PersistentList<VilageFcst.Item> by items  {
    data class Item(
        val fcstDate: String,
        val fcstTime: String,
        val codeValues: PersistentMap<String, String>,
        val timesOfDay: TimesOfDay = TimesOfDay.DAYTIME,
        val type: Type = Type.VilageFcst
    ) {
        private val heatIndex: Double? get() = with(tmp) {
            if (isNotNull()) {
                calculateHeatIndex(
                    ta = doubleOrZero,
                    rh = reh?.doubleOrNull ?: Double.zero
                )
                    .roundToOneDecimalPlace()
            } else {
                null
            }
        }

        private val weatherIcons = when (timesOfDay) {
            TimesOfDay.DAYTIME -> WeatherIcons.Daytime
            TimesOfDay.NIGHTTIME -> WeatherIcons.Nighttime
        }

        private val windChill: Double? get() = with(tmp) {
            if (isNotNull()) {
                calculateWindChill(
                    ta = doubleOrZero,
                    v = wsd?.doubleOrNull ?: Double.zero
                )
                    .roundToOneDecimalPlace()
            } else {
                null
            }
        }

        val feelsLikeTemperature: Double? get() = when(season) {
            Season.SUMMER -> heatIndex
            Season.WINTER -> windChill
        }

        val hourOfDay: Int get() = fcstTime.int.div(Int.oneHundred)
        val pcp: String? get() = when (type) {
            is Type.UltraSrtFcst -> rn1
            else -> codeValues[Category.PCP]
        }

        val pop = codeValues[Category.POP]
        val pty: CodeValue.Pty get() = CodeValue.Pty(code = codeValues[Category.PTY])
        val reh = codeValues[Category.REH]
        val rn1 = codeValues[Category.RN1]
        val sky: CodeValue.Sky get() = CodeValue.Sky(code = codeValues[Category.SKY])
        val t1h = codeValues[Category.T1H]
        val tmp: String? get() = when (type) {
            is Type.UltraSrtFcst -> t1h
            else -> codeValues[Category.TMP]
        }

        val tmn = codeValues[Category.TMN]
        val tmx = codeValues[Category.TMX]
        val weatherIcon: Int? @DrawableRes get() = type.getWeatherIcon(this)
        val vec = codeValues[Category.VEC]
        val wsd = codeValues[Category.WSD]

        sealed class Type {
            @DrawableRes
            abstract fun getWeatherIcon(item: Item): Int?

            object UltraSrtFcst : Type() {
                override fun getWeatherIcon(item: Item) = with(item) {
                    weatherIcons.let {
                        it.pty[pty.code] ?: it.sky[sky.code]
                    }
                }
            }

            object VilageFcst : Type() {
                override fun getWeatherIcon(item: Item) = with(item) {
                    weatherIcons.let {
                        it.pty[pty.code] ?: it.sky[sky.code]
                    }
                }
            }

            sealed class RiseSet : Type() {
                operator fun not() = when(this) {
                    Sunrise -> Sunset
                    Sunset -> Sunrise
                }

                object Sunrise : RiseSet() {
                    override fun getWeatherIcon(item: Item) = item.weatherIcons.sunrise
                }

                object Sunset : RiseSet() {
                    override fun getWeatherIcon(item: Item): Int = item.weatherIcons.sunset
                }
            }
        }
    }

    fun insertLCRiseSetInfo(lcRiseSetInfo: List<LCRiseSetInfo.Local>): VilageFcst {
        val items = lcRiseSetInfo.fold(items.builder()) { acc, element ->
            acc.insertLCRiseSetInfo(element)
        }

        items.updateTimesOfDay()

        return copy(items = items.build())
    }

    fun overwrite(vilageFcst: VilageFcst): VilageFcst {
        val builder = items.builder()

        vilageFcst.items.forEach { item ->
            builder.indexOfFirst {
                when {
                    it.fcstDate not item.fcstDate -> false
                    it.fcstTime not item.fcstTime -> false
                    else -> true
                }
            }.let { index ->
                if (index.isNonNegative) {
                    builder.replaceAt(
                        index,
                        item.copy(
                            codeValues = item.codeValues.mutate {
                                with(builder[index]) {
                                    it.putIfAbsent(Category.POP, pop ?: String.empty)
                                    it.putIfAbsent(Category.PTY, pty.code ?: String.empty)
                                    it.putIfAbsent(Category.REH, reh ?: String.empty)
                                    it.putIfAbsent(Category.SKY, sky.code ?: String.empty)
                                    it.putIfAbsent(Category.TMP, tmp ?: String.empty)
                                }
                            }
                        )
                    )
                }
            }
        }

        return copy(items = builder.build())
    }

    private fun PersistentList.Builder<Item>.insertLCRiseSetInfo(
        lcRiseSetInfo: LCRiseSetInfo.Local
    ): PersistentList.Builder<Item> {
        val locdate = lcRiseSetInfo.locdate.trim()
        val sunrise = lcRiseSetInfo.sunrise.trim()
        val sunset = lcRiseSetInfo.sunset.trim()

        firstOrNull { item ->
            val hourOfDay = item.hourOfDay.ifZero(defaultValue = 24)

            with(sunrise.int.div(Int.oneHundred)) {
                when {
                    item.fcstDate not locdate -> false
                    hourOfDay < this -> false
                    else -> hourOfDay.minus(this) `is` Int.one
                }
            }
        }?.let {
            val item = Item(
                fcstDate = locdate,
                fcstTime = sunrise,
                codeValues = emptyPersistentMap(),
                type = RiseSet.Sunrise
            )

            add(indexOf(it), item)
        }

        firstOrNull { item ->
            val hourOfDay = item.hourOfDay.ifZero(defaultValue = 24)

            with(sunset.int.div(Int.oneHundred)) {
                when {
                    item.fcstDate not locdate -> false
                    hourOfDay < this -> false
                    else -> hourOfDay.minus(this) `is` Int.one
                }
            }
        }?.let {
            val item = Item(
                fcstDate = locdate,
                fcstTime = sunset,
                codeValues = emptyPersistentMap(),
                type = RiseSet.Sunset
            )

            add(indexOf(it), item)
        }

        if (firstOrNull()?.type is RiseSet) {
            removeFirst()
        }

        return this
    }

    private fun PersistentList.Builder<Item>.updateTimesOfDay() {
        var type = map {
            it.type
        }
            .filterIsInstance<RiseSet>()
            .firstOrNull()
            ?.not()
            ?: return

        forEachIndexed { index, item ->
            if (item.type is RiseSet) {
                type = item.type
            } else {
                val timesOfDay = when (type) {
                    RiseSet.Sunrise -> TimesOfDay.DAYTIME
                    RiseSet.Sunset -> TimesOfDay.NIGHTTIME
                }

                set(index, item.copy(timesOfDay = timesOfDay))
            }
        }
    }
}
