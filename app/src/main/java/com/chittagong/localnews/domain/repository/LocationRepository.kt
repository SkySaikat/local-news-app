package com.chittagong.localnews.domain.repository

import com.chittagong.localnews.domain.model.GeoPoint

/** Wraps FusedLocationProviderClient so the UI never touches Play Services. */
interface LocationRepository {

    /** True once either fine or coarse location has been granted. */
    fun hasLocationPermission(): Boolean

    /**
     * A single fresh fix. Returns a failure (rather than throwing) when
     * permission is missing or location services are switched off.
     */
    suspend fun getCurrentLocation(): Result<GeoPoint>
}
