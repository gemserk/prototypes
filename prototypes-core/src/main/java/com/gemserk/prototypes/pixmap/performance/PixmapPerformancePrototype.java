package com.gemserk.prototypes.pixmap.performance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.prototypes.pixmap.PixmapHelper;

public class PixmapPerformancePrototype extends GameStateImpl {

	private GL10 gl;
	private SpriteBatch spriteBatch;
	private OrthographicCamera orthographicCamera;

	private final Color color = new Color();
	private final Vector2 position = new Vector2();
	
	private PixmapHelper terrain;
	private PixmapHelper circle;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		orthographicCamera = new OrthographicCamera();
		
		Pixmap pixmapTerrain = new Pixmap(Gdx.files.internal("pixmap/performance/level01-0.png"));
//		Pixmap pixmapCircle = new Pixmap(Gdx.files.internal("pixmap/performance/circle.png"));
		
		Pixmap pixmapCircle = new Pixmap(32, 32, Format.RGBA8888);
		
		Pixmap.setBlending(Blending.None);
		pixmapCircle.setColor(1f, 1f, 1f, 0f);
		pixmapCircle.fillRectangle(0, 0, 32, 32);
		Pixmap.setBlending(Blending.None);
		pixmapCircle.setColor(1f, 1f, 1f, 1f);
		pixmapCircle.fillCircle(16, 16, 8);
		
		terrain = new PixmapHelper(pixmapTerrain);
		circle = new PixmapHelper(pixmapCircle);

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
		int y = (Gdx.graphics.getHeight() - Gdx.input.getY());
		
//		terrain.project(position, x, y);
		
//		Pixmap.setBlending(Blending.SourceOver);
		Pixmap.setBlending(Blending.None);
		
		for (int i = 0; i < 200; i++) {
			
			x = MathUtils.random(0, 512);
			y = MathUtils.random(0, 512);
			
			terrain.pixmap.setColor(0f, 0f, 0f, 0f);
			terrain.pixmap.fillCircle(x, y, 8);
			
			// terrain.pixmap.drawPixmap(circle.pixmap, x, y, 0, 0, 32, 32);
		}
		
		terrain.updateTexture();
		
//		Gdx.gl.glBindTexture(GL10.GL_TEXTURE_2D, terrain.texture.getTextureObjectHandle());
//		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//		Gdx.gl.glTexSubImage2D(GL10.GL_TEXTURE_2D, 0, (int)position.x, (int)position.y, circle.pixmap.getWidth(), circle.pixmap.getHeight(), circle.pixmap.getGLFormat(),
//				circle.pixmap.getGLType(), circle.pixmap.getPixels());
		
//		terrain.texture.draw(circle.pixmap, (int)position.x, (int)position.y);

		

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		orthographicCamera.apply(gl);

		spriteBatch.setProjectionMatrix(orthographicCamera.projection);
		spriteBatch.setTransformMatrix(orthographicCamera.view);

		spriteBatch.begin();
		terrain.sprite.draw(spriteBatch);
		spriteBatch.end();
	}
	
	@Override
	public void dispose() {
		spriteBatch.dispose();
		terrain.dispose();
	}

}