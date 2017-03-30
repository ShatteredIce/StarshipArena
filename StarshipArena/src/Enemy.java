import java.util.ArrayList;

public class Enemy {
	
	StarshipArena game;
	Player enemyPlayer;
	
	Enemy(StarshipArena mygame, Player newEnemyPlayer){
		game = mygame;
		enemyPlayer = newEnemyPlayer;
	}
	
	public Player getPlayer(){
		return enemyPlayer;
	}
	
	public void buyShips(){
		//placeholder
	}
	
	public void move(){
		ArrayList<Starship> myShips = getControlledShips();
		ArrayList<Planet> myPlanets = getControlledPlanets();
		for (int i = 0; i < myPlanets.size(); i++) {
			Planet p = myPlanets.get(i);
			for (int j = 0; j < myShips.size(); j++) {
				Starship s = myShips.get(j);
				if(game.distance(p.getX(), p.getY(), s.getX(), s.getY()) <= p.getSize() * 2){
					myShips.remove(s);
					j--;
				}
			}
			
		}
		for (int i = 0; i < myShips.size(); i++) {
			Starship s = myShips.get(i);
			if(game.distance(s.getX(), s.getY(), s.getSpawnPoint().X(), s.getSpawnPoint().Y()) > 100)
			myShips.get(i).setLocationTarget(myShips.get(i).getSpawnPoint());
		}
	}
	
	public ArrayList<Starship> getControlledShips(){
		ArrayList<Starship> myShips = new ArrayList<>();
		ArrayList<Starship> allShips = game.getAllShips();
		for (int i = 0; i < allShips.size(); i++) {
			if(allShips.get(i).getTeam().equals(enemyPlayer.getTeam())){
				myShips.add(allShips.get(i));
			}
		}
		return myShips;
	}
	
	public ArrayList<Planet> getControlledPlanets(){
		ArrayList<Planet> myPlanets = new ArrayList<>();
		ArrayList<Planet> allPlanets = game.getAllPlanets();
		for (int i = 0; i < allPlanets.size(); i++) {
			if(allPlanets.get(i).getTeam().equals(enemyPlayer.getTeam())){
				myPlanets.add(allPlanets.get(i));
			}
		}
		return myPlanets;
	}

}
