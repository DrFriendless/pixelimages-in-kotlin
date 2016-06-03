package kphotos

import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.util.*

/**
 * Created by john on 31/05/16.
 */
interface Signature<in T: Signature<T>> {
    fun distance(sig: T): Double
}

abstract class Signature432<in T: Signature<T>>: Signature<T> {
    protected var bytes = IntArray(432)
}

private fun miniaturise(image: BufferedImage): BufferedImage {
    val small = BufferedImage(12, 12, BufferedImage.TYPE_INT_RGB)
    val graphics = small.createGraphics()
    val at = AffineTransform.getScaleInstance(12.0 / image.width, 12.0 / image.height)
    graphics.drawRenderedImage(image, at)
    return small
}

abstract class YCbCrSignature<in T: Signature<T>>(bi: BufferedImage): Signature432<T>() {
    init {
        buildYCbCrSignature(bi, bytes)
    }

    private fun fromRGB(r: Int, g: Int, b: Int, yCbCr: IntArray) {
        val y = 16 + (65.738 * r + 129.057 * g + 25.064 * b).toInt() shr 8
        val Cb = 128 + (-37.945 * r + -74.494 * g + 112.439 * b).toInt() shr 8
        val Cr = 128 + (112.439 * r + -97.154 * g + -18.285 * b).toInt() shr 8
        if (y > 255) {
            yCbCr[0] = 255
        } else if (y < 0) {
            yCbCr[0] = 0
        } else {
            yCbCr[0] = y
        }
        if (Cb > 255) {
            yCbCr[1] = 255
        } else if (Cb < 0) {
            yCbCr[1] = 0
        } else {
            yCbCr[1] = Cb
        }
        if (Cr > 255) {
            yCbCr[2] = 255
        } else if (Cr < 0) {
            yCbCr[2] = 0
        } else {
            yCbCr[2] = Cr
        }
    }

    internal fun buildYCbCrSignature(image: BufferedImage, bytes: IntArray) {
        val small = miniaturise(image)
        var index = 0
        val ycbcr = IntArray(3)
        for (x in 0..11) {
            for (y in 0..11) {
                val rgb = small.getRGB(x, y)
                val r = rgb and 0xFF
                val g = rgb and 0xFF00 shr 8
                val b = rgb and 0xFF0000 shr 16
                fromRGB(r, g, b, ycbcr)
                bytes[index++] = ycbcr[0]
                bytes[index++] = ycbcr[1]
                bytes[index++] = ycbcr[2]
            }
        }
    }
}

abstract class RGBSignature<in T: Signature<T>>(bi: BufferedImage): Signature432<T>() {
    init {
        buildRGBSignature(bi, bytes)
    }

    internal fun buildRGBSignature(image: BufferedImage, bytes: IntArray) {
        val small = miniaturise(image)
        var index = 0
        for (x in 0..11) {
            for (y in 0..11) {
                val rgb = small.getRGB(x, y)
                bytes[index++] = rgb and 0xFF
                bytes[index++] = rgb and 0xFF00 shr 8
                bytes[index++] = rgb and 0xFF0000 shr 16
            }
        }
    }
}

abstract class HSBSignature<in T: Signature<T>>(bi: BufferedImage): Signature432<T>() {
    init {
        buildHSBSignature(bi, bytes)
    }

    internal fun buildHSBSignature(image: BufferedImage, bytes: IntArray) {
        val small = miniaturise(image)
        var index = 0
        val hsv = IntArray(3)
        val rgbs = ArrayList<Int>()
        for (x in 0..11) {
            for (y in 0..11) {
                val rgb = small.getRGB(x, y)
                rgbs.add(rgb)
                val r = rgb and 0xFF
                val g = rgb and 0xFF00 shr 8
                val b = rgb and 0xFF0000 shr 16
                rgbToHsbBytes(r, g, b, hsv)
                bytes[index++] = hsv[0]
                bytes[index++] = hsv[1]
                bytes[index++] = hsv[2]
            }
        }
    }

    internal fun rgbToHsbBytes(r: Int, g: Int, b: Int, result: IntArray) {
        val hsv = FloatArray(3)
        Color.RGBtoHSB(r, g, b, hsv)
        result[0] = (hsv[0] * 255.9999f).toInt()
        result[1] = (hsv[1] * 255.9999f).toInt()
        result[2] = (hsv[2] * 255.9999f).toInt()
    }
}