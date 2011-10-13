package com.gemserk.prototypes.kalleh.lighting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.prototypes.kalleh.lighting.RayHandler.Light;

public class LightingPrototype extends GameStateImpl {

	private GL10 gl;
	private SpriteBatch spriteBatch;

	private OrthographicCamera worldCamera;
	private OrthographicCamera guiCamera;

	private final Vector3 position = new Vector3();
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private RayHandler rayHandler;
	private World world;

	Box2DDebugRenderer box2dDebugRenderer;
	private Light light;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		worldCamera = new OrthographicCamera();

		worldCamera.setToOrtho(false, Gdx.graphics.getWidth() / 40f, Gdx.graphics.getHeight() / 40f);
		worldCamera.update();

		guiCamera = new OrthographicCamera();
		guiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		guiCamera.update();

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{

			}
		};

		rayHandler = new RayHandler(1024);
		// rayHandler.shadows = true;

		light = rayHandler.addLight(5f, 35f, 180f, 45f, 30f, 250, new Color(1f, 1f, 1f, 1f));

		// Light light2 = rayHandler.addLight(5f, 5f, 0f, 360f, 20f, 250, new Color(1f, 0f, 0f, 0.5f));
		//
		// rayHandler.addLight(8f, 9f, 180f, 30f, 30f, 250, new Color(0f, 1f, 0f, 1f));

		// for (int i = 0; i < 20; i++) {
		//
		// float x = MathUtils.random(3f, 16f);
		// float y = MathUtils.random(0f, 12f);
		//
		// Color color = new Color(0f, 1f, 0f, 1f);
		//
		// color.r = MathUtils.random(0.5f, 1f);
		// color.g = MathUtils.random(0.5f, 1f);
		// color.b = MathUtils.random(0.5f, 1f);
		// color.a = MathUtils.random(0.1f, 0.4f);
		//
		// rayHandler.addLight(x, y, MathUtils.random(0f, 360f), MathUtils.random(20f, 60f), MathUtils.random(10f, 40f), 50, color);
		//
		// }

		// rayHandler.addLight(0f, 3f, 90f, 30f, 30f, 250, new Color(1f, 0f, 0f, 1f));
		// rayHandler.addLight(16f, 3f, -90f, 30f, 30f, 250, new Color(1f, 1f, 0f, 1f));

		// light2.xray = true;

		world = new World(new Vector2(), false);

		BodyBuilder bodyBuilder = new BodyBuilder(world);

		Shape[] shapes = new Shape[] { //
		new Shape(new Vector2[] { new Vector2(3f, 1.5f), new Vector2(1f, 4f), new Vector2(-2.5f, 1f), new Vector2(-1.5f, -2.5f), new Vector2(1f, -1.5f), }), //
				new Shape(new Vector2[] { new Vector2(1.5f, 0f), new Vector2(0.5f, 2f), new Vector2(-1.5f, 1f), new Vector2(-0.5f, -2.5f) }), //
				new Shape(new Vector2[] { new Vector2(2f, 1f), new Vector2(-3f, 1.2f), new Vector2(-2.5f, -0.8f), new Vector2(2.5f, -2f) }), //
		};

		for (int i = 0; i < shapes.length; i++) {
			Shape shape = shapes[i];
			for (int j = 0; j < shape.vertices.length; j++) {
				Vector2 vertex = shape.vertices[j];
				vertex.mul(0.2f);
			}
		}

		for (int i = 0; i < 20; i++) {

			Shape shape = shapes[MathUtils.random(shapes.length - 1)];

			float x = MathUtils.random(3f, 15f);
			float y = MathUtils.random(1f, 5f);

			bodyBuilder //
					.fixture(bodyBuilder.fixtureDefBuilder() //
							.polygonShape(shape.vertices) //
					) //
					.type(BodyType.StaticBody) //
					.position(x, y) //
					.build();

		}

		bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.boxShape(20f, 1f) //
				) //
				.type(BodyType.StaticBody) //
				.position(15f, 0f) //
				.build();

		// bodyBuilder //
		// .fixture(bodyBuilder.fixtureDefBuilder() //
		// .boxShape(1f, 0.1f) //
		// ) //
		// .type(BodyType.StaticBody) //
		// .position(5f, 3f) //
		// .build();
		//
		// bodyBuilder //
		// .fixture(bodyBuilder.fixtureDefBuilder() //
		// .circleShape(0.5f) //
		// ) //
		// .type(BodyType.StaticBody) //
		// .position(9f, 3f) //
		// .build();

		box2dDebugRenderer = new Box2DDebugRenderer();

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 1f, 0f);

	}

	@Override
	public void update() {
		super.update();

		inputDevicesMonitor.update();

		int x = Gdx.input.getX();
		// int y = (Gdx.graphics.getHeight() - Gdx.input.getY());
		int y = Gdx.input.getY();

		// if (Gdx.input.justTouched()) {

		position.x = x;
		position.y = y;
		position.z = 1f;

		worldCamera.unproject(position);
		// System.out.println(position);
		light.setPos(position.x, position.y);
		// }

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		worldCamera.apply(gl);

		spriteBatch.setProjectionMatrix(worldCamera.projection);
		spriteBatch.setTransformMatrix(worldCamera.view);

		spriteBatch.begin();
		spriteBatch.end();

		rayHandler.updateAndRender(world);
		box2dDebugRenderer.render(world, worldCamera.combined);

		spriteBatch.setProjectionMatrix(guiCamera.projection);
		spriteBatch.setTransformMatrix(guiCamera.view);
		spriteBatch.begin();
		spriteBatch.end();

	}

}