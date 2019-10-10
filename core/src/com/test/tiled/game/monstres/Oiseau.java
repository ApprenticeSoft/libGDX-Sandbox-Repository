package com.test.tiled.game.monstres;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.test.tiled.game.Variables;
import com.test.tiled.game.body.Hero;
import com.test.tiled.game.body.Obstacle;
import com.test.tiled.game.enums.MonstreEnum;

public class Oiseau extends Monstre{
	World world;
	private float positionInitX, positionInitY, rayon;

	public Oiseau(World world, Camera camera, MapObject mapObject) {
		super(world, camera, mapObject);
		
		this.world = world;
		
		monstreEnum = MonstreEnum.Oiseau;
		vitesse = 110;
		d�placementCharacter = new Vector2(vitesse * Variables.WORLD_TO_BOX,0);
		
		//Rayon de d�placement de l'oiseau
		if(mapObject.getProperties().get("Rayon") != null)
			rayon = Float.parseFloat((String) mapObject.getProperties().get("Rayon"));
		else rayon = MathUtils.random(2.3f, 3.2f); 		
		
		monstreAtlas = new TextureAtlas(Gdx.files.internal("Animations/AnimationOiseau.pack"));
		
		create(world, mapObject);
		positionInitX = bodyMonstre.getPosition().x;
		positionInitY = bodyMonstre.getPosition().y;
	}

	@Override
	public void create(World world, MapObject mapObject){
		animDroite = new Animation(0.1f, monstreAtlas.findRegions("MarcheDroite"), Animation.PlayMode.LOOP_PINGPONG);
		
		width = monstreAtlas.findRegion("MarcheDroite").getRegionWidth()/2 * Variables.WORLD_TO_BOX;
		height = monstreAtlas.findRegion("MarcheDroite").getRegionHeight()/2 * Variables.WORLD_TO_BOX;
		
		bodyDef = new BodyDef();
		fixtureDef = new FixtureDef();
		
		bodyDef.type = BodyType.DynamicBody;

		bodyDef.position.set((mapObject.getProperties().get("x", float.class) + mapObject.getProperties().get("width", float.class)/2/* + monstreAtlas.findRegion("MarcheDroite").getRegionWidth()/2*/) * Variables.WORLD_TO_BOX,
							(mapObject.getProperties().get("y", float.class) + mapObject.getProperties().get("height", float.class) + monstreAtlas.findRegion("MarcheDroite").getRegionHeight()/2) * Variables.WORLD_TO_BOX);
		
		monstreShape = new PolygonShape();
		monstreShape.setAsBox(width, height);
		
		fixtureDef.shape = monstreShape;
		fixtureDef.density = (float)0.026/(width*height);  
        fixtureDef.friction = 0f;  
        fixtureDef.restitution = 0f;
		
        bodyMonstre = world.createBody(bodyDef);
        bodyMonstre.createFixture(fixtureDef).setUserData("CorpsMonstre");
		
		//Cr�ation de la t�te (d�tecteur)
        t�teShape = new PolygonShape();
        t�teShape.setAsBox(width,width/10, new Vector2(0,height),0); //Taille et positionnement (new Vector2()) du detecteur par rapport au body 
		fixtureDef.shape = t�teShape;
		fixtureDef.density = 0.0f;  
		
		fixtureT�te = bodyMonstre.createFixture(fixtureDef);
		fixtureT�te.setUserData("T�te");
		bodyMonstre.setFixedRotation(true);
		
		monstreShape.dispose();
		t�teShape.dispose();
		
		bodyMonstre.setUserData("Monstre");
		bodyMonstre.setLinearVelocity(d�placementCharacter);
	}
	
	@Override
	public void d�placement(Camera camera, Hero hero, World world, Array<Monstre> monstres, Obstacle obstacleBas){
		if(!mort){
			if(Math.abs(positionInitX - bodyMonstre.getPosition().x) > rayon * (32 * Variables.WORLD_TO_BOX)){ //(32 * Variables.WORLD_TO_BOX) = la taille d'une tuile
				vitesse = -vitesse;
				d�placementCharacter.x = vitesse * Variables.WORLD_TO_BOX;					
		        bodyMonstre.setLinearVelocity(d�placementCharacter);
			}
			else if(Math.abs(bodyMonstre.getLinearVelocity().x) < 0.5f*Math.abs(vitesse * Variables.WORLD_TO_BOX)){
				vitesse = -vitesse;
				d�placementCharacter.x = vitesse * Variables.WORLD_TO_BOX;					
		        bodyMonstre.setLinearVelocity(d�placementCharacter);
			}
			
			d�placementCharacter.y = 100*(positionInitY - bodyMonstre.getPosition().y);  
			bodyMonstre.setLinearVelocity(d�placementCharacter);
		}
		
		//Mort
    	if(bodyMonstre.getPosition().y + getHeight() < obstacleBas.getY()){
    		bodyMonstre.setActive(false);
    		world.destroyBody(bodyMonstre);
    		monstres.removeIndex(monstres.indexOf(this, true));
    	}
	}

	@Override
	public void draw(SpriteBatch batch, float animTime){
		if(!mort && peutSauter){
			if(vitesse < 0)
				batch.draw(animDroite.getKeyFrame(animTime, true), 
						bodyMonstre.getPosition().x + 0.5f*animDroite.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
						bodyMonstre.getPosition().y - 0.5f*animDroite.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX, 
						-animDroite.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
						animDroite.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX);
		    else if(vitesse > 0)
				batch.draw(animDroite.getKeyFrame(animTime, true), 
							bodyMonstre.getPosition().x - 0.5f*animDroite.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
							bodyMonstre.getPosition().y - 0.5f*animDroite.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX, 
							animDroite.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
							animDroite.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX);
		    else if(vitesse == 0 && droite)
		        batch.draw(monstreAtlas.findRegion("MarcheDroite"), bodyMonstre.getPosition().x - width, bodyMonstre.getPosition().y - height, 2*width, 2*height);
		    else if(vitesse == 0 && !droite)
		        batch.draw(monstreAtlas.findRegion("MarcheDroite"), bodyMonstre.getPosition().x - width, bodyMonstre.getPosition().y - height, 2*width, 2*height);
		}	
		else if(mort){
			if(droite)
				batch.draw(monstreAtlas.findRegion("MarcheDroite"), bodyMonstre.getPosition().x - width, bodyMonstre.getPosition().y + height, 2*width, -2*height);
			else
				batch.draw(monstreAtlas.findRegion("MarcheDroite"), bodyMonstre.getPosition().x + width, bodyMonstre.getPosition().y + height, -2*width, -2*height);
		}	
	}
	/*
	@Override
	public void beginContact(Contact contact) {
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();

		
		if((a.getUserData().equals("Pied") && b.getUserData().equals("T�te")) || (a.getUserData().equals("T�te") && b.getUserData().equals("Pied"))){
	    	if(a == this.fixtureT�te || b == this.fixtureT�te){
		    	this.mort = true;
		    	this.bodyMonstre.setLinearVelocity(bodyMonstre.getLinearVelocity().x, 0);
				this.bodyMonstre.applyForceToCenter(0, saut/2, true);
				this.bodyMonstre.setUserData("Mort");
	    	}
	    }
	}
	*/
}
