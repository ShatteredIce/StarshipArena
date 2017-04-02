import java.util.ArrayList;

public class Turret {
	
	StarshipArena game;
	String team;
	
	//turret firing variables
	int cooldown;
	int current_cooldown = 0;
	int spread;
	int accuracy;
	int scan_range;
	int projectile_damage;
	int projectile_speed;
	int projectile_lifetime;
	int projectile_textureId;
	Point center;
	//turret turning variables
	int turn_speed;
	int angle;
	
	Turret(StarshipArena mygame, String myteam, double spawnx, double spawny, int newangle, int newdamage, int newcooldown, int newspread, int newaccuracy, int newscanrange, int newspeed, int newlifetime, int newid){
		game = mygame;
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
	
	public void update(){
		if(current_cooldown == 0){
			ArrayList<Starship> enemyShips = getEnemyShips();
			int relativeAngle;
			for (int i = 0; i < enemyShips.size(); i++) {
				relativeAngle = game.angleToPoint(center.X(), center.Y(), enemyShips.get(i).getX(), enemyShips.get(i).getY());
				if(Math.abs(relativeAngle - angle) < spread){
					fire();
					current_cooldown = cooldown;
					break;
				}
			}
		}
		else{
			current_cooldown--;
		}
	}
	
	public void fire(){
		new Projectile(game, team, center.X(), center.Y(), projectile_damage, angle, accuracy, projectile_speed, projectile_lifetime);
	}
	
	public ArrayList<Starship> getEnemyShips(){
		ArrayList<Starship> allShips = game.getAllShips();
		if(team == "none"){
			return allShips;
		}
		ArrayList<Starship> enemyShips = new ArrayList<>();
		for (int i = 0; i < allShips.size(); i++) {
			if(!allShips.get(i).getTeam().equals(team) && game.distance(center.X(), center.Y(), allShips.get(i).getX(), allShips.get(i).getY()) <= scan_range){
				enemyShips.add(allShips.get(i));
			}
		}
		return enemyShips;
	}
	
	public int getAngle(){
		return angle;
	}
	
	public void setAngle(int newangle){
		angle = newangle;
	}
	

}
