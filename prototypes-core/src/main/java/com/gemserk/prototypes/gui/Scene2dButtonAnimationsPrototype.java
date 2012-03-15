package com.gemserk.prototypes.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.commons.gdx.scene2d.actions.ScaleOnFocusAction;
import com.gemserk.commons.gdx.scene2d.ui.LabelImageButton;
import com.gemserk.highscores.gui.RegisterUserListener;
import com.gemserk.prototypes.Launcher;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class Scene2dButtonAnimationsPrototype extends GameStateImpl {

	protected static final Logger logger = LoggerFactory.getLogger(Scene2dButtonAnimationsPrototype.class);

	Launcher launcher;

	private GL10 gl;

	private Stage stage;

	RegisterUserListener registerUserListener;

	ResourceManager<String> resourceManager;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();
		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

		resourceManager = new ResourceManagerImpl<String>();

		Texture.setEnforcePotImages(false);

		new LibgdxResourceBuilder(resourceManager) {
			{
				// resource("skin", skin("data/ui/uiskin.json", "data/ui/uiskin.png"));
				resource("texture", texture2(internal("gui/button-tick.png")).magFilter(TextureFilter.Linear).minFilter(TextureFilter.Linear));
				resource("button", sprite2().texture("texture"));
			}
		};

		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false) {
			@Override
			public void act(float delta) {
				super.act(delta);
				Actor touchFocus = getTouchFocus(0);

				if (touchFocus == null)
					return;

			}
		};

		Gdx.input.setInputProcessor(stage);

		Sprite sprite = resourceManager.getResourceValue("button");

		Label label = new Label("HOLA", new LabelStyle(new BitmapFont(), Color.BLACK));
		ImageButton button = new LabelImageButton(new NinePatch(sprite), label);

		button.getImageCell().expand().fill().align(Align.CENTER);
		center(button, stage.width() * 0.25f, stage.height() * 0.5f, 0f, 0f);
		button.action(new ScaleOnFocusAction());

		//
		// label.x = button.width * 0.5f;
		// label.y = button.height * 0.5f;
		// button.add(label).ignore().align(Align.CENTER).fill().expand();

		stage.addActor(button);

		ImageButton button2 = new ImageButton(new NinePatch(sprite));
		button2.getImageCell().expand().fill().align(Align.CENTER);
		center(button2, stage.width() * 0.75f, stage.height() * 0.5f, 0f, 0f);
		button2.action(new ScaleOnFocusAction());

		stage.addActor(button2);
	}

	public void center(Actor actor, float x, float y, float cx, float cy) {
		actor.x = x - actor.width * cx;
		actor.y = y - actor.height * cy;
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
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void dispose() {
		super.dispose();
		resourceManager.unloadAll();
	}

}