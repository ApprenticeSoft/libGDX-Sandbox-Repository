package com.test.tiled.game;

public class Langue {

	public String commencer, options, noter, removeAds,
					langage, vitesse, sons, plusDApp,
					lent, normal, rapide,
					activé, désactivé,
					groupe, niveau, 
					choisirNiveau,
					balles, objectif,
					reprendre, recommencer, menu, quitter, 
					niveauComplete, suivant, rejouer, fini, debloque, perdu, jeuComplete,
					textNotation, oui, plusTard, jamais;
	private int langue;
	
	public Langue(){
	}
	
	public void setLangue(int i){
		langue = i;
		
		switch(langue){
		case 1:								//Anglais
			commencer = "Start";
			options = "Options";
			noter = "Rate";
			langage = "Language";
			vitesse = "Speed";
			sons = "Sound";
			plusDApp = "More Apps";
			lent = "Slow";
			normal = "Normal";
			rapide = "Fast";
			activé = "On";
			désactivé = "Off";
			groupe = "Group";
			niveau = "Level";
			choisirNiveau = "Choose a level";
			balles = "Balls";
			objectif = "Objective";
			reprendre = "Resume";
			recommencer = "Restart";
			menu = "Menu";
			quitter =  "Quit";
			niveauComplete = "Level Cleared";
			suivant = "Next";
			rejouer = "Replay";
			fini = "Finished";
			debloque = "Unlocked";
			perdu = "You lost !";
			jeuComplete = "Game Cleared !";
			textNotation = "Do you enjoy\nMINIMAL JEZZ ?\nDo you want to rate it ?";
			oui = "YES";
			plusTard = "LATER";
			jamais = "NEVER";
			removeAds = "Remove Ads";
			break;
		case 2:								//Français
			commencer = "Jouer";
			options = "Options";
			noter = "Noter";
			langage = "Langage";
			vitesse = "Vitesse";
			sons = "Son";
			plusDApp = "Plus d'Apps";
			lent = "Lent";
			normal = "Normal";
			rapide = "Rapide";
			activé = "Activé";
			désactivé = "Désactivé";
			groupe = "Groupe";
			niveau = "Niveau";
			choisirNiveau = "Chosissez\nun niveau";
			balles = "Balles";
			objectif = "Objectif";
			reprendre = "Reprendre";
			recommencer = "Recommencer";
			menu = "Menu";
			quitter =  "Quitter";
			niveauComplete = "Niveau Terminé";
			suivant = "Suivant";
			rejouer = "Rejouer";
			fini = "Fini";
			debloque = "Débloqué";
			perdu = "Vous avez perdu !";
			jeuComplete = "Jeu Terminé !";
			textNotation = "Vous aimez\nMINIMAL JEZZ ?\nVoulez vous le noter ?";
			oui = "OUI";
			plusTard = "PLUS\nTARD";
			jamais = "JAMAIS";
			removeAds = "Supprimer\nLes Pubs";
			break;
		case 3:								//Espagnol
			commencer = "Jugar";
			options = "Opciones";
			noter = "Evaluar";
			langage = "Idioma";
			vitesse = "Velocidad";
			sons = "Sonido";
			plusDApp = "Más Apps";
			lent = "Lento";
			normal = "Normal";
			rapide = "Rápido";
			activé = "Encendido";
			désactivé = "Apagado";
			groupe = "Grupo";
			niveau = "Nivel";
			choisirNiveau = "Seleccione\nun nivel";
			balles = "Balas";
			objectif = "Objetivo";
			reprendre = "Continuar";
			recommencer = "Reiniciar";
			menu = "Menú";
			quitter =  "Salir";
			niveauComplete = "Nivel Completado";
			suivant = "Siguiente";
			rejouer = "Repetir";
			fini = "Terminado";
			debloque = "Desbloqueado";
			perdu = "Has perdido !";
			jeuComplete = "Juego Completado !";
			textNotation = "¿Te gusta\nMINIMAL JEZZ?\n¿Le evalúe?";
			oui = "SÍ";
			plusTard = "MÁS\nTARDE";
			jamais = "NUNCA";
			removeAds = "Eliminar\nAnuncios";
			break;		
		}
	}
}
