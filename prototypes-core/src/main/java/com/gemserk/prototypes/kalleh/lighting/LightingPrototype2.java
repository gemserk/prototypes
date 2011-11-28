package com.gemserk.prototypes.kalleh.lighting;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.prototypes.kalleh.lighting.RayHandler.Light;

public class LightingPrototype2 extends GameStateImpl {

	private static final Color yellow = new Color(1f, 1f, 0f, 1f);
	private static final Color magenta = new Color(1f, 0f, 1f, 1f);

	private static final Color[] colors = { yellow, Color.RED, Color.BLUE, Color.GREEN, magenta };

	private GL10 gl;
	private SpriteBatch spriteBatch;

	private OrthographicCamera worldCamera;
	private OrthographicCamera guiCamera;

	private final Vector3 position = new Vector3();
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private RayHandler rayHandler;
	private World world;
	
	BitmapFont font;

	Box2DDebugRenderer box2dDebugRenderer;

	class BallLight {

		Body ball;
		Light light;

		void update() {
			Vector2 position = ball.getPosition();
			light.setPos(position.x, position.y);
		}

	}

	ArrayList<BallLight> ballLights;
//	private Body mouseBody;
	private BodyBuilder bodyBuilder;

	private <T> T random(T[] ts) {
		return ts[MathUtils.random(0, ts.length - 1)];
	}

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();
		font = new BitmapFont();

		worldCamera = new OrthographicCamera();

		worldCamera.setToOrtho(false, Gdx.graphics.getWidth() / 40f, Gdx.graphics.getHeight() / 40f);
		worldCamera.update();

		guiCamera = new OrthographicCamera();
		guiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		guiCamera.update();

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorMouseLeftButton("newBall");
				monitorMouseRightButton("removeBall");
			}
		};

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

		world = new World(new Vector2(0, -10f), false);

		rayHandler = new RayHandler(world, 1024);
		// rayHandler.shadows = true;

		bodyBuilder = new BodyBuilder(world);

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
							.restitution(1f) //
					) //
					.type(BodyType.StaticBody) //
					.position(x, y) //
					.build();

		}

//		mouseBody = bodyBuilder //
//				.fixture(bodyBuilder.fixtureDefBuilder() //
//						.circleShape(0.2f) //
//						.restitution(0.8f) //
//				) //
//				.type(BodyType.DynamicBody) //
//				.position(0f, 0f) //
//				.build();

		bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.boxShape(20f, 1f) //
						.restitution(1f) //
				) //
				.type(BodyType.StaticBody) //
				.position(15f, 0f) //
				.build();

		ballLights = new ArrayList<LightingPrototype2.BallLight>();

		for (int i = 0; i < 5; i++) {

			float x = MathUtils.random(2f, 18f);
			float y = MathUtils.random(5f, 10f);

			BallLight ballLight = new BallLight();

			ballLight.ball = bodyBuilder //
					.fixture(bodyBuilder.fixtureDefBuilder() //
							.circleShape(0.2f) //
							.restitution(1f) //
					) //
					.type(BodyType.DynamicBody) //
					.position(x, y) //
					.userData(ballLight) //
					.build();

			ballLight.light = rayHandler.addLight(0f, 0f, 180f, 180f, 5f, 150, new Color(random(colors)), false, false);

			ballLights.add(ballLight);
		}

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

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

	}

	@Override
	public void update() {
		super.update();

		world.step(getDelta(), 3, 3);

		inputDevicesMonitor.update();
		
		int x = Gdx.input.getX();
		// int y = (Gdx.graphics.getHeight() - Gdx.input.getY());
		int y = Gdx.input.getY();
		

		// if (Gdx.input.justTouched()) {

		position.x = x;
		position.y = y;
		position.z = 1f;

		worldCamera.unproject(position);
		
		if (inputDevicesMonitor.getButton("newBall").isReleased()) {
			BallLight ballLight = new BallLight();

			ballLight.ball = bodyBuilder //
					.fixture(bodyBuilder.fixtureDefBuilder() //
							.circleShape(0.2f) //
							.restitution(1f) //
					) //
					.type(BodyType.DynamicBody) //
					.position(position.x, position.y) //
					.userData(ballLight) //
					.build();

			ballLight.light = rayHandler.addLight(0f, 0f, 180f, 180f, 5f, 150, new Color(random(colors)), false, false);

			ballLights.add(ballLight);
		}
		
		if (inputDevicesMonitor.getButton("removeBall").isReleased()) {
			
			world.QueryAABB(new QueryCallback() {
				
				@Override
				public boolean reportFixture(Fixture fixture) {
					BallLight ballLight = (BallLight) fixture.getBody().getUserData();
					if (ballLight == null)
						return true;

					ballLight.light.remove();
					ballLights.remove(ballLight);
					world.destroyBody(ballLight.ball);
					
					return false;
				}
			}, position.x, position.y, position.x, position.y);
			
		}

		
//		mouseBody.setTransform(position.x, position.y, 0f);

		for (int i = 0; i < ballLights.size(); i++) {
			BallLight ballLight = ballLights.get(i);
			ballLight.update();
		}

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

		rayHandler.updateAndRender();
		box2dDebugRenderer.render(world, worldCamera.combined);

		spriteBatch.setProjectionMatrix(guiCamera.projection);
		spriteBatch.setTransformMatrix(guiCamera.view);
		spriteBatch.begin();
		SpriteBatchUtils.drawMultilineText(spriteBatch, font, "http://code.google.com/p/box2dlights/", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() - 30f, 0.5f, 0.5f);
		spriteBatch.end();

	}
	
	@Override
	public void dispose() {
		super.dispose();
		spriteBatch.dispose();
		font.dispose();
		rayHandler.dispose();
		world.dispose();
	}

}