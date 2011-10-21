package com.gemserk.prototypes.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.ImmediateModeRendererUtils;
import com.gemserk.prototypes.pixmap.PixmapHelper;

public class PixmapToTexturePrototype extends GameStateImpl {

	private GL10 gl;
	private SpriteBatch spriteBatch;
	private OrthographicCamera orthographicCamera;

	private PixmapHelper terrain1;
	private PixmapHelper terrain2;

	private final Vector2 position = new Vector2();

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		orthographicCamera = new OrthographicCamera();

		terrain1 = new PixmapHelper(new Pixmap(Gdx.files.internal("pixmap/performance/level01-0.png")));
		terrain2 = new PixmapHelper(new Pixmap(Gdx.files.internal("pixmap/performance/level01-1.png")));

		// terrain.updateTexture();

		// SpriteUtils.centerOn(terrain1.sprite, 256, Gdx.graphics.getHeight() * 0.55f);
		// terrain1.sprite.setOrigin(terrain1.sprite.getWidth() * 0.5f, terrain1.sprite.getHeight() * 0.5f);

		terrain1.sprite.setOrigin(terrain1.sprite.getWidth() * 0.5f, terrain1.sprite.getHeight() * 0.5f);
		terrain1.sprite.setSize(512, 512);
		terrain1.sprite.setPosition(256 - terrain1.sprite.getOriginX(), 256 - terrain1.sprite.getOriginY());

		// SpriteUtils.centerOn(terrain2.sprite, 512 + 256, Gdx.graphics.getHeight() * 0.55f);
		// terrain2.sprite.setOrigin(terrain2.sprite.getWidth() * 0.5f, terrain2.sprite.getHeight() * 0.5f);

		terrain2.sprite.setOrigin(terrain2.sprite.getWidth() * 0.5f, terrain2.sprite.getHeight() * 0.5f);
		terrain2.sprite.setSize(512, 512);
		terrain2.sprite.setPosition(512 + 256 - terrain2.sprite.getOriginX(), 256 - terrain2.sprite.getOriginY());

		// sprite.setOrigin(spatial.getWidth() * center.x, spatial.getHeight() * center.y);
		// sprite.setSize(spatial.getWidth(), spatial.getHeight());
		// sprite.setPosition(newX - sprite.getOriginX(), newY - sprite.getOriginY());

		orthographicCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		orthographicCamera.update();

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

	}

	@Override
	public void update() {
		super.update();

		int x = Gdx.input.getX();
		int y = Gdx.graphics.getHeight() - Gdx.input.getY();

		if (!Gdx.input.justTouched())
			return;

		terrain1.project(position, x, y);
		terrain1.eraseCircle(position.x, position.y, 16f);

		terrain2.project(position, x, y);
		terrain2.eraseCircle(position.x, position.y, 16f);

		terrain1.updateTexture();
		terrain2.updateTexture();

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		orthographicCamera.apply(gl);

		spriteBatch.setProjectionMatrix(orthographicCamera.projection);
		spriteBatch.setTransformMatrix(orthographicCamera.view);

		spriteBatch.begin();
		terrain1.sprite.draw(spriteBatch);
		terrain2.sprite.draw(spriteBatch);
		spriteBatch.end();

		ImmediateModeRendererUtils.getProjectionMatrix().set(orthographicCamera.combined);
		ImmediateModeRendererUtils.drawRectangle(0, 0, 512, 512, Color.GREEN);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		terrain1.dispose();
	}

}