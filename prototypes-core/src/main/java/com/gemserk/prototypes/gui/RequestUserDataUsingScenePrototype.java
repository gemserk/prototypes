package com.gemserk.prototypes.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.CustomTextField;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.gemserk.commons.gdx.GameStateImpl;

public class RequestUserDataUsingScenePrototype extends GameStateImpl {

	private GL10 gl;

	private Stage stage;

	private SpriteBatch spriteBatch;

	private Window window;

	CustomTextField passwordTextField;
	CustomTextField confirmPasswordTextField;
	CustomTextField nameTextField;
	CustomTextField usernameTextField;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();
		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

		Skin skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"), Gdx.files.internal("data/ui/uiskin.png"));

		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

		window = new Window("Register user", skin.getStyle(WindowStyle.class), "window");

		window.setMovable(false);
		
		TextFieldStyle textFieldStyle = skin.getStyle(TextFieldStyle.class);

		passwordTextField = new CustomTextField("", textFieldStyle);
		passwordTextField.setPasswordMode(true);

		confirmPasswordTextField = new CustomTextField("", textFieldStyle);
		confirmPasswordTextField.setPasswordMode(true);

		nameTextField = new CustomTextField("", textFieldStyle);
		usernameTextField = new CustomTextField("", textFieldStyle);

		window.defaults().spaceBottom(10);

		window.width = Gdx.graphics.getWidth() * 0.85f;
		window.height = Gdx.graphics.getHeight() * 0.85f;
		window.x = Gdx.graphics.getWidth() * 0.5f - window.width * 0.5f;
		window.y = Gdx.graphics.getHeight() * 0.5f - window.height * 0.5f;

		window.row().fill().expandX();
		window.add(new Label("Username", skin)).align(Align.RIGHT).fill(0f, 0f).padRight(20);
		window.add(usernameTextField).align(Align.LEFT).fill(0f, 0f);
		window.row();
		window.add(new Label("Name", skin)).align(Align.RIGHT).padRight(20);
		window.add(nameTextField).align(Align.LEFT).fill(0f, 0f);
		window.row();
		window.add(new Label("Password", skin)).align(Align.RIGHT).padRight(20);
		window.add(passwordTextField).align(Align.LEFT).fill(0f, 0f);
		window.row();
		window.add(new Label("Confirm password", skin)).align(Align.RIGHT).padRight(20);
		window.add(confirmPasswordTextField).align(Align.LEFT).fill(0f, 0f);
		window.row();
		window.add(new TextButton("Submit", skin)).align(Align.CENTER).fill(0.5f, 0f);
		window.add(new TextButton("Cancel", skin)).align(Align.CENTER).fill(0.5f, 0f);

		stage.addActor(window);

		Gdx.input.setInputProcessor(stage);

		Gdx.graphics.getGL10().glClearColor(0, 0, 0, 1);

		spriteBatch = new SpriteBatch();
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
		Table.drawDebug(stage);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		super.dispose();
	}

}