import java.util.ArrayList;

//TODO Couldn't I base sniper off of Turret? It's supposed to turn and target enemies like a turret.
public class Sniper extends Starship{
	int changeDirection = 0;
	int changeDirectionCooldown = 0;
	//TODO Wrong sprites
	static Texture missileship_sprites = new Texture("missileship_sprites.png");
	
	static double primary_damage = 180;
//	static double primary_damage = 4;
	static int primary_cooldown = 900;
//	static int primary_cooldown = 10;
	static int primary_spread = 360;
	static int primary_accuracy = 40;
	static int primary_range = 8000;
	static int primary_speed = 200;
	static int primary_lifetime = 8000;
	static int primary_xoffset = 0;
	static int primary_yoffset = -50;
	static int primary_id = 7;
	
	public Sniper(StarshipArena mygame, double spawnx, double spawny){
		super(mygame, spawnx, spawny);
	}
	
	public Sniper(StarshipArena mygame, double spawnx, double spawny, double spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public Sniper(StarshipArena mygame, String newteam, double spawnx, double spawny, double spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	//TODO Change stats
	public void shipStats(){
		max_health = 15;
		//movement
		acceleration = 0.1;
		max_velocity = 4.5;
//		max_velocity = 8;
		max_reverse_velocity = -2;
		min_turn_velocity = 1;
		max_turn_speed = 1.5;
		//weaponry
		scan_range = 8000;
		radar_range = 1500;
		//other
		clickRadius = 110;
		xOff = 0;
		yOff = 0;
		
		haloSize = 160;
		weight = 1;
	}
	//TODO Different turrets
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
		primaryTurret1.setOffset(primary_xoffset, primary_yoffset);
		turrets.add(primaryTurret1);
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
	//TODO Idk, this is probably wrong?
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			new Point(-192, 288, true),
			new Point(-192, -288, true),
			new Point(192, 288, true),
			new Point(192, -288, true)
		};
		return points;
	}
	//TODO Hitbox is wrong
	public Point[] generateHitbox(){
		Point[] hitbox = new Point[]{
			new Point(-9, 120, true),
			new Point(-18, 114, true),
			new Point(-24, 90, true),
			new Point(-30, 66, true),
			
			new Point(-30, 45, true),
			new Point(-36, 39, true),
			new Point(-39, 12, true),
			new Point(-39, -57, true),
			
			new Point(-39, -81, true),
			new Point(-18, -90, true),
			new Point(18, -90, true),
			new Point(36, -81, true),
			
			new Point(39, -57, true),
			new Point(39, 12, true),
			new Point(36, 39, true),
			new Point(30, 45, true),
			
			new Point(30, 66, true),
			new Point(24, 90, true),
			new Point(18, 114, true),
			new Point(6, 120, true),
			
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
//				System.out.println("Missileship distance to: " + (distance(getX(), getY(), target.getX(), target.getY()) - scan_range));
			}
		}
			
//		moveTurrets();
		edgeGuard();
		//Again, ignore checks if not direct targeting
		if (!directTarget) {
			getClosestEnemy();
		}
	
		
		
//		super.doRandomMovement();
//		if(target != null && target.getHealth() <= 0){
//			target = null;
//		}
//		getClosestEnemy();
//		moveToLocation();
//			
////		moveTurrets();
//		edgeGuard();
//		getClosestEnemy();
	}
	
	/* TODO If Sniper has various weapons of different range (probs won't), I will have to add a boolean "detached" to Turret
	 * which enables the shorter-range turrets to fire independent of longer range turrets, which determine the scan range
	 * Also, it's unknown whether Sniper's AI and movement should resemble small class (Fighter) or medium class (Battleship)
	 * or perhaps a hybrid of the two AI's!
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
