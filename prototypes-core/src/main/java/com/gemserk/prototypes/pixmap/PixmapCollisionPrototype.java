package com.gemserk.prototypes.pixmap;

import java.util.ArrayList;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.graphics.ColorUtils;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;

public class PixmapCollisionPrototype extends GameStateImpl {

	static class PixmapHelper {

		Pixmap pixmap;
		Sprite sprite;
		Texture texture;

		final Color color = new Color();

		public PixmapHelper(Pixmap pixmap, Sprite sprite, Texture texture) {
			this.pixmap = pixmap;
			this.sprite = sprite;
			this.texture = texture;
		}

		/**
		 * Projects the coordinates (x, y) to the Pixmap coordinates system and store the result in the specified Vector2.
		 * 
		 * @param position
		 *            The Vector2 to store the transformed coordinates.
		 * @param x
		 *            The x coordinate to be projected.
		 * @param y
		 *            The y coordinate to be prjected
		 */
		public void project(Vector2 position, float x, float y) {
			position.set(x, y);

			float centerX = sprite.getX() + sprite.getOriginX();
			float centerY = sprite.getY() + sprite.getOriginY();

			position.add(-centerX, -centerY);

			position.rotate(-sprite.getRotation());

			float scaleX = pixmap.getWidth() / sprite.getWidth();
			float scaleY = pixmap.getHeight() / sprite.getHeight();

			position.x *= scaleX;
			position.y *= scaleY;

			position.add( //
					pixmap.getWidth() * 0.5f, //
					-pixmap.getHeight() * 0.5f //
			);

			position.y *= -1f;
		}

		public int getPixel(Vector2 position) {
			return getPixel(position.x, position.y);
		}

		public int getPixel(float x, float y) {
			return pixmap.getPixel((int) x, (int) y);
		}

		public void setPixel(float x, float y, int value) {
			ColorUtils.rgba8888ToColor(color, value);
			pixmap.setColor(color);
		}

		public void drawPixel(float x, float y, float r, float g, float b, float a, float radius) {
			pixmap.setColor(r, g, b, a);
			pixmap.fillCircle(Math.round(x), Math.round(y), Math.round(radius));
			texture.draw(pixmap, 0, 0);
		}

		public void eraseCircle(float x, float y, float radius) {
			Blending blending = Pixmap.getBlending();
			pixmap.setColor(0f, 0f, 0f, 0f);
			Pixmap.setBlending(Blending.None);

			float scaleX = pixmap.getWidth() / sprite.getWidth();
			System.out.println(scaleX);

			pixmap.fillCircle(Math.round(x), Math.round(y), Math.round(radius * scaleX));
			texture.draw(pixmap, 0, 0);
			Pixmap.setBlending(blending);
		}

	}

	static class Bomb {

		Vector2 position = new Vector2();
		Vector2 center = new Vector2(0.5f, 0.5f);
		Vector2 velocity = new Vector2();

		float width;
		float height;

		float angle;
		Sprite sprite;
		
		long soundHandle;

		PixmapCollisionPrototype.PixmapHelper pixmapHelper;
		Color color = new Color();
		Vector2 projectedPosition = new Vector2();

		boolean deleted = false;

		public void setSprite(Sprite sprite) {
			this.sprite = sprite;
			this.width = sprite.getWidth();
			this.height = sprite.getHeight();
		}

		void update() {

			velocity.y += -1 * 100f * GlobalTime.getDelta();

			position.x += velocity.x * GlobalTime.getDelta();
			position.y += velocity.y * GlobalTime.getDelta();

			sprite.setRotation(angle);
			sprite.setOrigin(width * center.x, height * center.y);
			sprite.setSize(width, height);
			sprite.setPosition(position.x - sprite.getOriginX(), position.y - sprite.getOriginY());

			pixmapHelper.project(projectedPosition, position.x, position.y);

			ColorUtils.rgba8888ToColor(color, pixmapHelper.getPixel(projectedPosition.x, projectedPosition.y));

			if (color.a != 0) {
				pixmapHelper.eraseCircle(projectedPosition.x, projectedPosition.y, 40f);
				deleted = true;
				// remove this bomb...
			}
		}

		void draw(SpriteBatch spriteBatch) {
			sprite.draw(spriteBatch);
		}

	}

	private GL10 gl;
	private SpriteBatch spriteBatch;
	private OrthographicCamera orthographicCamera;

	private Color color = new Color();

	private PixmapCollisionPrototype.PixmapHelper pixmapHelper1;

	private final Vector2 position = new Vector2();
	private InputDevicesMonitorImpl inputDevicesMonitor;

	boolean rotate = false;

	ArrayList<PixmapCollisionPrototype.Bomb> bombs = new ArrayList<PixmapCollisionPrototype.Bomb>();
	ArrayList<PixmapCollisionPrototype.Bomb> bombsToDelete = new ArrayList<PixmapCollisionPrototype.Bomb>();
	
	Texture bombTexture;
	private Sound bombSound;
	private Sound bombExplosionSound;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		orthographicCamera = new OrthographicCamera();
		
		bombTexture = new Texture(Gdx.files.internal("pixmap/collisions/bazooka.png"));
		bombSound = Gdx.audio.newSound(Gdx.files.internal("pixmap/collisions/sounds/bomb-falling.wav"));
		
		bombExplosionSound = Gdx.audio.newSound(Gdx.files.internal("pixmap/collisions/sounds/bomb-explosion.ogg"));

		Pixmap pixmap1 = new Pixmap(Gdx.files.internal("pixmap/collisions/platform-01_rgba8_l.png"));
		Pixmap pixmap2 = new Pixmap(56, 46, Format.RGBA8888);

		Texture texture1 = new Texture(pixmap1);
		texture1.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		texture1.draw(pixmap2, 0, 0);

		Sprite sprite1 = new Sprite(texture1);

		SpriteUtils.resize(sprite1, pixmap1.getWidth() * 1f);
		SpriteUtils.centerOn(sprite1, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.25f);

		sprite1.setOrigin(sprite1.getWidth() * 0.5f, sprite1.getHeight() * 0.5f);

		orthographicCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		orthographicCamera.update();

		pixmapHelper1 = new PixmapHelper(pixmap1, sprite1, texture1);

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 1f, 0f);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKeys("toggleRotate", Keys.S);
				
				if (Gdx.app.getType() == ApplicationType.Android)
					monitorPointerDown("releaseBomb", 0);
				else
					monitorMouseLeftButton("releaseBomb");
			}
		};

		bombs = new ArrayList<PixmapCollisionPrototype.Bomb>();
	}

	@Override
	public void update() {
		super.update();

		inputDevicesMonitor.update();

		int x = Gdx.input.getX();
		int y = (Gdx.graphics.getHeight() - Gdx.input.getY());

		pixmapHelper1.project(position, x, y);

		int pixel = pixmapHelper1.getPixel(position.x, position.y);

		ColorUtils.rgba8888ToColor(color, pixel);

		// System.out.println(MessageFormat.format("({0},{1}) => ({2},{3},{4},{5}} and {6}", position.x, position.y, color.r, color.g, color.b, color.a, Integer.toHexString(pixel)));

		// System.out.println("" + x + "," + y + ": " + color + ", " + Integer.toHexString(pixel));

		if (inputDevicesMonitor.getButton("toggleRotate").isReleased())
			rotate = !rotate;

		if (rotate)
			pixmapHelper1.sprite.rotate(0.1f);

		if (inputDevicesMonitor.getButton("releaseBomb").isReleased()) {
			PixmapCollisionPrototype.Bomb bomb = new Bomb();
			
			bomb.soundHandle = bombSound.play();

			bombTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

			bomb.position.set(x, y);
			bomb.velocity.set(0f, -25f);
			bomb.angle = 270;
			bomb.pixmapHelper = pixmapHelper1;

			bomb.setSprite(new Sprite(bombTexture));

			bombs.add(bomb);
		}

		for (int i = 0; i < bombs.size(); i++) {
			PixmapCollisionPrototype.Bomb bomb = bombs.get(i);
			bomb.update();
			if (bomb.deleted) {
				bombSound.stop(bomb.soundHandle);
				bombsToDelete.add(bomb);
				bombExplosionSound.play();
				System.out.println("removing bomb");
			}
		}

		bombs.removeAll(bombsToDelete);
		bombsToDelete.clear();

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		orthographicCamera.apply(gl);

		spriteBatch.setProjectionMatrix(orthographicCamera.projection);
		spriteBatch.setTransformMatrix(orthographicCamera.view);

		spriteBatch.begin();
		pixmapHelper1.sprite.draw(spriteBatch);

		for (int i = 0; i < bombs.size(); i++) {
			bombs.get(i).draw(spriteBatch);
		}

		spriteBatch.end();
	}
	
	@Override
	public void dispose() {
		spriteBatch.dispose();
		bombTexture.dispose();
		pixmapHelper1.texture.dispose();
		pixmapHelper1.pixmap.dispose();
	}

}