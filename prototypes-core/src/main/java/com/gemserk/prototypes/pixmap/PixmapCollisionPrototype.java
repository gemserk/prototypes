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

	static class Bomb {

		Vector2 position = new Vector2();
		Vector2 center = new Vector2(0.5f, 0.5f);
		Vector2 velocity = new Vector2();

		float width;
		float height;

		float angle;
		Sprite sprite;
		
		long soundHandle;

		PixmapHelper pixmapHelper;
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
				pixmapHelper.eraseCircle(projectedPosition.x, projectedPosition.y, 20f);
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

	private PixmapHelper pixmapHelper1;

	private final Vector2 position = new Vector2();
	private InputDevicesMonitorImpl inputDevicesMonitor;

	boolean rotate = false;

	ArrayList<PixmapCollisionPrototype.Bomb> bombs = new ArrayList<PixmapCollisionPrototype.Bomb>();
	ArrayList<PixmapCollisionPrototype.Bomb> bombsToDelete = new ArrayList<PixmapCollisionPrototype.Bomb>();
	
	Texture bombTexture;
	private Sound bombFallingSound;
	private Sound bombExplosionSound;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		orthographicCamera = new OrthographicCamera();
		
		bombTexture = new Texture(Gdx.files.internal("pixmap/collisions/bazooka.png"));
		bombFallingSound = Gdx.audio.newSound(Gdx.files.internal("pixmap/collisions/sounds/bomb-falling.wav"));
		
		bombExplosionSound = Gdx.audio.newSound(Gdx.files.internal("pixmap/collisions/sounds/bomb-explosion.ogg"));

		Pixmap pixmap = new Pixmap(Gdx.files.internal("pixmap/collisions/platform-01_rgba8_l.png"));

		Texture texture = new Texture(pixmap);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		Sprite sprite = new Sprite(texture);

		SpriteUtils.resize(sprite, pixmap.getWidth() * 1f);
		SpriteUtils.centerOn(sprite, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.25f);

		sprite.setOrigin(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);

		orthographicCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		orthographicCamera.update();

		pixmapHelper1 = new PixmapHelper(pixmap, sprite, texture);

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
			
			bomb.soundHandle = bombFallingSound.play();

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
				bombFallingSound.stop(bomb.soundHandle);
				bombsToDelete.add(bomb);
				bombExplosionSound.play();
				System.out.println("removing bomb");
			}
		}

		bombs.removeAll(bombsToDelete);
		bombsToDelete.clear();
		
		pixmapHelper1.updateTexture();

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
		bombFallingSound.dispose();
		bombExplosionSound.dispose();
	}

}