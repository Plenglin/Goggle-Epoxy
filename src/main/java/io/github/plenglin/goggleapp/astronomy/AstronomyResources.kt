package io.github.plenglin.goggleapp.astronomy

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.charset.Charset

object AstronomyResources {

    const val LIGHTYEARS_PER_PARSEC = 3.26156
    const val RADIANS_PER_HOUR = Math.PI / 12

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

}

fun main(args: Array<String>) {
    AstronomyResources.stars.filter { it.name != null }.forEach {
        println(it)
    }
}