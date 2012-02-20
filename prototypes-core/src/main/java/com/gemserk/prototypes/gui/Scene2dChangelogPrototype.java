package com.gemserk.prototypes.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.scene2d.Actors;
import com.gemserk.commons.gdx.scene2d.Actors.DialogListener;
import com.gemserk.highscores.gui.RegisterUserListener;
import com.gemserk.prototypes.Launcher;

public class Scene2dChangelogPrototype extends GameStateImpl {

	Launcher launcher;

	private GL10 gl;

	private Stage stage;

	RegisterUserListener registerUserListener;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();
		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

		Skin skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"), Gdx.files.internal("data/ui/uiskin.png"));

		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

		String[] texts = { "Changes version 1.3", //
				"    - Removed OpenFeint, changed to use our highscores server again.", //
				"    - Added new dialogs to notify when there is a new version available.", //
				"    - Fixed some bugs.", //
		};

		Gdx.input.setInputProcessor(stage);

		stage.addActor(Actors.twoOptionsDialog(texts, new DialogListener() {

			@Override
			public void optionSelected(int option) {
				if (option == 0) {
					System.out.println("update game!!");
					launcher.transition(launcher.launcherGameState) //
							.leaveTime(0.25f) //
							.enterTime(0.5f) //
							.start();
				} else if (option == 1) {
					System.out.println("disable future notifications!!");
					launcher.transition(launcher.launcherGameState) //
							.leaveTime(0.25f) //
							.enterTime(0.5f) //
							.start();
				}
			}

		}, "New version available", "Update now", "Dismiss", skin));

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
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void dispose() {
		super.dispose();
	}

}