package com.test.tiled.game;

public class Langue {

	public String commencer, options, noter, removeAds,
					langage, vitesse, sons, plusDApp,
					lent, normal, rapide,
					activ�, d�sactiv�,
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
			activ� = "On";
			d�sactiv� = "Off";
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
		case 2:								//Fran�ais
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
			activ� = "Activ�";
			d�sactiv� = "D�sactiv�";
			groupe = "Groupe";
			niveau = "Niveau";
			choisirNiveau = "Chosissez\nun niveau";
			balles = "Balles";
			objectif = "Objectif";
			reprendre = "Reprendre";
			recommencer = "Recommencer";
			menu = "Menu";
			quitter =  "Quitter";
			niveauComplete = "Niveau Termin�";
			suivant = "Suivant";
			rejouer = "Rejouer";
			fini = "Fini";
			debloque = "D�bloqu�";
			perdu = "Vous avez perdu !";
			jeuComplete = "Jeu Termin� !";
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
			plusDApp = "M�s Apps";
			lent = "Lento";
			normal = "Normal";
			rapide = "R�pido";
			activ� = "Encendido";
			d�sactiv� = "Apagado";
			groupe = "Grupo";
			niveau = "Nivel";
			choisirNiveau = "Seleccione\nun nivel";
			balles = "Balas";
			objectif = "Objetivo";
			reprendre = "Continuar";
			recommencer = "Reiniciar";
			menu = "Men�";
			quitter =  "Salir";
			niveauComplete = "Nivel Completado";
			suivant = "Siguiente";
			rejouer = "Repetir";
			fini = "Terminado";
			debloque = "Desbloqueado";
			perdu = "Has perdido !";
			jeuComplete = "Juego Completado !";
			textNotation = "�Te gusta\nMINIMAL JEZZ?\n�Le eval�e?";
			oui = "S�";
			plusTard = "M�S\nTARDE";
			jamais = "NUNCA";
			removeAds = "Eliminar\nAnuncios";
			break;		
		}
	}
}
