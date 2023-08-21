package wing.tree.bionda.data.model

sealed interface RegId : List<String> {
    val default: String

    object MidLandFcst : RegId, List<String> by listOf(
        "11B00000",
        "11D10000",
        "11D20000",
        "11C20000",
        "11C10000",
        "11F20000",
        "11F10000",
        "11H10000",
        "11H20000",
        "11G00000"
    ) {
        override val default: String = "11B00000"
    }

    object MidTa : RegId, List<String> by listOf(
        "11A00101",
        "11B10101",
        "11B10102",
        "11B10103",
        "11B20101",
        "11B20102",
        "11B20201",
        "11B20202",
        "11B20203",
        "11B20204",
        "11B20301",
        "11B20302",
        "11B20304",
        "11B20305",
        "11B20401",
        "11B20402",
        "11B20403",
        "11B20404",
        "11B20501",
        "11B20502",
        "11B20503",
        "11B20504",
        "11B20601",
        "11B20602",
        "11B20603",
        "11B20604",
        "11B20605",
        "11B20606",
        "11B20609",
        "11B20610",
        "11B20611",
        "11B20612",
        "11B20701",
        "11B20702",
        "11B20703",
        "11C10101",
        "11C10102",
        "11C10103",
        "11C10201",
        "11C10202",
        "11C10301",
        "11C10302",
        "11C10303",
        "11C10304",
        "11C10401",
        "11C10402",
        "11C10403",
        "11C20101",
        "11C20102",
        "11C20103",
        "11C20104",
        "11C20201",
        "11C20202",
        "11C20301",
        "11C20302",
        "11C20303",
        "11C20401",
        "11C20402",
        "11C20403",
        "11C20404",
        "11C20501",
        "11C20502",
        "11C20601",
        "11C20602",
        "11D10101",
        "11D10102",
        "11D10201",
        "11D10202",
        "11D10301",
        "11D10302",
        "11D10401",
        "11D10402",
        "11D10501",
        "11D10502",
        "11D10503",
        "11D20201",
        "11D20301",
        "11D20401",
        "11D20402",
        "11D20403",
        "11D20501",
        "11D20601",
        "11D20602",
        "1.10E+102",
        "1.10E+103",
        "11F10201",
        "11F10202",
        "11F10203",
        "11F10204",
        "11F10301",
        "11F10302",
        "11F10303",
        "11F10401",
        "11F10402",
        "11F10403",
        "21F10501",
        "21F10502",
        "21F10601",
        "21F10602",
        "21F20101",
        "21F20102",
        "21F20201",
        "11F20301",
        "11F20302",
        "11F20303",
        "11F20304",
        "11F20401",
        "11F20402",
        "11F20403",
        "11F20404",
        "11F20405",
        "11F20501",
        "11F20502",
        "11F20503",
        "11F20504",
        "11F20505",
        "11F20601",
        "11F20602",
        "11F20603",
        "11F20701",
        "21F20801",
        "21F20802",
        "21F20803",
        "21F20804",
        "11G00101",
        "11G00201",
        "11G00302",
        "11G00401",
        "11G00501",
        "11G00601",
        "11G00800",
        "11H10101",
        "11H10102",
        "11H10201",
        "11H10202",
        "11H10301",
        "11H10302",
        "11H10303",
        "11H10401",
        "11H10402",
        "11H10403",
        "11H10501",
        "11H10502",
        "11H10503",
        "11H10601",
        "11H10602",
        "11H10603",
        "11H10604",
        "11H10605",
        "11H10701",
        "11H10702",
        "11H10703",
        "11H10704",
        "11H10705",
        "11H20101",
        "11H20102",
        "11H20201",
        "11H20301",
        "11H20304",
        "11H20401",
        "11H20402",
        "11H20403",
        "11H20404",
        "11H20405",
        "11H20501",
        "11H20502",
        "11H20503",
        "11H20601",
        "11H20602",
        "11H20603",
        "11H20604",
        "11H20701",
        "11H20703",
        "11H20704",
        "11I10001",
        "11I10002",
        "11I20001",
        "11I20002",
        "11I20003",
        "11J10001",
        "11J10002",
        "11J10003",
        "11J10004",
        "11J10005",
        "11J10006",
        "11J20001",
        "11J20002",
        "11J20004",
        "11J20005",
        "11K10001",
        "11K10002",
        "11K10003",
        "11K10004",
        "11K20001",
        "11K20002",
        "11K20003",
        "11K20004",
        "11K20005",
        "11L10001",
        "11L10002",
        "11L10003"
    ) {
        override val default: String = "11B10101"
    }
}
