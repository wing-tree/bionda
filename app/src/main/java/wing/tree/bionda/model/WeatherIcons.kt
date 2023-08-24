package wing.tree.bionda.model

import wing.tree.bionda.R

private typealias DrawableRes = Int

sealed interface WeatherIcons {
    val sky: Map<String, DrawableRes>
    val pty: Map<String, DrawableRes?>
    val wf: Map<String, DrawableRes>

    object Daytime : WeatherIcons {
        override val sky: Map<String, DrawableRes> = mapOf(
            "1" to R.drawable.wi_day_sunny,
            "3" to R.drawable.wi_day_cloudy,
            "4" to R.drawable.wi_cloudy
        )

        override val pty: Map<String, DrawableRes?> = mapOf(
            "0" to null,
            "1" to R.drawable.wi_day_rain,
            "2" to R.drawable.wi_day_rain_mix,
            "3" to R.drawable.wi_day_snow,
            "4" to R.drawable.wi_day_showers
        )

        override val wf: Map<String, DrawableRes> = mapOf(
            "맑음" to R.drawable.wi_day_sunny,
            "구름많음" to R.drawable.wi_day_cloudy,
            "구름많고 비" to R.drawable.wi_day_rain,
            "구름많고 눈" to R.drawable.wi_day_snow,
            "구름많고 비/눈" to R.drawable.wi_day_rain_mix,
            "구름많고 소나기" to R.drawable.wi_day_showers,
            "흐림" to R.drawable.wi_cloudy,
            "흐리고 비" to R.drawable.wi_rain,
            "흐리고 눈" to R.drawable.wi_snow,
            "흐리고 비/눈" to R.drawable.wi_rain_mix,
            "흐리고 소나기" to R.drawable.wi_showers
        )
    }

    object Nighttime : WeatherIcons {
        override val sky: Map<String, DrawableRes> = mapOf(
            "1" to R.drawable.wi_night_clear,
            "3" to R.drawable.wi_night_alt_cloudy,
            "4" to R.drawable.wi_cloudy
        )

        override val pty: Map<String, DrawableRes?> = mapOf(
            "0" to null,
            "1" to R.drawable.wi_night_alt_rain,
            "2" to R.drawable.wi_night_alt_rain_mix,
            "3" to R.drawable.wi_night_alt_snow,
            "4" to R.drawable.wi_night_alt_showers
        )

        override val wf: Map<String, DrawableRes> = mapOf(
            "맑음" to R.drawable.wi_night_clear,
            "구름많음" to R.drawable.wi_night_alt_cloudy,
            "구름많고 비" to R.drawable.wi_night_alt_rain,
            "구름많고 눈" to R.drawable.wi_night_alt_snow,
            "구름많고 비/눈" to R.drawable.wi_night_alt_rain_mix,
            "구름많고 소나기" to R.drawable.wi_night_alt_showers,
            "흐림" to R.drawable.wi_cloudy,
            "흐리고 비" to R.drawable.wi_rain,
            "흐리고 눈" to R.drawable.wi_snow,
            "흐리고 비/눈" to R.drawable.wi_rain_mix,
            "흐리고 소나기" to R.drawable.wi_showers
        )
    }
}
