package wing.tree.bionda.data

import wing.tree.bionda.data.constant.PATTERN_TM_FC
import wing.tree.bionda.data.extension.advanceHourOfDayBy
import wing.tree.bionda.data.extension.isNull
import wing.tree.bionda.data.model.weather.MidLandFcst
import wing.tree.bionda.data.model.weather.MidTa
import wing.tree.bionda.data.model.weather.VilageFcst
import wing.tree.bionda.data.service.VilageFcstInfoService
import wing.tree.bionda.data.source.local.WeatherDataSource as LocalDataSource

class PostProcessor(private val localDataSource: LocalDataSource) {
    suspend fun MidLandFcst.Remote.postProcess(regId: String, tmFc: String): MidLandFcst.Local {
        return with(toLocal(regId = regId, tmFc = tmFc)) {
            if (item.rnSt3Am.isNull()) {
                val previous = localDataSource.loadMidLandFcst(
                    regId = regId,
                    tmFc = tmFc.advanceHourOfDayBy(12, PATTERN_TM_FC)
                )

                prepend(previous)
            } else {
                this
            }
        }.also {
            localDataSource.cache(it)
        }
    }

    suspend fun MidTa.Remote.postProcess(regId: String, tmFc: String): MidTa.Local {
        return with(toLocal(regId, tmFc)) {
            if (item.taMin3.isNull()) {
                val previous = localDataSource.loadMidTa(
                    regId = regId,
                    tmFc = tmFc.advanceHourOfDayBy(12, PATTERN_TM_FC)
                )

                prepend(previous)
            } else {
                this
            }
        }.also {
            localDataSource.cache(it)
        }
    }

    suspend fun VilageFcst.Remote.postProcess(
        params: VilageFcstInfoService.Params
    ): VilageFcst.Local {
        val previous = params.baseCalendar.advanceHourOfDayBy(3).let {
            localDataSource.loadVilageFcst(params.copy(baseCalendar =  it))
        }

        return toLocal(params).prepend(previous).also {
            localDataSource.cache(it)
        }
    }
}
