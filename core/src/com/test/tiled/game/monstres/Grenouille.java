package com.test.tiled.game.monstres;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.test.tiled.game.Variables;
import com.test.tiled.game.body.Hero;
import com.test.tiled.game.body.Obstacle;
import com.test.tiled.game.enums.MonstreEnum;

public class Grenouille extends Monstre{

	long tempsDernierSaut;
	int intervalSaut;

	public Grenouille(World world, Camera camera, MapObject mapObject) {
		super(world, camera, mapObject);
		
		monstreEnum = MonstreEnum.Grenouille;
		vitesse = -20;
		taillePied = 1.15f;
		
		if(mapObject.getProperties().get("Saut", String.class) != null)
			saut = Integer.parseInt(mapObject.getProperties().get("Saut", String.class));
		else
			saut = MathUtils.random(40,55);
		
		intervalSaut = MathUtils.random(1700,3100);
		
		monstreAtlas = new TextureAtlas(Gdx.files.internal("Animations/AnimationGrenouille.pack"));
		
		create(world, mapObject);
		
		animDroite.setFrameDuration(0.25f);
		tempsDernierSaut = TimeUtils.millis();
	}
	
	@Override
	public void déplacement(Camera camera, Hero hero, World world, Array<Monstre> monstres, Obstacle obstacleBas){
		if(actif){
			if(!mort){
				if(!peutSauter && !demiTour && (int) Math.signum(bodyMonstre.getLinearVelocity().x) != (int) Math.signum(vitesse)){
					demiTour = true;
					vitesse = -vitesse;
				}
				
				if(piedGaucheContact == 0 && piedDroitContact == 0){
					plateforme = null;
					vitesseBase = 0;
				}
				
				if(plateforme != null) 
					vitesseBase = plateforme.getLinearVelocity().x;
				else
					vitesseBase = 0;
				
				if(vitesse > 0)
					droite = true;
				else if(vitesse < 0)
					droite = false;
				
				if (TimeUtils.millis() - tempsDernierSaut > intervalSaut){
					tempsDernierSaut = TimeUtils.millis();
					if(peutSauter){
				    	peutSauter = false;
						bodyMonstre.applyForceToCenter(vitesse, saut, true);
					}
				}
				
				if(peutSauter){
					demiTour = false;
					if(piedGaucheContact > 0 && piedDroitContact > 0)
						bodyMonstre.setLinearVelocity(new Vector2(vitesseBase,0));
				}
			}
		}
		else if(Math.abs(this.getX() - hero.getX()) < 4*camera.viewportWidth/6)
			actif = true;
		
		//Mort
    	if(bodyMonstre.getPosition().y + getHeight() < obstacleBas.getY()){
    		bodyMonstre.setActive(false);
    		world.destroyBody(bodyMonstre);
    		monstres.removeIndex(monstres.indexOf(this, true));
    	}
	}
	
	@Override
	public void draw(SpriteBatch batch, float animTime){
		super.draw(batch, animTime);
		
		if(!mort && !peutSauter){
			if(bodyMonstre.getLinearVelocity().y > 0){
				if(vitesse < 0){
					batch.draw(monstreAtlas.findRegion("Saut_Monte"), 
							bodyMonstre.getPosition().x + 0.5f*monstreAtlas.findRegion("Saut_Monte").getRegionWidth() * Variables.WORLD_TO_BOX, 
							bodyMonstre.getPosition().y - 0.5f*monstreAtlas.findRegion("Saut_Monte").getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
							- monstreAtlas.findRegion("Saut_Monte").getRegionWidth() * Variables.WORLD_TO_BOX, 
							monstreAtlas.findRegion("Saut_Monte").getRegionHeight() * Variables.WORLD_TO_BOX);
				}
				else{
					batch.draw(monstreAtlas.findRegion("Saut_Monte"), 
							bodyMonstre.getPosition().x - 0.5f*monstreAtlas.findRegion("Saut_Monte").getRegionWidth() * Variables.WORLD_TO_BOX, 
							bodyMonstre.getPosition().y - 0.5f*monstreAtlas.findRegion("Saut_Monte").getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
							monstreAtlas.findRegion("Saut_Monte").getRegionWidth() * Variables.WORLD_TO_BOX, 
							monstreAtlas.findRegion("Saut_Monte").getRegionHeight() * Variables.WORLD_TO_BOX);
				}
			}
			else{
				if(vitesse < 0){
					batch.draw(monstreAtlas.findRegion("Saut_Descent"), 
							bodyMonstre.getPosition().x + 0.5f*monstreAtlas.findRegion("Saut_Descent").getRegionWidth() * Variables.WORLD_TO_BOX, 
							bodyMonstre.getPosition().y - 0.5f*monstreAtlas.findRegion("Saut_Descent").getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
							- monstreAtlas.findRegion("Saut_Descent").getRegionWidth() * Variables.WORLD_TO_BOX, 
							monstreAtlas.findRegion("Saut_Descent").getRegionHeight() * Variables.WORLD_TO_BOX);
				}
				else{
					batch.draw(monstreAtlas.findRegion("Saut_Descent"), 
							bodyMonstre.getPosition().x - 0.5f*monstreAtlas.findRegion("Saut_Descent").getRegionWidth() * Variables.WORLD_TO_BOX, 
							bodyMonstre.getPosition().y - 0.5f*monstreAtlas.findRegion("Saut_Descent").getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
							monstreAtlas.findRegion("Saut_Descent").getRegionWidth() * Variables.WORLD_TO_BOX, 
							monstreAtlas.findRegion("Saut_Descent").getRegionHeight() * Variables.WORLD_TO_BOX);
				}
			}
		}
	}
	/*
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		super.preSolve(contact, oldManifold);
		
		Body a = contact.getFixtureA().getBody();
	    Body b = contact.getFixtureB().getBody();
	    if(a.getUserData() == b.getUserData())
	    	contact.setEnabled(false);
	}
	*/
}
