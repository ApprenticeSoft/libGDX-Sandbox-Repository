package com.test.tiled.game.pièges;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.test.tiled.game.MyGdxGame;
import com.test.tiled.game.Variables;

public class LameRotative extends Piège{

	final MyGdxGame game;
	private BodyDef bodyDef;
	public Body body;
	private PolygonShape polygonShape;
	private FixtureDef fixtureDef;
	private TextureAtlas textureAtlas;
	private float width, height, angle, vitesse;
	
	public LameRotative(final MyGdxGame game, World world, MapObject mapObject){
		super();
		this.game = game;
		
		//Vitesse de rotation
		if(mapObject.getProperties().get("Vitesse") != null)
			vitesse = Float.parseFloat((String) mapObject.getProperties().get("Vitesse"));
		else vitesse = 90;
		
		//Angle de départ
		if(mapObject.getProperties().get("Angle") != null)
			angle = Float.parseFloat((String) mapObject.getProperties().get("Angle"));
		else angle = 0;
		
		textureAtlas = game.assets.get("Images.pack", TextureAtlas.class);
		width = 8*Variables.WORLD_TO_BOX;
		height = 48*Variables.WORLD_TO_BOX;
		
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set((mapObject.getProperties().get("x", float.class) + mapObject.getProperties().get("width", float.class)/2)* Variables.WORLD_TO_BOX,
				(mapObject.getProperties().get("y", float.class) + mapObject.getProperties().get("height", float.class)/2)* Variables.WORLD_TO_BOX + height/2);	
		
		polygonShape = new PolygonShape();
		polygonShape.setAsBox(width, height, new Vector2(0,0.9f*height),0);		
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		fixtureDef.density = (float)(0.026/(width*height));  
		fixtureDef.friction = 0.0f;  
		fixtureDef.restitution = 0f;

		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef).setUserData("Piège");
		body.setUserData("Piège");
		body.setFixedRotation(false);
		body.setTransform(body.getPosition().x, body.getPosition().y, angle*MathUtils.degreesToRadians);		//Angle de départ
		body.setAngularVelocity(vitesse*MathUtils.degreesToRadians);											//Vitesse de rotation
				
		polygonShape.dispose();
	}
	
	public void draw(SpriteBatch batch){
		batch.draw(textureAtlas.findRegion("LameRotative"), 
					this.body.getPosition().x - width, 
					this.body.getPosition().y - 0.1f*height,
					width,
					0.1f*height,
					2 * width, 
					2 * height,
					1,
					1,
					body.getAngle()*MathUtils.radiansToDegrees);
	}
}
