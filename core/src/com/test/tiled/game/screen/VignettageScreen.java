package com.test.tiled.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.test.tiled.game.Controls;
import com.test.tiled.game.LecteurCarte;
import com.test.tiled.game.MyCamera;
import com.test.tiled.game.MyGdxGame;
import com.test.tiled.game.OrthogonalTiledMapRendererWithSprites;
import com.test.tiled.game.Variables;
import com.test.tiled.game.body.Hero;
import com.test.tiled.game.body.Obstacle;
import com.test.tiled.game.body.Plateforme;
import com.test.tiled.game.body.Projectile;
import com.test.tiled.game.enums.MonstreEnum;
import com.test.tiled.game.monstres.Monstre;

public class VignettageScreen extends InputAdapter implements Screen{

	final MyGdxGame game;
	private MyCamera camera;
	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
    SpriteBatch batch;
    MapObjects objects;
    
    //Boutons de control
	private InputMultiplexer inputMultiplexer;
	private TextureAtlas textureAtlas;
    private Stage stage;
    private Controls controls;
    
    //Test Box2D
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private int nbTileVertical, dimension;
    private float ratio;
    private Hero hero;
    private LecteurCarte lecteurCarte;
	private Obstacle obstacleBas, obstacleGauche, obstacleDroite;
    private boolean debugActif; 
    
    //Animation
    private float animTime;
    private int[] arri�rePlan = {0,1,2,3,4};
    private int[] premierPlan = {5};
    
    /*******************TEST SHADERS**********************/
    String vertexShader;
    String fragmentShader;
    ShaderProgram shaderProgram;
    //Vignettage
    float posX, posY;
    
	public VignettageScreen(final MyGdxGame gam){
		game = gam;

		nbTileVertical = 15;
		dimension = nbTileVertical * Variables.nbPixelTile;
		ratio = ((float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight());	
		
		camera = new MyCamera(); 
		camera.setToOrtho(false, (dimension * Variables.WORLD_TO_BOX) * ratio, dimension * Variables.WORLD_TO_BOX);
        camera.update();  
        tiledMap = new TmxMapLoader().load("DessinPad.tmx");
        
        /**********TEST BOX2D***********/
        world = new World(new Vector2(0, -22f), true);
		World.setVelocityThreshold(0);
        debugRenderer = new Box2DDebugRenderer();
        debugActif = false;
		/*******************************/     
        
        batch = new SpriteBatch();
        tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap, Variables.WORLD_TO_BOX, batch);
        
        lecteurCarte = new LecteurCarte(gam, tiledMap, world, camera);
        
        //Bordures du niveau
        obstacleBas = new Obstacle(world, camera, ((float)(tiledMap.getProperties().get("width", Integer.class)*Variables.nbPixelTile))*Variables.WORLD_TO_BOX/2, 
												-Variables.WORLD_TO_BOX, 
												((float)(tiledMap.getProperties().get("width", Integer.class)*Variables.nbPixelTile))*Variables.WORLD_TO_BOX/2, 
												Variables.WORLD_TO_BOX,
												1);
        
        obstacleGauche = new Obstacle(world, camera, -Variables.WORLD_TO_BOX, 
									        		((float)(tiledMap.getProperties().get("height", Integer.class)*Variables.nbPixelTile))*Variables.WORLD_TO_BOX/2, 
									        		Variables.WORLD_TO_BOX, 
													((float)(tiledMap.getProperties().get("height", Integer.class)*Variables.nbPixelTile))*Variables.WORLD_TO_BOX/2,
													1);
        
        obstacleDroite = new Obstacle(world, camera, ((float)(tiledMap.getProperties().get("width", Integer.class)*Variables.nbPixelTile))*Variables.WORLD_TO_BOX + Variables.WORLD_TO_BOX, 
									        		((float)(tiledMap.getProperties().get("height", Integer.class)*Variables.nbPixelTile))*Variables.WORLD_TO_BOX/2, 
									        		Variables.WORLD_TO_BOX, 
													((float)(tiledMap.getProperties().get("height", Integer.class)*Variables.nbPixelTile))*Variables.WORLD_TO_BOX/2,
													1);
        
		//Controls du jeu
      	inputMultiplexer = new InputMultiplexer();
		textureAtlas = game.assets.get("Images.pack", TextureAtlas.class);
		stage = new Stage();
		controls = new Controls(stage, textureAtlas);
        hero = new Hero(world, camera, tiledMap, controls.getGauche(), controls.getDroite(), controls.getSaut());
		camera.position.set(hero.getX(),hero.getY(),0);

		
		/*******************TEST SHADERS**********************/
		ShaderProgram.pedantic = false;	//Important pour pouvoir modifier les variables uniformes
      	vertexShader = Gdx.files.internal("Shaders/vVignette.glsl").readString();
      	fragmentShader = Gdx.files.internal("Shaders/fVignette.glsl").readString();
      	shaderProgram = new ShaderProgram(vertexShader,fragmentShader);
      	System.out.println("Shader log : " + shaderProgram.getLog());
      	batch.setShader(shaderProgram);

      	posX = 0.5f;
      	posY = 0.5f;
      	shaderProgram.begin();
      	shaderProgram.setUniformf("u_resolution", camera.viewportWidth, camera.viewportHeight);
      	shaderProgram.setUniformf("u_PosX", posX);
      	shaderProgram.setUniformf("u_PosY", posY);
      	shaderProgram.end();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f,0.1f,0.1f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.mouvement(hero, tiledMap);
        camera.update();
        
        //Test animation
        animTime+=Gdx.graphics.getDeltaTime();
        
        // -----------------------------
    	//SHADER D�BUT
    	// -----------------------------
		//Position de l'effet (Shader) sur le h�ro
        posX = camera.project(new Vector3(hero.getX(),hero.getY(),0)).x / camera.viewportWidth;
        posY = camera.project(new Vector3(hero.getX(),hero.getY(),0)).y / camera.viewportHeight;
		
		shaderProgram.begin();
        shaderProgram.setUniformf("u_PosX", posX);
        shaderProgram.setUniformf("u_PosY", posY);
        shaderProgram.end();
        
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render(arri�rePlan);
        //batch.setShader(null);        
        batch.begin(); 
        lecteurCarte.draw(batch, animTime, textureAtlas.findRegion("Barre"));
        hero.draw(batch, animTime); 
        batch.end();
        tiledMapRenderer.render(premierPlan);
        // -----------------------------
    	//SHADER FIN
    	// -----------------------------
  		
        //D�placements
	    hero.d�placement();
	    lecteurCarte.d�placement(hero, obstacleBas);
	    
        stage.act();
        //stage.draw();
         
		world.step(Variables.BOX_STEP, Variables.BOX_VELOCITY_ITERATIONS, Variables.BOX_POSITION_ITERATIONS); 
        
        //TEST BOX2D     
        if(debugActif)
        	debugRenderer.render(world, camera.combined);	
        if(Gdx.input.isKeyPressed(Keys.Q))
	    	debugActif = !debugActif;    
	}

	@Override
	public void show() {
		inputMultiplexer.addProcessor(this);
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		hero.control(controls.getGauche(), controls.getDroite(), controls.getSaut());
		
		world.setContactListener(new ContactListener(){
			@Override
			public void beginContact(Contact contact) {
				Fixture a = contact.getFixtureA();
			    Fixture b = contact.getFixtureB();
			    
			    //Contact du h�ro
			    if((a.getUserData() != null && a.getUserData().equals("PiedDetecteur")) || (b.getUserData() != null && b.getUserData().equals("PiedDetecteur"))) {
			    	hero.peutSauter++;
				}
			    
			    if(a.getUserData().equals("PiedHero") || b.getUserData().equals("PiedHero")){
			    	if(a.getBody().getUserData() != "Mort" && b.getBody().getUserData() != "Mort"){
			    		for(Monstre monstre : lecteurCarte.getMonstres()){
			    			if(a.getBody() == monstre.bodyMonstre || b.getBody() == monstre.bodyMonstre){
						    	if(a.getUserData().equals("T�te") || b.getUserData().equals("T�te")){
							    	hero.bodyHero.setLinearVelocity(hero.bodyHero.getLinearVelocity().x, 0);
							    	hero.bodyHero.applyForceToCenter(0, 2*hero.saut/3, true);
							    	monstre.contactT�te++;
							    	System.out.println("monstre.contactT�te = " + monstre.contactT�te + " (T�te)");
						    	}
						    	if(a.getUserData().equals("CorpsMonstre") || b.getUserData().equals("CorpsMonstre")){
							    	monstre.contactT�te++;
							    	System.out.println("monstre.contactT�te = " + monstre.contactT�te + " (Corps)");
						    	}
			    			}
			    		}
			    	}
			    	else{
			    		System.out.println("a.getBody().getUserData() = " + a.getBody().getUserData());
			    		System.out.println("b.getBody().getUserData() = " + b.getBody().getUserData());
			    	}
			    }
			    
			    //Contact des lecteurCarte.getMonstres()
			    if(a.getUserData().equals("Objet") || b.getUserData().equals("Objet")){
	
			    	if(a.getUserData().equals("PiedHero") || b.getUserData().equals("PiedHero")){
			    		for(Plateforme plateforme : lecteurCarte.getPlateformes()){
			    			if(a.getBody() == plateforme.bodyPlateforme || b.getBody() == plateforme.bodyPlateforme){
			    				hero.setPlateforme(plateforme.bodyPlateforme);
			    			}
			    		}
			    	}
			    	
					for(Monstre monstre : lecteurCarte.getMonstres()){
					    if(a == monstre.fixturePiedDroit || b == monstre.fixturePiedDroit) {
					    	monstre.piedDroitContact++;
					    	monstre.peutSauter = true;
					    	monstre.demiTour = false;
					    	
					    	for(Plateforme plateforme : lecteurCarte.getPlateformes()){
				    			if(a.getBody() == plateforme.bodyPlateforme || b.getBody() == plateforme.bodyPlateforme){
				    				monstre.setPlateforme(plateforme.bodyPlateforme);
				    				System.out.println("monstre.plateforme = " + monstre.plateforme);
				    			}
				    		}
						}
					    if(a == monstre.fixturePiedGauche || b == monstre.fixturePiedGauche) {
					    	monstre.piedGaucheContact++;
					    	monstre.peutSauter = true;
					    	monstre.demiTour = false;
					    	
					    	for(Plateforme plateforme : lecteurCarte.getPlateformes()){
				    			if(a.getBody() == plateforme.bodyPlateforme || b.getBody() == plateforme.bodyPlateforme){
				    				monstre.setPlateforme(plateforme.bodyPlateforme);
				    				System.out.println("monstre.plateforme = " + monstre.plateforme);
				    			}
				    		}
						}
					}
				}
			    else if((a.getUserData().equals("PiedHero") && b.getUserData().equals("T�te")) || (a.getUserData().equals("T�te") && b.getUserData().equals("PiedHero"))){
					for(Monstre monstre : lecteurCarte.getMonstres()){
				    	if(a == monstre.fixtureT�te || b == monstre.fixtureT�te){
				    		monstre.mort = true;
				    		monstre.bodyMonstre.setLinearVelocity(monstre.bodyMonstre.getLinearVelocity().x, 0);
				    		monstre.bodyMonstre.applyForceToCenter(0, monstre.saut/2, true);
				    		monstre.bodyMonstre.setUserData("Mort");
				    	}
					}
			    }
			    
			}

			@Override
			public void endContact(Contact contact) {
				Fixture a = contact.getFixtureA();
			    Fixture b = contact.getFixtureB();
			    //Contact du h�ro
			    if((a.getUserData() != null && a.getUserData().equals("PiedDetecteur")) || (b.getUserData() != null && b.getUserData().equals("PiedDetecteur"))) {
			    	if(hero.peutSauter > 0)
			    		hero.peutSauter--;
				}

			    //Contact des lecteurCarte.getMonstres()
				if(a.getUserData().equals("Objet") || b.getUserData().equals("Objet")){
					if(a.getUserData().equals("PiedHero") || b.getUserData().equals("PiedHero")){
			    		hero.setPlateforme(null);
			    	}
					
					for(Monstre monstre : lecteurCarte.getMonstres()){
					    if(a == monstre.fixturePiedDroit || b == monstre.fixturePiedDroit) {
					    	monstre.piedDroitContact--;
						}
					    if(a == monstre.fixturePiedGauche || b == monstre.fixturePiedGauche) {
					    	monstre.piedGaucheContact--;
						}
					}
				}
				//EN TRAVAIL
				if(a.getUserData().equals("PiedHero") || b.getUserData().equals("PiedHero")){
			    	if(a.getBody().getUserData() != "Mort" && b.getBody().getUserData() != "Mort"){
			    		for(Monstre monstre : lecteurCarte.getMonstres()){
			    			if(a.getBody() == monstre.bodyMonstre || b.getBody() == monstre.bodyMonstre){
						    	if(a.getUserData().equals("T�te") || b.getUserData().equals("T�te")){
							    	monstre.contactT�te--;
						    	}
						    	if(a.getUserData().equals("CorpsMonstre") || b.getUserData().equals("CorpsMonstre")){
							    	monstre.contactT�te--;
						    	}
			    			}
			    		}
			    	}
			    }
				
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				Body a = contact.getFixtureA().getBody();
			    Body b = contact.getFixtureB().getBody();
			    //Contact des lecteurCarte.getMonstres()
				for(Monstre monstre : lecteurCarte.getMonstres()){
				    //Contact Grenouille					
				    if((a == monstre.bodyMonstre && b.getUserData() != "Objet" && b.getUserData() != "Hero") || (b == monstre.bodyMonstre && a.getUserData() != "Objet" && a.getUserData() != "Hero")){
				    	if(monstre.monstreEnum == MonstreEnum.Grenouille){
				    		contact.setEnabled(false);
				    	}
				    }
				}
				
				//Contact ONE WAY
				if((a.getUserData().equals("Hero") && b.getUserData().equals("Objet")) ||(b.getUserData().equals("Hero") && a.getUserData().equals("Objet"))){					
					for(Obstacle obstacle : lecteurCarte.getObstacles()){
						if(a == obstacle.body || b == obstacle.body){
							if((hero.getY() - hero.getHeight()) > (obstacle.getY() + obstacle.getHeight())){
								contact.setEnabled(true);
							}
							else
								contact.setEnabled(false);
						}
					}
				}
				if((a.getUserData().equals("Monstre") && b.getUserData().equals("Objet")) ||(b.getUserData().equals("Monstre") && a.getUserData().equals("Objet"))){
					for(Monstre monstre : lecteurCarte.getMonstres()){
						if(a == monstre.bodyMonstre || b == monstre.bodyMonstre){
							for(Obstacle obstacle : lecteurCarte.getObstacles()){
								if(a == obstacle.body || b == obstacle.body){
									if((monstre.getY() - monstre.getHeight()) > (obstacle.getY() + obstacle.getHeight())){
										contact.setEnabled(true);
									}
									else
										contact.setEnabled(false);
								}
							}
						}
					}
				}
				
				
				if(a.getUserData() != null && a.getUserData().equals("Mort")){
					contact.setEnabled(false);
				}
				else if(b.getUserData() != null && b.getUserData().equals("Mort")){
					contact.setEnabled(false);
				}
				
				if(a.getUserData().equals("Projectile") || b.getUserData().equals("Projectile")){
					if(a.getUserData().equals("Hero") || b.getUserData().equals("Hero")){
						hero.bodyHero.setUserData("Mort");
		    			System.out.println("**************************** CONTACT PROJECTILE");
						for(Projectile projectile : lecteurCarte.getProjectiles()){
							if(a == projectile.body || b == projectile.body){
								System.out.println("Mort du h�ro");
								projectile.contact = true;
							}
						}
					}
					else
						contact.setEnabled(false);
				}
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				Fixture a = contact.getFixtureA();
			    Fixture b = contact.getFixtureB();
				Body BodyA = contact.getFixtureA().getBody();
			    Body BodyB = contact.getFixtureB().getBody();
			    
			    //Conditions de mort du h�ro
			    if(a.getUserData().equals("PiedHero") || b.getUserData().equals("PiedHero")){
			    	if(a.getBody().getUserData() != "Mort" && b.getBody().getUserData() != "Mort"){
			    		for(Monstre monstre : lecteurCarte.getMonstres()){
			    			if(a.getBody() == monstre.bodyMonstre || b.getBody() == monstre.bodyMonstre){
						    	if(a.getUserData().equals("CorpsMonstre") || b.getUserData().equals("CorpsMonstre")){
						    		if(monstre.contactT�te < 2){
						    			hero.bodyHero.setUserData("Mort");
						    			System.out.println("**************************** CONTACT PIED");
						    		}
						    	}
			    			}
			    		}
			    	}
			    }
			    else if((a.getUserData().equals("CorpsHero") || b.getUserData().equals("CorpsHero")) && (a.getUserData().equals("CorpsMonstre") || b.getUserData().equals("CorpsMonstre"))){
		    		for(Monstre monstre : lecteurCarte.getMonstres()){
		    			if(a.getBody() == monstre.bodyMonstre || b.getBody() == monstre.bodyMonstre){
		    				if(monstre.contactT�te == 0){
				    			hero.bodyHero.setUserData("Mort");
				    			System.out.println("**************************** CONTACT CORPS");
				    		}
		    			}
		    		}
			    }
			    
			    if((BodyA.getUserData().equals("Hero") || BodyB.getUserData().equals("Hero")) && (BodyA.getUserData().equals("Pi�ge") || BodyB.getUserData().equals("Pi�ge"))){
	    			hero.bodyHero.setUserData("Mort");
	    			System.out.println("**************************** CONTACT PI�GE");
			    }

			    //Mort du h�ro par �crasement
			    if(BodyA.getUserData().equals("Hero") || BodyB.getUserData().equals("Hero")){ 
			    	for(int i = 0; i < impulse.getNormalImpulses().length; i++){
				    	if(impulse.getNormalImpulses()[i] > 3){
				    		System.out.println("impulse.getNormalImpulses()[" + i + "] = " + impulse.getNormalImpulses()[i]);
			    			hero.bodyHero.setUserData("Mort");
			    			System.out.println("**************************** ECRASEMENT HERO");
				    	}
			    	}
			    }
			}
		});
	}


	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
