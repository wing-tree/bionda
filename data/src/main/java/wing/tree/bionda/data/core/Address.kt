package wing.tree.bionda.data.core

data class Address(
    val adminArea: String?,
    val subAdminArea: String? = null,
    val locality: String?,
    val subLocality: String? = null,
    val thoroughfare: String?,
    val subThoroughfare: String? = null
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

        private val List<android.location.Address>.adminArea: String? get() = firstNotNullOfOrNull {
            it.adminArea
        }

        private val List<android.location.Address>.subAdminArea: String? get() = firstNotNullOfOrNull {
            it.subAdminArea
        }

        private val List<android.location.Address>.locality: String? get() = firstNotNullOfOrNull {
            it.locality
        }

        private val List<android.location.Address>.subLocality: String? get() = firstNotNullOfOrNull {
            it.subLocality
        }

        private val List<android.location.Address>.thoroughfare: String? get() = firstNotNullOfOrNull {
            it.thoroughfare
        }

        private val List<android.location.Address>.subThoroughfare: String? get() = firstNotNullOfOrNull {
            it.subThoroughfare
        }
    }
}
