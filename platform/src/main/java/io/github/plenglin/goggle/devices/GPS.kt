package io.github.plenglin.goggle.devices

interface GPS {

    /**
     * Latitude, in degrees
     */
    val latitude: Double

    val latitudeInRadians get() = Math.toRadians(latitude)

    /**
     * Longitude, in degrees
     */
    val longitude: Double

    val longitudeInRadians get() = Math.toRadians(longitude)

}