package com.test.tiled.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.test.tiled.game.MyGdxGame;

public class MainMenuScreen  implements Screen {

	final MyGdxGame game;
	private OrthographicCamera camera;
	private Stage stage;
	private Skin skin;
	private TextureAtlas textureAtlas;
	private BitmapFont fontTitre;
	private static GlyphLayout glyphLayout;
	private TextButton startBouton, vignettageBouton, refletEauBouton, tremblementBouton, ondulationBouton/*, removeAdsBouton, moreAppsBouton*/;
	private Image transitionImage;
	private TextButtonStyle textButtonStyle;
	
	public MainMenuScreen(final MyGdxGame gam){
		game = gam;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		stage = new Stage();
		skin = new Skin();

		fontTitre = game.assets.get("fontTitre.ttf", BitmapFont.class);
		
		textureAtlas = game.assets.get("Images.pack", TextureAtlas.class);
		skin.addRegions(textureAtlas);
		glyphLayout = new GlyphLayout();
		glyphLayout.setText(game.assets.get("fontTitre.ttf", BitmapFont.class), "TESTS SHADERS");
		
		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("BoutonPetit");
		textButtonStyle.down = skin.getDrawable("BoutonPetitChecked");
		textButtonStyle.font = game.assets.get("font1.ttf", BitmapFont.class);
		textButtonStyle.fontColor = Color.WHITE;
		textButtonStyle.downFontColor = new Color(35/256f,59/256f,95/256f, 1);
		
		startBouton = new TextButton(gam.langue.commencer, textButtonStyle);
		startBouton.setWidth(Gdx.graphics.getWidth()/3);
		startBouton.setHeight(Gdx.graphics.getHeight()/10);
		startBouton.setX(Gdx.graphics.getWidth()/4 - startBouton.getWidth()/2);
		startBouton.setY(Gdx.graphics.getHeight()/2 - startBouton.getHeight()/2);
		
		vignettageBouton = new TextButton("Vignettage", textButtonStyle);
		vignettageBouton.setWidth(Gdx.graphics.getWidth()/3);
		vignettageBouton.setHeight(Gdx.graphics.getHeight()/10);
		vignettageBouton.setX(startBouton.getX());
		vignettageBouton.setY(startBouton.getY() - 1.1f*vignettageBouton.getHeight());
		
		refletEauBouton = new TextButton("Reflet Eau", textButtonStyle);
		refletEauBouton.setWidth(Gdx.graphics.getWidth()/3);
		refletEauBouton.setHeight(Gdx.graphics.getHeight()/10);
		refletEauBouton.setY(vignettageBouton.getY() - 1.1f*refletEauBouton.getHeight());
		refletEauBouton.setX(startBouton.getX());
		
		tremblementBouton = new TextButton("Tremblement", textButtonStyle);
		tremblementBouton.setWidth(Gdx.graphics.getWidth()/3);
		tremblementBouton.setHeight(Gdx.graphics.getHeight()/10);
		tremblementBouton.setY(refletEauBouton.getY() - 1.1f*tremblementBouton.getHeight());
		tremblementBouton.setX(startBouton.getX());
		
		ondulationBouton = new TextButton("Ondulation", textButtonStyle);
		ondulationBouton.setWidth(Gdx.graphics.getWidth()/3);
		ondulationBouton.setHeight(Gdx.graphics.getHeight()/10);
		ondulationBouton.setY(tremblementBouton.getY() - 1.1f*ondulationBouton.getHeight());
		ondulationBouton.setX(startBouton.getX());
		/*
		moreAppsBouton = new TextButton(gam.langue.plusDApp, textButtonStyle);
		moreAppsBouton.setWidth(Gdx.graphics.getWidth()/3);
		moreAppsBouton.setHeight(Gdx.graphics.getHeight()/11);
		moreAppsBouton.setY(vignettageBouton.getY() - 1.1f*refletEauBouton.getHeight());

		if(!Donnees.getRate()){
			refletEauBouton.setX(startBouton.getX());
			moreAppsBouton.setX(-Gdx.graphics.getWidth());
		}
		else{
			refletEauBouton.setX(-Gdx.graphics.getWidth());
			moreAppsBouton.setX(startBouton.getX());
		}
		
		
		removeAdsBouton = new TextButton(gam.langue.removeAds, textButtonStyle);
		removeAdsBouton.setWidth(Gdx.graphics.getWidth()/3);
		removeAdsBouton.setHeight(Gdx.graphics.getHeight()/11);
		if(!game.actionResolver.adsListener())
			removeAdsBouton.setX(startBouton.getX());	
		else 
			removeAdsBouton.setX(-Gdx.graphics.getWidth());
		*/
		/*
		if(!Donnees.getRate())
			removeAdsBouton.setY(refletEauBouton.getY() - 1.1f*removeAdsBouton.getHeight());
		else
			removeAdsBouton.setY(vignettageBouton.getY() - 1.1f*removeAdsBouton.getHeight());
		*/
		/*
		removeAdsBouton.setY(refletEauBouton.getY() - 1.1f*removeAdsBouton.getHeight());
		*/
		
		transitionImage = new Image(skin.getDrawable("Barre"));
		transitionImage.setWidth(Gdx.graphics.getWidth());
		transitionImage.setHeight(Gdx.graphics.getHeight());
		transitionImage.setColor(new Color(0.5f,0,0.2f,1));
		transitionImage.setX(-Gdx.graphics.getWidth());
		transitionImage.setY(0);
		transitionImage.addAction(Actions.alpha(0));
		
		
		stage.addActor(startBouton);
		stage.addActor(vignettageBouton);
		stage.addActor(refletEauBouton);
		stage.addActor(tremblementBouton);
		stage.addActor(ondulationBouton);
		//stage.addActor(moreAppsBouton);
		//stage.addActor(removeAdsBouton);
		stage.addActor(transitionImage);
		/*
		if(!Donnees.getRate() && Donnees.getRateCount() < 1){
			tableNotation = new TableNotation(game, skin);
			tableNotation.draw(stage);
		}
		
		if(!Donnees.getRate()){
			startBouton.addAction(Actions.parallel(Actions.alpha(0), Actions.addAction(Actions.alpha(0), vignettageBouton), Actions.addAction(Actions.alpha(0), refletEauBouton), Actions.addAction(Actions.alpha(0), removeAdsBouton)));
			startBouton.addAction(Actions.sequence(Actions.delay(0.1f), Actions.alpha(1, 0.1f), Actions.addAction(Actions.alpha(1, 0.1f), vignettageBouton), Actions.delay(0.1f), Actions.addAction(Actions.alpha(1, 0.1f), refletEauBouton), Actions.delay(0.1f), Actions.addAction(Actions.alpha(1, 0.1f), removeAdsBouton)));
		}
		else{
			startBouton.addAction(Actions.parallel(Actions.alpha(0), Actions.addAction(Actions.alpha(0), vignettageBouton), Actions.addAction(Actions.alpha(0), moreAppsBouton), Actions.addAction(Actions.alpha(0), removeAdsBouton)));
			startBouton.addAction(Actions.sequence(Actions.delay(0.1f), Actions.alpha(1, 0.1f), Actions.addAction(Actions.alpha(1, 0.1f), vignettageBouton), Actions.delay(0.1f), Actions.addAction(Actions.alpha(1, 0.1f), moreAppsBouton), Actions.delay(0.1f), Actions.addAction(Actions.alpha(1, 0.1f), removeAdsBouton)));
		}
		*/
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.5f,0,0.2f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		fontTitre.draw(game.batch, glyphLayout, Gdx.graphics.getWidth()/2 - glyphLayout.width/2, 8*Gdx.graphics.getHeight()/10);
		game.batch.end();
		
		stage.act();
		stage.draw();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		
		startBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				transitionImage.addAction(Actions.moveTo(0, 0));
				transitionImage.addAction(Actions.sequence(Actions.alpha(1, 0.2f),	 
															Actions.run(new Runnable() {
													            @Override
													            public void run() {
																	game.setScreen(new /*DemoScreen*/GameScreen(game));
													            }})));
			}
		});
		
		vignettageBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				transitionImage.addAction(Actions.moveTo(0, 0));
				transitionImage.addAction(Actions.sequence(Actions.alpha(1, 0.2f),	 
															Actions.run(new Runnable() {
													            @Override
													            public void run() {
																	game.setScreen(new VignettageScreen(game));
													            }})));
			}
		});
		
		refletEauBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				transitionImage.addAction(Actions.moveTo(0, 0));
				transitionImage.addAction(Actions.sequence(Actions.alpha(1, 0.2f),	 
															Actions.run(new Runnable() {
													            @Override
													            public void run() {
																	game.setScreen(new RefletEauScreen(game));
													            }})));
			}
		});
		
		tremblementBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				transitionImage.addAction(Actions.moveTo(0, 0));
				transitionImage.addAction(Actions.sequence(Actions.alpha(1, 0.2f),	 
															Actions.run(new Runnable() {
													            @Override
													            public void run() {
																	game.setScreen(new TremblementScreen(game));
													            }})));
			}
		});
		
		ondulationBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				transitionImage.addAction(Actions.moveTo(0, 0));
				transitionImage.addAction(Actions.sequence(Actions.alpha(1, 0.2f),	 
															Actions.run(new Runnable() {
													            @Override
													            public void run() {
																	game.setScreen(new OndulationScreen(game));
													            }})));
			}
		});
		/*
		removeAdsBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				game.actionResolver.removeAds();
				
			}
		});
		
		moreAppsBouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
		       	Gdx.net.openURI(Variables.GOOGLE_PLAY_STORE_URL);
		        //Gdx.net.openURI(Variables.AMAZON_STORE_URL);
			}
		});
		*/
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
		stage.dispose();	
	}
}
