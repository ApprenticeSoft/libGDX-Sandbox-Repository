package com.test.tiled.game.body;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.test.tiled.game.Variables;

public class Obstacle extends PolygonShape{
	
	public Body body;
	public BodyDef bodyDef;
	public float posX, posY, width, height;
	private FixtureDef fixtureDef;
	Camera camera;
	
	public Obstacle(World world, Camera camera, float posX, float posY, float width, float height, int type){
		super();
		this.camera = camera;
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		
		bodyDef = new BodyDef();
		
		switch(type){
		case 1: bodyDef.type = BodyType.StaticBody;
			break;
		case 2: bodyDef.type = BodyType.KinematicBody;
			break;
		case 3: bodyDef.type = BodyType.DynamicBody;
			break;
		default : bodyDef.type = BodyType.StaticBody;
			break;
		}
    	
		this.setAsBox(width, height);

		bodyDef.position.set(new Vector2(posX, posY));
		body = world.createBody(bodyDef);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = this;
        fixtureDef.density = 2.0f;  
        fixtureDef.friction = 0f;  
        fixtureDef.restitution = 0f;


        
        body.createFixture(fixtureDef).setUserData("Objet");
        body.setUserData("Objet");
        body.setFixedRotation(true);
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public float getX(){
		return posX;
	}
	
	public float getY(){
		return posY;
	}

	public void setX( float X){
		posX = X;
	}

	public void setY( float Y){
		posY = Y;
	}
	
	public void drawOmbre(SpriteBatch batch, TextureRegion textureRegion){
		batch.setColor(0,0,0,0.2f);
		batch.draw(textureRegion, 
				Variables.BOX_TO_WORLD * (this.body.getPosition().x - this.getWidth()) + Gdx.graphics.getWidth()/80, 
				Variables.BOX_TO_WORLD * (this.body.getPosition().y - this.getHeight()) - Gdx.graphics.getWidth()/68,
				Variables.BOX_TO_WORLD * this.getWidth(),
				Variables.BOX_TO_WORLD * this.getHeight(),
				Variables.BOX_TO_WORLD * 2 * this.getWidth(), 
				Variables.BOX_TO_WORLD * 2 * this.getHeight(),
				1,
				1,
				body.getAngle()*MathUtils.radiansToDegrees);
	}
}
