package com.gemserk.prototypes.render.culling;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;

public class CameraFrustumCullingPrototype extends GameStateImpl {

	private GL10 gl;

	private SpriteBatch spriteBatch;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{

			}
		};

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 1f, 0f);

		Libgdx2dCamera libgdx2dCamera = new Libgdx2dCameraTransformImpl(400f, 240f);
		libgdx2dCamera.zoom(1f);

		Rectangle frustum = new Rectangle();

		libgdx2dCamera.getFrustum(frustum);
		System.out.println(frustum);

		libgdx2dCamera.zoom(40f);
		libgdx2dCamera.move(2f, 2f);
		
		libgdx2dCamera.getFrustum(frustum);
		System.out.println(frustum);

	}

	@Override
	public void update() {
		super.update();

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

}