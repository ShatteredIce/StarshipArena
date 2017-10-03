



//TODO Implemetn quadtree/sectors, then we won't need to do any of this Surface complicated BS
import java.util.ArrayList;

public class Surface {
	StarshipArena game;
	ArrayList<Starship> ships = new ArrayList<>();
	ArrayList<Projectile> projectiles = new ArrayList<>();
	ArrayList<Explosion> explosions = new ArrayList<>();
	
    double START_X;
    double START_Y;
    double END_X;
    double END_Y;
    
	public Surface(StarshipArena newgame, int startx, int starty, int endx, int endy) {
		START_X = startx;
		START_Y = starty;
		END_X = endx;
		END_Y = endy;
		game = newgame;
		//Add some ships for testing
		for (int i = 0; i < 10; i++) {
			Starship newShip = new Fighter(game, "blue", START_X + 500, START_Y + 500, 0);
			ships.add(newShip);
			game.ships.remove(newShip);
		}
	}

}
