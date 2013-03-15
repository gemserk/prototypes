package com.gemserk.prototypes.superangrysheep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.ImmediateModeRendererUtils;
import com.gemserk.commons.gdx.graphics.SpriteUtils;

public class SpriteScissorsPrototype extends GameStateImpl {

	private SpriteBatch spriteBatch;
	private Texture texture;
	private Sprite sprite;
	private SpriteScissors spriteScissors;
	private Rectangle rectangle;
	private ShapeRenderer shapeRenderer;

	static class SpriteScissors {

		private Sprite spriteBase;
		private Sprite sprite;

		public Sprite getSpriteBase() {
			return spriteBase;
		}

		public SpriteScissors(Sprite sprite) {
			this.spriteBase = SpriteUtils.cloneSprite(sprite);
			this.sprite = sprite;
		}

		public void cut(Rectangle area) {
			spriteBase.setPosition(sprite.getX(), sprite.getY());

			float[] vertices = sprite.getVertices();
			float[] spriteBaseVertices = spriteBase.getVertices();

			float rectangleX1 = area.x;
			float rectangleX2 = rectangleX1 + area.width;

			vertices[SpriteBatch.X1] = truncateMin(vertices[SpriteBatch.X1], rectangleX1);
			vertices[SpriteBatch.X2] = truncateMin(vertices[SpriteBatch.X2], rectangleX1);
			vertices[SpriteBatch.X3] = truncateMin(vertices[SpriteBatch.X3], rectangleX1);
			vertices[SpriteBatch.X4] = truncateMin(vertices[SpriteBatch.X4], rectangleX1);

			vertices[SpriteBatch.X1] = truncateMax(vertices[SpriteBatch.X1], rectangleX2);
			vertices[SpriteBatch.X2] = truncateMax(vertices[SpriteBatch.X2], rectangleX2);
			vertices[SpriteBatch.X3] = truncateMax(vertices[SpriteBatch.X3], rectangleX2);
			vertices[SpriteBatch.X4] = truncateMax(vertices[SpriteBatch.X4], rectangleX2);

			float rectangleY1 = area.y;
			float rectangleY2 = rectangleY1 + area.height;

			vertices[SpriteBatch.Y1] = truncateMin(vertices[SpriteBatch.Y1], rectangleY1);
			vertices[SpriteBatch.Y2] = truncateMin(vertices[SpriteBatch.Y2], rectangleY1);
			vertices[SpriteBatch.Y3] = truncateMin(vertices[SpriteBatch.Y3], rectangleY1);
			vertices[SpriteBatch.Y4] = truncateMin(vertices[SpriteBatch.Y4], rectangleY1);

			vertices[SpriteBatch.Y1] = truncateMax(vertices[SpriteBatch.Y1], rectangleY2);
			vertices[SpriteBatch.Y2] = truncateMax(vertices[SpriteBatch.Y2], rectangleY2);
			vertices[SpriteBatch.Y3] = truncateMax(vertices[SpriteBatch.Y3], rectangleY2);
			vertices[SpriteBatch.Y4] = truncateMax(vertices[SpriteBatch.Y4], rectangleY2);
			
			vertices[SpriteBatch.U1] = spriteBaseVertices[SpriteBatch.U1];
			vertices[SpriteBatch.U2] = spriteBaseVertices[SpriteBatch.U2];
			vertices[SpriteBatch.U3] = spriteBaseVertices[SpriteBatch.U3];
			vertices[SpriteBatch.U4] = spriteBaseVertices[SpriteBatch.U4];
			
			vertices[SpriteBatch.V1] = spriteBaseVertices[SpriteBatch.V1];
			vertices[SpriteBatch.V2] = spriteBaseVertices[SpriteBatch.V2];
			vertices[SpriteBatch.V3] = spriteBaseVertices[SpriteBatch.V3];
			vertices[SpriteBatch.V4] = spriteBaseVertices[SpriteBatch.V4];

			if (vertices[SpriteBatch.X3] < spriteBaseVertices[SpriteBatch.X3])
			{
				float u1 = spriteBase.getU();
				float u2 = spriteBase.getU2();

				float _u3 = ((vertices[SpriteBatch.X3] - vertices[SpriteBatch.X1]) / spriteBase.getWidth()) * (u2 - u1);

				vertices[SpriteBatch.U3] = _u3;
				vertices[SpriteBatch.U4] = _u3;
			}
			
			if (vertices[SpriteBatch.X1] > spriteBaseVertices[SpriteBatch.X1])
			{
				float u1 = spriteBase.getU();
				float u2 = spriteBase.getU2();

				float _u3 = ((vertices[SpriteBatch.X3] - vertices[SpriteBatch.X1]) / spriteBase.getWidth()) * (u2 - u1);

				vertices[SpriteBatch.U1] = u2 - _u3;
				vertices[SpriteBatch.U2] = u2 - _u3;
			}
			
			if (vertices[SpriteBatch.Y1] > spriteBaseVertices[SpriteBatch.Y1])
			{
				float v1 = spriteBase.getV();
				float v2 = spriteBase.getV2();

				float diff = ((vertices[SpriteBatch.Y2] - vertices[SpriteBatch.Y1]) / spriteBase.getHeight()) * (v2 - v1);

				vertices[SpriteBatch.V2] = v2 - diff;
				vertices[SpriteBatch.V3] = v2 - diff;
			}
			
			if (vertices[SpriteBatch.Y2] < spriteBaseVertices[SpriteBatch.Y2])
			{
				float v1 = spriteBase.getV();
				float v2 = spriteBase.getV2();

				float diff = ((vertices[SpriteBatch.Y2] - vertices[SpriteBatch.Y1]) / spriteBase.getHeight()) * (v2 - v1);

				vertices[SpriteBatch.V1] = diff;
				vertices[SpriteBatch.V4] = diff;
			}
			
//			{
//				float v1 = spriteBase.getV();
//				float v2 = spriteBase.getV2();
//
//				float height = (vertices[SpriteBatch.Y2] - vertices[SpriteBatch.Y1]) / spriteBase.getHeight();
//				// System.out.println(height);
//				float _v2 = v1 + height * (v2 - v1);
//				System.out.println(_v2);
//				System.out.println(v2);
//
//				vertices[SpriteBatch.V2] = v2;
//				vertices[SpriteBatch.V3] = v2;
//			}

		}

		static final float truncateMin(float x, float limit) {
			if (x < limit)
				return limit;
			return x;
		}

		static final float truncateMax(float x, float limit) {
			if (x > limit)
				return limit;
			return x;
		}

	}

	@Override
	public void init() {
		spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		Texture.setEnforcePotImages(false);
		texture = new Texture(Gdx.files.internal("pixmapconvexhull/farm.png"));
		sprite = new Sprite(texture);
		spriteScissors = new SpriteScissors(sprite);
		rectangle = new Rectangle(200, 80, 400, 320);
	}

	@Override
	public void update() {
		super.update();
		SpriteUtils.centerOn(sprite, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0.5f, 0.5f);
		spriteScissors.cut(rectangle);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		sprite.draw(spriteBatch);
		spriteBatch.end();

		shapeRenderer.begin(ShapeType.Rectangle);
		{
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

			Rectangle boundingRectangle = sprite.getBoundingRectangle();
			shapeRenderer.setColor(Color.BLUE);
			shapeRenderer.rect(boundingRectangle.x, boundingRectangle.y, boundingRectangle.width, boundingRectangle.height);

			boundingRectangle = spriteScissors.getSpriteBase().getBoundingRectangle();
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.rect(boundingRectangle.x, boundingRectangle.y, boundingRectangle.width, boundingRectangle.height);

		}
		shapeRenderer.end();

	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		texture.dispose();
		shapeRenderer.dispose();
	}

}