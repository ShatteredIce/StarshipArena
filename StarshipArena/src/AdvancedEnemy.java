import java.util.ArrayList;
import java.util.Random;

public class AdvancedEnemy extends Enemy{
	int attackDelay = 1000;
	int counter = 0;
	Random random = new Random();

	AdvancedEnemy(StarshipArena mygame, Player newEnemyPlayer) {
		super(mygame, newEnemyPlayer);
	}
	
	public void buyShips(){
		int shipType;
		ArrayList<Planet> myPlanets = enemyPlayer.getControlledPlanets();
		ArrayList<Starship> playerShips = game.player.getControlledShips();
		int fightersCost = 0;
		int interceptorsCost = 0;
		int battleshipsCost = 0;
		for (int i = 0; i < playerShips.size(); i++) {
			if (playerShips.get(i) instanceof Fighter) fightersCost += 5;
			else if (playerShips.get(i) instanceof Interceptor) interceptorsCost += 20;
			else if (playerShips.get(i) instanceof Battleship) battleshipsCost += 40;
		}
		int total = fightersCost + interceptorsCost + battleshipsCost;
		//Find the proportion of each type of ship. This helps weight ship purchase probability by enemy ship proportion
		double fighterProp = (double)fightersCost / total;
		double interceptorProp = (double)interceptorsCost / total;
		double battleshipProp = (double)battleshipsCost / total;
		for (int i = 0; i < myPlanets.size(); i++) {
			double rand = random.nextDouble();
			if (rand < fighterProp) {
				if (myPlanets.get(i).getResources() >= 40) {
					enemyPlayer.setSelectedPlanet(myPlanets.get(i));
					game.buyShips(enemyPlayer, 3);
				}
				else if (myPlanets.get(i).getResources() >= 5) {
					enemyPlayer.setSelectedPlanet(myPlanets.get(i));
					game.buyShips(enemyPlayer, 1);
					if (attackDelay < 2000)
						attackDelay += 1000;
				}
			}
			else if (rand < fighterProp + interceptorProp) {
				if (myPlanets.get(i).getResources() >= 5) {
					enemyPlayer.setSelectedPlanet(myPlanets.get(i));
					game.buyShips(enemyPlayer, 1);
				}
			}
			else if (rand < fighterProp + interceptorProp + battleshipProp) {
				if (myPlanets.get(i).getResources() >= 20) {
					enemyPlayer.setSelectedPlanet(myPlanets.get(i));
					game.buyShips(enemyPlayer, 2);
				}
				else if (myPlanets.get(i).getResources() >= 5) {
					enemyPlayer.setSelectedPlanet(myPlanets.get(i));
					game.buyShips(enemyPlayer, 1);
					if (attackDelay < 2000)
						attackDelay += 1000;
				}
			}
		}
	}
	
	public void move() {
		//TODO Maybe make the enemy purchase upgrades to the planet once they are implemented
		counter++;
		//If AI has delayed long enough, attempt an attack and buy ships. Then decide a random new attack delay
		if (counter > attackDelay) {
			counter = 0;
			attackDelay = random.nextInt(250) + 50;
			int shipCountCheck = random.nextInt(game.player.costOfIdleShips());
			//AI's chance of attacking increases from 0% (AI has 50% of idle ships in cost as player)
			//to 100% (AI has 150% of idle ships in cost as player)
			if (enemyPlayer.costOfIdleShips() - game.player.costOfIdleShips() / 2 > shipCountCheck)
				performAttack();
			buyShips();
		}
		ArrayList<Starship> myShips = enemyPlayer.getControlledShips();
		for (int i = 0; i < myShips.size(); i++) {
			if (myShips.get(i) instanceof Battleship && myShips.get(i).target != null)
				myShips.get(i).setLocationTarget(null);
		}
	}
	
	public void performAttack() {
		ArrayList<Starship> myShips = enemyPlayer.getControlledShips();
		ArrayList<Starship> myTransports = new ArrayList<Starship>();
		ArrayList<Planet> myPlanets = enemyPlayer.getControlledPlanets();
		ArrayList<Starship> attackGroup = new ArrayList<Starship>();
		int costOfShips = 0;
		//Remove ships that are currently capturing a non-controlled planet from the ships that can be part of a valid attack
		for (int i = 0; i < game.planets.size(); i++) {
			if (game.planets.get(i).capturingTeam.equals(enemyPlayer.getTeam())
					&& !game.planets.get(i).getTeam().equals(enemyPlayer.getTeam())) {
				ArrayList<Starship> orbitingShips = game.planets.get(i).getShips();
				//For each ship orbiting a non-controlled planet that has enemy player as the capturing team,
				//attempt to remove it from the myships array list
				for (int j = 0; j < orbitingShips.size(); j++) {
					myShips.remove(orbitingShips.get(j));
				}
			}
		}
		for (int i = 0; i < myShips.size(); i++) {
			if (myShips.get(i) instanceof Fighter && myShips.get(i).locationTarget == null) costOfShips += 5;
			else if (myShips.get(i) instanceof Interceptor && myShips.get(i).locationTarget == null) costOfShips += 20;
			else if (myShips.get(i) instanceof Battleship && myShips.get(i).locationTarget == null) costOfShips += 40;
			else if (myShips.get(i) instanceof Transport) {
				myTransports.add(myShips.remove(i));
				i--;
			}
		}
		//For idle transports, choose a random controlled planet and move it there
		for (int i = 0; i < myTransports.size(); i++) {
			if (myTransports.get(i).locationTarget == null) {
				if (myPlanets.size() > 0) {
					int targetPlanet = random.nextInt(myPlanets.size());
					myTransports.get(i).setLocationTarget(myPlanets.get(targetPlanet).center);
				}
			}
		}
		//AI spends roughly between 10% and 50% of their ships (in cost) on attack
		int costOfAttack = random.nextInt(costOfShips * 4 / 10 + 1) + costOfShips / 10;
		for (int i = 0; i < myShips.size(); i++) {
			if (costOfAttack <= 0) break;
			if (myShips.get(i) instanceof Fighter && myShips.get(i).locationTarget == null) {
				attackGroup.add(myShips.get(i));
				costOfAttack -= 5;
			}
			else if (myShips.get(i) instanceof Interceptor && myShips.get(i).locationTarget == null) {
				attackGroup.add(myShips.get(i));
				costOfAttack -= 20;
			}
			else if (myShips.get(i) instanceof Battleship && myShips.get(i).locationTarget == null) {
				attackGroup.add(myShips.get(i));
				costOfAttack -= 40;
			}
		}
		//First target any planets without ships around them
		boolean attackingUnprotected = false;
		for (int i = 0; i < game.planets.size(); i++) {
			if (game.planets.get(i).capturingTeam.equals("none")) {
				for (int j = 0; j < attackGroup.size(); j++) {
					attackGroup.get(j).setLocationTarget(game.planets.get(i).center);
				}
				attackingUnprotected = true;
				break;
			}
		}
		if (!attackingUnprotected) {
		//50-50 chance of targeting an enemy ship or a non-allied planet
		boolean attackPlanet = random.nextBoolean();
			if (attackPlanet) {
				int targetPlanet = -1;
				//Up to 5 attempts to choose a non-allied planet to attack.
				//Otherwise, an allied planet will have been chosen and AI will patrol it.
				for (int attempt = 0; attempt < 5; attempt++) {
					if (game.planets.size() > 0) {
						targetPlanet = random.nextInt(game.planets.size());
						if (!game.planets.get(targetPlanet).getTeam().equals(enemyPlayer.getTeam())) break;
					}
				}
				if (targetPlanet == -1) {
					attackPlanet = false;
				}
				else {
					for (int i = 0; i < attackGroup.size(); i++) {
						attackGroup.get(i).setLocationTarget(game.planets.get(targetPlanet).center);
					}
				}
			}
			if (!attackPlanet) {
				ArrayList<Starship> playerShips = game.player.getControlledShips();
				//TODO Remove this temporary workaround that exists to prevent game from crashing.
				//TODO Instead, make the level end when one side or the other is wiped out.
				if(playerShips.size() > 0) {
					int targetShip = random.nextInt(playerShips.size());
					for (int i = 0; i < attackGroup.size(); i++) {
						attackGroup.get(i).setLocationTarget(playerShips.get(targetShip).center);
					}
				}
			}
		}
		
	}

}
