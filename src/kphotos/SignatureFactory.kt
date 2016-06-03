package kphotos

import java.awt.image.BufferedImage
import java.lang.reflect.Constructor

/**
 * Created by john on 31/05/16.
 */
class SignatureFactory<C : Signature<C>>(clazz : Class<C>) {
    private var constr: Constructor<C> = clazz.getConstructor(BufferedImage::class.java)

    internal fun createSignature(bi: BufferedImage): C {
        return constr.newInstance(bi)
    }

    override fun toString(): String {
        return "SignatureFactory[$constr]"
    }
}