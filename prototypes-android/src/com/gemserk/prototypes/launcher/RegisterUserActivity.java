package com.gemserk.prototypes.launcher;

import android.app.Activity;
import android.os.Bundle;

import com.gemserk.prototypes.prototypes.R;

public class RegisterUserActivity extends Activity {
	
	public static final int REGISTERUSER_REQUEST_CODE = 0x0002;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.highscores_registeruser);
	}

}