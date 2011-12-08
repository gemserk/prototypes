package com.gemserk.prototypes.gui;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.gui.ButtonHandler;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.Control;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.gdx.gui.ImageButton;
import com.gemserk.commons.gdx.gui.animation4j.ControlPositionConverter;
import com.gemserk.commons.gdx.gui.animation4j.ImageButtonSizeConverter;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.componentsengine.input.ButtonMonitor;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class FocusedControlPrototype extends GameStateImpl {

	public static final ControlPositionConverter controlPositionConverter = new ControlPositionConverter();
	public static final ImageButtonSizeConverter imageButtonSizeConverter = new ImageButtonSizeConverter();
	
	public static class ResizeButtonHandler extends ButtonHandler {
		
		@Override
		public void onPressed(Control control) {
			ImageButton imageButton = (ImageButton) control;
			Synchronizers.transition(Transitions.transition(imageButton, imageButtonSizeConverter) //
					.end(0.05f, 256f * 0.9f, 128 * 0.9f) //
					.build());
		}

		@Override
		public void onReleased(Control control) {
			ImageButton imageButton = (ImageButton) control;
			Synchronizers.transition(Transitions.transition(imageButton, imageButtonSizeConverter) //
					.end(0.15f, 256f * 1f, 128f * 1f) //
					.build());
		}
	}

	private static class Gui {

		public static final String Screen = "Screen";
		public static final String ScreenBackground = "ScreenBackground";

		public static final String DialogWindow = "DialogWindow";
		public static final String DialogBackgroundImage = "DialogBackgroundImage";
		public static final String DialogText = "DialogText";

		public static final String ButtonOption1 = "ButtonOption1";
		public static final String ButtonOption2 = "ButtonOption2";
		public static final String ButtonOption3 = "ButtonOption3";

	}

	public class FocusMonitor {

		int focusedControlIndex;
		ArrayList<Control> controls;

		ButtonMonitor nextControlButtonMonitor;
		ButtonMonitor previousControlButtonMonitor;
		ButtonMonitor selectControlButtonMonitor;

		public FocusMonitor(Control... controls) {
			this.controls = new ArrayList<Control>();
			for (int i = 0; i < controls.length; i++)
				this.controls.add(controls[i]);
			focusedControlIndex = 0;

			nextControlButtonMonitor = LibgdxInputMappingBuilder.keyButtonMonitor(Gdx.input, Keys.DOWN);
			previousControlButtonMonitor = LibgdxInputMappingBuilder.keyButtonMonitor(Gdx.input, Keys.UP);
			selectControlButtonMonitor = LibgdxInputMappingBuilder.keyButtonMonitor(Gdx.input, Keys.ENTER);
		}

		public void update() {
			nextControlButtonMonitor.update();
			previousControlButtonMonitor.update();
			selectControlButtonMonitor.update();
			
			if (selectControlButtonMonitor.isPressed()) {
				Control focusedControl = controls.get(focusedControlIndex);
				ImageButton imageButton = (ImageButton) focusedControl;
				imageButton.getButtonHandler().onPressed(imageButton);
			}
			
			if (selectControlButtonMonitor.isReleased()) {
				Control focusedControl = controls.get(focusedControlIndex);
				ImageButton imageButton = (ImageButton) focusedControl;
				imageButton.getButtonHandler().onReleased(imageButton);
			}

			if (nextControlButtonMonitor.isReleased()) {
				focusedControlIndex++;
				if (focusedControlIndex >= controls.size())
					focusedControlIndex = 0;

				Control focusedControl = controls.get(focusedControlIndex);

				for (int i = 0; i < controls.size(); i++) {
					ImageButton imageButton = (ImageButton) controls.get(i);

					if (imageButton != focusedControl)
						Synchronizers.transition(Transitions.transition(imageButton, imageButtonSizeConverter) //
								.end(0.15f, 256f, 128f) //
								.build());
					else
						Synchronizers.transition(Transitions.transition(imageButton, imageButtonSizeConverter) //
								.end(0.15f, 280f, 140f) //
								.build());

				}

			}

			if (previousControlButtonMonitor.isReleased()) {
				focusedControlIndex--;
				if (focusedControlIndex < 0)
					focusedControlIndex = controls.size() - 1;

				Control focusedControl = controls.get(focusedControlIndex);

				for (int i = 0; i < controls.size(); i++) {
					ImageButton imageButton = (ImageButton) controls.get(i);

					if (imageButton != focusedControl)
						Synchronizers.transition(Transitions.transition(imageButton, imageButtonSizeConverter) //
								.end(0.15f, 256f, 128f) //
								.build());
					else
						Synchronizers.transition(Transitions.transition(imageButton, imageButtonSizeConverter) //
								.end(0.15f, 280f, 140f) //
								.build());

				}

			}

		}

	}

	private GL10 gl;
	private SpriteBatch spriteBatch;
	private boolean clickToShow;

	private Container screen;
	private ResourceManager<String> resourceManager;

	private FocusMonitor focusMonitor;
	private BitmapFont font1;
	private BitmapFont font2;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

		screen = new Container(Gui.Screen);

		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("BackgroundTexture", "gui/screen-background.png");
				sprite("BackgroundSprite", "BackgroundTexture", 0, 0, 800, 480);

				font("TextFont", "data/fonts/purisa-24.png", "data/fonts/purisa-24.fnt");

				texture("SquareButtonTexture", "gui/square-button.png");
				sprite("SquareButtonSprite", "SquareButtonTexture");
			}
		};

		Sprite screenBackgroundSprite = resourceManager.getResourceValue("BackgroundSprite");

		screen.add(GuiControls.imageButton(screenBackgroundSprite).id(Gui.ScreenBackground) //
				.position(0f, 0f) //
				.center(0f, 0f) //
				.size(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) //
				.color(1f, 1f, 1f, 1f) //
				.build());

		Container dialogWindow = new Container(Gui.DialogWindow);
		dialogWindow.setPosition(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);

		// BitmapFont bitmapFont = resourceManager.getResourceValue("TextFont");
		//
		// dialogWindow.add(GuiControls.label("Are you sure\n you want to Quit?").id(Gui.DialogText) //
		// .position(0f, 0f) //
		// .center(0.5f, 0.5f) //
		// .font(bitmapFont) //
		// .color(1f, 1f, 1f, 1f) //
		// .build());

		Sprite buttonSprite1 = resourceManager.getResourceValue("SquareButtonSprite");

		dialogWindow.add(GuiControls.imageButton(buttonSprite1).id(Gui.ButtonOption1) //
				.position(0f, Gdx.graphics.getHeight() * 0.25f) //
				.center(0.5f, 0.5f) //
				.color(1f, 1f, 1f, 1f) //
				.handler(new ResizeButtonHandler()) //
				.build());

		Sprite buttonSprite2 = resourceManager.getResourceValue("SquareButtonSprite");

		dialogWindow.add(GuiControls.imageButton(buttonSprite2).id(Gui.ButtonOption2) //
				.position(0f, 0f) //
				.center(0.5f, 0.5f) //
				.color(1f, 1f, 1f, 1f) //
				.handler(new ResizeButtonHandler()) //
				.build());

		Sprite buttonSprite3 = resourceManager.getResourceValue("SquareButtonSprite");

		dialogWindow.add(GuiControls.imageButton(buttonSprite3).id(Gui.ButtonOption3) //
				.position(0f, -Gdx.graphics.getHeight() * 0.25f) //
				.center(0.5f, 0.5f) //
				.color(1f, 1f, 1f, 1f) //
				.handler(new ResizeButtonHandler() {
					@Override
					public void onPressed(Control control) {
						super.onPressed(control);
						System.out.println("pressed option 3");
					}
					
					@Override
					public void onReleased(Control control) {
						super.onReleased(control);
						System.out.println("released option 3");
					}
				}) //
				.build());

		screen.add(dialogWindow);

		focusMonitor = new FocusMonitor(screen.findControl(Gui.ButtonOption1), screen.findControl(Gui.ButtonOption2), screen.findControl(Gui.ButtonOption3));

		clickToShow = true;
		
		font1 = resourceManager.getResourceValue("TextFont");
		font2 = resourceManager.getResourceValue("TextFont");
		
		font1.dispose();

	}

	@Override
	public void update() {
		super.update();
		Synchronizers.synchronize(getDelta());
		screen.update();
		focusMonitor.update();
	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		screen.draw(spriteBatch);
		font2.draw(spriteBatch, "HOLA", 50, 50);
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		resourceManager.unloadAll();
		super.dispose();
	}

}