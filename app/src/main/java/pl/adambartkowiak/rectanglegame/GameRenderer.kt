package pl.adambartkowiak.rectanglegame

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import pl.adambartkowiak.support.AdvanceMath
import pl.adambartkowiak.support.BitmapProviderImp
import pl.adambartkowiak.support.Timer
import pl.adambartkowiak.support.opengl.model.Model
import pl.adambartkowiak.support.opengl.GlTFRenderer
import pl.adambartkowiak.support.opengl.fps.FpsRenderer
import pl.adambartkowiak.support.opengl.hud.HudRenderer
import pl.adambartkowiak.support.opengl.model.Camera
import pl.adambartkowiak.support.opengl.model.Vec3
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class GameRenderer(var context: Context) : GLSurfaceView.Renderer {

    var models: List<Model>? = null
    val worldModel = WorldModel()

    var moveInXAxis = 0.0f

    private var hudRenderer: HudRenderer? = null
    private var fpsRenderer: FpsRenderer? = null
    private val fpsList = MutableList(300) { 1.0f }

    private lateinit var glTFRenderer: GlTFRenderer

    private val modelMatrix = AdvanceMath().identityMatrix()
    private val viewMatrix = FloatArray(16)
    private val invertedViewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val rotationMatrix = AdvanceMath().identityMatrix()

    private var startTime: Long = 0
    private val timer = Timer().apply { update() }
    private val camera = Camera(
        Vec3(0f, 20f*2, 5f*2),
        Vec3(0f, 1f, 0f),
        Vec3(0f, 1f, 0f)
    )

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        startTime = SystemClock.uptimeMillis()

        glTFRenderer = GlTFRenderer(models, BitmapProviderImp(context))

        hudRenderer = HudRenderer()
        fpsRenderer = FpsRenderer()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 100f)
    }

    override fun onDrawFrame(gl: GL10) {
        timer.update()
        val delta = timer.getDeltaInMs()
        fpsList.add(0, delta)

        gl.apply {
            glEnable(GL10.GL_DEPTH_TEST)
            glDepthFunc(GL_LESS)

            // enable face culling feature
//            glEnable(GL10.GL_CULL_FACE)
            glDisable(GL10.GL_CULL_FACE)

            // specify which faces to not draw
//            glCullFace(GL10.GL_BACK)
        }


        if (worldModel.cameraZ >= WorldModel.MAX_CAMERA_Z) {
            worldModel.cameraUp = false
        } else if (worldModel.cameraZ <= WorldModel.MIN_CAMERA_Z) {
            worldModel.cameraUp = true
        }
        if (worldModel.cameraUp) {
            worldModel.cameraZ += 0.05f
        } else {
            worldModel.cameraZ -= 0.05f
        }

        worldModel.cameraZ =
            worldModel.cameraZ.coerceIn(WorldModel.MIN_CAMERA_Z, WorldModel.MAX_CAMERA_Z)


        glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        GLES20.glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(
            viewMatrix,
            0,
            camera.position.x,
            camera.position.y,
            camera.position.z,
            camera.look.x,
            camera.look.y,
            camera.look.z,
            camera.up.x,
            camera.up.y,
            camera.up.z
        )

        val angle = moveInXAxis
        Matrix.setRotateM(rotationMatrix, 0, angle, 0.0f, 1.0f, 0f)
        Matrix.multiplyMM(viewMatrix, 0, viewMatrix, 0, rotationMatrix, 0)

        Matrix.invertM(invertedViewMatrix, 0, viewMatrix, 0)

        glTFRenderer.draw(modelMatrix, viewMatrix, projectionMatrix, invertedViewMatrix)


        //draw HUD
        gl.apply {
            glEnable(GL10.GL_DEPTH_TEST)
            glDepthFunc(GL_ALWAYS)
        }

//        hudRenderer?.draw()

        fpsRenderer?.draw(fpsList.toFloatArray())

    }

    fun setRotateX(moveInXAxis: Float) {
        this.moveInXAxis = moveInXAxis
    }
}