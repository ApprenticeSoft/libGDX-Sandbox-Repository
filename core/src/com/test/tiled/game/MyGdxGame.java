package com.test.tiled.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.test.tiled.game.screen.LoadingScreen;

public class MyGdxGame extends Game implements ApplicationListener {
	public SpriteBatch batch;
	public AssetManager assets;
	public Langue langue;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		assets = new AssetManager();
		langue = new Langue();
		langue.setLangue(2);

		this.setScreen(new LoadingScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
}
