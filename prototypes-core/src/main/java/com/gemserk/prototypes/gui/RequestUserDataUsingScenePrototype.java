package com.gemserk.prototypes.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.highscores.client.User;
import com.gemserk.highscores.gui.RegisterUserListener;
import com.gemserk.prototypes.Launcher;

public class RequestUserDataUsingScenePrototype extends GameStateImpl {

	Launcher launcher;

	private GL10 gl;

	private Stage stage;

	RegisterUserListener registerUserListener;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();
		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

		Skin skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"), new TextureAtlas(Gdx.files.internal("data/ui/uiskin.atlas")));
		
		registerUserListener = new RegisterUserListener() {

			@Override
			public void cancelled() {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						launcher.launcherGameState.getParameters().put("user", null);
						launcher.setGameState(launcher.launcherGameState, true);
					}
				});
			}

			@Override
			public void accepted(final User user) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						launcher.launcherGameState.getParameters().put("user", user);
						launcher.setGameState(launcher.launcherGameState, true);
					}
				});
			}
		};
		
		stage = new RegisterUserStage(skin, registerUserListener, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

		Gdx.input.setInputProcessor(stage);

		Gdx.graphics.getGL10().glClearColor(0, 0, 0, 1);
		
		
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
		Launcher.processGlobalInput = false;
	}

	@Override
	public void pause() {
		Launcher.processGlobalInput = true;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}