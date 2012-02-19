package com.gemserk.prototypes.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.FlickScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.highscores.gui.RegisterUserListener;
import com.gemserk.prototypes.Launcher;

public class Scene2dChangelogPrototype extends GameStateImpl {

	Launcher launcher;

	private GL10 gl;

	private Stage stage;

	RegisterUserListener registerUserListener;

	class NotificationListener {

		public void optionSelected(int option) {

		}

	}

	Actor twoOptionsDialog(String[] texts, final NotificationListener notificationListener, String titleText, String firstOption, String secondOption, Skin skin) {
		Window window = new Window(titleText, skin);

		window.setMovable(false);

		TextButton firstOptionButton = new TextButton(firstOption, skin);
		TextButton secondOptionButton = new TextButton(secondOption, skin);

		firstOptionButton.setClickListener(new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				notificationListener.optionSelected(0);
			}
		});

		secondOptionButton.setClickListener(new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				notificationListener.optionSelected(1);
			}
		});

		window.defaults().spaceBottom(10);
		window.row().fill().expandX();

		for (int i = 0; i < texts.length; i++) {
			window.row().padLeft(20);
			Label label = new Label(texts[i], skin);
			window.add(label).align(Align.LEFT).colspan(2);
		}

		window.row().fill().expandX();
		window.add(firstOptionButton).align(Align.CENTER).padLeft(20).padRight(20).expandX();
		window.add(secondOptionButton).align(Align.CENTER).padLeft(20).padRight(20).expandX();

		FlickScrollPane scrollPane = new FlickScrollPane(window);

		scrollPane.width = Gdx.graphics.getWidth() * 0.95f;
		scrollPane.height = Gdx.graphics.getHeight() * 0.95f;
		scrollPane.x = Gdx.graphics.getWidth() * 0.5f - scrollPane.width * 0.5f;
		scrollPane.y = Gdx.graphics.getHeight() * 0.5f - scrollPane.height * 0.5f;

		return scrollPane;
	}

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

		stage.addActor(twoOptionsDialog(texts, new NotificationListener() {

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

		// Window window = new Window("New version available", skin);
		//
		// window.setMovable(false);
		//
		// TextButton yesButton = new TextButton("Update now", skin);
		// TextButton laterButton = new TextButton("Dismiss", skin);
		// // TextButton noButton = new TextButton("Never", skin);
		//
		// window.defaults().spaceBottom(10);
		// window.row().fill().expandX();
		//
		// for (int i = 0; i < texts.length; i++) {
		// window.row().padLeft(20);
		// Label label = new Label(texts[i], skin);
		// window.add(label).align(Align.LEFT).colspan(2);
		// }
		//
		// window.row().fill().expandX();
		// window.add(yesButton).align(Align.CENTER).padLeft(20).padRight(20).expandX();
		// window.add(laterButton).align(Align.CENTER).padLeft(20).padRight(20).expandX();
		//
		// FlickScrollPane scrollPane = new FlickScrollPane(window);
		//
		// scrollPane.width = Gdx.graphics.getWidth() * 0.95f;
		// scrollPane.height = Gdx.graphics.getHeight() * 0.95f;
		// scrollPane.x = Gdx.graphics.getWidth() * 0.5f - scrollPane.width * 0.5f;
		// scrollPane.y = Gdx.graphics.getHeight() * 0.5f - scrollPane.height * 0.5f;
		//
		// stage.addActor(scrollPane);

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