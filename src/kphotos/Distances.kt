package kphotos

/**
 * Created by john on 1/06/16.
 */
interface Euclidean {
    fun euclideanDistance(sig1: IntArray, sig2: IntArray): Double {
        var tot = 0.0
        var i = 0
        while (i < sig1.size) {
            var t = 0.0
            t += diffSquare(sig1[i], sig2[i]).toDouble()
            i++
            t += diffSquare(sig1[i], sig2[i]).toDouble()
            i++
            t += diffSquare(sig1[i], sig2[i]).toDouble()
            i++
            tot += Math.sqrt(t)
        }
        return tot
    }
}

interface Orthogonal {
    fun orthogonalDistance(sig1: IntArray, sig2: IntArray): Double {
        var tot = 0.0
        for (i in sig1.indices) {
            tot += diff(sig1[i], sig2[i]).toDouble()
        }
        return tot
    }
}

interface WeightedEuclidean {
    fun weightedEuclideanDistance(sig1: IntArray, sig2: IntArray, weight0: Int, weight1: Int, weight2: Int): Double {
        var tot = 0.0
        var i = 0
        while (i < sig1.size) {
            var t = 0.0
            t += (diffSquare(sig1[i], sig2[i]) * weight0).toDouble()
            i++
            t += (diffSquare(sig1[i], sig2[i]) * weight1).toDouble()
            i++
            t += (diffSquare(sig1[i], sig2[i]) * weight2).toDouble()
            i++
            tot += Math.sqrt(t)
        }
        return tot
    }
}

interface MultidimensionalEuclidean {
    fun multidimensionalEuclideanDistance(sig1: IntArray, sig2: IntArray): Double {
        var tot = 0.0
        for (i in sig1.indices) {
            tot += diffSquare(sig1[i], sig2[i]).toDouble()
        }
        return Math.sqrt(tot)
    }
}

internal fun diff(i1: Int, i2: Int): Int {
    return Math.abs(i1 - i2)
}

internal fun diffSquare(i1: Int, i2: Int): Int {
    val d = i1 - i2
    return d * d
}

