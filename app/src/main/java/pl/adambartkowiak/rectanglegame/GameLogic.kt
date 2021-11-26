package pl.adambartkowiak.rectanglegame

class GameLogic {
    fun update(worldModel: WorldModel) {

        cachePreviousState(worldModel)

        //physic
        worldModel.rectVelocityY += INTERVAL_IN_S * WorldModel.GRAVITY
        worldModel.rectY += worldModel.rectVelocityY * INTERVAL_IN_S
        worldModel.rectY =
            worldModel.rectY.coerceAtMost(worldModel.height.toFloat() - worldModel.rectSize)
    }

    private fun cachePreviousState(worldModel: WorldModel) {
        worldModel.rectOldX = worldModel.rectX
        worldModel.rectOldY = worldModel.rectY
    }

    companion object {
        const val INTERVAL = 20f
        private const val INTERVAL_IN_S = INTERVAL / 1000f
    }
}