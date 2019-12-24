package io.homeassistant.companion.android.domain.integration

import java.net.URL

interface IntegrationUseCase {

    suspend fun registerDevice(deviceRegistration: DeviceRegistration)
    suspend fun updateRegistration(
        appVersion: String? = null,
        deviceName: String? = null,
        manufacturer: String? = null,
        model: String? = null,
        osVersion: String? = null,
        pushUrl: String? = null,
        pushToken: String? = null
    )
    suspend fun getRegistration(): DeviceRegistration

    suspend fun isRegistered(): Boolean

    suspend fun getUiUrl(isInternal: Boolean): URL?

    suspend fun updateLocation(updateLocation: UpdateLocation)

    suspend fun getZones(): Array<Entity<ZoneAttributes>>

    suspend fun setZoneTrackingEnabled(enabled: Boolean)
    suspend fun isZoneTrackingEnabled(): Boolean

    suspend fun setBackgroundTrackingEnabled(enabled: Boolean)
    suspend fun isBackgroundTrackingEnabled(): Boolean
}
