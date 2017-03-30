
public class Player {
	
	StarshipArena game;
	String team;
	Planet selectedPlanet = null;
	
	Player(StarshipArena mygame, String myteam){
		game = mygame;
		team = myteam;
	}
	
	public void setSelectedPlanet(Planet p){
		selectedPlanet = p;
	}
	
	public Planet getSelectedPlanet(){
		return selectedPlanet;
	}
	
	public String getTeam(){
		return team;
	}

}
