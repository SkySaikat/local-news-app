package com.chittagong.localnews.core.common

/**
 * The neighbourhoods a user can register against. Chittagong-specific by
 * design — the proposal's proximity feed (v3.0) buckets posts by home area
 * before falling back to geohash queries.
 *
 * Split into the dense city wards people actually name when they say where they
 * live, and the wider metro upazilas.
 */
object ChittagongAreas {

    /** Neighbourhoods, bazaars and landmarks inside Chittagong City Corporation. */
    val CITY_AREAS: List<String> = listOf(
        "Agrabad",
        "Alankar Mor",
        "Amin Jute Mills",
        "Anderkilla",
        "Askar Dighi",
        "Bahaddarhat",
        "Bakalia",
        "Baluchara",
        "Bandar (Port Area)",
        "Bandartila",
        "Bayezid Bostami",
        "Boropool",
        "CDA Avenue",
        "Chandgaon",
        "Chatteswari Road",
        "Chawkbazar",
        "Colonel Hat",
        "CRB",
        "Dampara",
        "Dewanhat",
        "Double Mooring",
        "EPZ",
        "Firingi Bazar",
        "Foy's Lake",
        "GEC Circle",
        "Halishahar",
        "Jamal Khan",
        "Kadamtali",
        "Kalurghat",
        "Kattali",
        "Khatunganj",
        "Khulshi",
        "Kotwali",
        "Lalkhan Bazar",
        "Mehedibag",
        "Muradpur",
        "Nasirabad",
        "New Market",
        "O. R. Nizam Road",
        "Oxygen",
        "Pahartali",
        "Panchlaish",
        "Patenga",
        "Probortok Circle",
        "Riazuddin Bazar",
        "Sadarghat",
        "Sagorika",
        "Sholoshahar",
        "Sugandha R/A",
        "Tigerpass",
        "WASA Circle",
    )

    /** Upazilas and towns in the wider Chittagong district. */
    val GREATER_CHITTAGONG: List<String> = listOf(
        "Anwara",
        "Banshkhali",
        "Boalkhali",
        "Chandanaish",
        "Fatikchhari",
        "Hathazari",
        "Karnaphuli",
        "Lohagara",
        "Mirsharai",
        "Patiya",
        "Rangunia",
        "Raozan",
        "Sandwip",
        "Satkania",
        "Sitakunda",
    )

    /** Sections rendered as headers in the picker. */
    val GROUPED: List<AreaGroup> = listOf(
        AreaGroup("Chittagong city", CITY_AREAS),
        AreaGroup("Greater Chittagong", GREATER_CHITTAGONG),
    )

    val ALL: List<String> = CITY_AREAS + GREATER_CHITTAGONG

    /** Sensible default so the sign-up form is never in an invalid empty state. */
    val DEFAULT: String = CITY_AREAS.first()

    fun isValid(area: String): Boolean = area in ALL
}

/** A labelled section of the home-area picker. */
data class AreaGroup(
    val label: String,
    val areas: List<String>,
)
