package com.gemserk.prototypes.physicseditor;

import aurelienribon.box2deditor.FixtureAtlas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;

public class FixtureAtlasLoadShapeTest extends GameStateImpl {

	private GL10 gl;
	private SpriteBatch spriteBatch;

	private OrthographicCamera worldCamera;
	private OrthographicCamera guiCamera;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private World world;

	Box2DDebugRenderer box2dDebugRenderer;
	private FixtureAtlas fixtureAtlas;
	private Sprite islandSprite;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		worldCamera = new OrthographicCamera();

		worldCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		worldCamera.translate(-400, -240f, 0);
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

		box2dDebugRenderer = new Box2DDebugRenderer();

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 1f, 0f);

		fixtureAtlas = new FixtureAtlas(Gdx.files.internal("physicseditor/test1"));

		Body body = new BodyBuilder(world) //
				// .fixture(new FixtureDefBuilder().boxShape(5f, 5f)) //
				.position(-256f, -64f) //
				.type(BodyType.StaticBody) //
				.build();

		fixtureAtlas.createFixtures(body, "island01.png", 512f, 75f);

		Texture texture = new Texture(Gdx.files.internal("physicseditor/island01.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		islandSprite = new Sprite(texture);
		SpriteUtils.resize(islandSprite, 512f);
		
	}

	@Override
	public void update() {
		super.update();

		inputDevicesMonitor.update();

		int x = Gdx.input.getX();
		int y = Gdx.input.getY();

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// worldCamera.apply(gl);

		spriteBatch.setProjectionMatrix(worldCamera.projection);
		spriteBatch.setTransformMatrix(worldCamera.view);

		spriteBatch.begin();
		SpriteBatchUtils.drawCentered(spriteBatch, islandSprite, 0, 0, islandSprite.getWidth(), islandSprite.getHeight(), 0, 0.5f, 0.5f);
		// islandSprite.draw(spriteBatch);
		spriteBatch.end();

		box2dDebugRenderer.render(world, worldCamera.combined);

	}

	@Override
	public void dispose() {
		islandSprite.getTexture().dispose();
		fixtureAtlas.dispose();
		spriteBatch.dispose();
		world.dispose();
	}
}