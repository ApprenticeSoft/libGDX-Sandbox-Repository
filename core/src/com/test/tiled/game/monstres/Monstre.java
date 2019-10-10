package com.test.tiled.game.monstres;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.test.tiled.game.Variables;
import com.test.tiled.game.body.Hero;
import com.test.tiled.game.body.Obstacle;
import com.test.tiled.game.enums.MonstreEnum;

public class Monstre {
	public Body bodyMonstre, plateforme;
	protected BodyDef bodyDef;
	protected FixtureDef fixtureDef;
	public Fixture fixtureCorps, fixturePiedGauche, fixturePiedDroit, fixtureTête;
	protected float width, height, taillePied, vitesseBase;
	public TiledMap tiledMap;
	public int vitesse, saut, piedGaucheContact, piedDroitContact, contactTête;
	protected Vector2 déplacementCharacter;
	protected PolygonShape monstreShape, piedGaucheShape, piedDroitShape, têteShape;
	protected CircleShape pied;
	public boolean peutSauter, tombe, droite, demiTour, mort, actif;
	public MonstreEnum monstreEnum;
    protected Animation animDroite;
	protected TextureAtlas monstreAtlas;
	
	public Monstre(World world, Camera camera, MapObject mapObject){
		peutSauter = true;
    	droite = true;
    	demiTour = false;
    	mort = false;
    	actif = false;
		plateforme = null;
		vitesseBase = 0;
    	contactTête = 0;
    	monstreEnum = MonstreEnum.Monstre;
    	taillePied = 1;
		piedGaucheContact = 0;
		piedDroitContact = 0;
		if(mapObject.getProperties().get("Comportement") != null && mapObject.getProperties().get("Comportement", String.class).equals("TombePas"))
			tombe = false;
		else
			tombe = true;
		vitesse = -100;
		saut = 50;
		déplacementCharacter = new Vector2(0,0);
		monstreAtlas = new TextureAtlas(); 
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public float getX(){
		return bodyMonstre.getPosition().x;
	}
	
	public float getY(){
		return bodyMonstre.getPosition().y;
	}
	
	public void setPlateforme(Body body){
		plateforme = body;
	}
	/*
	public void setX( float X){
		posX = X;
	}

	public void setY( float Y){
		posY = Y;
	}
	*/
	public void create(World world, MapObject mapObject){
		animDroite = new Animation(0.15f, monstreAtlas.findRegions("MarcheDroite"), Animation.PlayMode.LOOP_PINGPONG);
		
		width = monstreAtlas.findRegion("MarcheDroite").getRegionWidth()/2 * Variables.WORLD_TO_BOX;
		height = monstreAtlas.findRegion("MarcheDroite").getRegionHeight()/2 * Variables.WORLD_TO_BOX;
		
		bodyDef = new BodyDef();
		fixtureDef = new FixtureDef();
		
		bodyDef.type = BodyType.DynamicBody;

		bodyDef.position.set((mapObject.getProperties().get("x", float.class) + mapObject.getProperties().get("width", float.class)/2/* + monstreAtlas.findRegion("MarcheDroite").getRegionWidth()/2*/) * Variables.WORLD_TO_BOX,
							(mapObject.getProperties().get("y", float.class) + mapObject.getProperties().get("height", float.class) + monstreAtlas.findRegion("MarcheDroite").getRegionHeight()/2) * Variables.WORLD_TO_BOX);
		
		monstreShape = new PolygonShape();
		monstreShape.setAsBox(width, height - width/2);
		
		fixtureDef.shape = monstreShape;
		fixtureDef.density = (float)0.026/(width*(height - width/2));  
        fixtureDef.friction = 0.2f;  
        fixtureDef.restitution = 0f;
		
        bodyMonstre = world.createBody(bodyDef);
        bodyMonstre.createFixture(fixtureDef).setUserData("CorpsMonstre");
        
        //Création du pied    
        pied = new CircleShape();
        pied.setRadius(taillePied*width);
        pied.setPosition(new Vector2(0,-height + width/2));
        fixtureDef.shape = pied;
        fixtureDef.density = 0.0f; 	
		
		fixtureCorps = bodyMonstre.createFixture(fixtureDef);
		fixtureCorps.setUserData("CorpsMonstre");
		bodyMonstre.setFixedRotation(true);
        
        //Création des pieds (détecteurs)
        fixtureDef.density = 0.0f;  
		fixtureDef.isSensor = true;	
		
        piedGaucheShape = new PolygonShape();
        piedGaucheShape.setAsBox(0.2f*width,width/3, new Vector2(-0.7f*width,-height - width/2),0); //Taille et positionnement (new Vector2()) du detecteur par rapport au body 
		fixtureDef.shape = piedGaucheShape;		
		
		fixturePiedGauche = bodyMonstre.createFixture(fixtureDef);
		fixturePiedGauche.setUserData("PiedGauche");
		
        piedDroitShape = new PolygonShape();
        piedDroitShape.setAsBox(0.2f*width,width/3, new Vector2(0.7f*width,-height - width/2),0); //Taille et positionnement (new Vector2()) du detecteur par rapport au body 
		fixtureDef.shape = piedDroitShape;
		
		fixturePiedDroit = bodyMonstre.createFixture(fixtureDef);
		fixturePiedDroit.setUserData("PiedDroit");
		
		//Création de la tête (détecteur)
        têteShape = new PolygonShape();
        têteShape.setAsBox(0.9f*width,width/10, new Vector2(0,height - width/2),0); //Taille et positionnement (new Vector2()) du detecteur par rapport au body 
		fixtureDef.shape = têteShape;
		
		fixtureTête = bodyMonstre.createFixture(fixtureDef);
		fixtureTête.setUserData("Tête");
		bodyMonstre.setFixedRotation(true);
		
		monstreShape.dispose();
		piedGaucheShape.dispose();
		piedDroitShape.dispose();
		pied.dispose();
		têteShape.dispose();
		
		bodyMonstre.setUserData("Monstre");
		bodyMonstre.setLinearVelocity(déplacementCharacter);
	}
		
	public void déplacement(Camera camera, Hero hero, World world, Array<Monstre> monstres, Obstacle obstacleBas){
		
		if(actif){
			if((int) Math.signum(bodyMonstre.getLinearVelocity().x) != (int) Math.signum(vitesse))
				vitesse = -vitesse;
			
			if(!tombe && !demiTour){
		        if(piedGaucheContact == 0 || piedDroitContact == 0){
		        	vitesse = -vitesse;
		        	demiTour = true;
		        }
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
			
			déplacementCharacter.x = vitesse * Variables.WORLD_TO_BOX;
			
			déplacementCharacter.y = bodyMonstre.getLinearVelocity().y;    		
	        bodyMonstre.setLinearVelocity(déplacementCharacter.x + vitesseBase, déplacementCharacter.y);
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
	
	public void draw(SpriteBatch batch, float animTime){
		if(!mort && peutSauter){
			if(vitesse < 0)
				batch.draw(animDroite.getKeyFrame(animTime, true), 
						bodyMonstre.getPosition().x + 0.5f*animDroite.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
						bodyMonstre.getPosition().y - 0.5f*animDroite.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
						-animDroite.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
						animDroite.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX);
		    else if(vitesse > 0)
				batch.draw(animDroite.getKeyFrame(animTime, true), 
							bodyMonstre.getPosition().x - 0.5f*animDroite.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
							bodyMonstre.getPosition().y - 0.5f*animDroite.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
							animDroite.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
							animDroite.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX);
		    else if(vitesse == 0 && droite)
		        batch.draw(monstreAtlas.findRegion("MarcheDroite"), bodyMonstre.getPosition().x - width, bodyMonstre.getPosition().y - height - width/2, 2*width, 2*height);
		    else if(vitesse == 0 && !droite)
		        batch.draw(monstreAtlas.findRegion("MarcheDroite"), bodyMonstre.getPosition().x - width, bodyMonstre.getPosition().y - height - width/2, 2*width, 2*height);
		}	
		else if(mort){
			if(droite)
				batch.draw(monstreAtlas.findRegion("MarcheDroite"), bodyMonstre.getPosition().x - width, bodyMonstre.getPosition().y + height + width/2, 2*width, -2*height);
			else
				batch.draw(monstreAtlas.findRegion("MarcheDroite"), bodyMonstre.getPosition().x + width, bodyMonstre.getPosition().y + height + width/2, -2*width, -2*height);
		}	
	}
}
