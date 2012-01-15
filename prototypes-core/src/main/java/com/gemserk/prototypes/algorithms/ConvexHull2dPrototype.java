package com.gemserk.prototypes.algorithms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;

public class ConvexHull2dPrototype extends GameStateImpl {

	private GL10 gl;
	private SpriteBatch spriteBatch;

	private OrthographicCamera worldCamera;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private World world;

	private ShapeRenderer shapeRenderer;

	private Array<Vector2> points;
	private Array<Vector2> polygonPoints;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		worldCamera = new OrthographicCamera();

		worldCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		worldCamera.update();

		shapeRenderer = new ShapeRenderer();

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorPointerDown("touch", 0);
			}
		};

		world = new World(new Vector2(), false);

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 1f, 0f);

		points = new Array<Vector2>();
		polygonPoints = new Array<Vector2>();
	}

	Vector2 nearPoint = null;

	@Override
	public void update() {
		super.update();

		world.step(GlobalTime.getDelta(), 5, 5);

		inputDevicesMonitor.update();

		int x = Gdx.input.getX();
		int y = Gdx.graphics.getHeight() - Gdx.input.getY();

		if (inputDevicesMonitor.getButton("touch").isPressed()) {
			nearPoint = findNear(x, y, 4f);
		}

		if (inputDevicesMonitor.getButton("touch").isHolded()) {
			if (nearPoint != null) {
				nearPoint.set(x, y);
				recalculateConvexHull();
			}
		}

		if (inputDevicesMonitor.getButton("touch").isReleased()) {
			if (nearPoint == null) {
				points.add(new Vector2(x, y));
				recalculateConvexHull();
			}
		}

	}

	private Vector2 findNear(float x, float y, float distance) {

		for (int i = 0; i < points.size; i++) {
			Vector2 p = points.get(i);
			if (p.dst(x, y) < distance)
				return p;
		}

		return null;
	}

	private void recalculateConvexHull() {

		// extracted algorithm to calculate a convex hull of a 2d polygon from http://www.cse.unsw.edu.au/~lambert/java/3d/ConvexHull.html

		polygonPoints.clear();

		if (points.size <= 1)
			return;

		Vector2 p;
		Vector2 bot = points.get(0);

		for (int i = 1; i < points.size; i++) {
			Vector2 point = points.get(i);
			if (point.y < bot.y)
				bot = point;
		}

		polygonPoints.add(bot);

		p = bot;

		do {
			int i;
			i = points.get(0) == p ? 1 : 0;
			Vector2 cand = points.get(i);

			for (i = i + 1; i < points.size; i++) {
				Vector2 point = points.get(i);
				if (point != p && area(p, cand, point) > 0)
					cand = points.get(i);
			}

			polygonPoints.add(cand);
			p = cand;
		} while (p != bot);

	}

	/* signed area of a triangle */
	float area(Vector2 a, Vector2 b, Vector2 c) {
		return b.x * c.y - b.y * c.x + c.x * a.y - c.y * a.x + a.x * b.y - a.y * b.x;
	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// spriteBatch.setProjectionMatrix(worldCamera.projection);
		// spriteBatch.setTransformMatrix(worldCamera.view);
		// spriteBatch.begin();
		// spriteBatch.end();

		shapeRenderer.setProjectionMatrix(worldCamera.projection);
		shapeRenderer.setTransformMatrix(worldCamera.view);

		if (polygonPoints.size > 2) {
			shapeRenderer.setColor(1f, 1f, 1f, 1f);
			shapeRenderer.begin(ShapeType.Line);
			for (int i = 0; i < polygonPoints.size; i++) {
				Vector2 p0 = polygonPoints.get(i);
				if (i + 1 == polygonPoints.size)
					break;
				Vector2 p1 = polygonPoints.get(i + 1);
				shapeRenderer.line(p0.x, p0.y, p1.x, p1.y);
			}
			shapeRenderer.end();
		}

		shapeRenderer.setColor(1f, 0f, 0f, 1f);
		shapeRenderer.begin(ShapeType.FilledCircle);
		for (int i = 0; i < points.size; i++) {
			Vector2 p0 = points.get(i);
			shapeRenderer.filledCircle(p0.x, p0.y, 4f);
			// shapeRenderer.point(p0.x, p0.y, 0f);
		}
		shapeRenderer.end();

	}

}