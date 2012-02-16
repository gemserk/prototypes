package com.gemserk.highscores.gui;

import com.gemserk.highscores.client.User;

public interface RequestUserListener {

	void cancelled();
	
	void accepted(User user);
	
}