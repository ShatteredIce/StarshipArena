
public class Player {
	
	StarshipArena game;
	String team;
	Planet selectedPlanet = null;
	int resources;
	
	Player(StarshipArena mygame, String myteam){
		game = mygame;
		team = myteam;
	}
	
	public void setResources(int i){
		resources = i;
	}
	
	public int getResources(){
		return resources;
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
