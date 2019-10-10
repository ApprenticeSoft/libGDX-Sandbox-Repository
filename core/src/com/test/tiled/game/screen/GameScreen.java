package com.test.tiled.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.test.tiled.game.MyGdxGame;
import com.test.tiled.game.OrthogonalTiledMapRendererWithSprites;

public class GameScreen extends InputAdapter implements Screen{

	final MyGdxGame game;
	private OrthographicCamera camera;
	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
    SpriteBatch batch;
    Texture texture;
    //Sprite sprite;
    MapLayer objectLayer;
    TextureRegion textureRegion;
    TextureMapObject character;
    
    /*********TEST COLLISION***********/
    Rectangle characterRectangle;
    MapObjects objects;
    
    /*********TEST ANIMATION***********/
    //TextureAtlas persoAtlas;
    //Animation animDroite, animGauche;
    //float animTime;
    
    /*******************TEST SHADERS**********************/
    String vertexShader;
    String fragmentShader;
    ShaderProgram shaderProgram;
    float posX, posY;
	
	public GameScreen(final MyGdxGame gam){
		game = gam;
        
		camera = new OrthographicCamera(); 
		camera.setToOrtho(false, 7*Gdx.graphics.getWidth()/10, 7*Gdx.graphics.getHeight()/10);
        camera.update();  
        tiledMap = new TmxMapLoader().load("CarteDeMerde.tmx");
        //tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		
		/*******************TEST SHADERS**********************/
		ShaderProgram.pedantic = false;
		
        vertexShader = Gdx.files.internal("Shaders/vVignette.glsl").readString();
        fragmentShader = Gdx.files.internal("Shaders/fVignette.glsl").readString();
        shaderProgram = new ShaderProgram(vertexShader,fragmentShader);
        System.out.println("Shader log : " + shaderProgram.getLog());
        posX = 0.5f;
        posY = 0.5f;
        
        //posX = camera.viewportWidth/2;
        //posY = camera.viewportHeight/2;
        
		shaderProgram.begin();
        shaderProgram.setUniformf("u_resolution", camera.viewportWidth, camera.viewportHeight);
        shaderProgram.setUniformf("u_PosX", posX);
        shaderProgram.setUniformf("u_PosY", posY);
        shaderProgram.end();
        
        batch = new SpriteBatch();
        batch.setShader(shaderProgram);
        tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap, batch);
        
        //Test animation
        //persoAtlas = new TextureAtlas(Gdx.files.internal("Animations/AnimationPerso.pack"));
        //animDroite = new Animation(0.2f, persoAtlas.findRegions("MarcheDroite"), Animation.PlayMode.LOOP);
        //animGauche = new Animation(0.2f, persoAtlas.findRegions("MarcheGauche"), Animation.PlayMode.LOOP);
        
        texture = new Texture(Gdx.files.internal("Pika.png"));
        //sprite = new Sprite(texture);
        
        objectLayer = tiledMap.getLayers().get("Personnage");
        textureRegion = new TextureRegion(texture, 64, 64);
        
        //On ajoute le sprite du personnage dans la couche "Personnage" de la carte
        TextureMapObject tmo = new TextureMapObject(textureRegion);
        tmo.setX(0);
        tmo.setY(0);
        objectLayer.getObjects().add(tmo);
        
        character = (TextureMapObject)tiledMap.getLayers().get("Personnage").getObjects().get(0);
        characterRectangle = new Rectangle();
        characterRectangle.x = character.getX();
        characterRectangle.y = character.getY();
        characterRectangle.width = 64;
        characterRectangle.height = 64;
        

        /******************************TEST COLLISION***************************************/
        /*
        int objectLayerId = 3;
        //TiledMapTileLayer collisionObjectLayer = (TiledMapTileLayer)tiledMap.getLayers().get(objectLayerId);
        objects = tiledMap.getLayers().get(objectLayerId).getObjects();
        for(int i = 0; i < objects.getCount(); i++){
        	System.out.println("objects.get(" + i + ") = " + objects.get(i));
        }
        
        // there are several other types, Rectangle is probably the most common one
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
        	System.out.println("TEST 1");
            Rectangle rectangle = rectangleObject.getRectangle();
        	System.out.println("TEST 2");
            
            
            if (Intersector.overlaps(rectangle, characterRectangle)) {
                System.out.println("Collisions !!!!!!!!!!!!!!!!!!!!!!");
            }
        }
        */
        /***********************************************************************************/
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.5f,0.5f,0.5f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        
        //Test animation
        //animTime+=Gdx.graphics.getDeltaTime();
        //batch.begin();
        //batch.draw(animDroite.getKeyFrame(animTime, true), 50,50);
        //batch.end();
        //character.setTextureRegion(animDroite.getKeyFrame(animTime, true));
        
        //Affichage de la carte
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        
        
		//Control de la camera
        if(Gdx.input.isKeyPressed(Keys.DPAD_LEFT))
        	camera.translate(-32f,0);
        if(Gdx.input.isKeyPressed(Keys.DPAD_RIGHT))
        	camera.translate(32f,0);
        if(Gdx.input.isKeyPressed(Keys.DPAD_UP))
        	camera.translate(0,32f);
        if(Gdx.input.isKeyPressed(Keys.DPAD_DOWN))
        	camera.translate(0,-32f);
        

        if(Gdx.input.isKeyPressed(Keys.NUMPAD_4))
        	character.setX(character.getX() - 5);
        if(Gdx.input.isKeyPressed(Keys.NUMPAD_6))
        	character.setX(character.getX() + 5);
        if(Gdx.input.isKeyPressed(Keys.NUMPAD_8))
        	character.setY(character.getY() + 5);
        if(Gdx.input.isKeyPressed(Keys.NUMPAD_5))
        	character.setY(character.getY() - 5);
        
        //Déplacement du sprite
        if(Gdx.input.isTouched()){
        	Vector3 position = camera.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0)); //Permet d'avoir les mêmes coordonnées pour l'affichage et la détection des clics
        	//sprite.setPosition(position.x, position.y);
            //TextureMapObject character = (TextureMapObject)tiledMap.getLayers().get("Personnage").getObjects().get(0);
            character.setX((float)position.x);
            character.setY((float)position.y);
            

            System.out.println("character.getX() = " + character.getX() + ", character.getY() = " + character.getY());
            /*************TEST COLLISIONS**************/
            //System.out.println("characterRectangle.x = " + characterRectangle.x);
            //System.out.println("characterRectangle.y = " + characterRectangle.y);       
        }

        //posX = camera.project(new Vector3(character.getX(),character.getY(),0)).x * (Gdx.graphics.getWidth()/camera.viewportWidth)/camera.viewportWidth;
        //posY = camera.project(new Vector3(character.getX(),character.getY(),0)).y * (Gdx.graphics.getHeight()/camera.viewportHeight)/camera.viewportHeight;
        
        posX = camera.project(new Vector3(character.getX(),character.getY(),0)).x /camera.viewportWidth;
        posY = camera.project(new Vector3(character.getX(),character.getY(),0)).y /camera.viewportHeight;
        
        System.out.println("posX = " + posX + ", posY = " + posY);
        System.out.println("character.getX() = " + character.getX() + ", character.getY() = " + character.getY());
        
        shaderProgram.begin();
        shaderProgram.setUniformf("u_PosX", posX);
        shaderProgram.setUniformf("u_PosY", posY);
        shaderProgram.end();
        /*************TEST COLLISIONS**************/
        /*
        characterRectangle.x = character.getX();
        characterRectangle.y = character.getY();
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            
            if (Intersector.overlaps(rectangle, characterRectangle)) {
                System.out.println("Collisions !!!!!!!!!!!!!!!!!!!!!!");
            }
        }
        */
	}
	
	@Override
	public void show() {
        Gdx.input.setInputProcessor(this);
	}
	
	

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
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
		batch.dispose();
		texture.dispose();
		tiledMap.dispose();
	}

}
