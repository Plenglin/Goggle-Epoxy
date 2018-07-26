package io.github.plenglin.goggleapp.astronomy

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.charset.Charset

object AstronomyResources {

    const val LIGHTYEARS_PER_PARSEC = 3.26156

    /**
     * Stars, listed in order of apparent magnitude.
     */
    val stars: List<Star> by lazy {
        val stream = javaClass.classLoader.getResourceAsStream("astronomy/hygfull.csv")
        val parser = CSVParser.parse(stream, Charset.defaultCharset(), CSVFormat.DEFAULT.withHeader())
        parser.records.map {
            Star(
                    name = it.get("ProperName").let { name ->
                        if (name.isEmpty() || name == " ") null else name
                    },
                    rightAscension = Math.toRadians(it.get("RA").toDouble()),
                    declination = Math.toRadians(it.get("Dec").toDouble()),
                    distance = it.get("Distance").toDouble() * LIGHTYEARS_PER_PARSEC,
                    apparentMagnitude = it.get("Mag").toDouble(),
                    colorIndex = it.get("ColorIndex").toDoubleOrNull())
        }.sortedBy { it.apparentMagnitude }
    }

}

fun main(args: Array<String>) {
    AstronomyResources.stars.filter { it.name != null }.forEach {
        println(it)
    }
}