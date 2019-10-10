package com.test.tiled.game.plantes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.test.tiled.game.MyCamera;
import com.test.tiled.game.Variables;
import com.test.tiled.game.body.Hero;
import com.test.tiled.game.body.Projectile;

public class Plante {
	public Body bodyPlante, plateforme;
	protected BodyDef bodyDef;
	protected FixtureDef fixtureDef;
	protected float width, height, vitesseBase, animTime;
	protected long tempsDerniereAttaque;
	public TiledMap tiledMap;
	protected PolygonShape planteShape;
	public boolean droite, mort, attaqueAnimation, attaqueProjectile;
    protected Animation animPlante, animAttaque;
	protected TextureAtlas planteAtlas;
	protected World world;
	
	public Plante(World world, Camera camera, MapObject mapObject){
		this.world = world;
    	droite = true;
    	mort = false;
    	attaqueAnimation = false;
    	attaqueProjectile = false;
		tempsDerniereAttaque = TimeUtils.millis();
		plateforme = null;
    	
		planteAtlas = new TextureAtlas(/*Gdx.files.internal("Animations/AnimationPlante.pack")*/);
		
		//create(world, mapObject);
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public float getX(){
		return bodyPlante.getPosition().x;
	}
	
	public float getY(){
		return bodyPlante.getPosition().y;
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
		animPlante = new Animation(0.15f, planteAtlas.findRegions("PlanteAnimation"), Animation.PlayMode.LOOP_PINGPONG);
		animAttaque = new Animation(0.12f, planteAtlas.findRegions("PlanteAttaque"), Animation.PlayMode.NORMAL);
		
		width = planteAtlas.findRegion("Plante").getRegionWidth()/2 * Variables.WORLD_TO_BOX;
		height = planteAtlas.findRegion("Plante").getRegionHeight()/2 * Variables.WORLD_TO_BOX;
		
		bodyDef = new BodyDef();
		fixtureDef = new FixtureDef();
		
		bodyDef.type = BodyType.KinematicBody;

		bodyDef.position.set((mapObject.getProperties().get("x", float.class) + mapObject.getProperties().get("width", float.class)/2) * Variables.WORLD_TO_BOX,
							(mapObject.getProperties().get("y", float.class) + mapObject.getProperties().get("height", float.class) + planteAtlas.findRegion("Plante").getRegionHeight()/2) * Variables.WORLD_TO_BOX);
		
		planteShape = new PolygonShape();
		planteShape.setAsBox(width, height);
		
		fixtureDef.shape = planteShape;
		fixtureDef.density = (float)0.026/(width*height);  
        fixtureDef.friction = 0.2f;  
        fixtureDef.restitution = 0f;
		
        bodyPlante = world.createBody(bodyDef);
        bodyPlante.createFixture(fixtureDef).setUserData("Plante");
		bodyPlante.setFixedRotation(true);
		
		planteShape.dispose();
		
		bodyPlante.setUserData("Plante");
	}
	
	public void draw(SpriteBatch batch){
		if(!mort){
			if(!attaqueAnimation){
				if(!droite)
					batch.draw(animPlante.getKeyFrame(animTime, true), 
							bodyPlante.getPosition().x + 0.5f*animPlante.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
							bodyPlante.getPosition().y - height, 
							-animPlante.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
							animPlante.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX);
			    else if(droite)
					batch.draw(animPlante.getKeyFrame(animTime, true), 
								bodyPlante.getPosition().x - 0.5f*animPlante.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
								bodyPlante.getPosition().y  - height, 
								animPlante.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
								animPlante.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX);
			}
			else{
				if(!droite)
					batch.draw(animAttaque.getKeyFrame(animTime), 
							bodyPlante.getPosition().x + 0.5f*animAttaque.getKeyFrame(animTime).getRegionWidth() * Variables.WORLD_TO_BOX, 
							bodyPlante.getPosition().y - height, 
							-animAttaque.getKeyFrame(animTime).getRegionWidth() * Variables.WORLD_TO_BOX, 
							animAttaque.getKeyFrame(animTime).getRegionHeight() * Variables.WORLD_TO_BOX);
			    else if(droite)
					batch.draw(animAttaque.getKeyFrame(animTime), 
								bodyPlante.getPosition().x - 0.5f*animAttaque.getKeyFrame(animTime).getRegionWidth() * Variables.WORLD_TO_BOX, 
								bodyPlante.getPosition().y  - height, 
								animAttaque.getKeyFrame(animTime).getRegionWidth() * Variables.WORLD_TO_BOX, 
								animAttaque.getKeyFrame(animTime).getRegionHeight() * Variables.WORLD_TO_BOX);
			}
		}	
		else if(mort){
			if(droite)
				batch.draw(planteAtlas.findRegion("Plante"), bodyPlante.getPosition().x - width, bodyPlante.getPosition().y + height + width/2, 2*width, -2*height);
			else
				batch.draw(planteAtlas.findRegion("Plante"), bodyPlante.getPosition().x + width, bodyPlante.getPosition().y + height + width/2, -2*width, -2*height);
		}	
	}
	
	public void active(Camera camera, Hero hero, Array<Projectile> projectiles){
        animTime+=Gdx.graphics.getDeltaTime();
		
		if(hero.bodyHero.getPosition().x < bodyPlante.getPosition().x)
			droite = false;
		else droite = true;
		
		if(Math.abs(this.getX() - hero.getX()) < 4*camera.viewportWidth/6){
			if (TimeUtils.millis() - tempsDerniereAttaque > 2200){
				tempsDerniereAttaque = TimeUtils.millis();
				animTime = 0;
				attaqueProjectile = true;
				attaqueAnimation = true;
			}	
			else if(attaqueAnimation &&	attaqueProjectile && animAttaque.getKeyFrameIndex(animTime) == 3){
				Projectile projectile = new Projectile(world, getX(), getY() + 0.56f*height, droite);
				projectiles.add(projectile);
				attaqueProjectile = false;
			}
			else if(attaqueAnimation && animAttaque.isAnimationFinished(animTime)){
				animTime = 0;
				attaqueAnimation = false;
			}
		}
		
	}
}
