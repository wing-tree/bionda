package wing.tree.bionda.data.source.remote

import kotlinx.coroutines.delay
import timber.log.Timber
import wing.tree.bionda.data.BuildConfig
import wing.tree.bionda.data.constant.COMMA
import wing.tree.bionda.data.constant.SPACE
import wing.tree.bionda.data.core.DataType
import wing.tree.bionda.data.core.Response
import wing.tree.bionda.data.extension.advanceHourOfDayBy
import wing.tree.bionda.data.extension.cloneAsCalendar
import wing.tree.bionda.data.extension.eight
import wing.tree.bionda.data.extension.five
import wing.tree.bionda.data.extension.hundreds
import wing.tree.bionda.data.extension.long
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.time
import wing.tree.bionda.data.extension.two
import wing.tree.bionda.data.model.LCRiseSetInfo
import wing.tree.bionda.data.model.UVIdx
import wing.tree.bionda.data.model.UltraSrtFcst
import wing.tree.bionda.data.model.VilageFcst
import wing.tree.bionda.data.service.LivingWthrIdxService
import wing.tree.bionda.data.service.MidFcstInfoService
import wing.tree.bionda.data.service.RiseSetInfoService
import wing.tree.bionda.data.service.VilageFcstInfoService
import wing.tree.bionda.data.top.level.uvIdxCalendar
import kotlin.math.pow

class WeatherDataSource(
    private val livingWthrIdxService: LivingWthrIdxService,
    private val midFcstInfoService: MidFcstInfoService,
    private val riseSetInfoService: RiseSetInfoService,
    private val vilageFcstInfoService: VilageFcstInfoService
) {
    private suspend fun <T> retry(
        retries: Int = Int.two,
        initialDelay: Long = Long.five.hundreds,
        block: suspend () -> T
    ): T {
        repeat(retries) { attempt ->
            try {
                return block()
            } catch (cause: Throwable) {
                Timber.e(cause)
            }

            delay(initialDelay.times(Double.two.pow(attempt)).long)
        }

        return block()
    }

    suspend fun getLCRiseSetInfo(
        params: RiseSetInfoService.Params
    ) = retry {
        with(params) {
            riseSetInfoService.getLCRiseSetInfo(
                serviceKey = BuildConfig.riseSetInfoServiceKey,
                locdate = locdate,
                longitude = longitude,
                latitude = latitude,
                dnYn = dnYn
            )
        }.let {
            LCRiseSetInfo.Remote(it)
        }
    }

    suspend fun getMidLandFcst(
        numOfRows: Int = Int.one,
        pageNo: Int = Int.one,
        regId: String,
        tmFc: String
    ) = retry {
        midFcstInfoService.getMidLandFcst(
            serviceKey = BuildConfig.midFcstInfoServiceKey,
            numOfRows = numOfRows,
            pageNo = pageNo,
            dataType = DataType.JSON,
            regId = regId,
            tmFc = tmFc
        )
    }
        .validate(
            errorMsg = {
                buildList {
                    add("resultCode=${it.header.resultCode}")
                    add("resultMsg=${it.header.resultMsg}")
                    add("regId=$regId")
                    add("tmFc=$tmFc")
                }
                    .joinToString("$COMMA$SPACE")
            }
        )

    suspend fun getMidTa(
        numOfRows: Int = Int.one,
        pageNo: Int = Int.one,
        regId: String,
        tmFc: String
    ) = retry {
        midFcstInfoService.getMidTa(
            serviceKey = BuildConfig.midFcstInfoServiceKey,
            numOfRows = numOfRows,
            pageNo = pageNo,
            dataType = DataType.JSON,
            regId = regId,
            tmFc = tmFc
        )
    }
        .validate(
            errorMsg = {
                buildList {
                    add("resultCode=${it.header.resultCode}")
                    add("resultMsg=${it.header.resultMsg}")
                    add("regId=$regId")
                    add("tmFc=$tmFc")
                }
                    .joinToString("$COMMA$SPACE")
            }
        )

    suspend fun getUVIdx(
        numOfRows: Int = Int.one,
        pageNo: Int = Int.one,
        areaNo: String,
        time: String
    ): UVIdx.Remote {
        fun block(areaNo: String, time: String) = suspend {
            livingWthrIdxService.getUVIdx(
                serviceKey = BuildConfig.livingWthrIdxServiceKey,
                numOfRows = numOfRows,
                pageNo = pageNo,
                dataType = DataType.JSON,
                areaNo = areaNo,
                time = time
            )
        }

        fun errorMsg(areaNo: String, time: String): (Response<UVIdx.Item>) -> String = {
            buildList {
                add("resultCode=${it.header.resultCode}")
                add("resultMsg=${it.header.resultMsg}")
                add("areaNo=$areaNo")
                add("time=$time")
            }
                .joinToString("$COMMA$SPACE")
        }

        return retry(block = block(areaNo = areaNo, time = time)).validate(
            errorMsg = errorMsg(areaNo = areaNo, time = time)
        ) {
            if (it.isErrorCode03) {
                // TODO set to const. time inverval ect.. uvIdx는 세 시간 주기.
                val uvIdxCalendar = uvIdxCalendar(time).advanceHourOfDayBy(3)

                with(uvIdxCalendar) {
                    val errorMsg = errorMsg(areaNo = areaNo, time = time())

                    block(areaNo = areaNo, time = time())
                        .invoke()
                        .validate(errorMsg = errorMsg)
                }
            } else {
                throw it
            }
        }
    }

    suspend fun getUltraSrtFcst(
        numOfRows: Int,
        pageNo: Int = Int.one,
        params: VilageFcstInfoService.Params
    ): UltraSrtFcst.Remote {
        fun block(params: VilageFcstInfoService.Params) = suspend {
            with(params) {
                vilageFcstInfoService.getUltraSrtFcst(
                    serviceKey = BuildConfig.vilageFcstInfoServiceKey,
                    numOfRows = numOfRows,
                    pageNo = pageNo,
                    dataType = DataType.JSON,
                    baseDate = baseDate,
                    baseTime = baseTime,
                    nx = nx,
                    ny = ny
                )
            }
        }

        fun errorMsg(params: VilageFcstInfoService.Params): (Response<VilageFcst.Item>) -> String = {
            buildList {
                add("resultCode=${it.header.resultCode}")
                add("resultMsg=${it.header.resultMsg}")
                add("baseDate=${params.baseDate}")
                add("baseTime=${params.baseTime}")
                add("nx=${params.nx}")
                add("ny=${params.ny}")
            }
                .joinToString("$COMMA$SPACE")
        }

        return retry(block = block(params)).validate(
            errorMsg = errorMsg(params)
        ) {
            if (it.isErrorCode03) {
                val baseCalendar = params.baseCalendar
                    .cloneAsCalendar()
                    .advanceHourOfDayBy(1) // TODO set as property,, const..

                with(params.copy(baseCalendar = baseCalendar)) {
                    block(this).invoke().validate(errorMsg(this))
                }
            } else {
                throw it
            }
        }
    }

    suspend fun getUltraSrtNcst(
        numOfRows: Int = Int.eight,
        pageNo: Int = Int.one,
        params: VilageFcstInfoService.Params
    ) = retry {
        with(params) {
            vilageFcstInfoService.getUltraSrtNcst(
                serviceKey = BuildConfig.vilageFcstInfoServiceKey,
                numOfRows = numOfRows,
                pageNo = pageNo,
                dataType = DataType.JSON,
                baseDate = baseDate,
                baseTime = baseTime,
                nx = nx,
                ny = ny
            )
        }
    }
        .validate(
            errorMsg = {
                buildList {
                    add("resultCode=${it.header.resultCode}")
                    add("resultMsg=${it.header.resultMsg}")
                    add("baseDate=${params.baseDate}")
                    add("baseTime=${params.baseTime}")
                    add("nx=${params.nx}")
                    add("ny=${params.ny}")
                }
                    .joinToString("$COMMA$SPACE")
            }
        )

    suspend fun getVilageFcst(
        numOfRows: Int,
        pageNo: Int = Int.one,
        params: VilageFcstInfoService.Params
    ): VilageFcst.Remote {
        fun block(params: VilageFcstInfoService.Params) = suspend {
            with(params) {
                vilageFcstInfoService.getVilageFcst(
                    serviceKey = BuildConfig.vilageFcstInfoServiceKey,
                    numOfRows = numOfRows,
                    pageNo = pageNo,
                    dataType = DataType.JSON,
                    baseDate = baseDate,
                    baseTime = baseTime,
                    nx = nx,
                    ny = ny
                )
            }
        }

        fun errorMsg(params: VilageFcstInfoService.Params): (Response<VilageFcst.Item>) -> String = {
            buildList {
                add("resultCode=${it.header.resultCode}")
                add("resultMsg=${it.header.resultMsg}")
                add("baseDate=${params.baseDate}")
                add("baseTime=${params.baseTime}")
                add("nx=${params.nx}")
                add("ny=${params.ny}")
            }
                .joinToString("$COMMA$SPACE")
        }

        return retry(block = block(params)).validate(
            errorMsg = errorMsg(params)
        ) {
            if (it.isErrorCode03) {
                val baseCalendar = params.baseCalendar
                    .cloneAsCalendar()
                    .advanceHourOfDayBy(3) // TODO set as property,, const..

                with(params.copy(baseCalendar = baseCalendar)) {
                    block(this).invoke().validate(errorMsg(this))
                }
            } else {
                throw it
            }
        }
    }
}
