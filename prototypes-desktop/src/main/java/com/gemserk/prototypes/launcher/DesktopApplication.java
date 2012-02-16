package com.gemserk.prototypes.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.commons.utils.BrowserUtilsDesktopImpl;
import com.gemserk.commons.utils.FacebookUtilsDesktopImpl;
import com.gemserk.commons.utils.MailUtilsDesktopImpl;
import com.gemserk.highscores.gui.RequestUserListener;
import com.gemserk.highscores.gui.UserDataRegistrator;
import com.gemserk.prototypes.Launcher;
import com.gemserk.prototypes.Utils;

public class DesktopApplication {

	protected static final Logger logger = LoggerFactory.getLogger(DesktopApplication.class);

	private static class Arguments {

		int width = 800;
		int height = 480;

		public void parse(String[] argv) {
			if (argv.length == 0)
				return;

			String displayString = argv[0];
			String[] displayValues = displayString.split("x");

			if (displayValues.length < 2)
				return;

			try {
				width = Integer.parseInt(displayValues[0]);
				height = Integer.parseInt(displayValues[1]);
			} catch (NumberFormatException e) {
				System.out.println("error when parsing resolution from arguments: " + displayString);
			}

		}

	}

	public static void main(String[] argv) {

		Arguments arguments = new Arguments();
		arguments.parse(argv);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		Utils.mailUtils = new MailUtilsDesktopImpl();
		Utils.facebookUtils = new FacebookUtilsDesktopImpl(new BrowserUtilsDesktopImpl());
		Utils.browserUtils = new BrowserUtilsDesktopImpl();

		config.title = "Gemserk's Prototypes";
		config.width = arguments.width;
		config.height = arguments.height;
		config.fullscreen = false;
		config.useGL20 = false;
		config.useCPUSynch = true;
		config.forceExit = true;
		config.vSyncEnabled = true;

		Injector injector = new InjectorImpl();

		UserDataRegistrator userDataRegistrator = new UserDataRegistrator() {
			RegisterUserJFrame registerUserJFrame = new RegisterUserJFrame();
			@Override
			public void requestUserData(RequestUserListener requestUserListener, String currentUsername, String currentName) {
				registerUserJFrame.handle(requestUserListener, currentUsername, currentName);				
			}
		};

		injector.bind("userDataRegistrator", userDataRegistrator);

		// Game game = new LightingPrototype();
		// Game game = new PixmapFromTextureAtlasPrototype();
		// Game game = new PixmapCollisionPrototype();

		// Game game = new com.gemserk.prototypes.launcher.Launcher();

		ApplicationListener game = new Launcher();

		injector.injectMembers(game);

		// boolean runningInDebug = System.getProperty("runningInDebug") != null;

		new LwjglApplication(game, config);
	}

}
