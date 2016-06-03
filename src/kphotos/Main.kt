package kphotos

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

/**
 * Created by john on 31/05/16.
 */
fun main(args: Array<String>) {
    val start = System.currentTimeMillis()
    val mainImagePath = File(args[0])
    val imageDir = File(args[1])
    val xImages = Integer.parseInt(args[2])
    val yImages = Integer.parseInt(args[3])
    val resultWidth = Integer.parseInt(args[4])
    if (!imageDir.isDirectory) {
        println(imageDir.toString() + " is not a directory.")
        return
    }
    val mainImage = ImageIO.read(mainImagePath)
    // maintain the aspect ratio
    val resultHeight = resultWidth * mainImage.height / mainImage.width

    process(mainImage, xImages, yImages, resultWidth, resultHeight, mainImagePath,
            SignatureFactory(OrthogonalYCbCrSignature::class.java), "orthycbcr", imageDir)
    process(mainImage, xImages, yImages, resultWidth, resultHeight, mainImagePath,
            SignatureFactory(EuclideanYCbCrSignature::class.java), "eucycbcr", imageDir)
    process(mainImage, xImages, yImages, resultWidth, resultHeight, mainImagePath,
            SignatureFactory(OrthogonalRGBSignature::class.java), "orthrgb", imageDir)
    process(mainImage, xImages, yImages, resultWidth, resultHeight, mainImagePath,
            SignatureFactory(OrthogonalHSBSignature::class.java), "orthhsb", imageDir)
    process(mainImage, xImages, yImages, resultWidth, resultHeight, mainImagePath,
            SignatureFactory(EuclideanRGBSignature::class.java), "eucrgb", imageDir)
    process(mainImage, xImages, yImages, resultWidth, resultHeight, mainImagePath,
            SignatureFactory(EuclideanMultidimensionalRGBSignature::class.java), "eucmultirgb", imageDir)
    process(mainImage, xImages, yImages, resultWidth, resultHeight, mainImagePath,
            SignatureFactory(EuclideanHSBSignature::class.java), "euchsb", imageDir)
    process(mainImage, xImages, yImages, resultWidth, resultHeight, mainImagePath,
            SignatureFactory(CustomHSBSignature::class.java), "custhsb", imageDir)
    val end = System.currentTimeMillis()
    println("${(end - start) / 1000L} seconds")
    System.exit(0)
}

@Throws(IOException::class)
private fun <T: Signature<T>> process(mainImage: BufferedImage, xImages: Int, yImages: Int, resultWidth: Int, resultHeight: Int,
                    mainImagePath: File, fac: SignatureFactory<T>, nameKey: String, imageDir: File) {
    // load the pixel images
    println("Loading...")
    val pixelImages = imageDir.listFiles().asSequence().filter { !it.isDirectory }.map { PixelImage(it, fac) }.toList()
    println("There are " + pixelImages.count() + " pixel images.")
    println("Calculating...")
    val regions = calculate(mainImage, xImages, yImages, resultWidth, resultHeight, fac, pixelImages)
    println("Composing...")
    val result = compose(regions, resultWidth, resultHeight)
    val resultPath = Paths.get(mainImagePath.parent.toString(), "result_$nameKey.jpg")
    ImageIO.write(result, "jpg", resultPath.toFile())
}

private fun <T: Signature<T>> calculate(src: BufferedImage, xImages: Int, yImages: Int, resultWidth: Int, resultHeight: Int,
                      fac: SignatureFactory<T>, pixelImages: List<PixelImage<T>>): List<Region> {
    val srcWidth = src.width
    val srcHeight = src.height
    val result = ArrayList<Region>()
    for (x in 0..xImages - 1) {
        for (y in 0..yImages - 1) {
            val xc = x * srcWidth / xImages
            val xc1 = (x + 1) * srcWidth / xImages
            val yc = y * srcHeight / yImages
            val yc1 = (y + 1) * srcHeight / yImages
            val sample = src.getSubimage(xc, yc, xc1 - xc, yc1 - yc)
            val sampleSig = fac.createSignature(sample)
            // TODO - make this parallel
            val best = pixelImages.map { EvaluatedPixelImage(it, it.distance(sampleSig)) }.minBy { it.score }
            if (best != null) {
                val dx = x * resultWidth / xImages
                val dx1 = (x + 1) * resultWidth / xImages
                val dy = y * resultHeight / yImages
                val dy1 = (y + 1) * resultHeight / yImages
                result.add(Region(best.getFile(), dx, dy, dx1 - dx, dy1 - dy))
            }
        }
    }
    return result
}

private fun compose(regions: List<Region>, resultWidth: Int, resultHeight: Int): BufferedImage {
    val cache = Cache()
    val result = BufferedImage(resultWidth, resultHeight, BufferedImage.TYPE_INT_RGB)
    val resultGraphics = result.graphics
    for (region in regions) {
        resultGraphics.drawImage(cache.get(region.file), region.x, region.y, region.w, region.h, null)
    }
    return result
}

class Cache {
    val store = HashMap<File, BufferedImage>();

    fun get(f: File): BufferedImage {
        if (store[f] == null) {
            store[f] = ImageIO.read(f)
        }
        return store[f]!!
    }
}


data class Region(val file: File, val x: Int, val y: Int, val w: Int, val h: Int)