package com.gemserk.highscores.gui;

public interface UserDataRegistrator {
	
	static interface RequestUserDataListener {
	
		void cancelled();
		
		void accepted(String username, String name, String password);
		
	}
	
	void requestUserData(RequestUserDataListener requestUserDataListener);

}
