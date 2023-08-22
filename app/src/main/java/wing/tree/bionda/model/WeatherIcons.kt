package wing.tree.bionda.model

import wing.tree.bionda.R

private typealias DrawableRes = Int

sealed interface WeatherIcons {
    val sky: Map<String, DrawableRes>
    val pty: Map<String, DrawableRes?>

    object Daytime : WeatherIcons {
        override val sky: Map<String, DrawableRes>
            get() = mapOf(
                "1" to R.drawable.wi_day_sunny,
                "3" to R.drawable.wi_day_cloudy,
                "4" to R.drawable.wi_cloudy
            )

        override val pty: Map<String, DrawableRes?>
            get() = mapOf(
                "0" to null,
                "1" to R.drawable.wi_day_rain,
                "2" to R.drawable.wi_day_rain_mix,
                "3" to R.drawable.wi_day_snow,
                "4" to R.drawable.wi_day_showers
            )
    }

    object Nighttime : WeatherIcons {
        override val sky: Map<String, DrawableRes>
            get() = mapOf(
                "1" to R.drawable.wi_night_clear,
                "3" to R.drawable.wi_night_alt_cloudy,
                "4" to R.drawable.wi_cloudy
            )

        override val pty: Map<String, DrawableRes?>
            get() = mapOf(
                "0" to null,
                "1" to R.drawable.wi_night_alt_rain,
                "2" to R.drawable.wi_night_alt_rain_mix,
                "3" to R.drawable.wi_night_alt_snow,
                "4" to R.drawable.wi_night_alt_showers
            )
    }
}
