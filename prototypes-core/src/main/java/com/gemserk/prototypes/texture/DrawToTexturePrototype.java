package com.gemserk.prototypes.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.prototypes.pixmap.PixmapHelper;

public class DrawToTexturePrototype extends GameStateImpl {

	private GL10 gl;
	private SpriteBatch spriteBatch;
	private OrthographicCamera orthographicCamera;

	private PixmapHelper terrain;
	private PixmapHelper circle;

	private final Vector2 position = new Vector2();

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		orthographicCamera = new OrthographicCamera();

		Pixmap pixmapTerrain = new Pixmap(Gdx.files.internal("pixmap/performance/level01-0.png"));
		Pixmap pixmapCircle = new Pixmap(32, 32, Format.RGBA8888);

		Pixmap.setBlending(Blending.None);
		pixmapCircle.setColor(1f, 1f, 1f, 1f);
		pixmapCircle.fillCircle(16, 16, 16);

		terrain = new PixmapHelper(pixmapTerrain);
		circle = new PixmapHelper(pixmapCircle);

		terrain.pixmap.setColor(1f, 1f, 1f, 1f);
		terrain.pixmap.fillCircle(512 - 10, 256, 16);

		pixmapCircle.drawPixmap(terrain.pixmap, 0, 0, 512 - 10 - 16, 256 - 16, 32, 32);

		// terrain.updateTexture();

		Sprite sprite = terrain.sprite;

		SpriteUtils.centerOn(sprite, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.55f);

		sprite.setOrigin(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);

		orthographicCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		orthographicCamera.update();

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

	}

	@Override
	public void update() {
		super.update();

		int x = Gdx.input.getX();
		int y = Gdx.graphics.getHeight() - Gdx.input.getY();

		SpriteUtils.centerOn(circle.sprite, x, y);

		if (!Gdx.input.justTouched())
			return;

		terrain.project(position, x, y);

		x = Math.round(position.x);
		y = Math.round(position.y);

		terrain.pixmap.setColor(1f, 1f, 1f, 1f);
		terrain.pixmap.fillCircle(x, y, 15);

		int width = terrain.pixmap.getWidth();
		int height = terrain.pixmap.getHeight();

		int dstWidth = circle.pixmap.getWidth();
		int dstHeight = circle.pixmap.getHeight();

		x -= dstWidth / 2;
		y -= dstHeight / 2;

		if (x + dstWidth > width)
			x = width - dstWidth;
		else if (x < 0)
			x = 0;

		if (y + dstHeight > height)
			y = height - dstHeight;
		else if (y < 0) {
			y = 0;
		}

		circle.pixmap.drawPixmap(terrain.pixmap, 0, 0, x, y, dstWidth, dstHeight);

		Gdx.gl.glBindTexture(GL10.GL_TEXTURE_2D, terrain.texture.getTextureObjectHandle());
		Gdx.gl.glTexSubImage2D(GL10.GL_TEXTURE_2D, 0, x, y, dstWidth, dstHeight, //
				circle.pixmap.getGLFormat(), circle.pixmap.getGLType(), circle.pixmap.getPixels());

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		orthographicCamera.apply(gl);

		spriteBatch.setProjectionMatrix(orthographicCamera.projection);
		spriteBatch.setTransformMatrix(orthographicCamera.view);

		spriteBatch.begin();
		terrain.sprite.draw(spriteBatch);
		circle.sprite.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		terrain.dispose();
	}

}