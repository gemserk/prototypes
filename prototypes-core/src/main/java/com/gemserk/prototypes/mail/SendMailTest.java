package com.gemserk.prototypes.mail;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.prototypes.Utils;

public class SendMailTest extends GameStateImpl {
	
	private GL10 gl;
	private SpriteBatch spriteBatch;

	private BitmapFont font;
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();
		spriteBatch = new SpriteBatch();
		
		font = new BitmapFont();
		
		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorPointerDown("sendMail", 0);
			}
		};

	}

	@Override
	public void update() {
		super.update();
		
		inputDevicesMonitor.update();
		
		if (inputDevicesMonitor.getButton("sendMail").isReleased()) {
			Utils.mailUtils.send("webmaster@gemserk.com", "the subject", "the contents");
		}

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		SpriteBatchUtils.drawMultilineText(spriteBatch, font, "touch to send mail", Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f, 0.5f, 0.5f);
		spriteBatch.end();
		
	}

	@Override
	public void dispose() {
		font.dispose();
		spriteBatch.dispose();
	}
}

