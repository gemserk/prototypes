package com.gemserk.prototypes.box2d.frustum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.prototypes.kalleh.lighting.Shape;

public class FustumCullingPrototype extends GameStateImpl {

	static class Categories {

		public static final short All = 0xFF;
		public static final short Frustum = 0x01;
		public static final short Object = 0x02;

	}

	static class Masks {

	}

	private GL10 gl;
	private SpriteBatch spriteBatch;

	private OrthographicCamera worldCamera;
	private OrthographicCamera guiCamera;

	private final Vector3 position = new Vector3();
	private final Vector2 position2 = new Vector2();
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private World world;

	CustomBox2DDebugRenderer box2dDebugRenderer;
	private Body frustumSensorBody;

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
							.categoryBits(Categories.Object) //
							.maskBits(Categories.All) //
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

		frustumSensorBody = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.categoryBits(Categories.Frustum) //
						.maskBits(Categories.All) //
						.boxShape(5f, 5f) //
						.sensor() //
				) //
				.type(BodyType.DynamicBody) //
				.position(0f, 0f) //
				.build();

		box2dDebugRenderer = new CustomBox2DDebugRenderer(true, false, true);
		
		world.setContactListener(new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				
			}
			
			@Override
			public void endContact(Contact contact) {
				if (contact.getFixtureA().getBody() == frustumSensorBody) {
					hide(contact.getFixtureB().getBody());
				} else if (contact.getFixtureB().getBody() == frustumSensorBody) {
					hide(contact.getFixtureA().getBody());
				}
			}
			
			private void hide(Body body) {
				body.setUserData(false);				
			}

			@Override
			public void beginContact(Contact contact) {
				if (contact.getFixtureA().getBody() == frustumSensorBody) {
					show(contact.getFixtureB().getBody());
				} else if (contact.getFixtureB().getBody() == frustumSensorBody) {
					show(contact.getFixtureA().getBody());
				}
			}

			private void show(Body body) {
				body.setUserData(true);
			}
			
		});

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 1f, 0f);

	}

	@Override
	public void update() {
		super.update();

		world.step(GlobalTime.getDelta(), 5, 5);
		
		inputDevicesMonitor.update();

		int x = Gdx.input.getX();
//		 int y = (Gdx.graphics.getHeight() - Gdx.input.getY());
		int y = Gdx.input.getY();

		// if (Gdx.input.justTouched()) {

		position.x = x;
		position.y = y;
		position.z = 1f;

		worldCamera.unproject(position);
		
		frustumSensorBody.setTransform(position.x, position.y, 0f);

		
	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		worldCamera.apply(gl);

		spriteBatch.setProjectionMatrix(worldCamera.projection);
		spriteBatch.setTransformMatrix(worldCamera.view);

		spriteBatch.begin();
		spriteBatch.end();

		box2dDebugRenderer.render(world, worldCamera.combined);

		spriteBatch.setProjectionMatrix(guiCamera.projection);
		spriteBatch.setTransformMatrix(guiCamera.view);
		spriteBatch.begin();
		spriteBatch.end();

	}

}