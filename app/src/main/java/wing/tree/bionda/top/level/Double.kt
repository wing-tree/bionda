package wing.tree.bionda.top.level

import kotlin.math.atan
import kotlin.math.pow

fun calculateHeatIndex(ta: Double, rh: Double): Double {
    val tw = ta.times(atan(0.151977.times(rh.plus(8.313659).pow(0.5))))
        .plus(atan(ta.plus(rh)))
        .minus(atan(rh.minus(1.67633)))
        .plus(0.00391838.times(rh.pow(1.5)).times(atan(0.023101.times(rh))))
        .minus(4.686035)

    return 0.55399.times(tw)
        .minus(0.2442)
        .plus(0.45535.times(ta))
        .minus(0.0022.times(tw.pow(2.0)))
        .plus(0.00278.times(tw).times(ta))
        .plus(3.0)
}

fun calculateWindChill(ta: Double, v: Double): Double {
    return if (v > 4.8) {
        with(v.times(3.6).pow(0.16)) {
            13.12.plus(0.6215.times(ta))
                .minus(11.37.times(this))
                .plus(0.3965.times(this).times(ta))
                .coerceAtMost(ta)
        }
    } else {
        ta
    }
}
