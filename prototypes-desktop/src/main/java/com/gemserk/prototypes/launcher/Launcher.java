package com.gemserk.prototypes.launcher;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.gdx.GameState;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.ScreenImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;

public class Launcher extends com.gemserk.commons.gdx.Game {

	public static class LauncherGameState extends GameStateImpl {

		private GL10 gl;

		Map<String, GameState> gameStates = com.gemserk.prototypes.Launcher.gameStates;

		Launcher launcher;
		private JFrame screen;

		private JComboBox comboBox;

		@Override
		public void init() {
			gl = Gdx.graphics.getGL10();

			screen = new JFrame();

			screen.setSize(640, 80);
			screen.setLayout(new GridLayout(2, 1));

			String[] items = new String[gameStates.keySet().size()];

			gameStates.keySet().toArray(items);

			comboBox = new JComboBox(items);

			screen.add(comboBox);

			screen.add(new JButton("Start") {
				{
					addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							String option = (String) comboBox.getSelectedItem();
							GameState gameState = gameStates.get(option);
							launcher.transition(gameState);
						}
					});
				}
			});
			
			screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			screen.invalidate();


			Gdx.graphics.getGL10().glClearColor(0, 0, 0, 1);
		}

		@Override
		public void pause() {
			super.pause();
			Gdx.input.setInputProcessor(null);

			screen.setVisible(false);
		}

		@Override
		public void resume() {
			super.resume();
			screen.setVisible(true);
		}

		@Override
		public void render() {
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		}

	}

	// private ResourceManager<String> resourceManager;
	private SpriteBatch spriteBatch;
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;
	private GameState launcherGameState;
	private GameState currentGameState;

	private BitmapFont bitmapFont;

	private Boolean transition = false;
	private GameState transitionGameState;

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

	public void transition(GameState gameState) {
		synchronized (transition) {
			transition = true;
			this.transitionGameState = gameState;
		}
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

				currentGameState.dispose();
				transition(launcherGameState);

				// transition(launcherGameState).disposeCurrent() //
				// .restartScreen() //
				// .start();

			} else {
				Gdx.app.exit();
			}
		}

		spriteBatch.begin();
		bitmapFont.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), Gdx.graphics.getWidth() * 0.02f, Gdx.graphics.getHeight() * 0.95f);
		spriteBatch.end();

		synchronized (transition) {
			if (transition) {
				this.currentGameState = transitionGameState;
				// return new TransitionBuilder(this, new ScreenImpl(gameState));
				transition = false;
				setScreen(new ScreenImpl(this.currentGameState));
			}

		}
	}

	@Override
	public void dispose() {
		super.dispose();
		spriteBatch.dispose();
		bitmapFont.dispose();
	}

}
