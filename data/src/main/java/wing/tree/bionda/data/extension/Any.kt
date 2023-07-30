package wing.tree.bionda.data.extension

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

infix fun Any?.`is`(other: Any?) = this == other
infix fun Any?.not(other: Any?) = `is`(other).not()

@OptIn(ExperimentalContracts::class)
fun Any?.isNull(): Boolean {
    contract {
        returns(false) implies (this@isNull != null)
    }

    return this `is` null
}

@OptIn(ExperimentalContracts::class)
fun Any?.isNotNull(): Boolean {
    contract {
        returns(true) implies (this@isNotNull != null)
    }

    return this not null
}
