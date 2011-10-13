package com.gemserk.prototypes.pixmap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.gemserk.commons.gdx.graphics.ColorUtils;

public class PixmapHelper implements Disposable {

	public Pixmap pixmap;
	public Sprite sprite;
	public Texture texture;

	public final Color color = new Color();

	public PixmapHelper(Pixmap pixmap, Sprite sprite, Texture texture) {
		this.pixmap = pixmap;
		this.sprite = sprite;
		this.texture = texture;
	}
	
	public PixmapHelper(Pixmap pixmap) {
		this.pixmap = pixmap;
		this.texture = new Texture(new PixmapTextureData(pixmap, pixmap.getFormat(), false, false));
		this.texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.sprite = new Sprite(texture);
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

	public void drawPixel(float x, float y, float r, float g, float b, float a, float radius) {
		pixmap.setColor(r, g, b, a);
		pixmap.fillCircle(Math.round(x), Math.round(y), Math.round(radius));
		texture.draw(pixmap, 0, 0);
	}

	public void eraseCircle(float x, float y, float radius) {
		Blending blending = Pixmap.getBlending();
		pixmap.setColor(0f, 0f, 0f, 0f);
		Pixmap.setBlending(Blending.None);

		float scaleX = pixmap.getWidth() / sprite.getWidth();
		System.out.println(scaleX);

		pixmap.fillCircle(Math.round(x), Math.round(y), Math.round(radius * scaleX));
		texture.draw(pixmap, 0, 0);
		Pixmap.setBlending(blending);
	}

	public void reloadTexture() {
		texture.load(new PixmapTextureData(pixmap, pixmap.getFormat(), false, false));
	}

	@Override
	public void dispose() {
		this.pixmap.dispose();
		this.texture.dispose();
	}

}