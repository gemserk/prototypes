package com.gemserk.prototypes.launcher;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.VisitorData;
import com.gemserk.analytics.googleanalytics.android.AnalyticsStoredConfig;
import com.gemserk.analytics.googleanalytics.android.BasicConfig;
import com.gemserk.prototypes.Game;

public class AndroidApplication extends com.badlogic.gdx.backends.android.AndroidApplication {

	private AnalyticsStoredConfig storedConfig;
	private VisitorData visitorData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout layout = new RelativeLayout(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		config.useGL20 = false;
		config.useAccelerometer = true;
		config.useCompass = true;
		config.useWakelock = true;

		Game game = new Game();

		View gameView = initializeForView(game, config);

		layout.addView(gameView);

		setContentView(layout);

		storedConfig = new AnalyticsStoredConfig(getApplicationContext());
		visitorData = storedConfig.loadVisitor();

		AnalyticsConfigData analyticsconfig = new AnalyticsConfigData("UA-23542248-5", visitorData);
		BasicConfig.configure(analyticsconfig, getApplicationContext());
	}

}