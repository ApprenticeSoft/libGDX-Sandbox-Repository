package com.test.tiled.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.test.tiled.game.body.Hero;

public class MyCamera extends  OrthographicCamera{
	
	private float posX;

	public MyCamera(){
		super();
	}
	
	public void mouvement(Hero hero, TiledMap tiledMap){
		//Positionnement par rapport au héro
		
		if(hero.droite)
			posX = hero.getX() + this.viewportWidth/6;
		else if(!hero.droite)
			posX = hero.getX() - this.viewportWidth/6;
		this.position.interpolate(new Vector3(posX,this.position.y,0), 0.18f, Interpolation.fade); //Mouvement transitoire de la caméra
		
		if(this.position.y < hero.getY() - Gdx.graphics.getHeight() * Variables.WORLD_TO_BOX/10)
			this.position.set(this.position.x,hero.getY() - Gdx.graphics.getHeight() * Variables.WORLD_TO_BOX/10,0);
		else if(this.position.y > hero.getY() + Gdx.graphics.getHeight() * Variables.WORLD_TO_BOX/10)
			this.position.set(this.position.x,hero.getY() + Gdx.graphics.getHeight() * Variables.WORLD_TO_BOX/10,0);
		
		//Positionnement par rapport au niveau
		if(this.position.x + this.viewportWidth/2 > ((float)(tiledMap.getProperties().get("width", Integer.class)*Variables.nbPixelTile))*Variables.WORLD_TO_BOX)
			this.position.set(((float)(tiledMap.getProperties().get("width", Integer.class)*Variables.nbPixelTile))*Variables.WORLD_TO_BOX - this.viewportWidth/2, this.position.y, 0);
		else if(this.position.x - this.viewportWidth/2 < 0)
			this.position.set(this.viewportWidth/2, this.position.y, 0);
		if(this.position.y + this.viewportHeight/2 > ((float)(tiledMap.getProperties().get("height", Integer.class)*Variables.nbPixelTile))*Variables.WORLD_TO_BOX)
			this.position.set(this.position.x, ((float)(tiledMap.getProperties().get("height", Integer.class)*Variables.nbPixelTile))*Variables.WORLD_TO_BOX - this.viewportHeight/2, 0);
		else if(this.position.y - this.viewportHeight/2 < 0)
			this.position.set(this.position.x, this.viewportHeight/2, 0);
	}
}
