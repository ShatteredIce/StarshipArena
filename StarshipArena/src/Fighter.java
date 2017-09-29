import java.util.ArrayList;

public class Fighter extends Starship{
	
	int changeDirection = 0;
	int changeDirectionCooldown = 0;
	static Texture fighter_sprites = new Texture("fighter_sprites.png");
	
	//weaponry
	static double primary_damage = 4;
	static int primary_cooldown = 90;
	static int primary_spread = 5;
	static int primary_accuracy = 95;
	static int primary_range = 700;
	static int primary_speed = 20;
	static int primary_lifetime = 800;
	static int primary_xoffset = 0;
	static int primary_yoffset = 10;
	int primary_id = 0;
	
	public Fighter(StarshipArena mygame, double spawnx, double spawny){
		super(mygame, spawnx, spawny);
	}
	
	public Fighter(StarshipArena mygame, double spawnx, double spawny, double spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public Fighter(StarshipArena mygame, String newteam, double spawnx, double spawny, double spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	
	public void shipStats(){
		max_health = 20;
		//movement
		acceleration = 0.5;
		max_velocity = 7;
		max_reverse_velocity = -2;
		min_turn_velocity = 2;
		max_turn_speed = 4;
		scan_range = 800;
		radar_range = 900;
		//other
		clickRadius = 45;
		xOff = 0;
		yOff = 10;
	}
	
	public void shipTurrets(){
		if(team.equals("blue")){
			primary_id = 1;
		}
		else if(team.equals("red")){
			primary_id = 2;
		}
		Turret primaryTurret = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id);
		primaryTurret.setOffset(primary_xoffset, primary_yoffset);
		turrets.add(primaryTurret);
	}
	
	
//	public void setTexture(){
//		fighter_sprites.bind();
//		if(team.equals("blue")){
//			if(current_velocity > 4){
//				setTextureCoords(0.75, 0, 0.75, 0.5, 1, 0, 1, 0.5);
//			}
//			else if(current_velocity > 2){
//				setTextureCoords(0.5, 0, 0.5, 0.5, 0.75, 0, 0.75, 0.5);
//			}
//			else if(current_velocity > 0){
//				setTextureCoords(0.25, 0, 0.25, 0.5, 0.5, 0, 0.5, 0.5);
//			}
//			else{
//				setTextureCoords(0, 0, 0, 0.5, 0.25, 0, 0.25, 0.5);
//			}
//		}
//		else{
//			if(current_velocity > 4){
//				setTextureCoords(0.75, 0.5, 0.75, 1, 1, 0.5, 1, 1);
//			}
//			else if(current_velocity > 2){
//				setTextureCoords(0.5, 0.5, 0.5, 1, 0.75, 0.5, 0.75, 1);
//			}
//			else if(current_velocity > 0){
//				setTextureCoords(0.25, 0.5, 0.25, 1, 0.5, 0.5, 0.5, 1);
//			}
//			else{
//				setTextureCoords(0, 0.5, 0, 1, 0.25, 0.5, 0.25, 1);
//			}
//		}
//		model.setTextureCoords(textureCoords);
//	}
	
	public void setTexture(){
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
	
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			new Point(-64, 64, true),
			new Point(-64, -64, true),
			new Point(64, 64, true),
			new Point(64, -64, true)
		};
		return points;
	}
	
	public Point[] generateHitbox(){
		Point[] hitbox = new Point[]{
			new Point(-50, -4, true),
			new Point(-50, -18, true),
			new Point(-16, -18, true),
			new Point(-12, -28, true),
			new Point(12, -28, true),
			new Point(16, -18, true),
			new Point(50, -18, true),
			new Point(50, -4, true),
			new Point(16, 14, true),
			new Point(16, 36, true),
			new Point(6, 36, true),
			new Point(6, 48, true),
			new Point(-6, 48, true),
			new Point(-6, 36, true),
			new Point(-16, 36, true),
			new Point(-16, 14, true),
		};
		return hitbox;
	}
	
	public void doRandomMovement(){
		super.doRandomMovement();
		changeDirection++;
		//Ignore checks if the target is directTarget
		if (!directTarget) {
			//check if the target is already dead
			if(target != null && target.getHealth() <= 0){
				target = null;
			}
			//get a new target if fighter has no target or target is far away
			if(target == null || game.distance(center.X(), center.Y(), target.getX(), target.getY()) >= scan_range / 2){
				getClosestEnemy();
			}
		}
		//if we have a location and attack move is false
		if(locationTarget != null && attackMove == false){
			moveToLocation();
		}
		//if we have no target
		else if(target == null){
			moveToLocation();
		}
		else{
			//If ship has no location target and it locks onto an enemy,
			//give it a location target so it returns to where it was before it was attacked (so defensive line is unbroken)
			//Actually, forget this
//			if (locationTarget == null) locationTarget = new Point(center.x, center.y);
			double relativeAngle = game.angleToPoint(center.X(), center.Y(), target.getX(), target.getY());
			double distanceToTarget = game.distance(center.X(), center.Y(), target.getX(), target.getY());
			double leftBearing = getTurnDistance(relativeAngle, true);
			double rightBearing = getTurnDistance(relativeAngle, false);
			//if interceptor is facing target
			if(angle > (relativeAngle - distanceToTarget / 5) && angle < (relativeAngle + distanceToTarget / 5)){
				targeted_velocity = max_velocity;
				//adjust course
				if(leftBearing <= rightBearing){ //turn left
					current_turn_speed = Math.min((relativeAngle - angle + 3600) % 360, max_turn_speed);
				}
				else{ //turn right
					current_turn_speed = Math.max(-((angle - relativeAngle + 3600) % 360), -max_turn_speed);
				}
			}
			//turn away from target if too close
			else if(distanceToTarget <= 50){
				//System.out.println("point a");
				targeted_velocity = max_velocity;
				if(angle > target.getAngle() - 80 && angle < target.getAngle() + 80){
					if(leftBearing <= rightBearing){ //turn right
						current_turn_speed = -max_turn_speed;
					}
					else{ //turn left
						current_turn_speed = max_turn_speed;
					}
				}
			}
			//if target is behind
			else if(distanceToTarget < 150 && getClosestBearing(target) > 140 &&
					Math.abs(relativeAngle - target.getAngle()) > 160 && Math.abs(relativeAngle - target.getAngle()) < 200){
				//System.out.println("point b");
				targeted_velocity = max_velocity;
				//if fighter can outrun
				if(max_velocity > target.getMVelocity()){
					current_turn_speed = 0;
				}
				else{
					if(angle > target.getAngle() - 90 && angle < target.getAngle() + 90){
						if(leftBearing <= rightBearing){ //turn right
							current_turn_speed = -max_turn_speed;
						}
						else{ //turn left
							current_turn_speed = max_turn_speed;
						}
					}
				}
			}
			//if target is far away and not pointed at target, point at target
			else if(!(angle > relativeAngle - 5 && angle < relativeAngle + 5)){
				//System.out.println("point c");
				if(distanceToTarget < 100){
					targeted_velocity = max_velocity;
					current_turn_speed = 0;
				}
				else{
					targeted_velocity = max_velocity / 2;
					//targeted_velocity = Math.min(max_velocity, Math.max(max_velocity / 2, distanceToTarget * Math.PI / (2 * 270 / max_turn_speed)));
					if(leftBearing <= rightBearing){ //turn left
						current_turn_speed = max_turn_speed;
					}
					else{ //turn right
						current_turn_speed = -max_turn_speed;
					}
				}
			}

		//System.out.println(team + " " +relativeAngle + " " + leftBearing + " " + rightBearing);
		}
//		moveTurrets();
		edgeGuard();
		if(current_turn_speed != 0 && targeted_velocity < min_turn_velocity){
			targeted_velocity = min_turn_velocity;
		}
	}
	
	
	//gets the closest enemy and changes target accordingly
	public void getClosestEnemy(){
		if(target != null && game.distance(center.X(), center.Y(), target.getX(), target.getY()) > scan_range * 2){
			target = null;
		}
		ArrayList<Starship> scanned = scan();
		if(scanned.size() != 0){
			for (int i = 0; i < scanned.size(); i++) {
				Starship s = scanned.get(i);
				//if fighter has no team, or scanned enemy is on another team or closer than current target
				if((team.equals("none") || !s.getTeam().equals(team)) && (target == null ||
					game.distance(center.X(), center.Y(), s.getX(), s.getY()) - getClosestBearing(s) < 
					game.distance(center.X(), center.Y(), target.getX(), target.getY()) - getClosestBearing(target))){
					 target = scanned.get(i);
				}
			}
		}
	}
	
}