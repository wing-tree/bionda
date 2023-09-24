package wing.tree.bionda.mapper

import wing.tree.bionda.data.extension.doubleOrNull
import wing.tree.bionda.data.extension.int
import wing.tree.bionda.data.extension.`is`
import wing.tree.bionda.data.extension.isNotNull
import wing.tree.bionda.data.extension.julianDay
import wing.tree.bionda.data.extension.zero
import wing.tree.bionda.data.model.MidTa.Local.Ta
import wing.tree.bionda.data.top.level.baseDateFormat
import wing.tree.bionda.data.top.level.koreaCalendar
import wing.tree.bionda.model.VilageFcst.Item

object TaMapper : DataModelMapper<List<Item>, Ta?> {
    override fun toPresentationModel(dataModel: List<Item>): Ta? {
        if (dataModel.isEmpty()) {
            return null
        }

        val min = with(dataModel) {
            find {
                it.tmn.isNotNull()
            }
                ?.tmn?.doubleOrNull
                ?: dataModel.minOfOrNull {
                    it.tmp?.doubleOrNull ?: Double.MAX_VALUE
                }
        }
            ?: return null

        val max = with(dataModel) {
            find {
                it.tmx.isNotNull()
            }
                ?.tmx?.doubleOrNull
                ?: dataModel.maxOfOrNull {
                    it.tmp?.doubleOrNull ?: Double.MIN_VALUE
                }
        }
            ?: return null

        when {
            min `is` Double.MAX_VALUE -> return null
            max `is` Double.MIN_VALUE -> return null
        }

        val fcstDate = dataModel
            .first()
            .fcstDate

        val julianDay = koreaCalendar(baseDateFormat.parse(fcstDate)).julianDay
        val n = julianDay.minus(koreaCalendar.julianDay)

        return Ta(
            min = min.int,
            minLow = Int.zero,
            minHigh = Int.zero,
            max = max.int,
            maxLow = Int.zero,
            maxHigh = Int.zero,
            n = n
        )
    }
}
