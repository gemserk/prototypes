package com.gemserk.prototypes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;

public class Game extends com.gemserk.commons.gdx.Game {

	// private ResourceManager<String> resourceManager;
	private SpriteBatch spriteBatch;
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	@Override
	public void create() {
		spriteBatch = new SpriteBatch();
		Gdx.graphics.getGL10().glClearColor(0, 0, 0, 1);
	}

	@Override
	public void dispose() {
		super.dispose();
		spriteBatch.dispose();
	}

}
