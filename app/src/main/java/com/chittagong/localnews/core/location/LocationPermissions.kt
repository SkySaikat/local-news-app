package com.chittagong.localnews.core.location

import android.Manifest

/**
 * The runtime location permissions the app asks for, in one place so the
 * request, the rationale copy and the granted-check can't drift apart.
 */
object LocationPermissions {

    val REQUIRED: Array<String> = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    /**
     * Coarse alone is enough to show a useful feed — precise only sharpens the
     * distance sort, so we treat either grant as success.
     */
    fun isGranted(results: Map<String, Boolean>): Boolean =
        results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
}
