package mainframe;
import java.util.ArrayList;

import entities.Fighter;
import entities.Interceptor;
import entities.Missileship;
import entities.Planet;
import entities.Starship;


public class Player {
	
	StarshipArena game;
	public String team;
	Planet selectedPlanet = null;
	public ArrayList<Starship> visibleShips = new ArrayList<Starship>();
	ArrayList<Planet> visiblePlanets = new ArrayList<Planet>();
	
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
	
	public ArrayList<Starship> getSelectedShips(){
		ArrayList<Starship> selectedShips = new ArrayList<>();
		ArrayList<Starship> allShips = game.getAllShips();
		for (int i = 0; i < allShips.size(); i++) {
			if(allShips.get(i).isSelected()){
				selectedShips.add(allShips.get(i));
			}
		}
		return selectedShips;
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
			else if (myShips.get(i) instanceof Missileship&& myShips.get(i).locationTarget == null) costOfShips += 40;
		}
		return costOfShips;
	}
	
	public void checkVisible(ArrayList<Planet> controlledPlanets, ArrayList<Starship> controlledShips) {
		for (int i = 0; i < visibleShips.size(); i++) {
			//TODO If and when we implement static structures (e.g. buildings), don't remove them here, unless they are destroyed.
			visibleShips.remove(i);
			i--;
		}
		for (int i = 0; i < visiblePlanets.size(); i++) {
			visiblePlanets.remove(i);
			i--;
		}
		
		//Visibility of planets checked by similar argument to ships; look below
		loop2:
		for (int i = 0; i < game.planets.size(); i++) {
			Planet planet = game.planets.get(i);
			if(game.fog == false){
				visiblePlanets.add(planet);
				continue;
			}
			if(planet.getTeam().equals(this.getTeam())){
				visiblePlanets.add(planet);
				continue;
			}
			Planet currentPlanet;
			for (int j = 0; j < this.getControlledPlanets().size(); j++) {
				currentPlanet = this.getControlledPlanets().get(j);
				//Here, we check if distance from the edge of the planet we're checking to the planet we're checking against is less than radar range
				if(game.distance(planet.getX(), planet.getY(), currentPlanet.getX(), currentPlanet.getY()) <= currentPlanet.getRadarRange() + planet.planetSize){
					visiblePlanets.add(planet);
					continue loop2;
				}
			}
			Starship currentShip;
			for (int s = 0; s < this.getControlledShips().size(); s++) {
				currentShip = this.getControlledShips().get(s);
				//Again, we only care if the ship can see the outer edge of the planet, not the very center of the planet.
				if(game.distance(planet.getX(), planet.getY(), currentShip.getX(), currentShip.getY()) <= currentShip.getRadarRange() + planet.planetSize){
					visiblePlanets.add(planet);
					continue loop2;
				}
			}
		}
		loop:
			//Loop through Starships
		for (int i = 0; i < game.ships.size(); i++) {
			Starship ship = game.ships.get(i);
			//If Fog Of War is off, everything is visible
			if(game.fog == false){
				visibleShips.add(ship);
				continue;
			}
			if(ship.getTeam().equals(this.getTeam())){
				//If ship is on our team, it is visible
				visibleShips.add(ship);
				continue;
			}
			Planet currentPlanet;
			for (int j = 0; j < controlledPlanets.size(); j++) {
				//If any of our planets can see it (distance from ship to planet < planet's radar range), ship is visible
				currentPlanet = controlledPlanets.get(j);
				if(game.distance(ship.getX(), ship.getY(), currentPlanet.getX(), currentPlanet.getY()) <= currentPlanet.getRadarRange()){
					visibleShips.add(ship);
					continue loop;
				}
			}
			//We can see all ships that are on planets that we can see (to prevent player confusion: "Where are those missiles coming from?")
			for (int j = 0; j < visiblePlanets.size(); j++) {
				currentPlanet = visiblePlanets.get(j);
				if (game.distance(ship.getX(), ship.getY(), currentPlanet.getX(), currentPlanet.getY()) <= currentPlanet.planetSize) {
					visibleShips.add(ship);
					continue loop;
				}
			}
			Starship currentShip;
			//If any of our starships can see it, ship is visible
			for (int s = 0; s < controlledShips.size(); s++) {
				currentShip = controlledShips.get(s);
				if(game.distance(ship.getX(), ship.getY(), currentShip.getX(), currentShip.getY()) <= currentShip.getRadarRange()){
					visibleShips.add(ship);
					continue loop;
				}
			}
		}
	}

}
