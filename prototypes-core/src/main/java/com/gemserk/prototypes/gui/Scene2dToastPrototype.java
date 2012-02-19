package com.gemserk.prototypes.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.scene2d.Actors;
import com.gemserk.commons.utils.RandomUtils;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.highscores.gui.RegisterUserListener;
import com.gemserk.prototypes.Launcher;

public class Scene2dToastPrototype extends GameStateImpl {

	Launcher launcher;

	private GL10 gl;

	private Stage stage;

	RegisterUserListener registerUserListener;

	InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private Skin skin;
	
	String[] texts = { // 
			"This is a toast implementation using scene2d\nwith multiple lines.", // 
			"And this one is a random toast.", // 
			"Another random toast? nice huh?", //
			"Toast engine for the win!!", //
			"Some times Random sucks and you see the same toast\nmultiple times...", //
			"Multiline toasts are also\navailable by adding \\n to the\ntext, however I should learn how to use text wrap.", //
		};

	
	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();
		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

		skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"), Gdx.files.internal("data/ui/uiskin.png"));

		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false) {
			@Override
			public boolean touchUp(int x, int y, int pointer, int button) {
				addActor(Actors.topToast(RandomUtils.random(texts), 3f, skin));
				return super.touchUp(x, y, pointer, button);
			}
		};
		
		stage.addActor(Actors.topToast("This is a toast implementation using scene2d\nwith multiple lines.", 3f, skin));

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void update() {
		super.update();
		stage.act(getDelta());
	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.draw();
	}

	@Override
	public void dispose() {
		skin.dispose();
		stage.dispose();
		super.dispose();
	}

}