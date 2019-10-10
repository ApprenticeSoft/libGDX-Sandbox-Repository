package com.test.tiled.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Controls {

    private ImageButton boutonGauche, boutonDroite, boutonSaut;
	private Skin skin;
	
	public Controls(Stage stage, TextureAtlas textureAtlas){
		skin = new Skin();
		skin.addRegions(textureAtlas);	
		
		boutonGauche = new ImageButton(skin.getDrawable("boutonGauche"));
		boutonGauche.setHeight(Gdx.graphics.getWidth()/13);
		boutonGauche.setWidth(boutonGauche.getHeight() * 1.6f);
		boutonGauche.setX(Gdx.graphics.getWidth()/32);
		boutonGauche.setY(boutonGauche.getX());
		boutonGauche.setColor(1,1,1,0.5f);
		boutonDroite = new ImageButton(skin.getDrawable("boutonDroite"));
		boutonDroite.setWidth(boutonGauche.getWidth());
		boutonDroite.setHeight(boutonGauche.getHeight());
		boutonDroite.setX(boutonGauche.getX() + 1.1f*boutonGauche.getWidth());
		boutonDroite.setY(boutonGauche.getX());
		boutonDroite.setColor(1,1,1,0.5f);
		boutonSaut = new ImageButton(skin.getDrawable("boutonSaut"));
		boutonSaut.setWidth(boutonGauche.getHeight());
		boutonSaut.setHeight(boutonGauche.getHeight());
		boutonSaut.setX(Gdx.graphics.getWidth() - boutonGauche.getWidth() - boutonGauche.getX());
		boutonSaut.setY(boutonGauche.getX());
		boutonSaut.setColor(1,1,1,0.5f);

		stage.addActor(boutonGauche);
		stage.addActor(boutonDroite);
		stage.addActor(boutonSaut);	
	}
	
	public ImageButton getGauche(){
		return boutonGauche;
	}
	
	public ImageButton getDroite(){
		return boutonDroite;
	}
	
	public ImageButton getSaut(){
		return boutonSaut;
	}
}
