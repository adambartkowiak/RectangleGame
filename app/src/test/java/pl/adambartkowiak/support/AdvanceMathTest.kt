package pl.adambartkowiak.support

import org.junit.Test

import org.junit.Assert.*
import pl.adambartkowiak.support.opengl.model.Vec4

class AdvanceMathTest {

    @Test
    fun quaterionToAngles() {

        val quaterion = Vec4(
            0.07713747769594193,
            0.08668795228004456,
            -0.9654816389083862,
            0.23319590091705322
        )

        val result = AdvanceMath().quaterionToAngles(quaterion)

        val x = (result.x == 0.2060302)
        val y = (result.y == -0.1087332)
        val z = (result.z == -2.6563523)
        assertEquals(true, x && y && z)
    }
}