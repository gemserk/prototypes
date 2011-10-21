package com.gemserk.prototypes.pixmap.reload;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.prototypes.pixmap.PixmapHelper;

public class ReloadPixmapTestGameState extends GameStateImpl {

	private GL10 gl;
	private SpriteBatch spriteBatch;
	private OrthographicCamera orthographicCamera;

	PixmapHelper pixmapHelper;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		orthographicCamera = new OrthographicCamera();

		Pixmap pixmap = new Pixmap(Gdx.files.internal("pixmap/reload/platform-01_rgba8_l.png"));
		pixmapHelper = new PixmapHelper(pixmap);

		pixmapHelper.eraseCircle(20, 20, 100);

		Sprite sprite = pixmapHelper.sprite;

		SpriteUtils.resize(sprite, pixmap.getWidth() * 1f);
		SpriteUtils.centerOn(sprite, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.25f);

		sprite.setOrigin(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);

		orthographicCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		orthographicCamera.update();

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 0f);
	}
	
	@Override
	public void update() {
		super.update();
		pixmapHelper.updateTexture();
	}
	
	@Override
	public void resume() {
		pixmapHelper.reload();
	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		orthographicCamera.apply(gl);

		spriteBatch.setProjectionMatrix(orthographicCamera.projection);
		spriteBatch.setTransformMatrix(orthographicCamera.view);

		spriteBatch.begin();
		pixmapHelper.sprite.draw(spriteBatch);
		spriteBatch.end();
	}

}