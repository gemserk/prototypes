package com.gemserk.prototypes.algorithms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.ConvexHull2d;
import com.gemserk.commons.gdx.graphics.ConvexHull2dImpl;
import com.gemserk.commons.gdx.graphics.ShapeUtils;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class PixmapConvexHull2dPrototype extends GameStateImpl {

	private GL10 gl;
	private SpriteBatch spriteBatch;

	private OrthographicCamera worldCamera;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private ShapeRenderer shapeRenderer;

	private Array<Vector2> points;

	private ConvexHull2d convexHull2d = new ConvexHull2dImpl(5);
	private ConvexHull2d smallConvexHull2d = new ConvexHull2dImpl(5);

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		worldCamera = new OrthographicCamera();

		worldCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// worldCamera.translate(-Gdx.graphics.getWidth() * 0.5f, -Gdx.graphics.getHeight() * 0.5f, 0f);
		worldCamera.update();

		shapeRenderer = new ShapeRenderer();

		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("FarmTexture", "pixmapconvexhull/farm.png");
				// texture("FarmTexture", "physicseditor/island01.png");
				sprite("FarmSprite", "FarmTexture");
			}
		};

		farmSprite = resourceManager.getResourceValue("FarmSprite");

		Pixmap pixmap = new Pixmap(Gdx.files.internal("pixmapconvexhull/farm.png"));
		// Pixmap pixmap = new Pixmap(Gdx.files.internal("physicseditor/island01.png"));

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorPointerDown("touch", 0);
			}
		};

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 1f, 0f);

		points = new Array<Vector2>();

		Color color = new Color();

		for (int i = 0; i < pixmap.getWidth(); i++) {
			for (int j = 0; j < pixmap.getHeight(); j++) {
				int pixel = pixmap.getPixel(i, j);
				Color.rgba8888ToColor(color, pixel);
				if (color.a > 0) {
					convexHull2d.add(i, pixmap.getHeight() - j);
				}
			}
		}

		pixmap.dispose();

		convexHull2d.recalculate();

		int size = convexHull2d.getPointsCount();

		System.out.println("calculation without optimization: " + convexHull2d.getPointsCount() + " points");

		int i = 0;

		// removes aligned points!!

		while (i < size) {
			int p0 = i;
			float x0 = convexHull2d.getX(p0);
			float y0 = convexHull2d.getY(p0);

			boolean aligned = true;

			smallConvexHull2d.add(x0, y0);

			int p1 = 0;
			int p2 = 0;

			do {
				p1 = i + 1;
				p2 = i + 2;

				if (p1 >= size)
					p1 = p1 % size;

				if (p2 >= size)
					p2 = p2 % size;

				float x1 = convexHull2d.getX(p1);
				float y1 = convexHull2d.getY(p1);
				float x2 = convexHull2d.getX(p2);
				float y2 = convexHull2d.getY(p2);

				float triangleArea = Math.abs(ShapeUtils.area(x0, y0, x1, y1, x2, y2));
				aligned = triangleArea < 0.01f;

				if (!aligned)
					break;

				i++;

			} while (aligned && i < size);

			i++;
		}

		smallConvexHull2d.recalculate();

		System.out.println("calculation with optimization: " + smallConvexHull2d.getPointsCount() + " points");

	}

	Vector2 nearPoint = null;
	private ResourceManager<String> resourceManager;
	private Sprite farmSprite;

	@Override
	public void update() {
		super.update();

		inputDevicesMonitor.update();

		int x = Gdx.input.getX();
		int y = Gdx.graphics.getHeight() - Gdx.input.getY();

		// if (inputDevicesMonitor.getButton("touch").isPressed()) {
		// nearPoint = findNear(x, y, 15f);
		// }

		// if (inputDevicesMonitor.getButton("touch").isHolded()) {
		// if (nearPoint != null) {
		// nearPoint.set(x, y);
		// recalculateConvexHull(points, convexHull2d);
		// }
		// }
		//
		// if (inputDevicesMonitor.getButton("touch").isReleased()) {
		// if (nearPoint == null) {
		// points.add(new Vector2(x, y));
		// recalculateConvexHull(points, convexHull2d);
		// }
		// }

	}

	// private Vector2 findNear(float x, float y, float distance) {
	//
	// for (int i = 0; i < points.size; i++) {
	// Vector2 p = points.get(i);
	// if (p.dst(x, y) < distance)
	// return p;
	// }
	//
	// return null;
	// }
	//
	// private void recalculateConvexHull(Array<Vector2> points, ConvexHull2d convexHull2d) {
	// for (int i = 0; i < points.size; i++) {
	// Vector2 point = points.get(i);
	// convexHull2d.add(point.x, point.y);
	// }
	// convexHull2d.recalculate();
	// }

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.setProjectionMatrix(worldCamera.projection);
		spriteBatch.setTransformMatrix(worldCamera.view);
		spriteBatch.begin();
		farmSprite.draw(spriteBatch);
		spriteBatch.end();

		shapeRenderer.setProjectionMatrix(worldCamera.projection);
		shapeRenderer.setTransformMatrix(worldCamera.view);

		shapeRenderer.setColor(1f, 1f, 1f, 1f);
		shapeRenderer.begin(ShapeType.Line);
		for (int i = 0; i < convexHull2d.getPointsCount(); i++) {
			float x0 = convexHull2d.getX(i);
			float y0 = convexHull2d.getY(i);
			if (i + 1 == convexHull2d.getPointsCount()) {
				float x1 = convexHull2d.getX(0);
				float y1 = convexHull2d.getY(0);
				shapeRenderer.line(x0, y0, x1, y1);
				break;
			}
			float x1 = convexHull2d.getX(i + 1);
			float y1 = convexHull2d.getY(i + 1);
			shapeRenderer.line(x0, y0, x1, y1);
		}
		shapeRenderer.end();

		shapeRenderer.setColor(0f, 1f, 1f, 1f);
		shapeRenderer.begin(ShapeType.Line);
		for (int i = 0; i < smallConvexHull2d.getPointsCount(); i++) {
			float x0 = smallConvexHull2d.getX(i);
			float y0 = smallConvexHull2d.getY(i);
			if (i + 1 == smallConvexHull2d.getPointsCount()) {
				float x1 = smallConvexHull2d.getX(0);
				float y1 = smallConvexHull2d.getY(0);
				shapeRenderer.line(x0, y0, x1, y1);
				break;
			}
			float x1 = smallConvexHull2d.getX(i + 1);
			float y1 = smallConvexHull2d.getY(i + 1);
			shapeRenderer.line(x0, y0, x1, y1);
		}
		shapeRenderer.end();

		shapeRenderer.setColor(1f, 0f, 0f, 1f);
		shapeRenderer.begin(ShapeType.FilledCircle);
		for (int i = 0; i < convexHull2d.getPointsCount(); i++) {
			float x = convexHull2d.getX(i);
			float y = convexHull2d.getY(i);
			shapeRenderer.filledCircle(x, y, 1f, 5);
		}
		shapeRenderer.end();

	}

	@Override
	public void dispose() {
		super.dispose();
		spriteBatch.dispose();
		shapeRenderer.dispose();
		resourceManager.unloadAll();
	}

}