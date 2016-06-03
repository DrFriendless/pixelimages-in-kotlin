package kphotos

import java.awt.image.BufferedImage

/**
 * Created by john on 31/05/16.
 */
class OrthogonalYCbCrSignature(bi: BufferedImage) : YCbCrSignature<OrthogonalYCbCrSignature>(bi), Orthogonal {
    override fun distance(sig: OrthogonalYCbCrSignature): Double {
        return orthogonalDistance(bytes, sig.bytes)
    }
}

class EuclideanYCbCrSignature(bi: BufferedImage) : YCbCrSignature<EuclideanYCbCrSignature>(bi), Euclidean {
    override fun distance(sig: EuclideanYCbCrSignature): Double {
        return euclideanDistance(bytes, sig.bytes)
    }
}

class OrthogonalRGBSignature(bi: BufferedImage) : RGBSignature<OrthogonalRGBSignature>(bi), Orthogonal {
    override fun distance(sig: OrthogonalRGBSignature): Double {
        return orthogonalDistance(bytes, sig.bytes)
    }
}

class OrthogonalHSBSignature(bi: BufferedImage) : HSBSignature<OrthogonalHSBSignature>(bi), Orthogonal {
    override fun distance(sig: OrthogonalHSBSignature): Double {
        return orthogonalDistance(bytes, sig.bytes)
    }
}

class EuclideanRGBSignature(bi: BufferedImage) : RGBSignature<EuclideanRGBSignature>(bi), Euclidean {
    override fun distance(sig: EuclideanRGBSignature): Double {
        return euclideanDistance(bytes, sig.bytes)
    }
}

class EuclideanMultidimensionalRGBSignature(bi: BufferedImage) : RGBSignature<EuclideanMultidimensionalRGBSignature>(bi), MultidimensionalEuclidean {
    override fun distance(sig: EuclideanMultidimensionalRGBSignature): Double {
        return multidimensionalEuclideanDistance(bytes, sig.bytes)
    }
}

class EuclideanHSBSignature(bi: BufferedImage) : HSBSignature<EuclideanHSBSignature>(bi), Euclidean {
    override fun distance(sig: EuclideanHSBSignature): Double {
        return euclideanDistance(bytes, sig.bytes)
    }
}

class CustomHSBSignature(bi: BufferedImage) : HSBSignature<CustomHSBSignature>(bi), WeightedEuclidean {
    override fun distance(sig: CustomHSBSignature): Double {
        return weightedEuclideanDistance(bytes, sig.bytes, 2, 1, 1)
    }
}