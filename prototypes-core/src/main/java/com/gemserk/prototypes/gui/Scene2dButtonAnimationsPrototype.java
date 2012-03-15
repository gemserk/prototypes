package com.gemserk.prototypes.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.gemserk.animation4j.converters.TypeConverter;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizer;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.commons.gdx.scene2d.ActionAdapter;
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
	Synchronizer synchronizer;

	public static class ScaleOnFocusAction extends ActionAdapter {

		boolean hasFocus = false;
		float width, height;
		Transition<?> focusTransition;
		ActorDecorator actorDecorator;

		public ScaleOnFocusAction() {
			this(new ActorDecorator());
		}

		public ScaleOnFocusAction(ActorDecorator actorDecorator) {
			this.actorDecorator = actorDecorator;
		}

		@Override
		public void setTarget(Actor target) {
			super.setTarget(target);
			width = target.width;
			height = target.height;
			actorDecorator.setActor(target);
		}

		@Override
		public void act(float delta) {
			Actor actor = getTarget();
			Stage stage = actor.getStage();

			Actor focusedActor = stage.getTouchFocus(0);

			if (focusTransition != null)
				focusTransition.update(delta);

			if (hasFocus) {

				if (focusedActor != actor) {
					focusTransition = Transitions.transition(actorDecorator, actorDecoratorSizeTypeConverter) //
							.start(actor.width, actor.height) //
							.end(0.15f, width * 1f, height * 1f) //
							.build();
					logger.info("starting unfocus transition");
					hasFocus = false;
				}

				return;
			}

			if (!hasFocus) {

				if (focusedActor == actor) {
					focusTransition = Transitions.transition(actorDecorator, actorDecoratorSizeTypeConverter) //
							.start(actor.width, actor.height) //
							.end(0.15f, width * 1.1f, height * 1.1f) //
							.build();
					logger.info("starting focus transition");
					hasFocus = true;
				}

				return;
			}

		}
	}

	public static class ActorDecorator {

		Actor actor;

		float cx, cy;
		float x, y;

		public ActorDecorator() {
			this.cx = 0.5f;
			this.cy = 0.5f;
			this.x = 0f;
			this.y = 0f;
		}

		public float getWidth() {
			return actor.width;
		}

		public float getHeight() {
			return actor.height;
		}

		public void setWidth(float width) {
			actor.width = width;
			center(actor, x, y, cx, cy);
		}

		public void setHeight(float height) {
			actor.height = height;
			center(actor, x, y, cx, cy);
		}

		public void setActor(Actor actor) {
			this.actor = actor;
			this.x = actor.x;
			this.y = actor.y;
			center(actor, x, y, cx, cy);
		}

		public void setX(float x) {
			this.x = x;
		}

		public void setY(float y) {
			this.y = y;
		}

		public void setPosition(float x, float y) {
			setX(x);
			setY(y);
		}

		private void center(Actor actor, float x, float y, float cx, float cy) {
			actor.x = x - actor.width * cx;
			actor.y = y - actor.height * cy;
			if (actor instanceof Table)
				((Table) actor).invalidate();
		}

	}

	public static final TypeConverter<ActorDecorator> actorDecoratorSizeTypeConverter = new TypeConverter<ActorDecorator>() {
		@Override
		public int variables() {
			return 2;
		}

		@Override
		public float[] copyFromObject(ActorDecorator object, float[] x) {
			if (x == null)
				x = new float[variables()];
			x[0] = object.getWidth();
			x[1] = object.getHeight();
			return x;
		}

		@Override
		public ActorDecorator copyToObject(ActorDecorator object, float[] x) {
			object.setWidth(x[0]);
			object.setHeight(x[1]);
			return object;
		}
	};

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();
		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

		resourceManager = new ResourceManagerImpl<String>();
		synchronizer = new Synchronizer();

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
		
		ImageButton button = new ImageButton(new NinePatch(sprite));
		button.getImageCell().expand().fill().align(Align.CENTER);
		center(button, stage.width() * 0.25f, stage.height() * 0.5f, 0f, 0f);
		button.action(new ScaleOnFocusAction());

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
		synchronizer.synchronize(getDelta());
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