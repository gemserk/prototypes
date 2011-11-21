package com.gemserk.prototypes.launcher;

import java.awt.BorderLayout;
import java.awt.Canvas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.gemserk.commons.utils.BrowserUtilsDesktopImpl;
import com.gemserk.commons.utils.FacebookUtilsDesktopImpl;
import com.gemserk.commons.utils.MailUtilsDesktopImpl;
import com.gemserk.commons.utils.gdx.LwjglLibgdxLibraryUtils;
import com.gemserk.prototypes.Launcher;
import com.gemserk.prototypes.Utils;

public class Applet extends java.applet.Applet {

	private static final long serialVersionUID = 6396112708370503447L;

	private Canvas canvas;

	private LwjglApplication application;

	public void start() {

	}

	public void stop() {

	}

	public void destroy() {
		remove(canvas);
		super.destroy();
	}

	public void init() {

		GdxNativesLoader.disableNativesLoading = true;
		LwjglLibgdxLibraryUtils.loadLibgdxLibrary();
		
		Utils.mailUtils = new MailUtilsDesktopImpl();
		Utils.facebookUtils = new FacebookUtilsDesktopImpl(new BrowserUtilsDesktopImpl());

		try {
			setLayout(new BorderLayout());
			// ApplicationListener game = (ApplicationListener) Class.forName(getParameter("game")).newInstance();

			canvas = new Canvas() {
				public final void addNotify() {
					super.addNotify();

					Launcher launcher = new Launcher() {
						@Override
						public void create() {
							Gdx.graphics.setVSync(true);
							super.create();
						};
					};

					application = new LwjglApplication(launcher, false, this) {
						public com.badlogic.gdx.Application.ApplicationType getType() {
							return ApplicationType.Applet;
						};
					};
				}

				public final void removeNotify() {
					application.stop();
					super.removeNotify();
				}
			};
			canvas.setSize(getWidth(), getHeight());
			add(canvas);
			canvas.setFocusable(true);
			canvas.requestFocus();
			canvas.setIgnoreRepaint(true);
			setVisible(true);
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException("Unable to create display", e);
		}
	}
}