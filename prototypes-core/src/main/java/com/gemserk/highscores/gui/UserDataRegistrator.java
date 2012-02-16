package com.gemserk.highscores.gui;

public interface UserDataRegistrator {

	void requestUserData(RequestUserListener requestUserListener, String currentUsername, String currentName);

}
