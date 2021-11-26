package pl.adambartkowiak.rectanglegame

data class WorldModel(
    var width: Int = 0,
    var height: Int = 0,

    var rectY: Float = 0f,
    var rectOldY: Float = rectY,

    var rectX: Float = 300f,
    var rectOldX: Float = rectX,

    var rectAngle: Float = 0f,
    var rectVelocityY: Float = 0f,
    var rectSize: Float = 150f,

    var cameraZ: Float = 0f,
    var cameraUp: Boolean = false

) {
    companion object {
        const val GRAVITY = 10000f
        const val MAX_CAMERA_Z = 3f
        const val MIN_CAMERA_Z = -3f
    }
}