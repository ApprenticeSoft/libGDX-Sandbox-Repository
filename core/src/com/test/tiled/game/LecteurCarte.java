package com.test.tiled.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.test.tiled.game.body.Hero;
import com.test.tiled.game.body.Obstacle;
import com.test.tiled.game.body.Plateforme;
import com.test.tiled.game.body.Projectile;
import com.test.tiled.game.monstres.Castor;
import com.test.tiled.game.monstres.Grenouille;
import com.test.tiled.game.monstres.Monstre;
import com.test.tiled.game.monstres.Oiseau;
import com.test.tiled.game.pi�ges.Balance;
import com.test.tiled.game.pi�ges.BalleSautante;
import com.test.tiled.game.pi�ges.LameRotative;
import com.test.tiled.game.pi�ges.Pi�ge;
import com.test.tiled.game.plantes.Fleure;
import com.test.tiled.game.plantes.Plante;

public class LecteurCarte {

    MapObjects objects;
	Array<Monstre> monstres;
	Array<Plante> plantes;
	Array<Obstacle> obstacles;
	Array<Plateforme> plateformes;
	Array<Pi�ge> pi�ges;
	Array<Projectile> projectiles;
	MyCamera camera;
	World world;
	public float eauPosX, eauPosY;
    
	public LecteurCarte(final MyGdxGame game, TiledMap tiledMap, World world, MyCamera camera){
		this.camera = camera;
		this.world = world;
		
		objects = tiledMap.getLayers().get("Collisions").getObjects();
        
        obstacles = new Array<Obstacle>();
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            Obstacle obstacle = new Obstacle(world, camera, (rectangle.x + rectangle.width/2) * Variables.WORLD_TO_BOX, 
            										(rectangle.y + rectangle.height/2) * Variables.WORLD_TO_BOX, 
            										(rectangle.width/2) * Variables.WORLD_TO_BOX, 
            										(rectangle.height/2) * Variables.WORLD_TO_BOX,
            										1);
            if(rectangleObject.getProperties().get("Type", String.class) != null)
            	obstacles.add(obstacle);
        }
        
        //TEST POLYLINEMAPOBJECT
        plateformes = new Array<Plateforme>();
        
        for(PolylineMapObject polylineObject : objects.getByType(PolylineMapObject.class)){
        	if(polylineObject.getProperties().get("Eau") == null){
        		Plateforme plateforme = new Plateforme(game, world, polylineObject);
        		plateformes.add(plateforme);
        	}
        	else{
        		eauPosX = polylineObject.getPolyline().getTransformedVertices()[0];
        		eauPosY = polylineObject.getPolyline().getTransformedVertices()[1];
        		System.out.println("eauPosX = " + eauPosX);
        		System.out.println("eauPosY = " + eauPosY);
        	}
        }
        
        //Cr�ation des monstres
        monstres = new Array<Monstre>();
        plantes = new Array<Plante>();
        projectiles = new Array<Projectile>();
        pi�ges = new Array<Pi�ge>();
        
        for(int i = 0; i < tiledMap.getLayers().get("Spawn").getObjects().getCount(); i++){
        	if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("Monstre") != null){	
        		if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("Monstre").equals("Castor")){
        			Castor monstre = new Castor(world, camera, tiledMap.getLayers().get("Spawn").getObjects().get(i));
        			monstres.add(monstre);
        		}	
        		else if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("Monstre").equals("Grenouille")){
        			Grenouille monstre = new Grenouille(world, camera, tiledMap.getLayers().get("Spawn").getObjects().get(i));
        			monstres.add(monstre);
        		}	
        		else if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("Monstre").equals("Oiseau")){
        			Oiseau monstre = new Oiseau(world, camera, tiledMap.getLayers().get("Spawn").getObjects().get(i));
        			monstres.add(monstre);
        		}	
        	}
        	else if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("Plante") != null){	
        		if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("Plante").equals("Fleur")){
        			Fleure fleure = new Fleure(world, camera, tiledMap.getLayers().get("Spawn").getObjects().get(i));
        			plantes.add(fleure);
        		}		
        	}
        	else if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("Balance") != null){
        		Balance balance = new Balance(game, world, tiledMap.getLayers().get("Spawn").getObjects().get(i));
        		pi�ges.add(balance);
        	}
        	else if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("BalleSautante") != null){
        		BalleSautante balleSautante = new BalleSautante(game, world, tiledMap.getLayers().get("Spawn").getObjects().get(i));
        		pi�ges.add(balleSautante);
        	}
        	else if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("LameRotative") != null){
        		LameRotative lameRotative = new LameRotative(game, world, tiledMap.getLayers().get("Spawn").getObjects().get(i));
        		pi�ges.add(lameRotative);
        	}
        }
	}
	
	public Array<Monstre> getMonstres(){
		return monstres;
	}
	
	public Array<Plante> getPlantes(){
		return plantes;
	}
	
	public Array<Obstacle> getObstacles(){
		return obstacles;
	}
	
	public Array<Plateforme> getPlateformes(){
		return plateformes;
	}
	
	public Array<Pi�ge> getPi�ges(){
		return pi�ges;
	}
	
	public Array<Projectile> getProjectiles(){
		return projectiles;
	}
	
	public void draw(SpriteBatch batch, float animTime, TextureRegion textureRegion){
		drawMonstre(batch, animTime);
		drawProjectile(batch, textureRegion);
		drawPlante(batch);
		drawPlateforme(batch);
		drawPi�ge(batch);
	}
	
	public void drawMonstre(SpriteBatch batch, float animTime){
		for(Monstre monstre : monstres){
        	monstre.draw(batch, animTime);
        }     
	}
	
	public void drawProjectile(SpriteBatch batch, TextureRegion textureRegion){  
        for(Projectile projectile : projectiles){
        	projectile.draw(batch, textureRegion);
        }              
	}
	
	public void drawPlante(SpriteBatch batch){      
        for(Plante plante : plantes){
        	plante.draw(batch);
        }         
	}
	
	public void drawPlateforme(SpriteBatch batch){  
        for(Plateforme plateforme : plateformes){
        	plateforme.draw(batch);
        }         
	}
	
	public void drawPi�ge(SpriteBatch batch){  
        for(Pi�ge pi�ge : pi�ges){
        	pi�ge.draw(batch);
        }       
	}
	
	public void d�placement(Hero hero, Obstacle obstacleBas){
		for(int i = 0; i < monstres.size; i++){
	    	monstres.get(i).d�placement(camera, hero, world, monstres, obstacleBas);
        }
        for(Plateforme plateforme : plateformes){
        	plateforme.d�placement();
        }      
        for(Plante plante : plantes){
        	plante.active(camera, hero, projectiles);
        }       
        for(Projectile projectile : projectiles){
        	projectile.d�placement(world, projectiles);
        }     
        for(Pi�ge pi�ge : pi�ges){
        	pi�ge.actif();
        }
	}
		
}
