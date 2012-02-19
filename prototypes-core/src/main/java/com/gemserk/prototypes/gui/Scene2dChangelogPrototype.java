package com.gemserk.prototypes.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.FlickScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.highscores.gui.RegisterUserListener;
import com.gemserk.prototypes.Launcher;
import com.gemserk.prototypes.gui.RegisterUserStage.Texts;

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
		
		String[] texts = {"Changes version 1.3", // 
				"    - Removed OpenFeint, changed to use our highscores server again.", //
				"    - Added new dialogs to notify when there is a new version available.", //
				"    - Fixed some bugs.", //
				};

		Gdx.input.setInputProcessor(stage);
		
		Window window = new Window(Texts.title, skin.getStyle(WindowStyle.class), "window");

		window.setMovable(false);

		TextButton yesButton = new TextButton("Yes", skin);
		TextButton laterButton = new TextButton("Later", skin);
		TextButton noButton = new TextButton("Never", skin);

		window.defaults().spaceBottom(10);

		window.width = Gdx.graphics.getWidth() * 0.95f;
		window.height = Gdx.graphics.getHeight() * 0.95f;
		window.x = Gdx.graphics.getWidth() * 0.5f - window.width * 0.5f;
		window.y = Gdx.graphics.getHeight() * 0.5f - window.height * 0.5f;

		window.row().fill().expandX();
	
		for (int i = 0; i < texts.length; i++) {
			window.row().padLeft(20);
			window.add(new Label(texts[i], skin)).align(Align.LEFT).colspan(3);
		}

		window.row().padLeft(20).padRight(20).padTop(20).padBottom(20);
		window.add(new Label("Do you want to download latest version?", skin)).align(Align.CENTER).colspan(3);

		window.row().fill().expandX();
		window.add(yesButton).align(Align.CENTER).padLeft(20).padRight(20).expandX();
		window.add(laterButton).align(Align.CENTER).padLeft(20).padRight(20).expandX();
		window.add(noButton).align(Align.CENTER).padLeft(20).padRight(20).expandX();

		FlickScrollPane scrollPane = new FlickScrollPane(window);

		scrollPane.width = Gdx.graphics.getWidth() * 0.95f;
		scrollPane.height = Gdx.graphics.getHeight() * 0.95f;
		scrollPane.x = Gdx.graphics.getWidth() * 0.5f - scrollPane.width * 0.5f;
		scrollPane.y = Gdx.graphics.getHeight() * 0.5f - scrollPane.height * 0.5f;

		stage.addActor(scrollPane);

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