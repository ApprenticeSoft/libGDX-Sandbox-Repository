package com.test.tiled.game.body;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.test.tiled.game.MyGdxGame;
import com.test.tiled.game.Variables;

public class Plateforme {

	final MyGdxGame game;
	private PolylineMapObject polylineObject;
	private Vector2[] ligne;
	private Vector2 direction;
	private BodyDef bodyDef;
	private PolygonShape ps;
	public Body bodyPlateforme;
	private FixtureDef fixtureDef;
	private int �tape;
	public float width, height, largeur, vitesse;
	private boolean retour, boucle;
	private TextureAtlas textureAtlas; 
	
	public Plateforme(final MyGdxGame game, World world, PolylineMapObject polylineObject){
		this.game = game;
		this.polylineObject = polylineObject;
		retour = false;
		�tape = 1;
		
		textureAtlas = game.assets.get("Images.pack", TextureAtlas.class);
		
		if(polylineObject.getProperties().get("Vitesse") != null)
			vitesse = Float.parseFloat((String) polylineObject.getProperties().get("Vitesse"));
		else vitesse = 1;
		
		if(polylineObject.getProperties().get("Boucle") != null)
			boucle = true;
		else boucle = false;
		
		if(polylineObject.getProperties().get("Largeur") != null)
			largeur = Integer.parseInt((String) polylineObject.getProperties().get("Largeur"));
		else
			largeur = 2;
		
		width = largeur*(32 * Variables.WORLD_TO_BOX/2);
		height = 16*Variables.WORLD_TO_BOX;
		direction = new Vector2();
		
		ligne = new Vector2[polylineObject.getPolyline().getTransformedVertices().length/2];
    	for(int i = 0; i < ligne.length; i++){
    		ligne[i] = new Vector2(polylineObject.getPolyline().getTransformedVertices()[i*2]*Variables.WORLD_TO_BOX, polylineObject.getPolyline().getTransformedVertices()[i*2 + 1]*Variables.WORLD_TO_BOX);
    	}   
    	
    	ps = new PolygonShape();
    	ps.setAsBox(width, height);

    	bodyDef = new BodyDef();
    	bodyDef.type = BodyType.KinematicBody;
    	bodyDef.position.set(ligne[0]);
    	
    	fixtureDef = new FixtureDef();
		fixtureDef.shape = ps;
        fixtureDef.density = 0.0f;  
        fixtureDef.friction = 0.0f;  
        fixtureDef.restitution = 0f;

    	bodyPlateforme = world.createBody(bodyDef);
        bodyPlateforme.createFixture(fixtureDef).setUserData("Objet");
        bodyPlateforme.setUserData("Objet");
        
        ps.dispose();

        direction = new Vector2(ligne[�tape].x - bodyPlateforme.getPosition().x, ligne[�tape].y - bodyPlateforme.getPosition().y);
        bodyPlateforme.setLinearVelocity(direction.clamp(vitesse, vitesse));
	}
	
	public void d�placement(){
		if(!boucle){
			if(!retour){
				if(!new Vector2(ligne[�tape].x - bodyPlateforme.getPosition().x, ligne[�tape].y - bodyPlateforme.getPosition().y).hasSameDirection(direction)){
					�tape++;
					
			        if(�tape == ligne.length){
			        	retour = true;
			        	�tape = ligne.length - 2;
			        }
			        
					direction.set(ligne[�tape].x - bodyPlateforme.getPosition().x, ligne[�tape].y - bodyPlateforme.getPosition().y);
				}
			}
			else{
				if(!new Vector2(ligne[�tape].x - bodyPlateforme.getPosition().x, ligne[�tape].y - bodyPlateforme.getPosition().y).hasSameDirection(direction)){
					�tape--;
					
			        if(�tape < 0){
			        	retour = false;
			        	�tape = 1;
			        }
			        
					direction.set(ligne[�tape].x - bodyPlateforme.getPosition().x, ligne[�tape].y - bodyPlateforme.getPosition().y);
				}
			}	
		}
		else{
			if(!new Vector2(ligne[�tape].x - bodyPlateforme.getPosition().x, ligne[�tape].y - bodyPlateforme.getPosition().y).hasSameDirection(direction)){
				�tape++;
				
		        if(�tape == ligne.length){
		        	�tape = 0;
		        }
		        
				direction.set(ligne[�tape].x - bodyPlateforme.getPosition().x, ligne[�tape].y - bodyPlateforme.getPosition().y);
			}
		}
        bodyPlateforme.setLinearVelocity(direction.clamp(vitesse, vitesse));  
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public float getX(){
		return bodyPlateforme.getPosition().x;
	}
	
	public float getY(){
		return bodyPlateforme.getPosition().y;
	}
	
	public void draw(SpriteBatch batch){
		/*
		batch.draw(textureRegion, 
				this.bodyPlateforme.getPosition().x - width, 
				this.bodyPlateforme.getPosition().y - height,
				2*width,
				2*height);
		*/
		
		batch.draw(textureAtlas.findRegion("Plateforme1"), 
				this.bodyPlateforme.getPosition().x - width + 2 * height*(1 - (float)textureAtlas.findRegion("Plateforme1").getRegionWidth()/32), 
				this.bodyPlateforme.getPosition().y + height,
				2*height*((float)textureAtlas.findRegion("Plateforme1").getRegionWidth()/32),
				2*height*((float)textureAtlas.findRegion("Plateforme1").getRegionHeight()/32));
		
		batch.draw(textureAtlas.findRegion("Plateforme4"), 
				this.bodyPlateforme.getPosition().x - width, 
				this.bodyPlateforme.getPosition().y - height,
				2*height,
				2*height);
		
		for(int i = 1; i < width/height; i++){
			batch.draw(textureAtlas.findRegion("Plateforme2"), 
					this.bodyPlateforme.getPosition().x - width + 2*i*height, 
					this.bodyPlateforme.getPosition().y + height,
					2*height,
					2*height*((float)textureAtlas.findRegion("Plateforme2").getRegionHeight()/textureAtlas.findRegion("Plateforme2").getRegionWidth()));
		}
		
		for(int i = 1; i < width/height - 1; i++){
			batch.draw(textureAtlas.findRegion("Plateforme5"), 
					this.bodyPlateforme.getPosition().x - width + 2*i*height, 
					this.bodyPlateforme.getPosition().y - height,
					2*height,
					2*height);
		}
		
		batch.draw(textureAtlas.findRegion("Plateforme3"), 
				this.bodyPlateforme.getPosition().x + width, 
				this.bodyPlateforme.getPosition().y + height,
				2*height*((float)textureAtlas.findRegion("Plateforme3").getRegionWidth()/32),
				2*height*((float)textureAtlas.findRegion("Plateforme3").getRegionHeight()/32));
		
		batch.draw(textureAtlas.findRegion("Plateforme6"), 
				this.bodyPlateforme.getPosition().x + width - 2 * height, 
				this.bodyPlateforme.getPosition().y - height,
				2*height,
				2*height);
		
		batch.draw(textureAtlas.findRegion("Plateforme7"), 
				this.bodyPlateforme.getPosition().x + width, 
				this.bodyPlateforme.getPosition().y - height,
				2*height*((float)textureAtlas.findRegion("Plateforme7").getRegionWidth()/textureAtlas.findRegion("Plateforme7").getRegionHeight()),
				2*height);
	}
}
