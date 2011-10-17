package com.gemserk.prototypes.pixmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.gemserk.commons.gdx.graphics.ColorUtils;

public class PixmapHelper implements Disposable {
	
	private class PixmapChange {
		
		int x, y;
		
		void set(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public Pixmap pixmap;
	public Sprite sprite;
	public Texture texture;

	public final Color color = new Color();

	// only allow 10 modifications
	private PixmapChange[] modifications = new PixmapChange[10];
	private int lastModification = 0;
	private Pixmap renderPixmap;

	public PixmapHelper(Pixmap pixmap, Sprite sprite, Texture texture) {
		this.pixmap = pixmap;
		this.sprite = sprite;
		this.texture = texture;

		for (int i = 0; i < modifications.length; i++)
			modifications[i] = new PixmapChange();
		this.renderPixmap = new Pixmap(64, 64, Format.RGBA8888);
	}

	public PixmapHelper(Pixmap pixmap) {
		this.pixmap = pixmap;
		this.texture = new Texture(new PixmapTextureData(pixmap, pixmap.getFormat(), false, false));
		this.texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.sprite = new Sprite(texture);

		for (int i = 0; i < modifications.length; i++)
			modifications[i] = new PixmapChange();
		this.renderPixmap = new Pixmap(64, 64, Format.RGBA8888);
	}

	/**
	 * Projects the coordinates (x, y) to the Pixmap coordinates system and store the result in the specified Vector2.
	 * 
	 * @param position
	 *            The Vector2 to store the transformed coordinates.
	 * @param x
	 *            The x coordinate to be projected.
	 * @param y
	 *            The y coordinate to be prjected
	 */
	public void project(Vector2 position, float x, float y) {
		position.set(x, y);

		float centerX = sprite.getX() + sprite.getOriginX();
		float centerY = sprite.getY() + sprite.getOriginY();

		position.add(-centerX, -centerY);

		position.rotate(-sprite.getRotation());

		float scaleX = pixmap.getWidth() / sprite.getWidth();
		float scaleY = pixmap.getHeight() / sprite.getHeight();

		position.x *= scaleX;
		position.y *= scaleY;

		position.add( //
				pixmap.getWidth() * 0.5f, //
				-pixmap.getHeight() * 0.5f //
		);

		position.y *= -1f;
	}

	public int getPixel(Vector2 position) {
		return getPixel(position.x, position.y);
	}

	public int getPixel(float x, float y) {
		return pixmap.getPixel((int) x, (int) y);
	}

	public void setPixel(float x, float y, int value) {
		ColorUtils.rgba8888ToColor(color, value);
		pixmap.setColor(color);
	}

	public void eraseCircle(float x, float y, float radius) {
		if (lastModification == modifications.length)
			return;
		
		if (x + radius < 0 || y + radius < 0) 
			return;
		
		if (x - radius > pixmap.getWidth() || y - radius > pixmap.getHeight())
			return;

		Blending blending = Pixmap.getBlending();
		pixmap.setColor(0f, 0f, 0f, 0f);
		Pixmap.setBlending(Blending.None);

		float scaleX = pixmap.getWidth() / sprite.getWidth();

		int newRadius = Math.round(radius * scaleX);
		int newX = Math.round(x);
		int newY = Math.round(y);

		pixmap.fillCircle(newX, newY, newRadius);
		Pixmap.setBlending(blending);

		modifications[lastModification++].set(newX, newY);
	}

	public void updateTexture() {

		if (lastModification == 0)
			return;

		Gdx.gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.getTextureObjectHandle());

		int width = pixmap.getWidth();
		int height = pixmap.getHeight();
		
		int dstWidth = renderPixmap.getWidth();
		int dstHeight = renderPixmap.getHeight();

		for (int i = 0; i < lastModification; i++) {

			PixmapChange pixmapChange = modifications[i];

			Pixmap.setBlending(Blending.None);
			
			int x = Math.round(pixmapChange.x) - dstWidth / 2;
			int y = Math.round(pixmapChange.y) - dstHeight / 2;

			if (x + dstWidth > width)
				x = width - dstWidth;
			else if (x < 0)
				x = 0;

			if (y + dstHeight > height)
				y = height - dstHeight;
			else if (y < 0) {
				y = 0;
			}
			
			renderPixmap.drawPixmap(pixmap, 0, 0, x, y, dstWidth, dstHeight);

			Gdx.gl.glTexSubImage2D(GL10.GL_TEXTURE_2D, 0, x, y, dstWidth, dstHeight, //
					renderPixmap.getGLFormat(), renderPixmap.getGLType(), renderPixmap.getPixels());

		}

		lastModification = 0;
	}

	/**
	 * Reload all the pixmap data to the opengl texture.
	 */
	public void reload() {
		texture.load(new PixmapTextureData(pixmap, pixmap.getFormat(), false, false));
	}

	@Override
	public void dispose() {
		this.pixmap.dispose();
		this.texture.dispose();
		this.renderPixmap.dispose();
	}

}