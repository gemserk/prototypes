package com.gemserk.highscores.gui;

public interface UserDataRegistrator {

	void requestUserData(RegisterUserListener registerUserListener, String currentUsername, String currentName);

}
