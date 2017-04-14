import java.util.ArrayList;

public class Turret {
	
	StarshipArena game;
	Starship owner;
	String team;
	
	//turret firing variables
	int cooldown;
	int current_cooldown = 0;
	int spread;
	int accuracy;
	int scan_range;
	double projectile_damage;
	int projectile_speed;
	int projectile_lifetime;
	int projectile_textureId;
	Point center;
	//turret turning variables
	int turn_speed;
	int angle;
	double xOff = 0;
	double yOff = 0;
	boolean autoAiming = false;
	boolean fireMissiles = false;
	int angle_offset = 0;
	
	Turret(StarshipArena mygame, Starship newowner, String myteam, double spawnx, double spawny, int newangle, double newdamage, int newcooldown, int newspread, int newaccuracy, int newscanrange, int newspeed, int newlifetime, int newid){
		game = mygame;
		owner = newowner;
		team = myteam;
		center = new Point(spawnx, spawny);
		angle = newangle;
		cooldown = newcooldown;
		spread = newspread;
		accuracy = newaccuracy;
		scan_range = newscanrange;
		projectile_damage = newdamage;
		projectile_speed = newspeed;
		projectile_lifetime = newlifetime / projectile_speed;
		projectile_textureId = newid;
	}
	
	Turret(StarshipArena mygame, Starship newowner, String myteam, double spawnx, double spawny, int newangle, double newdamage, int newcooldown, int newspread, int newaccuracy, int newscanrange, int newspeed, int newlifetime, int newid, int modifier, int newangle_offset){
		game = mygame;
		owner = newowner;
		team = myteam;
		center = new Point(spawnx, spawny);
		angle = (newangle + newangle_offset) % 360;
		cooldown = newcooldown;
		spread = newspread;
		accuracy = newaccuracy;
		scan_range = newscanrange;
		projectile_damage = newdamage;
		projectile_speed = newspeed;
		projectile_lifetime = newlifetime / projectile_speed;
		projectile_textureId = newid;
		if (modifier % 2 == 1) autoAiming = true;
		if (modifier > 1) fireMissiles = true;
		angle_offset = newangle_offset;
	}
	
	public void setOffset(double newx, double newy){
		xOff = newx;
		yOff = newy;
	}
	
	public void update(){
		if(current_cooldown == 0){
			ArrayList<Starship> enemyShips = getEnemyShips();
			int relativeAngle;
			for (int i = 0; i < enemyShips.size(); i++) {
				relativeAngle = game.angleToPoint(center.X(), center.Y(), enemyShips.get(i).getX(), enemyShips.get(i).getY());
				if(Math.abs(relativeAngle - angle) < spread){
					boolean fired = false;
					if (!autoAiming) {
						fire(angle);
						fired = true;
					}
					else if (enemyShips.get(i).equals(owner.target) || spread < 180) {
						fire(relativeAngle);
						fired = true;
					}
					if (fired == true) {
						current_cooldown = cooldown;
						break;
					}
				}
			}
		}
		else{
			current_cooldown--;
		}
	}
	
	public void fire(int newAngle){
		if (!fireMissiles)
			new Projectile(game, owner, team, center.X(), center.Y(), projectile_damage, newAngle, accuracy, projectile_speed, projectile_lifetime, projectile_textureId);
		else
			new Missile(game, owner, team, center.X(), center.Y(), projectile_damage, newAngle, accuracy, projectile_speed, projectile_lifetime, projectile_textureId);
	}
	
	public ArrayList<Starship> getEnemyShips(){
		ArrayList<Starship> allShips = game.getAllShips();
		ArrayList<Starship> enemyShips = new ArrayList<>();
		if(team == "none"){
			for (int i = 0; i < allShips.size(); i++) {
				if(game.distance(center.X(), center.Y(), allShips.get(i).getX(), allShips.get(i).getY()) <= scan_range){
					enemyShips.add(allShips.get(i));
				}
			}
		}
		else {
			for (int i = 0; i < allShips.size(); i++) {
				if(!allShips.get(i).getTeam().equals(team) && game.distance(center.X(), center.Y(), allShips.get(i).getX(), allShips.get(i).getY()) <= scan_range){
					enemyShips.add(allShips.get(i));
				}
			}
		}
		return enemyShips;
	}
	
	public int getAngle(){
		return angle;
	}
	
	public void setAngle(int newangle){
		angle = (newangle + angle_offset) % 360;
	}
	
	public void setCenter(Point p){
		center = p;
	}
	
	public void setX(double newx){
		center.setX(newx);
	}
	
	public void setY(double newy){
		center.setY(newy);
	}
	
	public double getXOffset(){
		return xOff;
	}
	
	public double getYOffset(){
		return yOff;
	}

}
