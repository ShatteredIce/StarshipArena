
public class Missile extends Projectile {

	public Missile(StarshipArena mygame, Starship newowner, String myteam, double spawnx, double spawny, double newdamage, int spawnangle, int accuracy, double newspeed, double newlifetime, int id) {
		super(mygame, newowner, myteam, spawnx, spawny, newdamage, spawnangle, accuracy, newspeed, newlifetime, id);
	}
	
	public void destroy(){
		model.destroy();
		game.removeProjectile(this);
		for (int i = 0; i < 360; i += 5) {
			new Projectile(game, null, "none", center.x, center.y, 0.5, i, 100, 10, 10, 3);
			new Projectile(game, null, "none", center.x, center.y, 0.5, i, 100, 10, 10, 3);
			new Projectile(game, null, "none", center.x, center.y, 0.5, i, 100, 10, 10, 3);
		}
	}

}
