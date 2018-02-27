import java.util.ArrayList;

public class Wallship extends Starship{
	int changeDirection = 0;
	int changeDirectionCooldown = 0;
	//TODO Wrong sprites
	static Texture missileship_sprites = new Texture("missileship_sprites.png");
	
	//Dual machinegun turrets (360 degree)
	static double primary_damage = 0.5;
	static int primary_cooldown = 20;
	static int primary_spread = 360;
	static int primary_accuracy = 97;
	static int primary_range = 500;
	static int primary_speed = 20; 
	static int primary_lifetime = 550;
	static int primary_xoffset = -30;
	static int primary_yoffset = 30;
	static int primary_id = 3;
	
	public Wallship(StarshipArena mygame, double spawnx, double spawny){
		super(mygame, spawnx, spawny);
	}
	
	public Wallship(StarshipArena mygame, double spawnx, double spawny, double spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public Wallship(StarshipArena mygame, String newteam, double spawnx, double spawny, double spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	//Stats were modified
	public void shipStats(){
		max_health = 300;
		//movement
		acceleration = 0.1;
		max_velocity = 6;
		max_reverse_velocity = -2;
		min_turn_velocity = 1;
		max_turn_speed = 2;
		//weaponry
		scan_range = 500;
		radar_range = 1000;
		//other
		clickRadius = 110;
		xOff = 0;
		yOff = 0;
		
		//Wallship has larger halo and weight
		haloSize = 160;
		weight = 3;
	}
	public void shipTurrets(){
//		if(team.equals("blue")){
//			primary_id = 1;
//		}
//		else if(team.equals("red")){
//			primary_id = 2;
//		}
		Turret primaryTurret1 = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id, 0, 1);
//		primaryTurret1.setOffset(primary_xoffset + 25, primary_yoffset + 20, -15);
		primaryTurret1.setOffset(-primary_xoffset, primary_yoffset);
		turrets.add(primaryTurret1);
		
		Turret primaryTurret2 = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id, 0, 1);
//		primaryTurret2.setOffset(primary_xoffset - 25, primary_yoffset + 20, -5);
		primaryTurret2.setOffset(primary_xoffset, primary_yoffset);
		turrets.add(primaryTurret2);
		
		//Seems like giving Wallship 4 turrets is too OP
		
//		Turret primaryTurret3 = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
//				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id, 0, 1);
////		primaryTurret1.setOffset(primary_xoffset + 25, primary_yoffset + 20, -15);
//		primaryTurret3.setOffset(-2 * primary_xoffset, primary_yoffset);
//		turrets.add(primaryTurret3);
//		
//		Turret primaryTurret4 = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
//				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id, 0, 1);
////		primaryTurret2.setOffset(primary_xoffset - 25, primary_yoffset + 20, -5);
//		primaryTurret4.setOffset(2 * primary_xoffset, primary_yoffset);
//		turrets.add(primaryTurret4);
	}
	
//	public void moveTurrets(){
//		for (int i = 0; i < turrets.size(); i++) {
//			Point p = new Point();
//			p.setX(turrets.get(i).getXOffset() + center.X());
//			p.setY(turrets.get(i).getYOffset() + center.Y());
//			p.rotatePoint(center.X(), center.Y(), angle);
//			turrets.get(i).setCenter(p);
//			turrets.get(i).setAngle(angle);
//			turrets.get(i).update();
//		}
//	}
	//TODO This should be roughly right?
	public void setTexture(){
		missileship_sprites.bind();
		if(team.equals("blue")){
			if(current_velocity > 4){
				setTextureCoords(0.75, 0, 1, 0.5);
			}
			else if(current_velocity > 2){
				setTextureCoords(0.5, 0, 0.75, 0.5);
			}
			else if(current_velocity > 0){
				setTextureCoords(0.25, 0, 0.5, 0.5);
			}
			else{
				setTextureCoords(0, 0, 0.25, 0.5);
			}
		}
		else{
			if(current_velocity > 4){
				setTextureCoords(0.75, 0.5, 1, 1);
			}
			else if(current_velocity > 2){
				setTextureCoords(0.5, 0.5, 0.75, 1);
			}
			else if(current_velocity > 0){
				setTextureCoords(0.25, 0.5, 0.5, 1);
			}
			else{
				setTextureCoords(0, 0.5, 0.25, 1);
			}
		}
	}
	
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 0, 0, 1, 1, 0, 1, 1};
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2, 2, 1, 3};
	}
	//TODO Idk, this is probably wrong? Rouhgly using the large Missileship size
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			new Point(288, -192, true),
			new Point(-288, -192, true),
			new Point(288, 192, true),
			new Point(-288, 192, true)
		};
		return points;
	}
	//TODO Hitbox is wrong. Using same as size for now
	public Point[] generateHitbox(){
		Point[] hitbox = new Point[]{
			new Point(288, -192, true),
			new Point(-288, -192, true),
			new Point(288, 192, true),
			new Point(-288, 192, true)
			
		};
		return hitbox;
	}
	
	
	
	public void doRandomMovement(){
		super.doRandomMovement();
		//Ignore checks if we are direct targeting; super will take care of that
		if (!directTarget) {
			if(target != null && target.getHealth() <= 0){
				target = null;
			}
			getClosestEnemy();
		}
		//if we have a location and attack move is false and we are not direct targeting anyone
		if(!attackMove && !directTarget){
			moveToLocation();
		}
		//if we have no target or target not close enough
		else if(!directTarget && (target == null || distance(getX(), getY(), target.getX(), target.getY()) > scan_range * 0.9)){
			moveToLocation();
		}
		//Else, we must be attack moving and/or direct targeting.
		else {
			//If we have a target and we are attack moving, stop moving.
			if (attackMove) {
				targeted_velocity = 0;
			}
			//If we are not attack moving but have a direct target, move to engage them.
			if (directTarget) {
				//If we are not close enough, move towards them
				if (distance(getX(), getY(), target.getX(), target.getY()) > scan_range) {
					lockPosition = false;
					locationTarget = new Point(target.getX(), target.getY());
				}
				//Else, stop moving towards them
				else {
					lockPosition = false;
					locationTarget = null;
				}
				moveToLocation();
			}
		}
			
		edgeGuard();
		//Again, ignore checks if not direct targeting
		if (!directTarget) {
			getClosestEnemy();
		}
	}
	
	/* TODO If Wallship has various weapons of different range (probs won't), I will have to add a boolean "detached" to Turret
	 * which enables the shorter-range turrets to fire independent of longer range turrets, which determine the scan range
	 */
	//gets the closest enemy and changes target accordingly
	public void getClosestEnemy(){
		if (target != null && game.distance(center.x, center.y, target.getX(), target.getY()) > scan_range) target = null;
		ArrayList<Starship> scanned = scan();
		if(scanned.size() != 0){
			for (int i = 0; i < scanned.size(); i++) {
				Starship s = scanned.get(i);
				//if fighter has no team, or scanned enemy is on another team or closer than current target
				//Missileship is a special case; it shoots further than it can see, so it is the only ship where
				//we must check whether it can even see target.
				if(game.isVisible(s, team) && (team.equals("none") || !s.getTeam().equals(team)) && (target == null ||
					game.distance(center.X(), center.Y(), s.getX(), s.getY()) - getClosestBearing(s) < 
					game.distance(center.X(), center.Y(), target.getX(), target.getY()) - getClosestBearing(target))){
					target = scanned.get(i);
				}
			}
		}
	}
}
