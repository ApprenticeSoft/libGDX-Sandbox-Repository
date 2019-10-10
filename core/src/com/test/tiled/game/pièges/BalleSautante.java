package com.test.tiled.game.pièges;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.TimeUtils;
import com.test.tiled.game.MyGdxGame;
import com.test.tiled.game.Variables;

public class BalleSautante extends Piège{
	
	final MyGdxGame game;
	private BodyDef bodyDef;
	public Body bodyBalle;
	private CircleShape circleShape;
	private FixtureDef fixtureDef;
	private TextureAtlas textureAtlas;
	private boolean actif, saute;
	private int intervalAttaque, delai;
	private float rayon, hauteur, positionInitX, positionInitY;
	private long tempsDerniereAttaque;
	private Vector2 déplacement;
	
	public BalleSautante(final MyGdxGame game, World world, MapObject mapObject){
		super();
		this.game = game;
		
		saute = false;
		tempsDerniereAttaque = TimeUtils.millis();
		déplacement = new Vector2(0,0);
		
		//Delai avant le premier saut (en millisecondes)
		if(mapObject.getProperties().get("Delai") != null){
			delai = Integer.parseInt((String) mapObject.getProperties().get("Delai"));
			actif = false;
		}
		else {
			delai = 0;
			actif = true;
		}
		
		//Interval entre les sauts
		if(mapObject.getProperties().get("Interval") != null)
			intervalAttaque = Integer.parseInt((String) mapObject.getProperties().get("Interval"));
		else intervalAttaque = 3000;
		
		//hauteur du saut
		if(mapObject.getProperties().get("Hauteur") != null)
			hauteur = Float.parseFloat((String) mapObject.getProperties().get("Hauteur"));
		else hauteur = 25;

		textureAtlas = game.assets.get("Images.pack", TextureAtlas.class);
		rayon = 16*Variables.WORLD_TO_BOX;
		
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		    	
		circleShape = new CircleShape();
		circleShape.setRadius(rayon);
		
		bodyDef.position.set((mapObject.getProperties().get("x", float.class) + mapObject.getProperties().get("width", float.class)/2)* Variables.WORLD_TO_BOX,
							(mapObject.getProperties().get("y", float.class) + mapObject.getProperties().get("height", float.class)/2)* Variables.WORLD_TO_BOX + 2*rayon);		

		bodyBalle = world.createBody(bodyDef);
				
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.density = (float)(0.026/(rayon*rayon*Math.PI));  
		fixtureDef.friction = 0.0f;  
		fixtureDef.restitution = 0f;
		        
		bodyBalle.createFixture(fixtureDef).setUserData("Piège");
		bodyBalle.setUserData("Piège");
		bodyBalle.setFixedRotation(false);
				
		circleShape.dispose();

		positionInitX = bodyBalle.getPosition().x;
		positionInitY = bodyBalle.getPosition().y;
	}
	
	@Override
	public void draw(SpriteBatch batch){
		batch.draw(textureAtlas.findRegion("BoulePointe"), 
				this.bodyBalle.getPosition().x - rayon, 
				this.bodyBalle.getPosition().y - rayon,
				2*rayon,
				2*rayon);
	}
	
	public void actif(){
		super.actif();
				
		if(actif){
			if(!saute){
				if(TimeUtils.millis() - tempsDerniereAttaque < intervalAttaque){
					déplacement.x = 100*(positionInitX - bodyBalle.getPosition().x); 
					déplacement.y = 100*(positionInitY - bodyBalle.getPosition().y);  
					bodyBalle.setLinearVelocity(déplacement);
				}
				else{
					tempsDerniereAttaque = TimeUtils.millis();
					saute = true;
					bodyBalle.applyForceToCenter(0, hauteur, true);
				}
			}
			else if(saute){
				if(bodyBalle.getPosition().y < positionInitY /*&& TimeUtils.millis() - tempsDerniereAttaque > 1000*/){
					saute = false;
				}
			}
		}
		else{
			if(TimeUtils.millis() - tempsDerniereAttaque >= delai){
				actif = true;
				tempsDerniereAttaque = TimeUtils.millis();
			}
		}
	}
}
