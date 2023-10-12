package wing.tree.bionda.mapper

import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.extension.julianDay
import wing.tree.bionda.data.extension.mostCommon
import wing.tree.bionda.data.extension.roundToOneDecimalPlace
import wing.tree.bionda.data.extension.two
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.model.MidLandFcst.Local.LandFcst
import wing.tree.bionda.data.top.level.baseDateFormat
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.model.VilageFcst.Item
import java.time.LocalTime

object LandFcstMapper : DataModelMapper<List<Item>, LandFcst?> {
    override fun toPresentationModel(dataModel: List<Item>): LandFcst? = with(dataModel) {
        if (isEmpty()) {
            return null
        }

        val (rnStAm, wfAm) = filter {
            it.fcstTime.take(Int.two).int < LocalTime.NOON.hour
        }.run {
            val rnStAm = mapNotNull {
                it.pop?.int
            }
                .average()
                .roundToOneDecimalPlace()

            val ptyAm = mapNotNull {
                it.pty.value
            }
                .mostCommon(String.zero)

            val wfAm = if (ptyAm.isNotNull()) {
                ptyToWf(ptyAm)
            } else {
                mapNotNull { item ->
                    item.sky.value
                }
                    .mostCommon()
                    ?.let {
                        skyToWf(it)
                    }
            }

            rnStAm to wfAm
        }

        val (rnStPm, wfPm) = filterNot {
            it.fcstTime.take(Int.two).int < LocalTime.NOON.hour
        }.run {
            val rnStPm = mapNotNull {
                it.pop?.int
            }
                .average()
                .roundToOneDecimalPlace()

            val ptyPm = mapNotNull {
                it.pty.value
            }
                .mostCommon(String.zero)

            val wfPm = if (ptyPm.isNotNull()) {
                ptyToWf(ptyPm)
            } else {
                mapNotNull {
                    it.sky.value
                }
                    .mostCommon()
                    ?.let {
                        skyToWf(it)
                    }
            }

            rnStPm to wfPm
        }

        val fcstDate = first().fcstDate
        val date = baseDateFormat.parse(fcstDate)
        val julianDay = koreaCalendar(date).julianDay
        val n = julianDay.minus(koreaCalendar.julianDay)

        return LandFcst(
            n = n,
            rnStAm = rnStAm,
            rnStPm = rnStPm,
            rnSt = null,
            wfAm = wfAm,
            wfPm = wfPm,
            wf = null
        )
    }

    private fun ptyToWf(pty: String) = when(pty) {
        "1" -> "구름많고 비"
        "2" -> "구름많고 비/눈"
        "3" -> "구름많고 눈"
        "4" -> "구름많고 소나기"
        else -> "맑음"
    }

    private fun skyToWf(sky: String) = when(sky) {
        "1" -> "맑음"
        "3" -> "구름많음"
        "4" -> "흐림"
        else -> "맑음"
    }
}
