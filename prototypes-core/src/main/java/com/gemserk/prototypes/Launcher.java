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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.gdx.ApplicationListenerGameStateBasedImpl;
import com.gemserk.commons.gdx.GameState;
import com.gemserk.commons.gdx.GameStateDelegateFixedTimestepImpl;
import com.gemserk.commons.gdx.GameStateDelegateWithInternalStateImpl;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.ImmediateModeRendererUtils;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.prototypes.algorithms.ConvexHull2dPrototype;
import com.gemserk.prototypes.algorithms.PixmapConvexHull2dPrototype;
import com.gemserk.prototypes.artemis.sprites.SpriteUpdateSystemPerformanceTest;
import com.gemserk.prototypes.box2d.frustum.FrustumCullingPrototype;
import com.gemserk.prototypes.camera.CameraParallaxPrototype;
import com.gemserk.prototypes.fonts.RenderScaledFontsTest;
import com.gemserk.prototypes.gdx.particles.ScaleParticleEmitterTest;
import com.gemserk.prototypes.gdx.particles.SnowParticleEmitterTest;
import com.gemserk.prototypes.gui.DialogHideShowPrototype;
import com.gemserk.prototypes.gui.FocusedControlPrototype;
import com.gemserk.prototypes.gui.RequestUserDataPrototype;
import com.gemserk.prototypes.gui.RequestUserDataUsingScenePrototype;
import com.gemserk.prototypes.gui.Scene2dButtonAnimationsPrototype;
import com.gemserk.prototypes.gui.Scene2dChangelogPrototype;
import com.gemserk.prototypes.gui.Scene2dToastPrototype;
import com.gemserk.prototypes.kalleh.lighting.LightingPrototype;
import com.gemserk.prototypes.kalleh.lighting.LightingPrototype2;
import com.gemserk.prototypes.mail.FacebookTest;
import com.gemserk.prototypes.mail.SendMailTest;
import com.gemserk.prototypes.physicseditor.FixtureAtlasLoadShapeTest;
import com.gemserk.prototypes.pixmap.PixmapCollisionPrototype;
import com.gemserk.prototypes.pixmap.PixmapToTexturePrototype;
import com.gemserk.prototypes.pixmap.performance.PixmapPerformancePrototype;
import com.gemserk.prototypes.pixmap.reload.ReloadPixmapTestGameState;
import com.gemserk.prototypes.spriteatlas.SpriteAtlasBugPrototype;
import com.gemserk.prototypes.superangrysheep.SpriteScissorsPrototype;
import com.gemserk.prototypes.superangrysheep.SuperAngrySheepPrototype;
import com.gemserk.prototypes.texture.DrawToTexturePrototype;
import com.gemserk.prototypes.trajectory.AngryBirdsTrajectoryPrototype;

public class Launcher extends ApplicationListenerGameStateBasedImpl {

	public static boolean processGlobalInput = true;

	@SuppressWarnings("serial")
	public static final Map<String, GameState> gameStates = new HashMap<String, GameState>() {
		{
			put("AngryBirds.Trajectory", new AngryBirdsTrajectoryPrototype());
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
			put("Gui.RequestUserDataPrototype", new RequestUserDataPrototype());
			put("Gui.RequestUserDataUsingScenePrototype", new RequestUserDataUsingScenePrototype());
			put("Gui.Scene2dToastPrototype", new Scene2dToastPrototype());
			put("Gui.Scene2dChangelogPrototype", new Scene2dChangelogPrototype());
			// put("Gui.Scene2dPasswordTextFieldPrototype", new Scene2dPasswordTextFieldPrototype());
			put("Gui.Scene2dButtonAnimationsPrototype", new Scene2dButtonAnimationsPrototype());
			put("FrustumCullingPrototype", new FrustumCullingPrototype());
			put("Artemis.SpriteUpdateSystemPerformanceTest", new SpriteUpdateSystemPerformanceTest());
			// put("Artemis.UiPrototype", new ArtemisUiPrototype());
			put("Gdx.ScaleParticleEmitterTest", new ScaleParticleEmitterTest());
			put("Gdx.SnowParticleEmitterTest", new SnowParticleEmitterTest());
			put("Fonts.RenderScaledFontsTest", new RenderScaledFontsTest());
			put("PhysicsEditor.FixtureAtlasLoadShapeTest", new FixtureAtlasLoadShapeTest());
			put("Internet.SendMailTest", new SendMailTest());
			put("Internet.FacebookTest", new FacebookTest());
			put("Polygons.ConvexHull2dPrototype", new ConvexHull2dPrototype());
			put("Polygons.PixmapConvexHull2dPrototype", new PixmapConvexHull2dPrototype());
			put("Camera.CameraParallaxPrototype", new CameraParallaxPrototype());
			put("SpriteAtlas.Test", new SpriteAtlasBugPrototype());
			put("SpriteScissorsPrototype", new SpriteScissorsPrototype());
			// put("Commons.CameraFrustumCullingPrototype", new CameraFrustumCullingPrototype());
		}
	};

	Injector injector;

	private static GameState delegate(GameState gameState) {
		return stateBased(fixedTimeStep(gameState));
	}

	private static GameState stateBased(GameState gameState) {
		return new GameStateDelegateWithInternalStateImpl(gameState);
	}

	private static GameStateDelegateFixedTimestepImpl fixedTimeStep(GameState gameState) {
		return new GameStateDelegateFixedTimestepImpl(gameState);
	}

	public static class LauncherGameState extends GameStateImpl {

		private Stage stage;

		Launcher launcher;

		Injector injector;

		@Override
		public void init() {
			Skin skin = new Skin(Gdx.files.internal("data/ui/uiskin.json"), new TextureAtlas(Gdx.files.internal("data/ui/uiskin.atlas")));

			stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

			Window window = new Window("Gemserk's Prototypes Launcher", skin);

			window.setWidth(Gdx.graphics.getWidth() * 0.85f);
			window.setHeight(Gdx.graphics.getHeight() * 0.85f);

			window.setX(Gdx.graphics.getWidth() * 0.5f - window.getWidth() * 0.5f);
			window.setY(Gdx.graphics.getHeight() * 0.5f - window.getHeight() * 0.5f);

			stage.addActor(window);

			String[] items = new String[gameStates.keySet().size()];

			gameStates.keySet().toArray(items);

			Arrays.sort(items);

			final List list = new List(items, skin);

			ScrollPane scrollPane = new ScrollPane(list);
			// ScrollPane scrollPane = new ScrollPane(flickScrollPane, skin);

			scrollPane.setWidth(window.getWidth() * 0.75f);
			scrollPane.setHeight(window.getHeight() * 0.5f);

			scrollPane.setX(window.getWidth() * 0.5f - scrollPane.getWidth() * 0.5f);
			scrollPane.setY(window.getHeight() * 0.35f);

			window.addActor(scrollPane);

			TextButton button = new TextButton("Start", skin);
			// button.setText("Start");

			button.setWidth(window.getWidth() * 0.2f);
			button.setHeight(window.getHeight() * 0.1f);

			button.setX(window.getWidth() * 0.5f - button.getWidth() * 0.5f);
			button.setY(scrollPane.getY() - 60f);

			button.addListener(new ClickListener() {
				
				@Override
				public void clicked(InputEvent event, float x, float y) {
					String selection = list.getSelection();
					GameState sourceGameState = gameStates.get(selection);
					injector.injectMembers(sourceGameState);
					GameState gameState = delegate(sourceGameState);
					if (gameState != null) {
						launcher.transition(gameState) //
								.disposeCurrent(false) //
								.fadeOut(0.25f) //
								.fadeIn(0.25f) //
								.start();
						// launcher.setGameState(gameState, false);
						// launcher.currentGameState = gameState;
						// launcher.transition(gameState).start();
					}
				}
			});

			window.addActor(button);

			Gdx.input.setInputProcessor(stage);

			Gdx.gl.glClearColor(0, 0, 0, 1);
		}

		@Override
		public void update() {
			stage.act(getDelta());
		}

		@Override
		public void pause() {
			super.pause();
			if (Gdx.input.getInputProcessor() == stage)
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
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
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

	public GameState launcherGameState;

	private BitmapFont bitmapFont;

	@Override
	public void create() {

		ImmediateModeRendererUtils.setRenderer(new ImmediateModeRenderer20(false, true, 2));
		
		Converters.register(Color.class, LibgdxConverters.color());
		Converters.register(Vector2.class, LibgdxConverters.vector2());

		spriteBatch = new SpriteBatch();

		bitmapFont = new BitmapFont();

		Injector injector = this.injector.createChildInjector();

		injector.bind("launcher", this);

		launcherGameState = delegate(injector.getInstance(LauncherGameState.class));

		injector.injectMembers(launcherGameState);

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
	public void resize(int width, int height) {
		super.resize(width, height);
		System.out.println("resize!!");
	}

	@Override
	public void render() {
		super.render();
		inputDevicesMonitor.update();

		if (processGlobalInput) {
			if (inputDevicesMonitor.getButton("restart").isReleased()) {
				System.out.println("restarting");
				getGameState().dispose();
				getGameState().init();
				// getScreen().restart();
			}

			if (inputDevicesMonitor.getButton("back").isReleased()) {
				if (getGameState() != launcherGameState) {

					transition(launcherGameState) //
							.disposeCurrent(true) //
							.fadeOut(0.25f) //
							.fadeIn(0.5f) //
							.start();

					setGameState(launcherGameState, true);

				} else {
					Gdx.app.exit();
				}
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
