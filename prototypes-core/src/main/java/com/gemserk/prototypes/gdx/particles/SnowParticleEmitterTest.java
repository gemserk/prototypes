package com.gemserk.prototypes.gdx.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class SnowParticleEmitterTest extends GameStateImpl {

	private GL10 gl;
	private SpriteBatch spriteBatch;

	Libgdx2dCamera worldCamera;
	ResourceManager<String> resourceManager;

	ParticleEmitter snowEmitter;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				particleEffect("SnowParticleEffect", "gdx/particles/SnowParticleEffect", "gdx/particles");
				particleEmitter("SnowEmitter", "SnowParticleEffect", "SnowEmitter", 1f / 64f);
			}
		};

		worldCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);

		worldCamera.move(Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.5f);
		worldCamera.zoom(64f);

		snowEmitter = resourceManager.getResourceValue("SnowEmitter");

		snowEmitter.setPosition(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		snowEmitter.start();

		// ParticleEmitterUtils.scaleEmitter(thrustParticleEmitter, 1f / 40f);

		// System.out.println(thrustParticleEmitter.getSpawnHeight().getLowMin());
		// System.out.println(thrustParticleEmitter.getSpawnWidth());

		// thrustParticleEmitter.update(2f);

		Gdx.graphics.getGL10().glClearColor(0, 0, 0, 1);
	}

	@Override
	public void update() {
		super.update();

		Vector2 position = new Vector2();

		int x = Gdx.input.getX();
		int y = (Gdx.graphics.getHeight() - Gdx.input.getY());

		position.x = x;
		position.y = y;

		worldCamera.unproject(position);
		
		 snowEmitter.setPosition(position.x, position.y);
		snowEmitter.update(getDelta());
	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		worldCamera.apply(spriteBatch);
		spriteBatch.begin();
		snowEmitter.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		resourceManager.unloadAll();
	}

}