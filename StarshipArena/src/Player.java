import java.util.ArrayList;


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
	
	public ArrayList<Starship> getControlledShips(){
		ArrayList<Starship> myShips = new ArrayList<>();
		ArrayList<Starship> allShips = game.getAllShips();
		for (int i = 0; i < allShips.size(); i++) {
			if(allShips.get(i).getTeam().equals(getTeam())){
				myShips.add(allShips.get(i));
			}
		}
		return myShips;
	}
	
	public ArrayList<Planet> getControlledPlanets(){
		ArrayList<Planet> myPlanets = new ArrayList<>();
		ArrayList<Planet> allPlanets = game.getAllPlanets();
		for (int i = 0; i < allPlanets.size(); i++) {
			if(allPlanets.get(i).getTeam().equals(getTeam())){
				myPlanets.add(allPlanets.get(i));
			}
		}
		return myPlanets;
	}
	
	public int costOfIdleShips() {
		int costOfShips = 1;
		ArrayList<Starship> myShips = getControlledShips();
		for (int i = 0; i < myShips.size(); i++) {
			if (myShips.get(i) instanceof Fighter && myShips.get(i).locationTarget == null) costOfShips += 5;
			else if (myShips.get(i) instanceof Interceptor && myShips.get(i).locationTarget == null) costOfShips += 20;
			else if (myShips.get(i) instanceof Battleship&& myShips.get(i).locationTarget == null) costOfShips += 40;
		}
		return costOfShips;
	}

}
