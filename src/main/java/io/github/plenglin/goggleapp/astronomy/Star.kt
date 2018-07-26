package io.github.plenglin.goggleapp.astronomy

/**
 * @param rightAscension radians
 * @param declination radians
 * @param apparentMagnitude apparent magnitude
 * @param distance distance in lightyears
 */
data class Star(val name: String? = null,
                val apparentMagnitude: Double,
                val distance: Double,
                val rightAscension: Double,
                val declination: Double,
                val colorIndex: Double?) {
    /**
     * Position relative to Earth. Conforms to right hand rule.
     */
    val position by lazy {
        val sinPhi = Math.cos(declination)
        val cosPhi = -Math.sin(declination)
        doubleArrayOf(
                distance * sinPhi * Math.cos(rightAscension),
                distance * sinPhi * Math.sin(rightAscension),
                distance * cosPhi
        )
    }

    /**
     * Position on the celestial sphere.
     */
    val cSpherePosition by lazy {
        val sinPhi = Math.cos(declination)
        val cosPhi = -Math.sin(declination)
        doubleArrayOf(
                sinPhi * Math.cos(rightAscension),
                sinPhi * Math.sin(rightAscension),
                cosPhi
        )
    }

}