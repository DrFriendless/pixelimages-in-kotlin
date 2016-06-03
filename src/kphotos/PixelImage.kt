package kphotos

import nl.komponents.kovenant.task
import nl.komponents.kovenant.then
import java.io.File
import java.util.concurrent.CountDownLatch
import javax.imageio.ImageIO

/**
 * Created by john on 1/06/16.
 */
class PixelImage<T: Signature<T>>(f: File, fac: SignatureFactory<T>) {
    var file = f
    var mySig: Signature<T>? = null
    var broken = false
    val finished = CountDownLatch(1)
    init {
        task {
            ImageIO.read(f)
        } then {
            fac.createSignature(it)
        } success {
            mySig = it
        } fail {
            broken = true
        } always {
            finished.countDown()
        }
    }

    fun distance(sig: T): Double {
        finished.await()
        if (broken) return Double.MAX_VALUE
        return mySig!!.distance(sig)
    }
}

data class EvaluatedPixelImage<T: Signature<T>>(val image: PixelImage<T>, val score: Double) {
    fun getFile(): File {
        return image.file
    }
}