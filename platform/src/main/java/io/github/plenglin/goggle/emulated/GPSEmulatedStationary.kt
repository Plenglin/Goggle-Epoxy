package io.github.plenglin.goggle.emulated

import io.github.plenglin.goggle.devices.GPS

/**
 * When you came here to use GPS and chew bubblegum but you're all out of GPS...
 */
class GPSEmulatedStationary(override var latitude: Double, override var longitude: Double) : GPS
