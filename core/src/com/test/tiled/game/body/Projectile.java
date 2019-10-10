package com.test.tiled.game.body;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.test.tiled.game.Variables;

public class Projectile {
	
	public Body body;
	protected BodyDef bodyDef;
	protected FixtureDef fixtureDef;
	PolygonShape ps;
	protected Fixture fixture;
	private float posX, posY, width, height;
	public int vitesse;
	protected boolean droite;
	public boolean contact;
	
	
	public Projectile(World world, float posX, float posY, boolean droite){
		this.posX = posX;
		this.posY = posY;
		this.droite = droite;
		
		contact = false;
		vitesse = 200;
		width = 5*Variables.WORLD_TO_BOX;
		height = 2*Variables.WORLD_TO_BOX;

    	ps = new PolygonShape();
    	ps.setAsBox(width, height);
    	
		bodyDef = new BodyDef();
    	bodyDef.type = BodyType.KinematicBody;
    	bodyDef.position.set(posX, posY);
    	
    	fixtureDef = new FixtureDef();
		fixtureDef.shape = ps;
        fixtureDef.density = 0.0f;  
        fixtureDef.friction = 0.0f;  
        fixtureDef.restitution = 0f;

    	body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData("Projectile");
        body.setUserData("Projectile");
        
        ps.dispose();
	}
	
	public void déplacement(World world, Array<Projectile> projectiles){
		if(droite){
	        body.setLinearVelocity(new Vector2(vitesse * Variables.WORLD_TO_BOX, 0));
		}
		else{
	        body.setLinearVelocity(new Vector2(-vitesse * Variables.WORLD_TO_BOX, 0));
		}
		
		if(contact){
			body.setActive(false);
    		world.destroyBody(body);
    		projectiles.removeIndex(projectiles.indexOf(this, true));
		}
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public float getX(){
		return body.getPosition().x;
	}
	
	public float getY(){
		return body.getPosition().y;
	}
	
	public void draw(SpriteBatch batch, TextureRegion textureRegion){
		batch.draw(textureRegion, 
				this.body.getPosition().x - width, 
				this.body.getPosition().y - height,
				2*width,
				2*height);
	}

}
