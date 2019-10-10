package com.test.tiled.game.monstres;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.test.tiled.game.enums.MonstreEnum;

public class Castor extends Monstre{

	public Castor(World world, Camera camera, MapObject mapObject) {
		super(world, camera, mapObject);
		
		monstreEnum = MonstreEnum.Castor;
		vitesse = 80;
		monstreAtlas = new TextureAtlas(Gdx.files.internal("Animations/AnimationCastor.pack"));
		
		create(world, mapObject);
	}
}
