package com.gemserk.prototypes.pixmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.gemserk.commons.gdx.Game;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.ScreenImpl;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;

public class PixmapFromTextureAtlasPrototype extends Game {

	public static class PixmapTextureAtlas implements Disposable {

		private TextureAtlas textureAtlas;
		private Pixmap textureAtlasPixmap;

		boolean shouldDispose = false;

		public PixmapTextureAtlas(FileHandle textureAtlasImageFile, FileHandle textureAtlasFile) {
			this(new TextureAtlas(textureAtlasFile), new Pixmap(textureAtlasImageFile));
			this.shouldDispose = true;
		}

		public PixmapTextureAtlas(TextureAtlas textureAtlas, Pixmap textureAtlasPixmap) {
			this.textureAtlas = textureAtlas;
			this.textureAtlasPixmap = textureAtlasPixmap;
		}

		public Pixmap createPixmap(String regionName) {
			AtlasRegion region = textureAtlas.findRegion(regionName);

			int width = MathUtils.nextPowerOfTwo(region.getRegionWidth());
			int height = MathUtils.nextPowerOfTwo(region.getRegionHeight());

			Pixmap regionPixmap = new Pixmap(width, height, textureAtlasPixmap.getFormat());

			int x = (width / 2) - (region.getRegionWidth() / 2);
			int y = (height / 2) - (region.getRegionHeight() / 2);

			regionPixmap.drawPixmap(textureAtlasPixmap, x, y, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());

			return regionPixmap;
		}

		@Override
		public void dispose() {
			if (shouldDispose) {
				textureAtlas.dispose();
				textureAtlasPixmap.dispose();
			}
		}

	}

	private static class BitmapCollisionsGameState extends GameStateImpl {

		private GL10 gl;
		private SpriteBatch spriteBatch;
		private OrthographicCamera orthographicCamera;

		private InputDevicesMonitorImpl inputDevicesMonitor;

		private Sprite sprite;

		@Override
		public void init() {
			gl = Gdx.graphics.getGL10();

			spriteBatch = new SpriteBatch();

			orthographicCamera = new OrthographicCamera();

			// TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("pixmap/pack"));
			// AtlasRegion region = textureAtlas.findRegion("button-play");

			// Texture region = new Texture(Gdx.files.internal("pixmap/buttons1.png"));

			// Pixmap pixmap1 = new Pixmap(Gdx.files.internal("unicornattack/platform-01_rgba8_l.png"));
			// Pixmap pixmap = new Pixmap(512, 128, Format.RGBA8888);
			
			PixmapTextureAtlas pixmapTextureAtlas = new PixmapTextureAtlas(Gdx.files.internal("pixmap/atlas/buttons1.png"), Gdx.files.internal("pixmap/atlas/pack"));
			
			Pixmap pixmap = pixmapTextureAtlas.createPixmap("button-play");

			// readTextureData(region, pixmap, 0, 0);

			Texture texture = new Texture(pixmap);
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

			texture.draw(pixmap, 0, 0);

			sprite = new Sprite(texture);

			SpriteUtils.resize(sprite, pixmap.getWidth() * 1f);
			SpriteUtils.centerOn(sprite, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.25f);

			sprite.setOrigin(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);

			orthographicCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			orthographicCamera.update();

			Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 0f);

			inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
			new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
				{
					monitorKeys("toggleRotate", Keys.S);
					monitorMouseLeftButton("releaseBomb");
				}
			};

		}

		@Override
		public void update() {
			super.update();

			inputDevicesMonitor.update();

			int x = Gdx.input.getX();
			int y = (Gdx.graphics.getHeight() - Gdx.input.getY());

			// System.out.println(MessageFormat.format("({0},{1}) => ({2},{3},{4},{5}} and {6}", position.x, position.y, color.r, color.g, color.b, color.a, Integer.toHexString(pixel)));

			// System.out.println("" + x + "," + y + ": " + color + ", " + Integer.toHexString(pixel));

		}

		@Override
		public void render() {
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			orthographicCamera.apply(gl);

			spriteBatch.setProjectionMatrix(orthographicCamera.projection);
			spriteBatch.setTransformMatrix(orthographicCamera.view);

			spriteBatch.begin();
			sprite.draw(spriteBatch);
			spriteBatch.end();
		}

	}
	
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	public void create() {
		setScreen(new ScreenImpl(new BitmapCollisionsGameState()));

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKeys("restart", Keys.R, Keys.MENU, Keys.NUM_1);
			}
		};
	}

	@Override
	public void render() {
		super.render();
		inputDevicesMonitor.update();

		if (inputDevicesMonitor.getButton("restart").isReleased()) {
			System.out.println("restarting");
			getScreen().restart();
		}
	}
}
