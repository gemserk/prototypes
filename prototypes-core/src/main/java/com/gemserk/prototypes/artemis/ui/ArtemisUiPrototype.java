package com.gemserk.prototypes.artemis.ui;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.systems.RenderLayerSpriteBatchImpl;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class ArtemisUiPrototype extends GameStateImpl {

	private GL10 gl;

	ResourceManager<String> resourceManager;

	WorldWrapper worldWrapper;
	Injector injector;

	private Libgdx2dCameraTransformImpl uiCamera;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				
			}
		};

		RenderLayers renderLayers = new RenderLayers();

		uiCamera = new Libgdx2dCameraTransformImpl();

		renderLayers.add("Ui", new RenderLayerSpriteBatchImpl(-1000, 1000, uiCamera));

		injector = new InjectorImpl();
		injector.bind("resourceManager", resourceManager);

		worldWrapper = new WorldWrapper(new World());
		

		worldWrapper.init();
	}

	@Override
	public void update() {
		super.update();
		worldWrapper.update(getDeltaInMs());
	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		worldWrapper.render();
	}

	@Override
	public void dispose() {
		worldWrapper.dispose();
		resourceManager.unloadAll();
	}

}