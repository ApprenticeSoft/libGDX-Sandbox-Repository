package com.test.tiled.game.plantes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;

public class Fleure extends Plante{

	public Fleure(World world, Camera camera, MapObject mapObject) {
		super(world, camera, mapObject);
		
		planteAtlas = new TextureAtlas(Gdx.files.internal("Animations/AnimationPlante.pack"));
		
		create(world, mapObject);
	}

}
