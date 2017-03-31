import java.util.ArrayList;
import java.util.Random;

public class AdvancedEnemy extends Enemy{
	
	Random random = new Random();

	AdvancedEnemy(StarshipArena mygame, Player newEnemyPlayer) {
		super(mygame, newEnemyPlayer);
	}
	
	public void buyShips(){
		int shipType;
		ArrayList<Planet> myPlanets = getControlledPlanets();
		for (int i = 0; i < myPlanets.size(); i++) {
			if(myPlanets.get(i).getResources() >= 20){
				shipType = random.nextInt(10) + 1;
				if(shipType > 3){
					shipType = 1;
				}
				enemyPlayer.setSelectedPlanet(myPlanets.get(i));
				game.buyShips(enemyPlayer, shipType);
			}
		}
	}

}
