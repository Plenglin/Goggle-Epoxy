package io.github.plenglin.goggleapp.tetris

import io.github.plenglin.goggle.IntPair
import java.awt.image.BufferedImage
import java.util.*

data class TetrisGlyph(val data: Array<BooleanArray>) : Iterable<IntPair> {
    override fun iterator(): Iterator<IntPair> {
        return object : Iterator<IntPair> {
            var x = -1
            var y = 0

            override fun hasNext(): Boolean {
                do {
                    x++
                    if (x == width) {
                        y++
                        x = 0
                        if (y == height) {
                            return false
                        }
                    }
                } while (!data[x][y])
                return true
            }

            override fun next(): IntPair {
                return x to y
            }
        }
    }

    val width = data.size
    val height = data[0].size

    val rotatedCW: TetrisGlyph get() {
        val out = Array(height) { BooleanArray(width) }
        for (i in 0 until width) {
            for (j in 0 until height) {
                out[j][width - i - 1] = data[i][j]
            }
        }
        return TetrisGlyph(out)
    }

    val rotatedCCW: TetrisGlyph get() {
        val out = Array(height) { BooleanArray(width) }
        for (i in 0 until width) {
            for (j in 0 until height) {
                out[height - j - 1][i] = data[i][j]
            }
        }
        return TetrisGlyph(out)
    }

    val rotated180: TetrisGlyph get() {
        val out = Array(width) { BooleanArray(height) }
        for (i in 0 until width) {
            for (j in 0 until height) {
                out[width - i - 1][height - j - 1] = data[i][j]
            }
        }
        return TetrisGlyph(out)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TetrisGlyph

        if (!Arrays.equals(data, other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(data)
    }

    override fun toString(): String {
        return "TetrisGlyph(${data.contentDeepToString()})"
    }

    fun drawToBuffer(buf: BufferedImage, dx: Int = 0, dy: Int = 0) {
        forEach { (x, y) ->
            buf.setRGB(x + dx, y + dy, TetrisGameActivity.WHITE)
        }
    }

}
