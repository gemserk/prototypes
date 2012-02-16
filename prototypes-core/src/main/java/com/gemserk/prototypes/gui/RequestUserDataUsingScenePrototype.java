package com.gemserk.prototypes.gui;

import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.CustomTextField;
import com.badlogic.gdx.scenes.scene2d.ui.CustomTextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.FlickScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.gemserk.animation4j.gdx.scenes.scene2d.Scene2dConverters;
import com.gemserk.animation4j.interpolator.function.InterpolationFunctions;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.highscores.client.User;
import com.gemserk.highscores.gui.RequestUserListener;
import com.gemserk.prototypes.Launcher;

public class RequestUserDataUsingScenePrototype extends GameStateImpl {

	class CancelButtonClickListener implements ClickListener {
		@Override
		public void click(Actor actor, float x, float y) {
			requestUserListener.cancelled();
		}
	}

	class SubmitButtonClickListener implements ClickListener {
		@Override
		public void click(Actor actor, float x, float y) {
			boolean localError = false;

			usernameErrorLabel.setText("");
			passwordErrorLabel.setText("");
			nameErrorLabel.setText("");
			
			Actor lastErrorActor = null;

			String username = usernameTextField.getText();
			String name = nameTextField.getText();
			String password = passwordTextField.getText();
			String confirmPassword = confirmPasswordTextField.getText();

			if (username.trim().length() == 0) {
				usernameErrorLabel.setText("Error: username can't be empty");
				lastErrorActor = usernameTextField;
				localError = true;
			}

			if (name.trim().length() == 0) {
				nameErrorLabel.setText("Error: name can't be empty");
				lastErrorActor = nameTextField;
				localError = true;
			}

			if (password.trim().length() < 4) {
				passwordErrorLabel.setText("Error: passwords must have 4 or more characters");
				lastErrorActor = passwordTextField;
				localError = true;
			} else if (!password.equals(confirmPassword)) {
				passwordErrorLabel.setText("Error: passwords don't match");
				lastErrorActor = confirmPasswordTextField;
				localError = true;
			}
			
			if (lastErrorActor != null)
				stage.setKeyboardFocus(lastErrorActor);

			// validate user with server...

			if (!localError)
				window.action(new RegisterUserAction());
		}
	}

	private class RegisterUserAction extends Action {

		private Actor actor;
		private float time;

		public RegisterUserAction() {
			window.touchable = false;
			passwordErrorLabel.setColor(0f, 1f, 0f, 1f);
			passwordErrorLabel.setText("please wait...");
		}

		@Override
		public void setTarget(Actor actor) {
			this.actor = actor;
			this.time = 5f;
		}

		@Override
		public boolean isDone() {
			return time < 0f;
		}

		@Override
		public Actor getTarget() {
			return actor;
		}

		@Override
		public Action copy() {
			return null;
		}

		@Override
		public void act(float delta) {
			time -= delta;

			if (isDone()) {
				window.touchable = true;

				passwordErrorLabel.setText("");
				passwordErrorLabel.setColor(1f, 0f, 0f, 1f);

				usernameErrorLabel.setText("Error: username already used");

				requestUserListener.accepted(new User("a", "b", "c", false));
			}

		}
	}

	Launcher launcher;

	private GL10 gl;

	private Stage stage;

	private Window window;

	CustomTextField passwordTextField;
	CustomTextField confirmPasswordTextField;
	CustomTextField nameTextField;
	CustomTextField usernameTextField;

	private TextButton cancelButton;
	private TextButton submitButton;

	private Label passwordErrorLabel;
	private Label usernameErrorLabel;

	TextFieldFilter usernameTextFieldFilter = new TextFieldFilter() {

		String rexexp = "^[a-zA-Z0-9]+";

		@Override
		public boolean acceptChar(CustomTextField customTextField, char key) {
			if (key == '\t')
				return true;
			if (!Pattern.matches(rexexp, Character.toString(key)))
				return false;
			return customTextField.getText().length() < 15;
		}
	};

	TextFieldFilter nameTextFieldFilter = new TextFieldFilter() {
		@Override
		public boolean acceptChar(CustomTextField customTextField, char key) {
			return customTextField.getText().length() < 30;
		}
	};

	Label nameErrorLabel;

	FlickScrollPane scrollPane;
	
	private RequestUserListener requestUserListener;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();
		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

		Skin skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"), Gdx.files.internal("data/ui/uiskin.png"));
		
		requestUserListener = new RequestUserListener() {

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

		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

		window = new Window("Register user", skin.getStyle(WindowStyle.class), "window");

		window.setMovable(false);

		TextFieldStyle textFieldStyle = skin.getStyle(TextFieldStyle.class);

		usernameTextField = new CustomTextField("", textFieldStyle);
		usernameTextField.setTextFieldFilter(usernameTextFieldFilter);
		// usernameTextField.setOnscreenKeyboard(testKeyboard);

		usernameErrorLabel = new Label(skin);
		usernameErrorLabel.setColor(1f, 0f, 0f, 1f);

		nameTextField = new CustomTextField("", textFieldStyle);
		nameTextField.setTextFieldFilter(nameTextFieldFilter);
		// nameTextField.setOnscreenKeyboard(testKeyboard);

		nameErrorLabel = new Label(skin);
		nameErrorLabel.setColor(1f, 0f, 0f, 1f);

		passwordTextField = new CustomTextField("", textFieldStyle);
		passwordTextField.setPasswordMode(true);
		// passwordTextField.setOnscreenKeyboard(testKeyboard);

		confirmPasswordTextField = new CustomTextField("", textFieldStyle);
		confirmPasswordTextField.setPasswordMode(true);
		// confirmPasswordTextField.setOnscreenKeyboard(testKeyboard);

		passwordErrorLabel = new Label(skin);
		passwordErrorLabel.setColor(1f, 0f, 0f, 1f);

		submitButton = new TextButton("Submit", skin);
		cancelButton = new TextButton("Cancel", skin);

		submitButton.setClickListener(new SubmitButtonClickListener());
		cancelButton.setClickListener(new CancelButtonClickListener());

		window.defaults().spaceBottom(10);

		window.width = Gdx.graphics.getWidth() * 0.95f;
		window.height = Gdx.graphics.getHeight() * 0.95f;
		window.x = Gdx.graphics.getWidth() * 0.5f - window.width * 0.5f;
		window.y = Gdx.graphics.getHeight() * 0.5f - window.height * 0.5f;

		window.row().fill().expandX();
		window.add(new Label("Username", skin)).align(Align.RIGHT).fill(0f, 0f).padRight(20);
		window.add(usernameTextField).align(Align.LEFT).fill(0f, 0f);
		window.row();
		window.add(usernameErrorLabel).align(Align.CENTER).colspan(2);
		window.row();
		window.add(new Label("Name", skin)).align(Align.RIGHT).padRight(20);
		window.add(nameTextField).align(Align.LEFT).fill(0f, 0f);
		window.row();
		window.add(nameErrorLabel).align(Align.CENTER).colspan(2);
		window.row();
		window.add(new Label("Password", skin)).align(Align.RIGHT).padRight(20);
		window.add(passwordTextField).align(Align.LEFT).fill(0f, 0f);
		window.row();
		window.add(new Label("Confirm password", skin)).align(Align.RIGHT).padRight(20);
		window.add(confirmPasswordTextField).align(Align.LEFT).fill(0f, 0f);
		window.row();
		window.add(passwordErrorLabel).align(Align.CENTER).colspan(2);
		window.row();
		window.add(submitButton).align(Align.CENTER).fill(0.5f, 0f);
		window.add(cancelButton).align(Align.CENTER).fill(0.5f, 0f);

		scrollPane = new FlickScrollPane(window);

		scrollPane.width = Gdx.graphics.getWidth() * 0.95f;
		scrollPane.height = Gdx.graphics.getHeight() * 0.95f;
		scrollPane.x = Gdx.graphics.getWidth() * 0.5f - scrollPane.width * 0.5f;
		scrollPane.y = Gdx.graphics.getHeight() * 0.5f - scrollPane.height * 0.5f;

		// stage.addActor(window);
		stage.addActor(scrollPane);

		Gdx.input.setInputProcessor(stage);

		Gdx.graphics.getGL10().glClearColor(0, 0, 0, 1);
		
		
	}

	Transition scrollPanePositionTransition;

	@Override
	public void update() {
		super.update();
		stage.act(getDelta());

		if (scrollPanePositionTransition != null) {
			scrollPanePositionTransition.update(getDelta());
			if (scrollPanePositionTransition.isFinished())
				scrollPanePositionTransition = null;
			return;
		}
		
		Actor focusedActor = stage.getKeyboardFocus();
		
		if (focusedActor == null) {
			
			scrollPanePositionTransition = Transitions.transition(scrollPane, Scene2dConverters.actorPositionTypeConverter) //
					.end(0.25f, scrollPane.x, Gdx.graphics.getHeight() * 0.5f - scrollPane.height * 0.5f) //
					.functions(InterpolationFunctions.linear(), InterpolationFunctions.easeIn())
					.build();
			
			return;
		}

		float desiredY = Gdx.graphics.getHeight() * 0.75f;

		if (focusedActor.y < desiredY) {
			float diff = Math.abs(desiredY - focusedActor.y);

			float finalY = Gdx.graphics.getHeight() * 0.5f - scrollPane.height * 0.5f + diff + 1;

			if (finalY == scrollPane.y)
				return;

			scrollPanePositionTransition = Transitions.transition(scrollPane, Scene2dConverters.actorPositionTypeConverter) //
					.end(0.25f, scrollPane.x, finalY) //
					.functions(InterpolationFunctions.linear(), InterpolationFunctions.easeIn())
					.build();

		} else {
			float finalY = Gdx.graphics.getHeight() * 0.5f - scrollPane.height * 0.5f;
			
			if (finalY == scrollPane.y)
				return;

			scrollPanePositionTransition = Transitions.transition(scrollPane, Scene2dConverters.actorPositionTypeConverter) //
					.end(0.25f, scrollPane.x, finalY) //
					.functions(InterpolationFunctions.linear(), InterpolationFunctions.easeIn())
					.build();

		}

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.draw();
		Table.drawDebug(stage);
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