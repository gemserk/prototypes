package com.gemserk.prototypes.spriteatlas;

import java.text.MessageFormat;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.ImmediateModeRendererUtils;
import com.gemserk.commons.gdx.graphics.SpriteUtils;

public class SpriteAtlasBugPrototype extends GameStateImpl {

	/** Describes the region of a packed image and provides information about the original image before it was packed. */
	static public class AtlasRegion extends TextureRegion {
		/**
		 * The number at the end of the original image file name, or -1 if none.<br>
		 * <br>
		 * When sprites are packed, if the original file name ends with a number, it is stored as the index and is not considered as part of the sprite's name. This is useful for keeping animation frames in order.
		 * 
		 * @see TextureAtlas#findRegions(String)
		 */
		public int index;

		/**
		 * The name of the original image file, up to the first underscore. Underscores denote special instructions to the texture packer.
		 */
		public String name;

		/** The offset from the left of the original image to the left of the packed image, after whitespace was removed for packing. */
		public float offsetX;

		/**
		 * The offset from the bottom of the original image to the bottom of the packed image, after whitespace was removed for packing.
		 */
		public float offsetY;

		/** The width of the image, after whitespace was removed for packing. */
		public int packedWidth;

		/** The height of the image, after whitespace was removed for packing. */
		public int packedHeight;

		/** The width of the image, before whitespace was removed for packing. */
		public int originalWidth;

		/** The height of the image, before whitespace was removed for packing. */
		public int originalHeight;

		/** If true, the region has been rotated 90 degrees counter clockwise. */
		public boolean rotate;

		public AtlasRegion(Texture texture, int x, int y, int width, int height) {
			super(texture, x, y, width, height);
			packedWidth = width;
			packedHeight = height;
		}

		public AtlasRegion(AtlasRegion region) {
			setRegion(region);
			index = region.index;
			name = region.name;
			offsetX = region.offsetX;
			offsetY = region.offsetY;
			packedWidth = region.packedWidth;
			packedHeight = region.packedHeight;
			originalWidth = region.originalWidth;
			originalHeight = region.originalHeight;
			rotate = region.rotate;
		}

		/**
		 * Flips the region, adjusting the offset so the image appears to be flipped as if no whitespace has been removed for packing.
		 */
		public void flip(boolean x, boolean y) {
			super.flip(x, y);
			if (x)
				offsetX = originalWidth - offsetX - packedWidth;
			if (y)
				offsetY = originalHeight - offsetY - packedHeight;
		}
	}

	/**
	 * A sprite that, if whitespace was stripped from the region when it was packed, is automatically positioned as if whitespace had not been stripped.
	 */
	static public class AtlasSprite extends Sprite {
		final AtlasRegion region;
		float originalOffsetX, originalOffsetY;

		public AtlasSprite(AtlasRegion region) {
			this.region = new AtlasRegion(region);
			originalOffsetX = region.offsetX;
			originalOffsetY = region.offsetY;
			setRegion(region);
			setOrigin(region.originalWidth / 2f, region.originalHeight / 2f);
			int width = Math.abs(region.getRegionWidth());
			int height = Math.abs(region.getRegionHeight());
			if (region.rotate) {
				super.rotate90(true);
				super.setBounds(region.offsetX, region.offsetY, height, width);
			} else
				super.setBounds(region.offsetX, region.offsetY, width, height);
			setColor(1, 1, 1, 1);
		}

		public void setPosition(float x, float y) {
			super.setPosition(x + region.offsetX, y + region.offsetY);
		}

		public void setBounds(float x, float y, float width, float height) {
			float widthRatio = width / region.originalWidth;
			float heightRatio = height / region.originalHeight;
			region.offsetX = originalOffsetX * widthRatio;
			region.offsetY = originalOffsetY * heightRatio;
			super.setBounds(x + region.offsetX, y + region.offsetY, region.packedWidth * widthRatio, region.packedHeight * heightRatio);
		}

		public void setSize(float width, float height) {
			super.setSize(width, height);
			setBounds(getX(), getY(), width, height);
		}

		public void setOrigin(float originX, float originY) {
			super.setOrigin(originX - region.offsetX, originY - region.offsetY);
		}

		public void flip(boolean x, boolean y) {
			// Flip texture.
			super.flip(x, y);

			float oldOriginX = getOriginX();
			float oldOriginY = getOriginY();
			float oldOffsetX = region.offsetX;
			float oldOffsetY = region.offsetY;

			// Updates x and y offsets.
			region.flip(x, y);

			// Update position and origin with new offsets.
			translate(region.offsetX - oldOffsetX, region.offsetY - oldOffsetY);
			setOrigin(oldOriginX, oldOriginY);
		}

		public float getX() {
			return super.getX() - region.offsetX;
		}

		public float getY() {
			return super.getY() - region.offsetY;
		}

		public float getOriginX() {
			return super.getOriginX() + region.offsetX;
		}

		public float getOriginY() {
			return super.getOriginY() + region.offsetY;
		}

		public float getWidth() {
			return super.getWidth() / region.packedWidth * region.originalWidth;
		}

		public float getHeight() {
			return super.getHeight() / region.packedHeight * region.originalHeight;
		}

		public AtlasRegion getAtlasRegion() {
			return region;
		}

		@Override
		public void rotate90(boolean clockwise) {
			super.rotate90(clockwise);

			// float originalOffsetX2 = originalOffsetX;
			// float originalOffsetY2 = originalOffsetY;
			//
			// originalOffsetX = originalOffsetY2;
			// originalOffsetY = originalOffsetX2;
			//
			// float offsetX = region.offsetX;
			// float offsetY = region.offsetY;
			// int originalWidth = region.originalWidth;
			// int originalHeight = region.originalHeight;
			// int packedWidth = region.packedWidth;
			// int packedHeight = region.packedHeight;
			//
			// region.offsetX = offsetY;
			// region.offsetY = offsetX;
			// region.originalWidth = originalHeight;
			// region.originalHeight = originalWidth;
			// region.packedWidth = packedHeight;
			// region.packedHeight = packedWidth;
			// region.rotate = !region.rotate;

			//
			// // super.setSize(super.getHeight(), super.getWidth());
			//
			// // super.setBounds(region.offsetX, region.offsetY, region.packedHeight, region.packedWidth);
		}

	}

	private SpriteBatch spriteBatch;
	private Texture texture;

	private ArrayList<Sprite> sprites;

	@Override
	public void init() {
		spriteBatch = new SpriteBatch();
		sprites = new ArrayList<Sprite>();
		texture = new Texture(Gdx.files.internal("data/spriteatlastest/spriteatlastest1.png"));

		AtlasRegion atlasRegion = new AtlasRegion(texture, 2, 2, 97, 38);
		atlasRegion.originalWidth = 128;
		atlasRegion.originalHeight = 64;
		atlasRegion.offsetX = 0f;
		atlasRegion.offsetY = 0f;

		AtlasRegion atlasRegion2 = new AtlasRegion(texture, 2, 2, 97, 38);
		atlasRegion2.originalWidth = 128;
		atlasRegion2.originalHeight = 64;
		atlasRegion2.offsetX = 0f;
		atlasRegion2.offsetY = 64f;
		atlasRegion2.rotate = true;

		Sprite normal = new Sprite(atlasRegion2);
		System.out.println(MessageFormat.format("{0}x{1}", normal.getWidth(), normal.getHeight()));
//		normal.rotate90(true);
//		normal.setSize(normal.getHeight(), normal.getWidth());
		
		Sprite rotated = new AtlasSprite(atlasRegion2);
//		rotated.rotate90(false);

//		System.out.println(MessageFormat.format("{0}x{1}", rotated.getWidth(), rotated.getHeight()));
//		rotated.setSize(rotated.getHeight(), rotated.getWidth());
//		System.out.println(MessageFormat.format("{0}x{1}", rotated.getWidth(), rotated.getHeight()));
		
		Gdx.gl.glClearColor(0f, 0f, 1f, 1f);

		normal.setPosition(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		rotated.setPosition(Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.5f);
		
		Rectangle normalBounds = normal.getBoundingRectangle();
		Rectangle rotatedBounds = rotated.getBoundingRectangle();
		
		System.out.println(MessageFormat.format("normal.bounds: {0}", normalBounds));
		System.out.println(MessageFormat.format("rotated.bounds: {0}", rotatedBounds));

		sprites.add(normal);
		sprites.add(rotated);

		{
			Sprite sprite = new Sprite(atlasRegion);
			SpriteUtils.transformSprite(sprite, 2f, 0.5f, 0.5f, false, false, true, true);
			sprite.setPosition(Gdx.graphics.getWidth() * 0.15f, Gdx.graphics.getHeight() * 0.25f);
			sprites.add(sprite);
		}

		{
			Sprite sprite = new AtlasSprite(atlasRegion);
			SpriteUtils.transformSprite(sprite, 2f, 0.5f, 0.5f, false, false, false, false);
			sprite.setPosition(Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.25f);
			sprites.add(sprite);
		}

		{
			Sprite sprite = new AtlasSprite(atlasRegion);
			SpriteUtils.transformSprite(sprite, 2f, 0.5f, 0.5f, false, false, true, true);
			sprite.setPosition(Gdx.graphics.getWidth() * 0.65f, Gdx.graphics.getHeight() * 0.15f);
			sprites.add(sprite);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		texture.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		for (int i = 0; i < sprites.size(); i++) {
			sprites.get(i).draw(spriteBatch);
			ImmediateModeRendererUtils.drawRectangle(sprites.get(i).getBoundingRectangle(), Color.WHITE);
		}
		spriteBatch.end();

	}

}
