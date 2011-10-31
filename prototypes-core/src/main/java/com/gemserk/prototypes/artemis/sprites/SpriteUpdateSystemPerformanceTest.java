package com.gemserk.prototypes.artemis.sprites;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.systems.PhysicsSystem;
import com.gemserk.commons.artemis.systems.RenderLayerSpriteBatchImpl;
import com.gemserk.commons.artemis.systems.RenderableSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityFactoryImpl;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class SpriteUpdateSystemPerformanceTest extends GameStateImpl {

	public static class ObjectTemplate extends EntityTemplateImpl {

		BodyBuilder bodyBuilder;
		ResourceManager<String> resourceManager;

		@Override
		public void apply(Entity entity) {

			Spatial spatial = parameters.get("spatial");
			Sprite sprite = resourceManager.getResourceValue("BombSprite");

			Body body = bodyBuilder //
					.fixture(bodyBuilder.fixtureDefBuilder() //
							.boxShape(spatial.getWidth() * 0.5f, spatial.getHeight() * 0.5f)) //
					.type(BodyType.StaticBody) //
					.angle(spatial.getAngle()) //
					.position(spatial.getX(), spatial.getY()) //
					.userData(entity) //
					.build();

			entity.addComponent(new PhysicsComponent(body));
			entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));

			entity.addComponent(new RenderableComponent(0));
			entity.addComponent(new SpriteComponent(sprite, 0.5f, 0.5f, Color.WHITE));

		}

	}

	private GL10 gl;
	// private OrthographicCamera orthographicCamera;

	ResourceManager<String> resourceManager;

	WorldWrapper worldWrapper;
	Injector injector;

	private Libgdx2dCameraTransformImpl worldCamera;

	private Box2DDebugRenderer box2dDebugRenderer;
	private com.badlogic.gdx.physics.box2d.World physicsWorld;

	boolean box2dDebugEnabled;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		// orthographicCamera = new OrthographicCamera();

		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("BombTexture", "superangrysheep/bomb.png", true);
				sprite("BombSprite", "BombTexture");
			}
		};

		RenderLayers renderLayers = new RenderLayers();
		physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0f, 0f), false);

		worldCamera = new Libgdx2dCameraTransformImpl();

		renderLayers.add("World", new RenderLayerSpriteBatchImpl(-1000, 1000, worldCamera));

		injector = new InjectorImpl();
		injector.bind("resourceManager", resourceManager);
		injector.bind("bodyBuilder", new BodyBuilder(physicsWorld));

		worldWrapper = new WorldWrapper(new World());

		worldWrapper.addUpdateSystem(new PhysicsSystem(physicsWorld));

		worldWrapper.addRenderSystem(new SpriteUpdateSystem());
		worldWrapper.addRenderSystem(new RenderableSystem(renderLayers));

		worldWrapper.init();

		EntityTemplate objectTemplate = injector.getInstance(ObjectTemplate.class);

		EntityFactory entityFactory = new EntityFactoryImpl(worldWrapper.getWorld());

		for (int i = 0; i < 500; i++) {

			float x = MathUtils.random(0f, Gdx.graphics.getWidth());
			float y = MathUtils.random(0f, Gdx.graphics.getHeight());

			float w = MathUtils.random(4f, 32f);
			float h = MathUtils.random(4f, 32f);

			float angle = MathUtils.random(0f, 360f);

			entityFactory.instantiate(objectTemplate, new ParametersWrapper().put("spatial", new SpatialImpl(x, y, w, h, angle)));

		}

		box2dDebugRenderer = new Box2DDebugRenderer();

		box2dDebugEnabled = false;

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorPointerDown("toggleBox2dDebug", 0);
			}
		};

		worldWrapper.update(100);
		worldWrapper.update(100);

		// worldWrapper.update(50);
	}

	@Override
	public void update() {
		super.update();
		worldWrapper.update(getDeltaInMs());
		inputDevicesMonitor.update();

		if (inputDevicesMonitor.getButton("toggleBox2dDebug").isReleased())
			box2dDebugEnabled = !box2dDebugEnabled;
	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		worldWrapper.render();
		if (box2dDebugEnabled)
			box2dDebugRenderer.render(physicsWorld, worldCamera.getCombinedMatrix());
	}

	@Override
	public void dispose() {
		worldWrapper.dispose();
		resourceManager.unloadAll();
	}

}