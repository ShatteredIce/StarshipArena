import java.util.ArrayList;

//TODO Most of these functions need to be modified; a TODO is placed next to each, sometimes with brief description
public class Battleship extends Starship{
	int changeDirection = 0;
	int changeDirectionCooldown = 0;
	//TODO Wrong sprites
	static Texture missileship_sprites = new Texture("missileship_sprites.png");
	
	//Dual missile launchers
	static double damage1 = 2.5;
	static int cooldown1 = 300;
	static int spread1 = 360;
	static int accuracy1 = 95;
	static int range1 = 4000;
	static int speed1 = 15;
	static int lifetime1 = 4300;
	static int xoffset1 = 50;
	static int yoffset1 = -50;
	static int id1 = 4;
	
	//Dual pulse lasers
	static double damage2 = 1;
	static int cooldown2 = 500;
	static int spread2 = 360;
	static int accuracy2 = 100;
	static int range2 = 1400;
	static int speed2 = 100;
	static int lifetime2 = 1400;
	static int xoffset2 = 0;
	static int yoffset2 = 75;
	static int id2 = 5;
	static int pulseSize2 = 70;
	
	//"Plasma hose" - Triple plasma
	static double damage3 = 4;
	static int cooldown3 = 15;
	static int spread3 = 5;
	static int accuracy3 = 100;
	static int range3 = 1000;
	static int speed3 = 20;
	static int lifetime3 = 900;
	static int xoffset3 = 10;
	static int yoffset3 = 100;
	static int id3 = 0;
	
	public Battleship(StarshipArena mygame, double spawnx, double spawny){
		super(mygame, spawnx, spawny);
	}
	
	public Battleship(StarshipArena mygame, double spawnx, double spawny, double spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public Battleship(StarshipArena mygame, String newteam, double spawnx, double spawny, double spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	//TODO Change stats
	public void shipStats(){
		max_health = 200;
		//movement
		acceleration = 0.2;
		max_velocity = 6;
		max_reverse_velocity = -2;
		min_turn_velocity = 1;
		max_turn_speed = 2;
		//weaponry
		scan_range = 4000;
		radar_range = 2000;
		//other
		clickRadius = 110;
		xOff = 0;
		yOff = 0;
		
		haloSize = 160;
		weight = 3;
	}
	//TODO Different turrets
	public void shipTurrets(){
		if(team.equals("blue")){
			id3 = 1;
		}
		else if(team.equals("red")){
			id2 = 6;
			id3 = 2;
		}
		Turret turret1_1 = new Turret(game, this, team, 0, 0, angle, damage1, cooldown1, 
				spread1, accuracy1, range1, speed1, lifetime1, id1, 0, 9);
//		primaryTurret1.setOffset(primary_xoffset + 25, primary_yoffset + 20, -15);
		turret1_1.setOffset(xoffset1, yoffset1);
		turrets.add(turret1_1);
		
		Turret turret1_2 = new Turret(game, this, team, 0, 0, angle, damage1, cooldown1, 
				spread1, accuracy1, range1, speed1, lifetime1, id1, 0, 9);
//		primaryTurret2.setOffset(primary_xoffset - 25, primary_yoffset + 20, -5);
		turret1_2.setOffset(-xoffset1, yoffset1);
		turrets.add(turret1_2);
		
		
		
		Turret turret2_1 = new Turret(game, this, team, 0, 0, angle, damage2, cooldown2, 
				spread2, accuracy2, range2, speed2, lifetime2, id2, 0, 7);
//		primaryTurret1.setOffset(primary_xoffset + 25, primary_yoffset + 20, -15);
		turret2_1.setOffset(xoffset2, yoffset2);
		turret2_1.setPulseSize(pulseSize2);
		turrets.add(turret2_1);
		
		Turret turret2_2 = new Turret(game, this, team, 0, 0, angle, damage2, cooldown2, 
				spread2, accuracy2, range2, speed2, lifetime2, id2, 0, 7);
//		primaryTurret2.setOffset(primary_xoffset - 25, primary_yoffset + 20, -5);
		turret2_2.setOffset(xoffset2, -yoffset2);
		turret2_2.setPulseSize(pulseSize2);
		turrets.add(turret2_2);
		
		
		
		Turret turret3_1 = new Turret(game, this, team, 0, 0, angle, damage3, cooldown3, 
				spread3, accuracy3, range3, speed3, lifetime3, id3, 0, 0);
//		primaryTurret2.setOffset(primary_xoffset - 25, primary_yoffset + 20, -5);
		turret3_1.setOffset(-xoffset3, yoffset3);
		turrets.add(turret3_1);
		
		Turret turret3_2 = new Turret(game, this, team, 0, 0, angle, damage3, cooldown3, 
				spread3, accuracy3, range3, speed3, lifetime3, id3, 0, 0);
//		primaryTurret1.setOffset(primary_xoffset + 25, primary_yoffset + 20, -15);
		turret3_2.setOffset(0, yoffset3 + 20);
		turrets.add(turret3_2);
		
		Turret turret3_3 = new Turret(game, this, team, 0, 0, angle, damage3, cooldown3, 
				spread3, accuracy3, range3, speed3, lifetime3, id3, 0, 0);
//		primaryTurret2.setOffset(primary_xoffset - 25, primary_yoffset + 20, -5);
		turret3_3.setOffset(xoffset3, yoffset3);
		turrets.add(turret3_3);
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
		if(locationTarget != null && !attackMove && !directTarget){
			moveToLocation();
		}
		//if we have no target or target not close enough
		else if(locationTarget != null && (target == null || distance(getX(), getY(), target.getX(), target.getY()) > range3 * 0.9)){
			moveToLocation();
		}
		//Else, we must be attack moving and/or direct targeting.
		else {
			//If we have a target and we are attack moving, stop moving.
			if (attackMove) {
				locationTarget = new Point(target.getX(), target.getY());
				lockPosition = true;
				moveToLocation();
			}
			//If we are not attack moving but have a direct target, move to engage them.
			if (directTarget) {
				locationTarget = new Point(target.getX(), target.getY());
				//If we are not close enough, move towards them
				if (distance(getX(), getY(), target.getX(), target.getY()) > range3) {
					lockPosition = false;
				}
				//Else, turn to face target
				else {
					lockPosition = true;
				}
				moveToLocation();
//				System.out.println("Missileship distance to: " + (distance(getX(), getY(), target.getX(), target.getY()) - scan_range));
			}
			//Final case: Battleship acquires a close range target while idle. Turn to face and destroy
			else if (target != null && distance(getX(), getY(), target.getX(), target.getY()) < range3 * 0.9){
				locationTarget = new Point(target.getX(), target.getY());
				lockPosition = true;
				moveToLocation();
			}
			//Prevent breaking?
			else {
				locationTarget = null;
				moveToLocation();
			}
		}
			
		edgeGuard();
		//Again, ignore checks if not direct targeting
		if (!directTarget) {
			getClosestEnemy();
		}
	}
	
	//gets the closest enemy and changes target accordingly
	public void getClosestEnemy(){
		if (target != null && game.distance(center.x, center.y, target.getX(), target.getY()) > scan_range) target = null;
		ArrayList<Starship> scanned = scan();
		if(scanned.size() != 0){
			for (int i = 0; i < scanned.size(); i++) {
				Starship s = scanned.get(i);
				//To prevent Battleship from turning like crazy to try to focus on a target, stop switching targets
				//once a target within plasma range is acquired.
				if (target != null && distance(getX(), getY(), target.getX(), target.getY()) < range3) break;
				if(game.isVisible(s, team) && (team.equals("none") || !s.getTeam().equals(team)) && (target == null ||
					game.distance(center.X(), center.Y(), s.getX(), s.getY()) - getClosestBearing(s) < 
					game.distance(center.X(), center.Y(), target.getX(), target.getY()) - getClosestBearing(target))){
					target = scanned.get(i);
				}
			}
		}
	}
}
