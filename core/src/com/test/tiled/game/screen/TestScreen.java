package com.test.tiled.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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
import com.badlogic.gdx.utils.TimeUtils;
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

public class TestScreen extends InputAdapter implements Screen{

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
    private int[] arrièrePlan = {0,1,2,3,4};
    private int[] premierPlan = {5};
    
    /*******************TEST SHADERS**********************/
    String vertexShader;
    String fragmentShader;
    ShaderProgram shaderProgram, waterShader;
    //Vignettage
    float posX, posY;
    //Reflet
    FrameBuffer fbo;
    TextureRegion fboRegion, refletRegion;
    float refletY;
    Vector3 posEau;
    Texture map;
    
	public TestScreen(final MyGdxGame gam){
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
		/*
		ShaderProgram.pedantic = false;								//Important pour pouvoir modifier les variables uniformes
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
        */
		map = new Texture(Gdx.files.internal("Images/map.png"));
		
        ShaderProgram.pedantic = false;	
        vertexShader = Gdx.files.internal("Shaders/vReflet.glsl").readString();
        fragmentShader = Gdx.files.internal("Shaders/fReflet.glsl").readString();
        shaderProgram = new ShaderProgram(vertexShader,fragmentShader);
        System.out.println("Shader log : " + shaderProgram.getLog());
        //game.batch.setShader(shaderProgram);
        
		shaderProgram.begin();
        shaderProgram.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shaderProgram.end();
        
        waterShader = new ShaderProgram(
        		  Gdx.files.internal("Shaders/vRefletEau.glsl"),
        		  Gdx.files.internal("Shaders/fRefletEau.glsl")
        		);
        waterShader.begin();
        waterShader.setUniformf("u_x_offset", lecteurCarte.eauPosX/Gdx.graphics.getWidth());
        waterShader.setUniformf("u_y_offset", lecteurCarte.eauPosY/Gdx.graphics.getHeight());
        waterShader.end();
        
        posEau = new Vector3();

        fbo = new FrameBuffer(Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f,0.1f,0.1f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.mouvement(hero, tiledMap);
        camera.update();
        
        //Test animation
        animTime+=Gdx.graphics.getDeltaTime();
        /*
		//Position de l'effet (Shader) sur le héro
        posX = camera.project(new Vector3(hero.getX(),hero.getY(),0)).x / camera.viewportWidth;
        posY = camera.project(new Vector3(hero.getX(),hero.getY(),0)).y / camera.viewportHeight;
		
		shaderProgram.begin();
        shaderProgram.setUniformf("u_PosX", posX);
        shaderProgram.setUniformf("u_PosY", posY);
        shaderProgram.end();
        
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render(arrièrePlan);
        //batch.setShader(null);        
        batch.begin(); 
        lecteurCarte.draw(batch, animTime, textureAtlas.findRegion("Barre"));
        hero.draw(batch, animTime); 
        batch.end();
        tiledMapRenderer.render(premierPlan);
        */
        
        /*
        //Le FrameBuffer est le buffer utilisé
      	fbo.begin();
      	//On efface le le FrameBuffer avec du noir
      	Gdx.graphics.getGL20().glClearColor( 0.0f, 0.0f, 0.0f, 1f ); //transparent black
      	Gdx.graphics.getGL20().glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT ); //clear the color buffer   
      		
      	camera.update();
      	game.batch.setProjectionMatrix(camera.combined);
      	//On dessine le contenu de l'écran dans le FrameBuffer
      	tiledMapRenderer.setView(camera);
        tiledMapRenderer.render(arrièrePlan);
        //batch.setShader(null);        
        batch.begin(); 
        lecteurCarte.draw(batch, animTime, textureAtlas.findRegion("Barre"));
        hero.draw(batch, animTime); 
        batch.end();
        tiledMapRenderer.render(premierPlan);
      	fbo.end();
      		
  		//On crée un texture unique à partir du FrameBuffer
  		//fbo.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
  		fboRegion = new TextureRegion(fbo.getColorBufferTexture());
  		//Important pour remettre l'image à l'endroit
  		fboRegion.flip(false, true);	
  		
  		//now we are rendering to the back buffer (Display) again
  		game.batch.begin();
        game.batch.setShader(shaderProgram);
  		//draw our offscreen FBO texture to the screen with the given alpha
  		game.batch.setColor(1f, 1f, 1f, 1);
  		game.batch.draw(fboRegion, camera.position.x - camera.viewportWidth/2, camera.position.y - camera.viewportHeight/2, camera.viewportWidth, camera.viewportHeight);
  		//Penser à remettre le shader par défaut
  		//Pour éviter d'applique le shader perso au FrameBuffer
        game.batch.setShader(null);
  		game.batch.end();
        */   
        
        //Le FrameBuffer est le buffer utilisé
      	fbo.begin();
      	//On efface le le FrameBuffer avec du noir
      	Gdx.graphics.getGL20().glClearColor( 0.0f, 0.0f, 0.0f, 1f );
      	//clear the color buffer 
      	Gdx.graphics.getGL20().glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );   
      		
      	camera.update();
      	game.batch.setProjectionMatrix(camera.combined);
      	//On dessine le contenu de l'écran dans le FrameBuffer
      	tiledMapRenderer.setView(camera);
        tiledMapRenderer.render(arrièrePlan);     
        batch.begin(); 
        lecteurCarte.draw(batch, animTime, textureAtlas.findRegion("Barre"));
        hero.draw(batch, animTime); 
        batch.end();
        tiledMapRenderer.render(premierPlan);
      	fbo.end();
      		
  		//On crée un texture unique à partir du FrameBuffer
  		fboRegion = new TextureRegion(fbo.getColorBufferTexture());
  		//Important pour remettre l'image à l'endroit
  		fboRegion.flip(false, true);	
  		/*
  		 * Dessin du reflet dans l'eau
  		 * On veut que la position de l'eau prenne en compte les mouvement de la caméra
  		 */
  		//Permet de calculer la position de la texture du reflet
  		refletY = lecteurCarte.eauPosY;		
  		//Permet de calculer la position de la texture de déplacement pour le shader
  		posEau.set(lecteurCarte.eauPosX,refletY,0);	
  		camera.unproject(posEau);
  		//On crée la texture du reflet
  		refletRegion = new TextureRegion(	fbo.getColorBufferTexture(), 
  											0, 
  											(int)((camera.viewportHeight - posEau.y)*Gdx.graphics.getHeight()/camera.viewportHeight),  
  											fbo.getColorBufferTexture().getWidth(), 
  											Gdx.graphics.getHeight() - (int)((camera.viewportHeight - posEau.y)*Gdx.graphics.getHeight()/camera.viewportHeight));
  		refletRegion.flip(false, true);
  		//On lie la texture de déplacement au bon buffer
  		map.bind(1);
  		//On lie la texture du paysage au buffer par défaut
  		fbo.getColorBufferTexture().bind(0);
  		
  		/*
  		 * On met à jour les différentes données dans le shader :
  		 * Texture de déplacement, temps (pour calculer l'animation),
  		 * positions horizontale et verticale de la texture de
  		 * déplacement en fonction de la position de la caméra
  		 */
        waterShader.begin();
        waterShader.setUniformi("u_texture1", 1);
        waterShader.setUniformf("u_time", animTime);
        waterShader.setUniformf("u_x_offset", ((camera.viewportWidth - posEau.x)*Gdx.graphics.getWidth()/camera.viewportWidth)/Gdx.graphics.getWidth());
        waterShader.setUniformf("u_y_offset", ((camera.viewportHeight - posEau.y)*Gdx.graphics.getHeight()/camera.viewportHeight)/Gdx.graphics.getHeight());
        waterShader.end();
  		//On dessine le paysage puis le reflet
  		game.batch.begin();
  		game.batch.draw(fboRegion, camera.position.x - camera.viewportWidth/2, camera.position.y - camera.viewportHeight/2, camera.viewportWidth, camera.viewportHeight);
  		game.batch.setShader(waterShader);
  		game.batch.draw(refletRegion, camera.position.x - camera.viewportWidth/2, refletY*Variables.WORLD_TO_BOX, camera.viewportWidth, -(camera.viewportHeight - refletY*Variables.WORLD_TO_BOX));
        game.batch.setShader(null);
  		game.batch.end();
  		
        //Déplacements
	    hero.déplacement();
	    lecteurCarte.déplacement(hero, obstacleBas);
	    
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
			    
			    //Contact du héro
			    if((a.getUserData() != null && a.getUserData().equals("PiedDetecteur")) || (b.getUserData() != null && b.getUserData().equals("PiedDetecteur"))) {
			    	hero.peutSauter++;
				}
			    
			    if(a.getUserData().equals("PiedHero") || b.getUserData().equals("PiedHero")){
			    	if(a.getBody().getUserData() != "Mort" && b.getBody().getUserData() != "Mort"){
			    		for(Monstre monstre : lecteurCarte.getMonstres()){
			    			if(a.getBody() == monstre.bodyMonstre || b.getBody() == monstre.bodyMonstre){
						    	if(a.getUserData().equals("Tête") || b.getUserData().equals("Tête")){
							    	hero.bodyHero.setLinearVelocity(hero.bodyHero.getLinearVelocity().x, 0);
							    	hero.bodyHero.applyForceToCenter(0, 2*hero.saut/3, true);
							    	monstre.contactTête++;
							    	System.out.println("monstre.contactTête = " + monstre.contactTête + " (Tête)");
						    	}
						    	if(a.getUserData().equals("CorpsMonstre") || b.getUserData().equals("CorpsMonstre")){
							    	monstre.contactTête++;
							    	System.out.println("monstre.contactTête = " + monstre.contactTête + " (Corps)");
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
			    else if((a.getUserData().equals("PiedHero") && b.getUserData().equals("Tête")) || (a.getUserData().equals("Tête") && b.getUserData().equals("PiedHero"))){
					for(Monstre monstre : lecteurCarte.getMonstres()){
				    	if(a == monstre.fixtureTête || b == monstre.fixtureTête){
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
			    //Contact du héro
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
						    	if(a.getUserData().equals("Tête") || b.getUserData().equals("Tête")){
							    	monstre.contactTête--;
						    	}
						    	if(a.getUserData().equals("CorpsMonstre") || b.getUserData().equals("CorpsMonstre")){
							    	monstre.contactTête--;
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
								System.out.println("Mort du héro");
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
			    
			    //Conditions de mort du héro
			    if(a.getUserData().equals("PiedHero") || b.getUserData().equals("PiedHero")){
			    	if(a.getBody().getUserData() != "Mort" && b.getBody().getUserData() != "Mort"){
			    		for(Monstre monstre : lecteurCarte.getMonstres()){
			    			if(a.getBody() == monstre.bodyMonstre || b.getBody() == monstre.bodyMonstre){
						    	if(a.getUserData().equals("CorpsMonstre") || b.getUserData().equals("CorpsMonstre")){
						    		if(monstre.contactTête < 2){
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
		    				if(monstre.contactTête == 0){
				    			hero.bodyHero.setUserData("Mort");
				    			System.out.println("**************************** CONTACT CORPS");
				    		}
		    			}
		    		}
			    }
			    
			    if((BodyA.getUserData().equals("Hero") || BodyB.getUserData().equals("Hero")) && (BodyA.getUserData().equals("Piège") || BodyB.getUserData().equals("Piège"))){
	    			hero.bodyHero.setUserData("Mort");
	    			System.out.println("**************************** CONTACT PIÈGE");
			    }

			    //Mort du héro par écrasement
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
