package com.gemserk.prototypes.fonts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class RenderScaledFontsTest extends GameStateImpl {

	private GL10 gl;

	ResourceManager<String> resourceManager;

	Injector injector;

	private Libgdx2dCameraTransformImpl uiCamera;

	private SpriteBatch spriteBatch;

	private BitmapFont testFont;
	private float zoom = 1f;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				font("TestFont", "data/fonts/purisa-24.png", "data/fonts/purisa-24.fnt", true);
			}
		};

		testFont = resourceManager.getResourceValue("TestFont");

		uiCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);

		injector = new InjectorImpl();
		injector.bind("resourceManager", resourceManager);

		spriteBatch = new SpriteBatch();

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorMouseLeftButton("increaseZoom");
				monitorMouseRightButton("decreaseZoom");
			}
		};

		zoom = 1f;
	}

	@Override
	public void update() {
		super.update();
		inputDevicesMonitor.update();

		if (inputDevicesMonitor.getButton("increaseZoom").isReleased()) {
			zoom *= 2f;
			uiCamera.zoom(zoom);
			testFont.setScale(1f / zoom);

			System.out.println("zoom: " + zoom);
			System.out.println(1f / zoom);
			System.out.println(testFont.getScaleX());
			System.out.println(testFont.getScaleY());
		}

		if (inputDevicesMonitor.getButton("decreaseZoom").isReleased()) {
			zoom /= 2f;
			uiCamera.zoom(zoom);
			testFont.setScale(1f / zoom);

			System.out.println("zoom: " + zoom);
			System.out.println(1f / zoom);
			System.out.println(testFont.getScaleX());
			System.out.println(testFont.getScaleY());
		}

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.setProjectionMatrix(uiCamera.getCombinedMatrix());
		spriteBatch.begin();
		SpriteBatchUtils.drawMultilineText(spriteBatch, testFont, "HELLO WORLD", 0f, 0f, 0.5f, 0.5f);
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
	}

}