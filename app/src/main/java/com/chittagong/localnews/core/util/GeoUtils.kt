package com.chittagong.localnews.core.util

import com.chittagong.localnews.domain.model.GeoPoint
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

/** Chittagong city centre — the map's fallback camera when we have no fix yet. */
val CHITTAGONG_CENTRE = GeoPoint(latitude = 22.3569, longitude = 91.7832)

object GeoUtils {

    private const val EARTH_RADIUS_METRES = 6_371_000.0

    /**
     * Great-circle distance in metres. Mirrors what `Location.distanceBetween`
     * would give, but stays pure Kotlin so it unit-tests without the framework.
     */
    fun distanceMetres(from: GeoPoint, to: GeoPoint): Double {
        val dLat = Math.toRadians(to.latitude - from.latitude)
        val dLon = Math.toRadians(to.longitude - from.longitude)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(from.latitude)) *
            cos(Math.toRadians(to.latitude)) *
            sin(dLon / 2).pow(2)
        return 2 * EARTH_RADIUS_METRES * atan2(sqrt(a), sqrt(1 - a))
    }

    /**
     * Coordinate obfuscation (proposal 9, "Location Privacy").
     *
     * Rounds to ~4 decimal places — roughly an 11m grid at this latitude — so a
     * pin lands on the street rather than on someone's front door.
     */
    fun obfuscate(point: GeoPoint, decimals: Int = 4): GeoPoint {
        val factor = 10.0.pow(decimals)
        return GeoPoint(
            latitude = round(point.latitude * factor) / factor,
            longitude = round(point.longitude * factor) / factor,
        )
    }

    /** "320 m away" / "1.4 km away" — the label under each feed card. */
    fun formatDistance(metres: Double): String = when {
        metres < 1_000 -> "${metres.toInt()} m away"
        metres < 10_000 -> String.format("%.1f km away", metres / 1000.0)
        else -> "${(metres / 1000).toInt()} km away"
    }

    /** Degrees of latitude covering [metres]; used to frame the map camera. */
    fun metresToLatitudeDelta(metres: Double): Double =
        (metres / EARTH_RADIUS_METRES) * (180.0 / PI)
}
