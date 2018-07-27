package io.github.plenglin.goggleapp.astronomy

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.charset.Charset

object AstronomyResources {

    const val LIGHTYEARS_PER_PARSEC = 3.26156
    const val RADIANS_PER_HOUR = Math.PI / 12
    const val SECONDS_PER_YEAR = 31556926
    const val SECONDS_PER_DAY = 86400

    /**
     * Stars, listed in order of apparent magnitude.
     */
    val stars: List<Star> by lazy {
        val stream = javaClass.classLoader.getResourceAsStream("astronomy/hygdata.csv")
        val parser = CSVParser.parse(stream, Charset.defaultCharset(), CSVFormat.DEFAULT.withHeader())
        parser.records.map {
            Star(
                    name = it.get("proper").let { name ->
                        if (name.isEmpty() || name == " ") null else name
                    },
                    rightAscension = it.get("ra").toDouble() * RADIANS_PER_HOUR,
                    declination = Math.toRadians(it.get("dec").toDouble()),
                    distance = it.get("dist").toDouble() * LIGHTYEARS_PER_PARSEC,
                    apparentMagnitude = it.get("mag").toDouble(),
                    colorIndex = it.get("ci").toDoubleOrNull())
        }.sortedBy { it.apparentMagnitude }
    }

    val SYMBOLS: List<Pair<String, DoubleArray>> = listOf(
            "N" to doubleArrayOf(0.0, 0.0, 1.0),
            "E" to doubleArrayOf(1.0, 0.0, 0.0),
            "S" to doubleArrayOf(0.0, 0.0, -1.0),
            "W" to doubleArrayOf(-1.0, 0.0, 0.0),
            "UP" to doubleArrayOf(0.0, -1.0, 0.0),
            "DN" to doubleArrayOf(0.0, 1.0, 0.0)
    )

}
