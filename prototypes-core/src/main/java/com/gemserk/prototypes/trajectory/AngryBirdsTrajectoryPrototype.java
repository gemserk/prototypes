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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
		public boolean charging = false;

		public boolean fixedHorizontalDistance = true;

	}

	public static class ControllerLogic {

		boolean wasPressed;
		Controller controller;

		final Vector2 pressedPosition = new Vector2();
		final Vector2 currentPosition = new Vector2();
		final Vector2 tmp = new Vector2();

		public ControllerLogic(Controller controller, Vector2 slingshotPosition) {
			this.controller = controller;
			wasPressed = false;
			this.pressedPosition.set(slingshotPosition);
		}

		public void update(float delta) {

			if (Gdx.input.isTouched()) {

				if (!wasPressed) {
					// pressedPosition.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
					wasPressed = true;
					controller.charging = true;
				}

				currentPosition.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

				tmp.set(currentPosition).sub(pressedPosition);
				tmp.mul(-1f);

				controller.angle = tmp.angle();
				controller.power = tmp.len();

			} else {

				if (wasPressed) {
					// shoot
					controller.charging = false;
					wasPressed = false;
				}

			}

		}

	}

	public static class TrajectoryActor extends Actor {

		private Controller controller;
		private ProjectileEquation projectileEquation;
		private Sprite trajectorySprite;

		public int trajectoryPointCount = 50;
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

			if (!controller.charging)
				return;

			projectileEquation.startVelocity.set(controller.power, 0f);
			projectileEquation.startVelocity.rotate(controller.angle);
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			if (!controller.charging)
				return;

			float t = 0f;
			float a = 1f;
			float adiff = a / trajectoryPointCount;
			float width = this.getWidth();
			float height = this.getHeight();
			float widthDiff = width / trajectoryPointCount;
			float heightDiff = height / trajectoryPointCount;

			float timeSeparation = this.timeSeparation;

			if (controller.fixedHorizontalDistance)
				timeSeparation = projectileEquation.getTForGivenX(15f);

			for (int i = 0; i < trajectoryPointCount; i++) {
				float x = this.getX() + projectileEquation.getX(t);
				float y = this.getY() + projectileEquation.getY(t);

				this.getColor().a = a;

				batch.setColor(this.getColor());
				batch.draw(trajectorySprite, x, y, width, height);

				a -= adiff;
				t += timeSeparation;

				width -= widthDiff;
				height -= heightDiff;
			}
		}
		
		@Override
		public Actor hit(float x, float y, boolean touchable) {
			return null;
		}

	}

	private SpriteBatch spriteBatch;
	private ShapeRenderer shapeRenderer;

	private Controller controller;
	private ControllerLogic controllerLogic;

	private Stage stage;
	private BitmapFont bitmapFont;
	private Texture targetTexture;
	private Texture backgroundTexture;
	private Sprite backgroundSprite;
	private Texture groundTexture;
	private Sprite groundSprite;
	private Sprite slingshotSprite;
	private Texture slingshotTexture;

	@Override
	public void init() {
		spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		bitmapFont = new BitmapFont();

		float gravity = -10f;

		Texture.setEnforcePotImages(false);

		targetTexture = new Texture(Gdx.files.internal("test/white-circle.png"));
		targetTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		backgroundTexture = new Texture(Gdx.files.internal("angrybirds/background.png"));
		backgroundTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		backgroundSprite = new Sprite(backgroundTexture);
		backgroundSprite.setPosition(0, 0);

		{
			groundTexture = new Texture(Gdx.files.internal("angrybirds/ground.png"));
			groundTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

			groundSprite = new Sprite(groundTexture);
			groundSprite.setPosition(0, 0);
		}

		{
			slingshotTexture = new Texture(Gdx.files.internal("angrybirds/slingshot.png"));
			slingshotTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

			slingshotSprite = new Sprite(slingshotTexture);
			slingshotSprite.setPosition(128 - slingshotSprite.getWidth() * 0.5f, 50);
		}

		Sprite trajectorySprite = new Sprite(targetTexture);

		controller = new Controller();
		controllerLogic = new ControllerLogic(controller, new Vector2(128f, 50 + slingshotSprite.getHeight() * 0.7f));

		TrajectoryActor trajectoryActor = new TrajectoryActor(controller, gravity, trajectorySprite);

		trajectoryActor.setX(128f);
		trajectoryActor.setY(50 + slingshotSprite.getHeight() * 0.7f);
		trajectoryActor.setWidth(10f);
		trajectoryActor.setHeight(10f);

		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		stage.addActor(trajectoryActor);

	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		float delta = Gdx.graphics.getDeltaTime();

		controllerLogic.update(delta);

		spriteBatch.begin();
		backgroundSprite.draw(spriteBatch);
		groundSprite.draw(spriteBatch);
		bitmapFont.draw(spriteBatch, "Touch over the slingshot and drag to modify the trajectory", Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.9f);
		bitmapFont.draw(spriteBatch, "5 and 6 to switch fixed horizontal distance between points (" + controller.fixedHorizontalDistance + ")", Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.8f);
		spriteBatch.end();
		
		if (controller.charging) {
			shapeRenderer.begin(ShapeType.FilledTriangle);
			shapeRenderer.setColor(0.2f, 0f, 0f, 1f);
			// shapeRenderer.filledTriangle(50f, 50f, 55f, 55f, 60f, 60f);
			shapeRenderer.filledTriangle(120f, 124f, 120f, 130f, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
			shapeRenderer.filledTriangle(140f, 144f, 140f, 150f, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
			shapeRenderer.end();
		} else {
			shapeRenderer.begin(ShapeType.FilledTriangle);
			shapeRenderer.setColor(0.2f, 0f, 0f, 1f);
			// shapeRenderer.filledTriangle(50f, 50f, 55f, 55f, 60f, 60f);
//			shapeRenderer.filledTriangle(120f, 124f, 120f, 130f, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
			shapeRenderer.filledTriangle(140f, 144f, 140f, 150f, 120f, 124f);
			shapeRenderer.filledTriangle(120f, 124f, 120f, 130f, 140f, 150f);
			shapeRenderer.end();
			
		}
		
		spriteBatch.begin();
		slingshotSprite.draw(spriteBatch);
		spriteBatch.end();

		stage.act(delta);
		stage.draw();



		// if (Gdx.input.isKeyPressed(Keys.UP)) {
		// controller.angle += 15f * Gdx.graphics.getDeltaTime();
		// if (controller.angle > 80f)
		// controller.angle = -30f;
		// }
		//
		// if (Gdx.input.isKeyPressed(Keys.DOWN)) {
		// controller.angle -= 10f * Gdx.graphics.getDeltaTime();
		// if (controller.angle < -30f)
		// controller.angle = 80f;
		// }

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
		targetTexture.dispose();
		backgroundTexture.dispose();
		groundTexture.dispose();
		slingshotTexture.dispose();
	}

}
