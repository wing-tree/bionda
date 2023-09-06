package wing.tree.bionda.data.source.remote

import wing.tree.bionda.data.BuildConfig
import wing.tree.bionda.data.constant.COMMA
import wing.tree.bionda.data.constant.SPACE
import wing.tree.bionda.data.core.DataType
import wing.tree.bionda.data.core.Response
import wing.tree.bionda.data.extension.advanceHourOfDayBy
import wing.tree.bionda.data.extension.one
import wing.tree.bionda.data.extension.time
import wing.tree.bionda.data.model.LivingWthrIdx
import wing.tree.bionda.data.service.LivingWthrIdxService
import wing.tree.bionda.data.top.level.koreaCalendar

class LivingWthrIdxDataSource(
    private val livingWthrIdxService: LivingWthrIdxService
) : DataSource() {
    suspend fun getAirDiffusionIdx(
        numOfRows: Int = Int.one,
        pageNo: Int = Int.one,
        areaNo: String,
        time: String
    ): LivingWthrIdx.AirDiffusionIdx.Remote {
        fun block(areaNo: String, time: String) = suspend {
            livingWthrIdxService.getAirDiffusionIdx(
                serviceKey = BuildConfig.livingWthrIdxServiceKey,
                numOfRows = numOfRows,
                pageNo = pageNo,
                dataType = DataType.JSON,
                areaNo = areaNo,
                time = time
            )
        }

        fun errorMsg(areaNo: String, time: String): (Response<LivingWthrIdx.Item>) -> String = {
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
                with(koreaCalendar(time).advanceHourOfDayBy(3)) {
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

    suspend fun getUVIdx(
        numOfRows: Int = Int.one,
        pageNo: Int = Int.one,
        areaNo: String,
        time: String
    ): LivingWthrIdx.UVIdx.Remote {
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

        fun errorMsg(areaNo: String, time: String): (Response<LivingWthrIdx.Item>) -> String = {
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
                with(koreaCalendar(time).advanceHourOfDayBy(3)) {
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
}
