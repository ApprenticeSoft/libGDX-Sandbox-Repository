package com.test.tiled.game.body;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObjects;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.test.tiled.game.Variables;

public class Hero {
	public Body bodyHero, plateforme;
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private float width, height;
	public float vitesseBase;
	public TiledMap tiledMap;
	private int vitesse;
	public int saut, peutSauter;
	private Vector2 déplacementCharacter;
	private PolygonShape heroShape, detecteurShape;
	private CircleShape pied;
	public boolean droite, mort, saute;
	private TextureAtlas persoAtlas;
    private Animation animDroite, animRepos;
    private ImageButton boutonGauche, boutonDroite, boutonSaut;
	
	public Hero(World world, Camera camera, TiledMap tiledMap, ImageButton boutonGauche, ImageButton boutonDroite, ImageButton boutonSaut){
		super();
		this.tiledMap = tiledMap;
		this.boutonDroite = boutonDroite;
		this.boutonGauche = boutonGauche;
		this.boutonSaut = boutonSaut;
		
		plateforme = null;
		peutSauter = 0;
    	droite = true;
    	mort = false;
    	saute = false;
		vitesse = 280;
		saut = 50;
		déplacementCharacter = new Vector2(0,0);
		
        persoAtlas = new TextureAtlas(Gdx.files.internal("Animations/AnimationPerso.pack"));
        animDroite = new Animation(0.1f, persoAtlas.findRegions("Marche"), Animation.PlayMode.LOOP);
        animRepos = new Animation(0.3f, persoAtlas.findRegions("Hero"), Animation.PlayMode.LOOP);
        
		width = persoAtlas.findRegion("Hero").getRegionWidth()/2 * Variables.WORLD_TO_BOX;
		height = persoAtlas.findRegion("Hero").getRegionHeight()/2 * Variables.WORLD_TO_BOX;

		MapObjects personnages = (MapObjects)tiledMap.getLayers().get("Spawn").getObjects();
		
		bodyDef = new BodyDef();
		fixtureDef = new FixtureDef();
		
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set((personnages.get("Hero").getProperties().get("x", float.class) + personnages.get("Hero").getProperties().get("width", float.class)/2/* + persoAtlas.findRegion("Hero").getRegionWidth()/2*/) * Variables.WORLD_TO_BOX,
							(personnages.get("Hero").getProperties().get("y", float.class) + personnages.get("Hero").getProperties().get("height", float.class) + persoAtlas.findRegion("Hero").getRegionHeight()/2) * Variables.WORLD_TO_BOX);

		heroShape = new PolygonShape();
		heroShape.setAsBox(width, height - width/2);
		
		fixtureDef.shape = heroShape;
        fixtureDef.density = (float)0.026/(width*(height-width/2));  
        fixtureDef.friction = 1.0f;  
        fixtureDef.restitution = 0f;
		
        bodyHero = world.createBody(bodyDef);
        bodyHero.createFixture(fixtureDef).setUserData("CorpsHero");
        
        //Création du pied
        pied = new CircleShape();
        pied.setRadius(1.1f*width);
        pied.setPosition(new Vector2(0,-height + width/2));
        fixtureDef.shape = pied;
        fixtureDef.density = 0.0f; 

		bodyHero.createFixture(fixtureDef).setUserData("PiedHero");
		bodyHero.setFixedRotation(true);
        
        //Création du détecteur
        detecteurShape = new PolygonShape();
        detecteurShape.setAsBox(0.20f*width,width/3, new Vector2(0,-height - width/2),0); //Taille et positionnement (new Vector2()) du detecteur par rapport au body 
		fixtureDef.shape = detecteurShape;
        fixtureDef.density = 0.0f;  
		fixtureDef.isSensor = true;			
		
		bodyHero.createFixture(fixtureDef).setUserData("PiedDetecteur");
		bodyHero.setUserData("Hero");
		bodyHero.setFixedRotation(true);
		
		heroShape.dispose();
		detecteurShape.dispose();
		pied.dispose();
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public float getX(){
		return bodyHero.getPosition().x;
	}
	
	public float getY(){
		return bodyHero.getPosition().y;
	}
	
	public Vector2 getDéplacement(){
		return déplacementCharacter;
	}
	
	public void setPlateforme(Body body){
		plateforme = body;
	}
	
	public void déplacement(){
		//Vitesse de la plateforme mobile
		if(plateforme != null){
			vitesseBase = plateforme.getLinearVelocity().x;
		}
		else vitesseBase = 0;
		
		//Déplacement au clavier
		
		if(Gdx.input.isKeyJustPressed(Keys.W) && peutSauter > 0){
        	bodyHero.applyForceToCenter(0, saut, true);
        	peutSauter = 0;			
        	saute = false;
        }
        /*
		if(saute && peutSauter > 0){
        	bodyHero.applyForceToCenter(0, saut, true);
        	peutSauter = 0;			
        	saute = false;
        }*/
        if(Gdx.input.isKeyPressed(Keys.A)){
        	déplacementCharacter.x = - vitesse * Variables.WORLD_TO_BOX;
        	droite = false;
        }
	    else if(Gdx.input.isKeyPressed(Keys.D)){
	    	déplacementCharacter.x = vitesse * Variables.WORLD_TO_BOX;
	    	droite = true;
	    }
	    else 
	    	déplacementCharacter.x = 0;
        
        //Déplacement avec les boutons
        /*
		if(boutonSaut.isPressed() && peutSauter > 0){
        	bodyHero.applyForceToCenter(0, saut, true);
        	peutSauter = 0;
        }
        if(boutonGauche.isPressed()){
        	déplacementCharacter.x = -vitesse * Variables.WORLD_TO_BOX;
        	droite = false;
        }
	    else if(boutonDroite.isPressed()){
	    	déplacementCharacter.x = vitesse * Variables.WORLD_TO_BOX;
	    	droite = true;
	    }
	    else 
	    	déplacementCharacter.x = 0;
        */
        
        déplacementCharacter.y = bodyHero.getLinearVelocity().y;   
        bodyHero.setLinearVelocity(new Vector2(déplacementCharacter.x + vitesseBase, déplacementCharacter.y));
        
        //Limitation de la vitesse verticale      
        if(bodyHero.getLinearVelocity().y > 10){
        	bodyHero.setLinearVelocity(new Vector2(bodyHero.getLinearVelocity().x, 10));
        }
        
        //Mort du héro
        meurt();
	}
	
	public void draw(SpriteBatch batch, float animTime){
		if(!mort){
			if(peutSauter > 0){		//Déplacement au sol
				if(déplacementCharacter.x < 0)
					batch.draw(animDroite.getKeyFrame(animTime, true), 
							bodyHero.getPosition().x + 0.5f*animDroite.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
							bodyHero.getPosition().y - 0.5f*animDroite.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
							-animDroite.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
							animDroite.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX);
			    else if(déplacementCharacter.x > 0)
			    	batch.draw(animDroite.getKeyFrame(animTime, true), 
			    			bodyHero.getPosition().x - 0.5f*animDroite.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
			    			bodyHero.getPosition().y - 0.5f*animDroite.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
							animDroite.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
							animDroite.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX);
			    else if(déplacementCharacter.x == 0 && droite)
			    	batch.draw(animRepos.getKeyFrame(animTime, true), 
			    			bodyHero.getPosition().x - 0.5f*animRepos.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
			    			bodyHero.getPosition().y - 0.5f*animRepos.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
							animRepos.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
							animRepos.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX);
			    else if(déplacementCharacter.x == 0 && !droite)
			    	batch.draw(animRepos.getKeyFrame(animTime, true), 
			    			bodyHero.getPosition().x + 0.5f*animRepos.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
			    			bodyHero.getPosition().y - 0.5f*animRepos.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
							-animRepos.getKeyFrame(animTime, true).getRegionWidth() * Variables.WORLD_TO_BOX, 
							animRepos.getKeyFrame(animTime, true).getRegionHeight() * Variables.WORLD_TO_BOX);
			}
			else {					//Déplacement en l'air
				if(Math.abs(déplacementCharacter.x) > Math.abs(déplacementCharacter.y)){	//Saut horizontal
					if(!droite){
						batch.draw(persoAtlas.findRegion("Saut_Horizontal"), 
								bodyHero.getPosition().x + 0.5f*persoAtlas.findRegion("Saut_Horizontal").getRegionWidth() * Variables.WORLD_TO_BOX, 
								bodyHero.getPosition().y - 0.5f*persoAtlas.findRegion("Saut_Horizontal").getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
								- persoAtlas.findRegion("Saut_Horizontal").getRegionWidth() * Variables.WORLD_TO_BOX, 
								persoAtlas.findRegion("Saut_Horizontal").getRegionHeight() * Variables.WORLD_TO_BOX);
					}
					else {
						batch.draw(persoAtlas.findRegion("Saut_Horizontal"), 
								bodyHero.getPosition().x - 0.5f*persoAtlas.findRegion("Saut_Horizontal").getRegionWidth() * Variables.WORLD_TO_BOX, 
								bodyHero.getPosition().y - 0.5f*persoAtlas.findRegion("Saut_Horizontal").getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
								persoAtlas.findRegion("Saut_Horizontal").getRegionWidth() * Variables.WORLD_TO_BOX, 
								persoAtlas.findRegion("Saut_Horizontal").getRegionHeight() * Variables.WORLD_TO_BOX);
					}
				}
				else{		//Saut vertical					
					if(déplacementCharacter.y > 0){		//Montée
						if(!droite){
							batch.draw(persoAtlas.findRegion("Saut_Monte"), 
									bodyHero.getPosition().x + 0.5f*persoAtlas.findRegion("Saut_Monte").getRegionWidth() * Variables.WORLD_TO_BOX, 
									bodyHero.getPosition().y - 0.5f*persoAtlas.findRegion("Saut_Monte").getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
									- persoAtlas.findRegion("Saut_Monte").getRegionWidth() * Variables.WORLD_TO_BOX, 
									persoAtlas.findRegion("Saut_Monte").getRegionHeight() * Variables.WORLD_TO_BOX);
						}
						else {
							batch.draw(persoAtlas.findRegion("Saut_Monte"), 
									bodyHero.getPosition().x - 0.5f*persoAtlas.findRegion("Saut_Monte").getRegionWidth() * Variables.WORLD_TO_BOX, 
									bodyHero.getPosition().y - 0.5f*persoAtlas.findRegion("Saut_Monte").getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
									persoAtlas.findRegion("Saut_Monte").getRegionWidth() * Variables.WORLD_TO_BOX, 
									persoAtlas.findRegion("Saut_Monte").getRegionHeight() * Variables.WORLD_TO_BOX);
						}		
					}
					else{								//Descente
						if(!droite){
							batch.draw(persoAtlas.findRegion("Saut_Dessent"), 
									bodyHero.getPosition().x + 0.5f*persoAtlas.findRegion("Saut_Dessent").getRegionWidth() * Variables.WORLD_TO_BOX, 
									bodyHero.getPosition().y - 0.5f*persoAtlas.findRegion("Saut_Dessent").getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
									- persoAtlas.findRegion("Saut_Dessent").getRegionWidth() * Variables.WORLD_TO_BOX, 
									persoAtlas.findRegion("Saut_Dessent").getRegionHeight() * Variables.WORLD_TO_BOX);
						}
						else {
							batch.draw(persoAtlas.findRegion("Saut_Dessent"), 
									bodyHero.getPosition().x - 0.5f*persoAtlas.findRegion("Saut_Dessent").getRegionWidth() * Variables.WORLD_TO_BOX, 
									bodyHero.getPosition().y - 0.5f*persoAtlas.findRegion("Saut_Dessent").getRegionHeight() * Variables.WORLD_TO_BOX - width/2, 
									persoAtlas.findRegion("Saut_Dessent").getRegionWidth() * Variables.WORLD_TO_BOX, 
									persoAtlas.findRegion("Saut_Dessent").getRegionHeight() * Variables.WORLD_TO_BOX);
						}
					}
				}
			}
		}
		else{
			if(droite)
				batch.draw(persoAtlas.findRegion("Marche"), 
						bodyHero.getPosition().x - 0.5f*persoAtlas.findRegion("Marche").getRegionWidth() * Variables.WORLD_TO_BOX, 
						bodyHero.getPosition().y + 0.5f*persoAtlas.findRegion("Marche").getRegionHeight() * Variables.WORLD_TO_BOX - width/2/*height + width/2*/, 
						persoAtlas.findRegion("Marche").getRegionWidth() * Variables.WORLD_TO_BOX, 
						-persoAtlas.findRegion("Marche").getRegionHeight() * Variables.WORLD_TO_BOX);
			else
				batch.draw(persoAtlas.findRegion("Marche"), 
						bodyHero.getPosition().x + 0.5f*persoAtlas.findRegion("Marche").getRegionWidth() * Variables.WORLD_TO_BOX, 
						bodyHero.getPosition().y + 0.5f*persoAtlas.findRegion("Marche").getRegionHeight() * Variables.WORLD_TO_BOX - width/2/*height + width/2*/, 
						-persoAtlas.findRegion("Marche").getRegionWidth() * Variables.WORLD_TO_BOX, 
						-persoAtlas.findRegion("Marche").getRegionHeight() * Variables.WORLD_TO_BOX);
		}
	}
	
	public void meurt(){
		if(bodyHero.getUserData().equals("Mort") && !mort){
			mort = true;
			for(Fixture fixture : bodyHero.getFixtureList()){
				fixture.setUserData("Mort");
			}
	    	bodyHero.setLinearVelocity(bodyHero.getLinearVelocity().x, 0);
	    	bodyHero.applyForceToCenter(0, 2*saut/3, true);
        	System.out.println("bodyHero.getUserData() = " + bodyHero.getUserData());
        }
	}
	
	public void control(ImageButton boutonGauche, ImageButton boutonDroite, ImageButton boutonSaut){
		boutonGauche.addListener(new ClickListener(){
		});

		boutonDroite.addListener(new ClickListener(){
		});

		boutonSaut.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y){
				if(peutSauter > 0)
					saute = true;
			}
		});
	}
}
