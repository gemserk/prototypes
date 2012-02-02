package com.gemserk.prototypes.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class CameraParallaxPrototype extends GameStateImpl {

	private GL10 gl;

	ResourceManager<String> resourceManager;

	Injector injector;

	OrthographicCamera worldCamera;
	OrthographicCamera backgroundCamera;
	OrthographicCamera secondBackgroundCamera;

	private SpriteBatch spriteBatch;

	private Sprite characterSprite;

	private Sprite backgroundSprite;

	private Sprite secondBackgroundSpriteTile1;

	private Sprite secondBackgroundSpriteTile2;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		resourceManager = new ResourceManagerImpl<String>();

		Texture.setEnforcePotImages(false);

		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("BackgroundTexture", "parallax/background.png");
				texture("SecondBackgroundTexture", "parallax/background2.png");
				texture("CharacterTexture", "parallax/character.png");

				sprite("BackgroundSprite", "BackgroundTexture");
				sprite("SecondBackgroundSprite", "SecondBackgroundTexture");
				sprite("CharacterSprite", "CharacterTexture");
			}
		};

		spriteBatch = new SpriteBatch();

		worldCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		backgroundCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		secondBackgroundCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		// centers the background camera, world (0,0) is on viewport (0,0)
		backgroundCamera.position.set(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f, 0f);
		backgroundCamera.update();
		
		// same for the other cameras, the only difference is that their position will be updated.
		secondBackgroundCamera.position.set(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f, 0f);
		secondBackgroundCamera.update();
		
		worldCamera.position.set(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f, 0f);
		worldCamera.update();

		characterSprite = resourceManager.getResourceValue("CharacterSprite");
		backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		secondBackgroundSpriteTile1 = resourceManager.getResourceValue("SecondBackgroundSprite");
		secondBackgroundSpriteTile2 = resourceManager.getResourceValue("SecondBackgroundSprite");
		
		backgroundSprite.setPosition(0f, 0f);
		secondBackgroundSpriteTile1.setPosition(0f, 0f);
		secondBackgroundSpriteTile2.setPosition(800f, 50f);
		characterSprite.setPosition(100f, 140f);
	}

	@Override
	public void update() {
		super.update();
		
		float x = characterSprite.getX();
		float y = characterSprite.getY();
		
		float velocity = 400f * Gdx.graphics.getDeltaTime();
		characterSprite.setPosition(x + velocity, y);
		
		worldCamera.position.set(characterSprite.getX() + Gdx.graphics.getWidth() * 0.35f, characterSprite.getY() + Gdx.graphics.getHeight() * 0.25f, 0f);
		worldCamera.update();
		
		secondBackgroundCamera.position.set(characterSprite.getX() * 0.25f + Gdx.graphics.getWidth() * 0.35f, Gdx.graphics.getHeight() * 0.5f, 0f);
		secondBackgroundCamera.update();
		
		// check if the second background tiles are behind, then move them forward .... 
		// I am using -400f because the viewport is 800 and the camera is centered....
		if (secondBackgroundSpriteTile1.getX() + secondBackgroundSpriteTile1.getWidth() < secondBackgroundCamera.position.x - 400f) {
			// 1600 because there are two tiles
			secondBackgroundSpriteTile1.setX(secondBackgroundSpriteTile1.getX() + 1600f);
		}
		
		if (secondBackgroundSpriteTile2.getX() + secondBackgroundSpriteTile2.getWidth() < secondBackgroundCamera.position.x - 400f) {
			// 1600 because there are two tiles
			secondBackgroundSpriteTile2.setX(secondBackgroundSpriteTile2.getX() + 1600f);
		}
		
	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// render the background
		spriteBatch.setTransformMatrix(backgroundCamera.view);
		spriteBatch.setProjectionMatrix(backgroundCamera.projection);
		spriteBatch.begin();
		backgroundSprite.draw(spriteBatch);
		spriteBatch.end();

		// render the second background
		spriteBatch.setTransformMatrix(secondBackgroundCamera.view);
		spriteBatch.setProjectionMatrix(secondBackgroundCamera.projection);
		spriteBatch.begin();
		secondBackgroundSpriteTile1.draw(spriteBatch);
		secondBackgroundSpriteTile2.draw(spriteBatch);
		spriteBatch.end();

		// render the world
		spriteBatch.setTransformMatrix(worldCamera.view);
		spriteBatch.setProjectionMatrix(worldCamera.projection);
		spriteBatch.begin();
		characterSprite.draw(spriteBatch);
		spriteBatch.end();

	}

	@Override
	public void dispose() {
		resourceManager.unloadAll();
		Texture.setEnforcePotImages(true);
		spriteBatch.dispose();
	}

}