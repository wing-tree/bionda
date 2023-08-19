package wing.tree.bionda.data.model

import wing.tree.bionda.data.extension.isNotNull

data class Address(
    val adminArea: String?,
    val subAdminArea: String?,
    val locality: String?,
    val subLocality: String?,
    val thoroughfare: String?,
    val subThoroughfare: String?
) {
    companion object {
        fun get(geocode: List<android.location.Address>): Address {
            return with(geocode) {
                Address(
                    adminArea = adminArea,
                    subAdminArea = subAdminArea,
                    locality = locality,
                    subLocality = subLocality,
                    thoroughfare = thoroughfare,
                    subThoroughfare = subThoroughfare
                )
            }
        }

        private val List<android.location.Address>.adminArea: String? get() = firstOrNull {
            it.adminArea.isNotNull()
        }?.adminArea

        private val List<android.location.Address>.subAdminArea: String? get() = firstOrNull {
            it.subAdminArea.isNotNull()
        }?.subAdminArea

        private val List<android.location.Address>.locality: String? get() = firstOrNull {
            it.locality.isNotNull()
        }?.locality

        private val List<android.location.Address>.subLocality: String? get() = firstOrNull {
            it.subLocality.isNotNull()
        }?.subLocality

        private val List<android.location.Address>.thoroughfare: String? get() = firstOrNull {
            it.thoroughfare.isNotNull()
        }?.thoroughfare

        private val List<android.location.Address>.subThoroughfare: String? get() = firstOrNull {
            it.subThoroughfare.isNotNull()
        }?.subThoroughfare
    }
}
