package game.robotm.gamesys

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.*
import game.robotm.ecs.components.PhysicsComponent
import game.robotm.ecs.components.PlayerComponent
import game.robotm.ecs.components.RendererComponent
import game.robotm.ecs.components.TransformComponent


object ObjBuilder {

    var assetManager: AssetManager? = null
    var world: World? = null
    var engine: Engine? = null

    fun createPlayer(x: Float, y: Float): Entity {

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(x, y)
        bodyDef.fixedRotation = true

        val body = world!!.createBody(bodyDef)

        val boxShape = PolygonShape()
        boxShape.setAsBox(0.45f, 0.3f)
        val fixtureDef = FixtureDef()
        fixtureDef.shape = boxShape
        fixtureDef.density = 0.5f
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_PLAYER.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_PLAYER.toShort()

        body.createFixture(fixtureDef)
        boxShape.dispose()

        val edgeShape = EdgeShape()
        edgeShape.set(-0.45f, -0.375f, 0.45f, -0.375f)
        fixtureDef.shape = edgeShape
        body.createFixture(fixtureDef)
        edgeShape.dispose()

        val textureAtlas = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java)
        val textureRegion = textureAtlas.findRegion("RobotM")

        val entity = Entity()
        entity.add(PlayerComponent())
        entity.add(PhysicsComponent(body))
        entity.add(RendererComponent(TextureRegion(textureRegion, 64 * 3, 0, 64, 48), 64 / GM.PPM, 48 / GM.PPM))
        entity.add(TransformComponent(x, y))

        engine!!.addEntity(entity)

        body.userData = entity
        return entity
    }

    private fun createWallBody(x: Float, y: Float): Body {

        val bodyDef = BodyDef()
        bodyDef.position.set(x, y)
        bodyDef.type = BodyDef.BodyType.StaticBody

        val body = world!!.createBody(bodyDef)

        val shape = PolygonShape()
        shape.setAsBox(0.5f, 0.5f)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_STATIC_OBSTACLE.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_STATIC_OBSTACLE.toShort()

        body.createFixture(fixtureDef)
        shape.dispose()

        return body
    }

    fun createWall(left: Float, right: Float, top: Float, height: Int, type: String = "Grass") {

        val textureAtlas = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java)
        val textureRegion = TextureRegion(textureAtlas.findRegion(type), 64 * 5, 0, 64, 64)

        for (i in 0..height - 1) {
            var body = createWallBody(left, top + i)
            var entity = Entity()
            entity.add(TransformComponent(left, top + i))
            entity.add(RendererComponent(textureRegion, 1f, 1f))
            body.userData = entity
            engine!!.addEntity(entity)

            body = createWallBody(right, top + i)
            entity = Entity()
            entity.add(TransformComponent(right, top + i))
            entity.add(RendererComponent(textureRegion, 1f, 1f))
            body.userData = entity
            engine!!.addEntity(entity)

        }
    }

    fun createFloor(x: Float, y: Float, width: Int, type: String = "Grass") {
        val textureAtlas = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java)

        for (i in 0..width - 1) {

            val bodyDef = BodyDef()
            bodyDef.type = BodyDef.BodyType.StaticBody
            bodyDef.position.set(x + i, y)

            val body = world!!.createBody(bodyDef)

            val shape = PolygonShape()
            shape.setAsBox(0.5f, 0.5f)

            val fixtureDef = FixtureDef()
            fixtureDef.shape = shape
            fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_STATIC_OBSTACLE.toShort()
            fixtureDef.filter.maskBits = GM.MASK_BITS_STATIC_OBSTACLE.toShort()

            body.createFixture(fixtureDef)
            shape.dispose()

            val textureRegion: TextureRegion

            if (width == 1) {
                textureRegion = TextureRegion(textureAtlas.findRegion(type), 0, 0, 64, 64)
            } else {
                when (i) {
                    0 -> textureRegion = TextureRegion(textureAtlas.findRegion(type), 64 * 1, 0, 64, 64)
                    width - 1 -> textureRegion = TextureRegion(textureAtlas.findRegion(type), 64 * 3, 0, 64, 64)
                    else -> textureRegion = TextureRegion(textureAtlas.findRegion(type), 64 * 2, 0, 64, 64)
                }
            }

            val entity = Entity()
            entity.add(TransformComponent(x + i, y))
            entity.add(RendererComponent(textureRegion, 1f, 1f))

            engine!!.addEntity(entity)

            body.userData = entity
        }
    }
}