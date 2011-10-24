package com.gemserk.prototypes.pixmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.ImmediateModeRendererUtils;

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

		terrain1 = new PixmapHelper(new Pixmap(Gdx.files.internal("pixmap/border/texture.png")));
		terrain2 = new PixmapHelper(new Pixmap(Gdx.files.internal("pixmap/border/texture.png")));

		// terrain.updateTexture();

		// SpriteUtils.centerOn(terrain1.sprite, 256, Gdx.graphics.getHeight() * 0.55f);
		// terrain1.sprite.setOrigin(terrain1.sprite.getWidth() * 0.5f, terrain1.sprite.getHeight() * 0.5f);
		
		float terrainWidth = 512f * 0.1f;
		float terrainHeight = 512f * 0.1f;

		terrain1.sprite.setOrigin(terrainWidth * 0.5f, terrainHeight * 0.5f);
		terrain1.sprite.setSize(terrainWidth, terrainHeight);
		terrain1.sprite.setPosition(terrainWidth * 0.5f - terrain1.sprite.getOriginX(), terrainHeight * 0.5f - terrain1.sprite.getOriginY());

		// SpriteUtils.centerOn(terrain2.sprite, 512 + 256, Gdx.graphics.getHeight() * 0.55f);
		// terrain2.sprite.setOrigin(terrain2.sprite.getWidth() * 0.5f, terrain2.sprite.getHeight() * 0.5f);

		terrain2.sprite.setOrigin(terrainWidth * 0.5f, terrainHeight * 0.5f);
		terrain2.sprite.setSize(terrainWidth, terrainHeight);
		terrain2.sprite.setPosition(terrainWidth + terrainWidth * 0.5f - terrain2.sprite.getOriginX(), terrainHeight * 0.5f - terrain2.sprite.getOriginY());

		// sprite.setOrigin(spatial.getWidth() * center.x, spatial.getHeight() * center.y);
		// sprite.setSize(spatial.getWidth(), spatial.getHeight());
		// sprite.setPosition(newX - sprite.getOriginX(), newY - sprite.getOriginY());

		orthographicCamera.setToOrtho(false, Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.1f);
		orthographicCamera.update();

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

	}

	@Override
	public void update() {
		super.update();

		int x = Gdx.input.getX();
		int y = Gdx.graphics.getHeight() - Gdx.input.getY();

		if (!Gdx.input.isTouched())
			return;
		
		position.set(x,y).mul(0.1f);

		terrain1.project(position, position.x, position.y);
		terrain1.eraseCircle(position.x, position.y, 32f * 0.1f);

		position.set(x,y).mul(0.1f);
		
		terrain2.project(position, position.x, position.y);
		terrain2.eraseCircle(position.x, position.y, 32f * 0.1f);

		terrain1.update();
		terrain2.update();

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
		ImmediateModeRendererUtils.drawRectangle(0, 0, 512 * 0.1f, 512 * 0.1f, Color.GREEN);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		terrain1.dispose();
	}

}