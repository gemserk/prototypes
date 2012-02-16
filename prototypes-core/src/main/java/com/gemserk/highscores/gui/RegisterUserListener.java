package com.gemserk.highscores.gui;

import com.gemserk.highscores.client.User;

public interface RegisterUserListener {

	void cancelled();
	
	void accepted(User user);
	
}