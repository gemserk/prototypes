package com.gemserk.prototypes.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.gemserk.animation4j.gdx.scenes.scene2d.Scene2dConverters;
import com.gemserk.animation4j.interpolator.function.InterpolationFunctions;
import com.gemserk.animation4j.timeline.Builders;
import com.gemserk.animation4j.timeline.TimelineAnimation;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.highscores.gui.RegisterUserListener;
import com.gemserk.prototypes.Launcher;

public class Scene2dToastPrototype extends GameStateImpl {

	Launcher launcher;

	private GL10 gl;

	private Stage stage;

	RegisterUserListener registerUserListener;

	InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private Skin skin;

	Actor toast(final String text, final Skin skin) {

		return new Window("", skin) {

			TimelineAnimation timelineAnimation;

			{
				setMovable(false);

				defaults().spaceBottom(5);

				Label toastLabel = new Label(text, skin);

				width = Gdx.graphics.getWidth() * 0.95f;
				height = toastLabel.getTextBounds().height + 20 + getStyle().titleFont.getLineHeight();

				x = Gdx.graphics.getWidth() * 0.5f - width * 0.5f;

				row().fill().expandX();
				add(toastLabel).align(Align.LEFT).fill(0f, 0f).padLeft(20);

				float outsideY = Gdx.graphics.getHeight() + height;
				float insideY = Gdx.graphics.getHeight() - height + getStyle().titleFont.getLineHeight();
				
				y = outsideY;

				timelineAnimation = Builders.animation( //
						Builders.timeline() //
								.value(Builders.timelineValue(this, Scene2dConverters.actorPositionTypeConverter) //
										.keyFrame(0f, new float[] { x, outsideY }, //
												InterpolationFunctions.linear(), InterpolationFunctions.easeIn()) //
										.keyFrame(1f, new float[] { x, insideY }) //
										.keyFrame(4f, new float[] { x, insideY }, //
												InterpolationFunctions.linear(), InterpolationFunctions.easeOut()) //
										.keyFrame(5f, new float[] { x, outsideY }) //
								) //
						) //
						.started(true) //
						.delay(0f) //
						.speed(2f) //
						.build();
			}

			@Override
			public void act(float delta) {
				super.act(delta);
				timelineAnimation.update(delta);

				if (timelineAnimation.isFinished()) {
					getStage().removeActor(this);
				}
			}

		};

	}

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();
		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

		skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"), Gdx.files.internal("data/ui/uiskin.png"));

		// stage = new ToastStage(skin, "This is a toast implementation using scene2d\nwith multiple lines.", Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		stage.addActor(toast("This is a toast implementation using scene2d\nwith multiple lines.", skin));

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorPointerDown("toast", 0);
			}
		};

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void update() {
		super.update();
		stage.act(getDelta());

		inputDevicesMonitor.update();

		if (inputDevicesMonitor.getButton("toast").isReleased()) {
			stage.addActor(toast("This is a toast implementation using scene2d\nwith multiple lines.", skin));
			// stage = new ToastStage(skin, "This is a toast implementation using scene2d \nwith multiple lines.", Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
			// Gdx.input.setInputProcessor(stage);
		}

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.draw();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}