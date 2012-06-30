package com.gemserk.prototypes.trajectory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gemserk.commons.gdx.GameStateImpl;

public class AngryBirdsTrajectoryPrototype extends GameStateImpl {

	public static class ProjectileEquation {

		public float gravity;
		public Vector2 startVelocity = new Vector2();
		public Vector2 startPoint = new Vector2();

		public float getX(float t) {
			return startVelocity.x * t + startPoint.x;
		}

		public float getY(float t) {
			return 0.5f * gravity * t * t + startVelocity.y * t + startPoint.y;
		}

		public float getTForGivenX(float x) {
			// x = startVelocity.x * t + startPoint.x
			// x - startPoint.x = startVelocity.x * t
			// t = (x - startPoint.x) / (startVelocity.x);
			return (x - startPoint.x) / (startVelocity.x);
		}

	}

	public static class Controller {

		public float power = 50f;
		public float angle = 0f;
		
		public boolean fixedHorizontalDistance = false; 

	}

	public static class TrajectoryActor extends Actor {

		private Controller controller;
		private ProjectileEquation projectileEquation;
		private Sprite trajectorySprite;

		public int trajectoryPointCount = 30;
		public float timeSeparation = 1f;

		public TrajectoryActor(Controller controller, float gravity, Sprite trajectorySprite) {
			this.controller = controller;
			this.trajectorySprite = trajectorySprite;
			this.projectileEquation = new ProjectileEquation();
			this.projectileEquation.gravity = gravity;
		}

		@Override
		public void act(float delta) {
			super.act(delta);
			projectileEquation.startVelocity.set(controller.power, 0f);
			projectileEquation.startVelocity.rotate(controller.angle);
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			float t = 0f;
			float a = 1f;
			float adiff = a / trajectoryPointCount;
			float width = this.width;
			float height = this.height;
			float widthDiff = width / trajectoryPointCount;
			float heightDiff = height / trajectoryPointCount;

			float timeSeparation = this.timeSeparation;
			
			if (controller.fixedHorizontalDistance) 
				timeSeparation = projectileEquation.getTForGivenX(15f);

			for (int i = 0; i < trajectoryPointCount; i++) {
				float x = this.x + projectileEquation.getX(t);
				float y = this.y + projectileEquation.getY(t);

				this.color.a = a;

				batch.setColor(this.color);
				batch.draw(trajectorySprite, x, y, width, height);

				a -= adiff;
				t += timeSeparation;

				width -= widthDiff;
				height -= heightDiff;
			}
		}

		@Override
		public Actor hit(float x, float y) {
			return null;
		}

	}

	private SpriteBatch spriteBatch;
	private ShapeRenderer shapeRenderer;
	private Controller controller;
	private Stage stage;
	private BitmapFont bitmapFont;

	@Override
	public void init() {
		spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		bitmapFont = new BitmapFont();

		float gravity = -10f;

		Texture texture = new Texture(Gdx.files.internal("test/white-circle.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Sprite trajectorySprite = new Sprite(texture);

		controller = new Controller();
		TrajectoryActor trajectoryActor = new TrajectoryActor(controller, gravity, trajectorySprite);

		trajectoryActor.x = 100f;
		trajectoryActor.y = 100f;
		trajectoryActor.width = 10f;
		trajectoryActor.height = 10f;

		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		stage.addActor(trajectoryActor);

	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		float deltaTime = Gdx.graphics.getDeltaTime();

		stage.act(deltaTime);
		stage.draw();
		
		spriteBatch.begin();
		bitmapFont.draw(spriteBatch, "LEFT and RIGHT keys to change the start velocity", Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.9f);
		bitmapFont.draw(spriteBatch, "UP and DOWN keys to change the angle", Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.85f);
		bitmapFont.draw(spriteBatch, "5 and 6 to switch fixed horizontal distance between points (" + controller.fixedHorizontalDistance + ")", Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.8f);
		spriteBatch.end();

		if (Gdx.input.isKeyPressed(Keys.UP)) {
			controller.angle += 15f * Gdx.graphics.getDeltaTime();
			if (controller.angle > 80f)
				controller.angle = -30f;
		}

		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			controller.angle -= 10f * Gdx.graphics.getDeltaTime();
			if (controller.angle < -30f)
				controller.angle = 80f;
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			controller.power += 10f * Gdx.graphics.getDeltaTime();
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			controller.power -= 10f * Gdx.graphics.getDeltaTime();
		}
		
		if (Gdx.input.isKeyPressed(Keys.NUM_5)) {
			controller.fixedHorizontalDistance = false;
		}

		if (Gdx.input.isKeyPressed(Keys.NUM_6)) {
			controller.fixedHorizontalDistance = true;
		}

	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		shapeRenderer.dispose();
		stage.dispose();
		bitmapFont.dispose();
	}

}
