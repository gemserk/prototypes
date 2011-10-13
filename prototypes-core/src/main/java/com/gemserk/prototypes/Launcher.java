package com.gemserk.prototypes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.ComboBox;
import com.badlogic.gdx.scenes.scene2d.ui.ComboBox.ComboBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.gdx.GameState;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.ScreenImpl;
import com.gemserk.commons.gdx.screens.transitions.TransitionBuilder;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.prototypes.kalleh.lighting.LightingPrototype;
import com.gemserk.prototypes.pixmap.PixmapCollisionPrototype;
import com.gemserk.prototypes.pixmap.reload.ReloadPixmapTestGameState;
import com.gemserk.prototypes.superangrysheep.SuperAngrySheepPrototype;

public class Launcher extends com.gemserk.commons.gdx.Game {

	public static class LauncherGameState extends GameStateImpl {

		private Stage stage;
		private GL10 gl;

		Map<String, GameState> gameStates;

		Launcher launcher;

		@Override
		public void init() {
			gl = Gdx.graphics.getGL10();

			gameStates = new HashMap<String, GameState>() {
				{
					put("Lighting", new LightingPrototype());
					put("PixmapCollision", new PixmapCollisionPrototype());
					put("SuperAngrySheep", new SuperAngrySheepPrototype());
					put("ReloadPixmapTest", new ReloadPixmapTestGameState());
				}
			};

			Skin skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"), Gdx.files.internal("data/ui/uiskin.png"));

			stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

			Window window = new Window("window", "Gemserk's Prototypes Launcher", stage, skin.getStyle(WindowStyle.class), 420, 440);

			window.width = Gdx.graphics.getWidth() * 0.85f;
			window.height = Gdx.graphics.getHeight() * 0.85f;

			window.x = Gdx.graphics.getWidth() * 0.5f - window.width * 0.5f;
			window.y = Gdx.graphics.getHeight() * 0.5f - window.height * 0.5f;

			stage.addActor(window);

			// String[] items = { "Lighting", "Lighting" };

			String[] items = new String[gameStates.keySet().size()];
			
			gameStates.keySet().toArray(items);
			
			Arrays.sort(items);

			ComboBoxStyle style = skin.getStyle(ComboBoxStyle.class);

			final ComboBox comboBox = new ComboBox(items, stage, style, "combo");

			comboBox.width = window.width * 0.75f;

			comboBox.x = window.width * 0.5f - comboBox.width * 0.5f;
			comboBox.y = window.height * 0.5f;

			comboBox.touchable = true;

			comboBox.setSelectionListener(new ComboBox.SelectionListener() {
				@Override
				public void selected(ComboBox comboBox, int selectionIndex, String selection) {
					System.out.println(selection);
					// GameState gameState = gameStates.get(selection);
					// if (gameState != null) {
					// launcher.transition(gameState)
					// .start();
					// }
				}
			});

			Button button = new Button(skin);
			button.setText("Start");

			button.width = window.width * 0.2f;
			button.height = window.height * 0.1f;

			button.x = window.width * 0.5f - button.width * 0.5f;
			button.y = comboBox.y - 60f;

			button.setClickListener(new ClickListener() {
				@Override
				public void click(Actor actor) {
					String selection = comboBox.getSelection();
					GameState gameState = gameStates.get(selection);
					if (gameState != null) {
						launcher.transition(gameState).start();
					}
				}
			});

			window.addActor(comboBox);
			window.addActor(button);

			Gdx.input.setInputProcessor(stage);

			Gdx.graphics.getGL10().glClearColor(0, 0, 0, 1);
		}

		@Override
		public void update() {
			stage.act(getDelta());
		}
		
		@Override
		public void pause() {
			super.pause();
			Gdx.input.setInputProcessor(null);
		}
		
		@Override
		public void resume() {
			super.resume();
			Gdx.input.setInputProcessor(stage);
		}

		@Override
		public void render() {
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			stage.draw();
		}

	}

	// private ResourceManager<String> resourceManager;
	private SpriteBatch spriteBatch;
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;
	private GameState launcherGameState;
	private GameState currentGameState;
	
	private BitmapFont bitmapFont;

	@Override
	public void create() {

		Converters.register(Color.class, LibgdxConverters.color());
		Converters.register(Vector2.class, LibgdxConverters.vector2());

		spriteBatch = new SpriteBatch();
		
		bitmapFont = new BitmapFont();

		Injector injector = new InjectorImpl();

		injector.bind("launcher", this);

		launcherGameState = injector.getInstance(LauncherGameState.class);
		currentGameState = launcherGameState;

		setScreen(new ScreenImpl(launcherGameState));

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKeys("restart", Keys.R, Keys.MENU, Keys.NUM_1);
				monitorKeys("back", Keys.ESCAPE, Keys.BACK);
			}
		};

		Gdx.input.setCatchBackKey(true);

	}

	public TransitionBuilder transition(GameState gameState) {
		this.currentGameState = gameState;
		return new TransitionBuilder(this, new ScreenImpl(gameState));
	}

	@Override
	public void render() {
		super.render();
		inputDevicesMonitor.update();

		if (inputDevicesMonitor.getButton("restart").isReleased()) {
			System.out.println("restarting");
			getScreen().restart();
		}

		if (inputDevicesMonitor.getButton("back").isReleased()) {
			if (currentGameState != launcherGameState) {
				transition(launcherGameState).disposeCurrent() //
						.restartScreen() //
						.start();
			} else {
				Gdx.app.exit();
			}
		}

		spriteBatch.begin();
		bitmapFont.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), Gdx.graphics.getWidth() * 0.02f, Gdx.graphics.getHeight() * 0.95f);
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		super.dispose();
		spriteBatch.dispose();
		bitmapFont.dispose();
	}

}
