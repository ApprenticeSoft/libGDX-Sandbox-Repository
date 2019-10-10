package com.test.tiled.game;


public class Variables {
	
	//Constantes du World
	public static float WORLD_TO_BOX = 0.01f;
	public static float BOX_TO_WORLD = 1/WORLD_TO_BOX;
	public static float BOX_STEP = 1/60f; 
	public static int BOX_VELOCITY_ITERATIONS = 6;
	public static int BOX_POSITION_ITERATIONS = 2;
	
	//Constantes de la Tiled Map
	public static int nbPixelTile = 32;
	
	//Gestion des niveaux
	public static int nombreNiveaux = 25;
	public static int niveauSelectione = 1;
	public static float objectif = 70;
	public static int couleurSelectionee = 1;
	
	public static boolean début = true;
	public static boolean pause = true;
	public static boolean perdu = true;
	public static boolean gagné = true;
	public static int INTERSTITIAL_TRIGGER = 2;
	
	//Propriétés de la balle
	public static float vitesseBalle = 0.7f;
	
	//Gestion des barres
	public static boolean spawn = false;
	public static float largeurBordure; 
	public static float posBordure = 0.05f;
	
	//Liens vers les App Store
	public static final String GOOGLE_PLAY_GAME_URL = "https://play.google.com/store/apps/details?id=com.minimal.jezz.android";
	public static final String GOOGLE_PLAY_STORE_URL = "https://play.google.com/store/apps/developer?id=Apprentice+Soft";
	public static final String AMAZON_GAME_URL = "http://www.amazon.com/gp/mas/dl/android?p=com.minimal.jezz.android";
	public static final String AMAZON_STORE_URL = "http://www.amazon.com/gp/mas/dl/android?p=com.premier.jeu.android&showAll=1";
	
	
}
