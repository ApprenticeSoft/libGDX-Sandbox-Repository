package com.test.tiled.game.pièges;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.test.tiled.game.MyGdxGame;
import com.test.tiled.game.Variables;

public class Balance extends Piège{

	final MyGdxGame game;
	private BodyDef bodyDef;
	public Body bodyFix, bodyBalle;
	private CircleShape circleShape;
	private FixtureDef fixtureDef;
	private DistanceJointDef distanceJointDef;
	public RevoluteJointDef revoluteJointDef;
	private float rayon, longueurCorde, épaisseurCorde;
	private TextureAtlas textureAtlas; 
	private Vector2 corde;
	
	public Balance(final MyGdxGame game, World world, MapObject mapObject){
		super();
		this.game = game;

		corde = new Vector2();
		textureAtlas = game.assets.get("Images.pack", TextureAtlas.class);
		rayon = 16*Variables.WORLD_TO_BOX;
		longueurCorde = 11f * rayon;
		épaisseurCorde = rayon/5;
		
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		    	
		circleShape = new CircleShape();
		circleShape.setRadius(rayon/8);
		
		bodyDef.position.set((mapObject.getProperties().get("x", float.class) + mapObject.getProperties().get("width", float.class)/2)* Variables.WORLD_TO_BOX,
							(mapObject.getProperties().get("y", float.class) + 3*mapObject.getProperties().get("height", float.class)/2)* Variables.WORLD_TO_BOX);		

		bodyFix = world.createBody(bodyDef);
				
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = 10.0f;  
		fixtureDef.friction = 0.0f;  
		fixtureDef.restitution = 0f;
		        
		bodyFix.createFixture(fixtureDef).setUserData("FixationBalance");
		bodyFix.setUserData("Balance");
		bodyFix.setFixedRotation(false);
		
		//Création du deuxième body
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.y = bodyFix.getPosition().y;
		bodyDef.position.x = bodyFix.getPosition().x + longueurCorde;
		circleShape.setRadius(rayon);
		fixtureDef.shape = circleShape;
		bodyBalle = world.createBody(bodyDef);
		bodyBalle.createFixture(fixtureDef).setUserData("Piège");
		bodyBalle.setUserData("Piège");
				
		//Joint entre les deux body
		distanceJointDef = new DistanceJointDef();
		distanceJointDef.bodyA = bodyFix;
		distanceJointDef.bodyB = bodyBalle;
		distanceJointDef.length = longueurCorde;
		distanceJointDef.frequencyHz = 1000;
		distanceJointDef.dampingRatio = 0;
		//distanceJointDef.type = JointType.GearJoint;
		  
		world.createJoint(distanceJointDef);
		 
		/*
		//Revolute Joint		
		revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.bodyA=bodyFix;
		revoluteJointDef.bodyB=bodyBalle;
		revoluteJointDef.localAnchorA.set(0,0);
		revoluteJointDef.localAnchorB.set( 7 * rayon,0);
		
		revoluteJointDef.enableMotor=true;
		revoluteJointDef.maxMotorTorque=100;
		revoluteJointDef.motorSpeed=90*MathUtils.degreesToRadians;

		//revoluteJointDef.enableLimit=true;
		//revoluteJointDef.lowerAngle=0*MathUtils.degreesToRadians;
		//revoluteJointDef.upperAngle=180*MathUtils.degreesToRadians;		
		world.createJoint(revoluteJointDef);
		*/
		circleShape.dispose();
	}
	
	@Override
	public void draw(SpriteBatch batch){
		
		corde.set(bodyFix.getPosition().x - bodyBalle.getPosition().x, bodyFix.getPosition().y - bodyBalle.getPosition().y);
		bodyBalle.setTransform(bodyBalle.getPosition().x, bodyBalle.getPosition().y, corde.angleRad());
		//Dessin de la fixation
		batch.draw(textureAtlas.findRegion("Balle"), 
				this.bodyFix.getPosition().x - rayon/8, 
				this.bodyFix.getPosition().y - rayon/8,
				2*rayon/8,
				2*rayon/8);
		//Dessin de la corde
		batch.setColor(0,0,0,1);
		batch.draw(textureAtlas.findRegion("Barre"), 
					(this.bodyFix.getPosition().x + this.bodyBalle.getPosition().x)/2 - longueurCorde/2, 
					(this.bodyFix.getPosition().y + this.bodyBalle.getPosition().y)/2 - épaisseurCorde/2,
					longueurCorde/2,
					épaisseurCorde/2,
					longueurCorde, 
					épaisseurCorde,
					1,
					1,
					bodyBalle.getAngle()*MathUtils.radiansToDegrees);
		//Dessin de la boule
		batch.setColor(1,1,1,1);
		batch.draw(textureAtlas.findRegion("BoulePointe"), 
					this.bodyBalle.getPosition().x - rayon, 
					this.bodyBalle.getPosition().y - rayon,
					rayon,
					rayon,
					2*rayon, 
					2*rayon,
					1,
					1,
					bodyBalle.getAngle()*MathUtils.radiansToDegrees);
	}
}
