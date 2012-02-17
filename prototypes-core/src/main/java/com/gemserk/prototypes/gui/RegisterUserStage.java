package com.gemserk.prototypes.gui;

import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
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
import com.gemserk.animation4j.gdx.scenes.scene2d.Scene2dConverters;
import com.gemserk.animation4j.interpolator.function.InterpolationFunctions;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.highscores.client.User;
import com.gemserk.highscores.gui.RegisterUserListener;

public class RegisterUserStage extends Stage {

	static class Texts {

		static String title = "Register user";

		static String labelUsername = "Username";
		static String labelName = "Name";
		static String labelPassword = "Password";
		static String labelConfirmPassword = "Confirm password";

		static String buttonSubmit = "Submit";
		static String buttonCancel = "Cancel";
		
		static String errorUsernameEmpty = "Error: username can't be empty";
		static String errorNameEmpty = "Error: name can't be empty";
		
		static String errorPasswordEnoughCharacters = "Error: passwords must have 4 or more characters";
		static String errorPasswordsDontMatch = "Error: passwords don't match";
		
		static String messageSubmittingData = "Please wait...";

	}

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

	class CancelButtonClickListener implements ClickListener {
		@Override
		public void click(Actor actor, float x, float y) {
			registerUserListener.cancelled();
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
				usernameErrorLabel.setText(Texts.errorUsernameEmpty);
				lastErrorActor = usernameTextField;
				localError = true;
			}

			if (name.trim().length() == 0) {
				nameErrorLabel.setText(Texts.errorNameEmpty);
				lastErrorActor = nameTextField;
				localError = true;
			}

			if (password.trim().length() < 4) {
				passwordErrorLabel.setText(Texts.errorPasswordEnoughCharacters);
				lastErrorActor = passwordTextField;
				localError = true;
			} else if (!password.equals(confirmPassword)) {
				passwordErrorLabel.setText(Texts.errorPasswordsDontMatch);
				lastErrorActor = confirmPasswordTextField;
				localError = true;
			}

			if (lastErrorActor != null)
				setKeyboardFocus(lastErrorActor);

			// validate user with server...

			if (!localError)
				window.action(new RegisterUserAction());
		}
	}

	class RegisterUserAction extends Action {

		private Actor actor;
		private float time;

		public RegisterUserAction() {
			window.touchable = false;
			passwordErrorLabel.setColor(0f, 1f, 0f, 1f);
			passwordErrorLabel.setText(Texts.messageSubmittingData);
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

				registerUserListener.accepted(new User("a", "b", "c", false));
			}

		}
	}

	Window window;
	FlickScrollPane scrollPane;

	CustomTextField passwordTextField;
	CustomTextField confirmPasswordTextField;
	CustomTextField nameTextField;
	CustomTextField usernameTextField;

	TextButton cancelButton;
	TextButton submitButton;

	Label passwordErrorLabel;
	Label usernameErrorLabel;
	Label nameErrorLabel;

	RegisterUserListener registerUserListener;

	Transition<?> centerOnFocusedActorTransition;

	SubmitButtonClickListener submitButtonClickListener = new SubmitButtonClickListener();
	CancelButtonClickListener cancelButtonClickListener = new CancelButtonClickListener();

	public RegisterUserStage(Skin skin, RegisterUserListener registerUserListener, float width, float height, boolean stretch) {
		super(width, height, stretch);

		this.registerUserListener = registerUserListener;

		window = new Window(Texts.title, skin.getStyle(WindowStyle.class), "window");

		window.setMovable(false);

		TextFieldStyle textFieldStyle = skin.getStyle(TextFieldStyle.class);

		usernameTextField = new CustomTextField("", textFieldStyle);
		usernameTextField.setTextFieldFilter(usernameTextFieldFilter);

		usernameErrorLabel = new Label(skin);
		usernameErrorLabel.setColor(1f, 0f, 0f, 1f);

		nameTextField = new CustomTextField("", textFieldStyle);
		nameTextField.setTextFieldFilter(nameTextFieldFilter);

		nameErrorLabel = new Label(skin);
		nameErrorLabel.setColor(1f, 0f, 0f, 1f);

		passwordTextField = new CustomTextField("", textFieldStyle);
		passwordTextField.setPasswordMode(true);

		confirmPasswordTextField = new CustomTextField("", textFieldStyle);
		confirmPasswordTextField.setPasswordMode(true);

		passwordErrorLabel = new Label(skin);
		passwordErrorLabel.setColor(1f, 0f, 0f, 1f);

		submitButton = new TextButton(Texts.buttonSubmit, skin);
		cancelButton = new TextButton(Texts.buttonCancel, skin);

		submitButton.setClickListener(submitButtonClickListener);
		cancelButton.setClickListener(cancelButtonClickListener);

		window.defaults().spaceBottom(10);

		window.width = Gdx.graphics.getWidth() * 0.95f;
		window.height = Gdx.graphics.getHeight() * 0.95f;
		window.x = Gdx.graphics.getWidth() * 0.5f - window.width * 0.5f;
		window.y = Gdx.graphics.getHeight() * 0.5f - window.height * 0.5f;

		window.row().fill().expandX();
		window.add(new Label(Texts.labelUsername, skin)).align(Align.RIGHT).fill(0f, 0f).padRight(20);
		window.add(usernameTextField).align(Align.LEFT).fill(0f, 0f);
		window.row();
		window.add(usernameErrorLabel).align(Align.CENTER).colspan(2);
		window.row();
		window.add(new Label(Texts.labelName, skin)).align(Align.RIGHT).padRight(20);
		window.add(nameTextField).align(Align.LEFT).fill(0f, 0f);
		window.row();
		window.add(nameErrorLabel).align(Align.CENTER).colspan(2);
		window.row();
		window.add(new Label(Texts.labelPassword, skin)).align(Align.RIGHT).padRight(20);
		window.add(passwordTextField).align(Align.LEFT).fill(0f, 0f);
		window.row();
		window.add(new Label(Texts.labelConfirmPassword, skin)).align(Align.RIGHT).padRight(20);
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

		addActor(scrollPane);
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (centerOnFocusedActorTransition != null) {
			centerOnFocusedActorTransition.update(delta);
			if (centerOnFocusedActorTransition.isFinished())
				centerOnFocusedActorTransition = null;
			return;
		}

		Actor focusedActor = getKeyboardFocus();

		if (focusedActor == null) {

			centerOnFocusedActorTransition = Transitions.transition(scrollPane, Scene2dConverters.actorPositionTypeConverter) //
					.end(0.25f, scrollPane.x, Gdx.graphics.getHeight() * 0.5f - scrollPane.height * 0.5f) //
					.functions(InterpolationFunctions.linear(), InterpolationFunctions.easeIn()).build();

			return;
		}

		float desiredY = Gdx.graphics.getHeight() * 0.75f;

		if (focusedActor.y < desiredY) {
			float diff = Math.abs(desiredY - focusedActor.y);

			float finalY = Gdx.graphics.getHeight() * 0.5f - scrollPane.height * 0.5f + diff + 1;

			if (finalY == scrollPane.y)
				return;

			centerOnFocusedActorTransition = Transitions.transition(scrollPane, Scene2dConverters.actorPositionTypeConverter) //
					.end(0.25f, scrollPane.x, finalY) //
					.functions(InterpolationFunctions.linear(), InterpolationFunctions.easeIn()).build();

		} else {
			float finalY = Gdx.graphics.getHeight() * 0.5f - scrollPane.height * 0.5f;

			if (finalY == scrollPane.y)
				return;

			centerOnFocusedActorTransition = Transitions.transition(scrollPane, Scene2dConverters.actorPositionTypeConverter) //
					.end(0.25f, scrollPane.x, finalY) //
					.functions(InterpolationFunctions.linear(), InterpolationFunctions.easeIn()).build();

		}

	}

}
