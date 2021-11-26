package pl.adambartkowiak.support

import pl.adambartkowiak.support.opengl.model.Vec3
import pl.adambartkowiak.support.opengl.model.Vec4
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sqrt

class AdvanceMath {

    fun quaterionToAngles(q1: Vec4<Double>): Vec3<Double> {

        val sqw: Double = q1.w * q1.w
        val sqx: Double = q1.x * q1.x
        val sqy: Double = q1.y * q1.y
        val sqz: Double = q1.z * q1.z

        val sum = sqx + sqy + sqz + sqw


        val x = if (sum != 0.0) {
            asin(-2.0 * (q1.x * q1.z - q1.y * q1.w) / sum)
        } else {
            0.0
        }
        val y = atan2(2.0 * (q1.y * q1.z + q1.x * q1.w), -sqx - sqy + sqz + sqw)
        val z = atan2(2.0 * (q1.x * q1.y + q1.z * q1.w), sqx - sqy - sqz + sqw)

        return Vec3(x, y, z)
    }

    fun quaterionToAngles2(q1: Vec4<Double>): Vec3<Double> {
        val sqw = q1.w * q1.w;
        val sqx = q1.x * q1.x;
        val sqy = q1.y * q1.y;
        val sqz = q1.z * q1.z;
        val unit = sqx + sqy + sqz + sqw; // if normalised is one, otherwise is correction factor
        val test = q1.x * q1.y + q1.z * q1.w;

        if (unit == 0.0) {
            return Vec3(0.0, 0.0, 0.0);
        }

        val x: Double
        val y: Double
        val z: Double

        if (test > 0.499 * unit) { // singularity at north pole
            z = 2 * atan2(q1.x, q1.w);
            x = Math.PI / 2;
            y = 0.0;
            return Vec3(z, x, y);
        }
        if (test < -0.499 * unit) { // singularity at south pole
            z = -2 * atan2(q1.x, q1.w);
            x = -Math.PI / 2;
            y = 0.0;
            return Vec3(z, x, y);
        }
        z = atan2(2 * q1.y * q1.w - 2 * q1.x * q1.z, sqx - sqy - sqz + sqw);
        x = asin(2 * test / unit);
        y = atan2(2 * q1.x * q1.w - 2 * q1.y * q1.z, -sqx + sqy - sqz + sqw)

        return Vec3(z, x, y);
    }

    fun quaterionToEuler(q: Vec4<Float>): Vec3<Float> {
        val epsilon = 0.00001
        val halfpi = 0.5 * Math.PI

        val x = q.x.toDouble()
        val y = q.y.toDouble()
        val z = q.z.toDouble()
        val w = q.w.toDouble()

        val xa: Double
        val ya: Double
        val za: Double

        val temp = 2 * (y * z - x * w)
        when {
            temp >= 1 - epsilon -> {
                xa = halfpi
                ya = -atan2(y, w)
                za = -atan2(z, w)
            }
            -temp >= 1 - epsilon -> {
                xa = -halfpi
                ya = -atan2(y, w)
                za = -atan2(z, w)
            }
            else -> {
                xa = asin(temp)
                ya = -atan2(x * z + y * w, 0.5 - x * x - y * y)
                za = -atan2(x * y + z * w, 0.5 - x * x - z * z)
            }
        }

        return Vec3(xa.toFloat(), ya.toFloat(), za.toFloat())
    }

    fun identityMatrix(): FloatArray {
        return arrayOf(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        ).toFloatArray()
    }


    fun quaterionToRotationMatrix(q: Vec4<Float>): FloatArray {

        var qw = q.w
        var qx = q.x
        var qy = q.y
        var qz = q.z

        if (q.w + q.x + q.y + q.z == 0.0f) {
            return identityMatrix()
        }

        val n = 2.0f / (qx * qx + qy * qy + qz * qz + qw * qw)
        val result = FloatArray(16)

        result[0] = 1.0f - n * qy * qy - n * qz * qz
        result[1] = n * qx * qy - n * qz * qw
        result[2] = n * qx * qz + n * qy * qw
        result[3] = 0.0f
        result[4] = n * qx * qy + n * qz * qw
        result[5] = 1.0f - n * qx * qx - n * qz * qz
        result[6] = n * qy * qz - n * qx * qw
        result[7] = 0.0f
        result[8] = n * qx * qz - n * qy * qw
        result[9] = n * qy * qz + n * qx * qw
        result[10] = 1.0f - n * qx * qx - n * qy * qy
        result[11] = 0.0f
        result[12] = 0.0f
        result[13] = 0.0f
        result[14] = 0.0f
        result[15] = 1.0f

        return result
    }
}