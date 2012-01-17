package com.gemserk.prototypes;

import java.text.MessageFormat;
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
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.FlickScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.gdx.ApplicationListenerGameStateBasedImpl;
import com.gemserk.commons.gdx.GameState;
import com.gemserk.commons.gdx.GameStateDelegateFixedTimestepImpl;
import com.gemserk.commons.gdx.GameStateDelegateWithInternalStateImpl;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.prototypes.algorithms.ConvexHull2dPrototype;
import com.gemserk.prototypes.algorithms.PixmapConvexHull2dPrototype;
import com.gemserk.prototypes.artemis.sprites.SpriteUpdateSystemPerformanceTest;
import com.gemserk.prototypes.box2d.frustum.FrustumCullingPrototype;
import com.gemserk.prototypes.fonts.RenderScaledFontsTest;
import com.gemserk.prototypes.gdx.particles.ScaleParticleEmitterTest;
import com.gemserk.prototypes.gdx.particles.SnowParticleEmitterTest;
import com.gemserk.prototypes.gui.DialogHideShowPrototype;
import com.gemserk.prototypes.gui.FocusedControlPrototype;
import com.gemserk.prototypes.kalleh.lighting.LightingPrototype;
import com.gemserk.prototypes.kalleh.lighting.LightingPrototype2;
import com.gemserk.prototypes.mail.FacebookTest;
import com.gemserk.prototypes.mail.SendMailTest;
import com.gemserk.prototypes.physicseditor.FixtureAtlasLoadShapeTest;
import com.gemserk.prototypes.pixmap.PixmapCollisionPrototype;
import com.gemserk.prototypes.pixmap.PixmapToTexturePrototype;
import com.gemserk.prototypes.pixmap.performance.PixmapPerformancePrototype;
import com.gemserk.prototypes.pixmap.reload.ReloadPixmapTestGameState;
import com.gemserk.prototypes.superangrysheep.SuperAngrySheepPrototype;
import com.gemserk.prototypes.texture.DrawToTexturePrototype;

public class Launcher extends ApplicationListenerGameStateBasedImpl {

	public static final Map<String, GameState> gameStates = new HashMap<String, GameState>() {
		{
			put("Lighting", new LightingPrototype());
			put("Lighting2", new LightingPrototype2());
			put("PixmapCollision", new PixmapCollisionPrototype());
			put("SuperAngrySheep", new SuperAngrySheepPrototype());
			put("ReloadPixmapTest", new ReloadPixmapTestGameState());
			put("PixmapPerformance", new PixmapPerformancePrototype());
			put("DrawToTextureBorderTest", new DrawToTexturePrototype());
			put("PixmapToTextureBorderTest", new PixmapToTexturePrototype());
			put("Gui.DialogHideShowPrototype", new DialogHideShowPrototype());
			put("Gui.FocusedControlPrototype", new FocusedControlPrototype());
			put("FrustumCullingPrototype", new FrustumCullingPrototype());
			put("Artemis.SpriteUpdateSystemPerformanceTest", new SpriteUpdateSystemPerformanceTest());
//			put("Artemis.UiPrototype", new ArtemisUiPrototype());
			put("Gdx.ScaleParticleEmitterTest", new ScaleParticleEmitterTest());
			put("Gdx.SnowParticleEmitterTest", new SnowParticleEmitterTest());
			put("Fonts.RenderScaledFontsTest", new RenderScaledFontsTest());
			put("PhysicsEditor.FixtureAtlasLoadShapeTest", new FixtureAtlasLoadShapeTest());
			put("Internet.SendMailTest", new SendMailTest());
			put("Internet.FacebookTest", new FacebookTest());
			put("Polygons.ConvexHull2dPrototype", new ConvexHull2dPrototype());
			put("Polygons.PixmapConvexHull2dPrototype", new PixmapConvexHull2dPrototype());
//			put("Commons.CameraFrustumCullingPrototype", new CameraFrustumCullingPrototype());
		}
	};

	private static GameState delegate(GameState gameState) {
		return new GameStateDelegateWithInternalStateImpl(new GameStateDelegateFixedTimestepImpl(gameState));
	}
	
	public static class LauncherGameState extends GameStateImpl {

		private Stage stage;
		private GL10 gl;

		Launcher launcher;

		@Override
		public void init() {
			gl = Gdx.graphics.getGL10();

			Skin skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"), Gdx.files.internal("data/ui/uiskin.png"));

			stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

			Window window = new Window("Gemserk's Prototypes Launcher", skin.getStyle(WindowStyle.class), "window");

			window.width = Gdx.graphics.getWidth() * 0.85f;
			window.height = Gdx.graphics.getHeight() * 0.85f;

			window.x = Gdx.graphics.getWidth() * 0.5f - window.width * 0.5f;
			window.y = Gdx.graphics.getHeight() * 0.5f - window.height * 0.5f;

			stage.addActor(window);

			String[] items = new String[gameStates.keySet().size()];

			gameStates.keySet().toArray(items);

			Arrays.sort(items);

			final List list = new List(items, skin);

			FlickScrollPane scrollPane = new FlickScrollPane(list);
			// ScrollPane scrollPane = new ScrollPane(flickScrollPane, skin);

			scrollPane.width = window.width * 0.75f;
			scrollPane.height = window.height * 0.5f;

			scrollPane.x = window.width * 0.5f - scrollPane.width * 0.5f;
			scrollPane.y = window.height * 0.35f;

			window.addActor(scrollPane);

			TextButton button = new TextButton("Start", skin);
			// button.setText("Start");

			button.width = window.width * 0.2f;
			button.height = window.height * 0.1f;

			button.x = window.width * 0.5f - button.width * 0.5f;
			button.y = scrollPane.y - 60f;

			button.setClickListener(new ClickListener() {

				@Override
				public void click(Actor arg0, float arg1, float arg2) {
					String selection = list.getSelection();
					GameState gameState = delegate(gameStates.get(selection));
					if (gameState != null) {
						launcher.setGameState(gameState, false);
						launcher.currentGameState = gameState;
						// launcher.transition(gameState).start();
					}
				}
			});

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
			Gdx.input.setCatchBackKey(false);
		}

		@Override
		public void render() {
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			stage.draw();
		}

		@Override
		public void resize(int width, int height) {
			super.resize(width, height);

			System.out.println(MessageFormat.format("resizing: {0}x{1}", width, height));

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

		launcherGameState = delegate(injector.getInstance(LauncherGameState.class));
		currentGameState = launcherGameState;

		setGameState(launcherGameState);

		// setScreen(new ScreenImpl(launcherGameState));

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKeys("restart", Keys.R, Keys.MENU, Keys.NUM_1);
				monitorKeys("back", Keys.ESCAPE, Keys.BACK);
			}
		};

		Gdx.input.setCatchBackKey(true);

	}

	// public TransitionBuilder transition(GameState gameState) {
	// this.currentGameState = gameState;
	// return new TransitionBuilder(this, new ScreenImpl(gameState));
	// }

	@Override
	public void render() {
		super.render();
		inputDevicesMonitor.update();

		if (inputDevicesMonitor.getButton("restart").isReleased()) {
			System.out.println("restarting");
			getGameState().dispose();
			getGameState().init();
			// getScreen().restart();
		}

		if (inputDevicesMonitor.getButton("back").isReleased()) {
			if (currentGameState != launcherGameState) {
				// transition(launcherGameState).disposeCurrent() //
				// .restartScreen() //
				// .start();

				setGameState(launcherGameState, true);

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
