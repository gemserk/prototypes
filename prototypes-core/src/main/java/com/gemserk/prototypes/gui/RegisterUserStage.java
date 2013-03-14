package com.gemserk.prototypes.gui;

import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
		public boolean acceptChar(TextField customTextField, char key) {
			if (key == '\t')
				return true;
			if (!Pattern.matches(rexexp, Character.toString(key)))
				return false;
			return customTextField.getText().length() < 15;
		}
	};

	TextFieldFilter nameTextFieldFilter = new TextFieldFilter() {
		@Override
		public boolean acceptChar(TextField customTextField, char key) {
			return customTextField.getText().length() < 30;
		}
	};

	class CancelButtonClickListener extends ClickListener {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			registerUserListener.cancelled();
		}
	}

	class SubmitButtonClickListener extends ClickListener {
		@Override
		public void clicked(InputEvent event, float x, float y) {
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
				window.addAction(new RegisterUserAction());
		}
	}

	class RegisterUserAction extends Action {

		private float time;

		public RegisterUserAction() {
			window.setTouchable(Touchable.disabled);
			passwordErrorLabel.setColor(0f, 1f, 0f, 1f);
			passwordErrorLabel.setText(Texts.messageSubmittingData);
		}
		
		@Override
		public void setActor(Actor actor) {
			super.setActor(actor);
			this.time = 5f;
		}

		public boolean isDone() {
			return time < 0f;
		}

		@Override
		public boolean act(float delta) {
			time -= delta;

			if (isDone()) {
				window.setTouchable(Touchable.enabled);

				passwordErrorLabel.setText("");
				passwordErrorLabel.setColor(1f, 0f, 0f, 1f);

				usernameErrorLabel.setText("Error: username already used");

				registerUserListener.accepted(new User(1230L, "b", "c", false));
				
				return true;
			}
			
			return false;
		}
	}

	Window window;
	ScrollPane scrollPane;

	TextField passwordTextField;
	TextField confirmPasswordTextField;
	TextField nameTextField;
	TextField usernameTextField;

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

		window = new Window(Texts.title, skin);

		window.setMovable(false);

		usernameTextField = new TextField("", skin);
		usernameTextField.setTextFieldFilter(usernameTextFieldFilter);

		usernameErrorLabel = new Label("", skin);
		usernameErrorLabel.setColor(1f, 0f, 0f, 1f);

		nameTextField = new TextField("", skin);
		nameTextField.setTextFieldFilter(nameTextFieldFilter);

		nameErrorLabel = new Label("", skin);
		nameErrorLabel.setColor(1f, 0f, 0f, 1f);

		passwordTextField = new TextField("", skin);
		passwordTextField.setPasswordMode(true);
		passwordTextField.setPasswordCharacter('*');

		confirmPasswordTextField = new TextField("", skin);
		confirmPasswordTextField.setPasswordMode(true);
		confirmPasswordTextField.setPasswordCharacter('*');

		passwordErrorLabel = new Label("", skin);
		passwordErrorLabel.setColor(1f, 0f, 0f, 1f);

		submitButton = new TextButton(Texts.buttonSubmit, skin);
		cancelButton = new TextButton(Texts.buttonCancel, skin);

		submitButton.addListener(submitButtonClickListener);
		cancelButton.addListener(cancelButtonClickListener);

		window.defaults().spaceBottom(10);

		window.setWidth(Gdx.graphics.getWidth() * 0.95f);
		window.setHeight(Gdx.graphics.getHeight() * 0.95f);
		window.setX(Gdx.graphics.getWidth() * 0.5f - window.getWidth() * 0.5f);
		window.setY(Gdx.graphics.getHeight() * 0.5f - window.getHeight() * 0.5f);

		window.row().fill().expandX();
		window.add(new Label(Texts.labelUsername, skin)).align(Align.right).fill(0f, 0f).padRight(20);
		window.add(usernameTextField).align(Align.left).fill(0f, 0f);
		window.row();
		window.add(usernameErrorLabel).align(Align.center).colspan(2);
		window.row();
		window.add(new Label(Texts.labelName, skin)).align(Align.right).padRight(20);
		window.add(nameTextField).align(Align.left).fill(0f, 0f);
		window.row();
		window.add(nameErrorLabel).align(Align.center).colspan(2);
		window.row();
		window.add(new Label(Texts.labelPassword, skin)).align(Align.right).padRight(20);
		window.add(passwordTextField).align(Align.left).fill(0f, 0f);
		window.row();
		window.add(new Label(Texts.labelConfirmPassword, skin)).align(Align.right).padRight(20);
		window.add(confirmPasswordTextField).align(Align.left).fill(0f, 0f);
		window.row();
		window.add(passwordErrorLabel).align(Align.center).colspan(2);
		window.row();
		window.add(submitButton).align(Align.center).fill(0.5f, 0f);
		window.add(cancelButton).align(Align.center).fill(0.5f, 0f);

		scrollPane = new ScrollPane(window);

		scrollPane.setWidth(Gdx.graphics.getWidth() * 0.95f);
		scrollPane.setHeight(Gdx.graphics.getHeight() * 0.95f);
		scrollPane.setX(Gdx.graphics.getWidth() * 0.5f - scrollPane.getWidth() * 0.5f);
		scrollPane.setY(Gdx.graphics.getHeight() * 0.5f - scrollPane.getHeight() * 0.5f);

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
					.end(0.25f, scrollPane.getX(), Gdx.graphics.getHeight() * 0.5f - scrollPane.getHeight() * 0.5f) //
					.functions(InterpolationFunctions.linear(), InterpolationFunctions.easeIn()).build();

			return;
		}

		float desiredY = Gdx.graphics.getHeight() * 0.75f;

		if (focusedActor.getY() < desiredY) {
			float diff = Math.abs(desiredY - focusedActor.getY());

			float finalY = Gdx.graphics.getHeight() * 0.5f - scrollPane.getHeight() * 0.5f + diff + 1;

			if (finalY == scrollPane.getY())
				return;

			centerOnFocusedActorTransition = Transitions.transition(scrollPane, Scene2dConverters.actorPositionTypeConverter) //
					.end(0.25f, scrollPane.getX(), finalY) //
					.functions(InterpolationFunctions.linear(), InterpolationFunctions.easeIn()).build();

		} else {
			float finalY = Gdx.graphics.getHeight() * 0.5f - scrollPane.getHeight() * 0.5f;

			if (finalY == scrollPane.getY())
				return;

			centerOnFocusedActorTransition = Transitions.transition(scrollPane, Scene2dConverters.actorPositionTypeConverter) //
					.end(0.25f, scrollPane.getX(), finalY) //
					.functions(InterpolationFunctions.linear(), InterpolationFunctions.easeIn()).build();

		}

	}

}
