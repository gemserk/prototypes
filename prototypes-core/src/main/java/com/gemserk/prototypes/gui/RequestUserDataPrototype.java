package com.gemserk.prototypes.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.highscores.client.User;
import com.gemserk.highscores.gui.RegisterUserListener;
import com.gemserk.highscores.gui.UserDataRegistrator;

public class RequestUserDataPrototype extends GameStateImpl {

	private GL10 gl;
	private InputDevicesMonitorImpl inputDevicesMonitor;
	
	private UserDataRegistrator userDataRegistrator;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();
		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);
		
		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorPointerDown("touch", 0);
			}
		};
	}

	@Override
	public void update() {
		super.update();
		inputDevicesMonitor.update();
		
		if (inputDevicesMonitor.getButton("touch").isReleased()) {
			userDataRegistrator.requestUserData(new RegisterUserListener() {
				@Override
				public void cancelled() {
					System.out.println("cancelled");
				}
				
				@Override
				public void accepted(User user) {
					System.out.println("accepted! " + user.getUsername() + ", " + user.getName()+ ", " + user.getPrivatekey());
				}
			}, "player779123", "player779123");
		}
		
	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}