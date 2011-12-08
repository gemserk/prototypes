package com.gemserk.prototypes.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.animation4j.interpolator.function.InterpolationFunctions;
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

public class DialogHideShowPrototype extends GameStateImpl {

	public static final ControlPositionConverter controlPositionConverter = new ControlPositionConverter();
	public static final ImageButtonSizeConverter imageButtonSizeConverter = new ImageButtonSizeConverter();

	public static class ResizeButtonHandler extends ButtonHandler {
		
		@Override
		public void onOver(Control control) {
			ImageButton imageButton = (ImageButton) control;
			Synchronizers.transition(Transitions.transition(imageButton, imageButtonSizeConverter) //
					.end(0.15f, 108f, 108f) //
					.build());
		}

		@Override
		public void onLeave(Control control) {
			ImageButton imageButton = (ImageButton) control;
			Synchronizers.transition(Transitions.transition(imageButton, imageButtonSizeConverter) //
					.end(0.15f, 96f, 96f) //
					.build());
		}

		@Override
		public void onPressed(Control control) {
			ImageButton imageButton = (ImageButton) control;
			Synchronizers.transition(Transitions.transition(imageButton, imageButtonSizeConverter) //
					.end(0.05f, 84f, 84f) //
					.build());
		}

		@Override
		public void onReleased(Control control) {
			ImageButton imageButton = (ImageButton) control;
			Synchronizers.transition(Transitions.transition(imageButton, imageButtonSizeConverter) //
					.end(0.15f, 96f, 96f) //
					.build());
		}
	}

	private static class Gui {

		public static final String Screen = "Screen";
		public static final String ScreenBackground = "ScreenBackground";

		public static final String DialogWindow = "DialogWindow";
		public static final String DialogBackgroundImage = "DialogBackgroundImage";
		public static final String DialogText = "DialogText";

		public static final String ButtonOk = "ButtonOk";
		public static final String ButtonCross = "ButtonCross";

	}

	private GL10 gl;
	private SpriteBatch spriteBatch;
	private boolean clickToShow;

	private Container screen;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

		screen = new Container(Gui.Screen);

		Sprite screenBackgroundSprite = new Sprite(new Texture(Gdx.files.internal("gui/screen-background.png")), 0, 0, 800, 480);

		screen.add(GuiControls.imageButton(screenBackgroundSprite).id(Gui.ScreenBackground) //
				.position(0f, 0f) //
				.center(0f, 0f) //
				.size(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) //
				.color(1f, 1f, 1f, 1f) //
				.build());

		Container dialogWindow = new Container(Gui.DialogWindow);
		dialogWindow.setPosition(0, -500f);

		Sprite dialogWindowSprite = new Sprite(new Texture(Gdx.files.internal("gui/gui-menu.png")));

		dialogWindow.add(GuiControls.imageButton(dialogWindowSprite).id(Gui.DialogBackgroundImage) //
				.position(0, 0) //
				.center(0.5f, 0.5f) //
				.color(1f, 1f, 1f, 1f) //
				.build());
		
		BitmapFont bitmapFont = new BitmapFont(Gdx.files.internal("data/fonts/purisa-24.fnt"), Gdx.files.internal("data/fonts/purisa-24.png"), false);
		
		dialogWindow.add(GuiControls.label("Are you sure\n you want to Quit?").id(Gui.DialogText) //
				.position(0f, 0f) //
				.center(0.5f, 0.75f) //
				.font(bitmapFont) //
				.color(1f, 1f, 1f, 1f) //
				.build());

		Texture buttonTickTexture = new Texture(Gdx.files.internal("gui/button-tick.png"));
		buttonTickTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		Sprite buttonTickSprite = new Sprite(buttonTickTexture);

		dialogWindow.add(GuiControls.imageButton(buttonTickSprite).id(Gui.ButtonOk) //
				.position(-85f, -85f) //
				.center(0.5f, 0.5f) //
				.size(96f, 96f) //
				.color(1f, 1f, 1f, 1f) //
				.handler(new ResizeButtonHandler() {
					@Override
					public void onReleased(Control control) {
						super.onReleased(control);
						hideDialog();
					}
				}).build());

		Texture buttonCrossTexture = new Texture(Gdx.files.internal("gui/button-cross.png"));
		buttonCrossTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		Sprite buttonCrossSprite = new Sprite(buttonCrossTexture);

		dialogWindow.add(GuiControls.imageButton(buttonCrossSprite).id(Gui.ButtonCross) //
				.position(85f, -85f) //
				.center(0.5f, 0.5f) //
				.size(96f, 96f) //
				.color(1f, 1f, 1f, 1f) //
				.handler(new ResizeButtonHandler() {
					@Override
					public void onReleased(Control control) {
						super.onReleased(control);
						hideDialog();
					}
				}).build());

		screen.add(dialogWindow);

		clickToShow = true;
	}

	public void showDialog() {
		Control dialogWindow = screen.findControl(Gui.DialogWindow);

		Synchronizers.transition(Transitions.transition(dialogWindow, controlPositionConverter) //
				.start(Gdx.graphics.getWidth() * 0.5f, -Gdx.graphics.getHeight() * 0.5f) //
				.end(0.85f, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f) //
				.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
				.build());

		ImageButton screenBackground = screen.findControl(Gui.ScreenBackground);

		Synchronizers.transition(Transitions.transition(screenBackground.getColor(), LibgdxConverters.color()) //
				.end(0.85f, 0.5f, 0.5f, 0.5f, 1f) //
				.build());

		clickToShow = false;
	}

	public void hideDialog() {
		Control dialogWindow = screen.findControl(Gui.DialogWindow);

		Synchronizers.transition(Transitions.transition(dialogWindow, controlPositionConverter) //
				.start(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f) //
				.end(0.5f, Gdx.graphics.getWidth() * 0.5f, -Gdx.graphics.getHeight() * 0.5f) //
				.functions(InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut()) //
				.build());

		ImageButton screenBackground = screen.findControl(Gui.ScreenBackground);

		Synchronizers.transition(Transitions.transition(screenBackground.getColor(), LibgdxConverters.color()) //
				.end(0.5f, 1f, 1f, 1f, 1f) //
				.build());

		clickToShow = true;
	}

	@Override
	public void update() {
		super.update();
		Synchronizers.synchronize(getDelta());
		screen.update();

		if (Gdx.input.justTouched() && clickToShow)
			showDialog();

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		screen.draw(spriteBatch);
		spriteBatch.end();
	}

}