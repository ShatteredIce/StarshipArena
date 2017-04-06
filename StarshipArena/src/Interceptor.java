import java.util.ArrayList;

public class Interceptor extends Starship{
	
	int changeDirection = 0;
	int changeDirectionCooldown = 0;
	static Texture blue_tex1 = new Texture("blue_interceptor1.png");
	static Texture blue_tex2 = new Texture("blue_interceptor2.png");
	static Texture blue_tex3 = new Texture("blue_interceptor3.png");
	static Texture blue_tex4 = new Texture("blue_interceptor4.png");
	static Texture red_tex1 = new Texture("red_interceptor1.png");
	static Texture red_tex2 = new Texture("red_interceptor2.png");
	static Texture red_tex3 = new Texture("red_interceptor3.png");
	static Texture red_tex4 = new Texture("red_interceptor4.png");
	
	//weaponry
	static double primary_damage = 2;
	static int primary_cooldown = 200;
//	static int primary_spread = 50;
	static int primary_spread = 10;
	static int primary_accuracy = 95;
	static int primary_range = 300;
	static int primary_speed = 20;
	static int primary_lifetime = 450;
	static int primary_xoffset = 0;
	static int primary_yoffset = 10;
	int primary_id = 0;
	
	static double secondary_damage = 0.5;
//	static int secondary_cooldown = 40;
	static int secondary_cooldown = 10;
	static int secondary_spread = 10;
	static int secondary_accuracy = 90;
//	static int secondary_range = 200;
	static int secondary_range = 2000;
//	static int secondary_speed = 15;
	static int secondary_speed = 30; 
//	static int secondary_lifetime = 200;
	static int secondary_lifetime = 4000;
	static int secondary_xoffset = 30;
	static int secondary_yoffset = 0;
	static int secondary_id = 3;

	
	public Interceptor(StarshipArena mygame, int spawnx, int spawny){
		super(mygame, spawnx, spawny);
	}
	
	public Interceptor(StarshipArena mygame, int spawnx, int spawny, int spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public Interceptor(StarshipArena mygame, String newteam, int spawnx, int spawny, int spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	
	public void shipStats(){
		max_health = 30;
		//movement
		acceleration = 0.5;
		max_velocity = 8;
		max_reverse_velocity = -2;
		min_turn_velocity = 3;
		max_turn_speed = 6;
		//weaponry
		scan_range = 500;
		//other
		clickRadius = 40;
		xOff = 0;
		yOff = 15;
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
		Turret secondaryTurret1 = new Turret(game, this, team, 0, 0, angle, secondary_damage, secondary_cooldown, 
				secondary_spread, secondary_accuracy, secondary_range, secondary_speed, secondary_lifetime, secondary_id);
		secondaryTurret1.setOffset(secondary_xoffset, secondary_yoffset);
		turrets.add(secondaryTurret1);
		
		Turret secondaryTurret2 = new Turret(game, this, team, 0, 0, angle, secondary_damage, secondary_cooldown, 
				secondary_spread, secondary_accuracy, secondary_range, secondary_speed, secondary_lifetime, secondary_id);
		secondaryTurret2.setOffset(-1*secondary_xoffset, secondary_yoffset);
		turrets.add(secondaryTurret2);
	}
	
	public void moveTurrets(){
		for (int i = 0; i < turrets.size(); i++) {
			Point p = new Point();
			p.setX(turrets.get(i).getXOffset() + center.X());
			p.setY(turrets.get(i).getYOffset() + center.Y());
			p.rotatePoint(center.X(), center.Y(), angle);
			turrets.get(i).setCenter(p);
			turrets.get(i).setAngle(angle);
			turrets.get(i).update();
		}
	}
	
	public void setTexture(){
		if(team.equals("red")){
			if(current_velocity > 7){
				red_tex4.bind();
			}
			else if(current_velocity > 4){
				red_tex3.bind();
			}
			else if(current_velocity > 0){
				red_tex2.bind();
			}
			else{
				red_tex1.bind();
			}
		}
		else{
			if(current_velocity > 7){
				blue_tex4.bind();
			}
			else if(current_velocity > 4){
				blue_tex3.bind();
			}
			else if(current_velocity > 0){
				blue_tex2.bind();
			}
			else{
				blue_tex1.bind();
			}
		}
	}
	
//	public void setTextureCoords(){
//		textureCoords = new double[]{0.1875, 0.2031, 0.1875, 0.2968, 0.1875, 0.4843, 
//		0.2343, 0.3281, 0.2968, 0.25, 0.2968, 0.3906, 0.3281, 0.25, 0.3281, 0.3125,
//		0.4062, 0.4218, 0.4062, 0.6093, 0.4062, 0.6562, 0.4375, 0.1875, 0.4375, 0.4218,
//		0.4687, 0.0781, 0.4687, 0.1875, 0.5312, 0.0781, 0.5312, 0.1875, 0.5625, 0.1875, 
//		0.5625, 0.4218, 0.5937, 0.4218, 0.5937, 0.6093, 0.5937, 0.6562, 0.6718, 0.25, 
//		0.6718, 0.3125, 0.7031, 0.25, 0.7031, 0.3906, 0.7656, 0.3281, 0.8125, 0.2031, 
//		0.8125, 0.2968, 0.8125, 0.4843, 0.2188, 0.2344, 0.7813, 0.2344};
//	}
//	
//	public void setIndices(){
//		indices = new int[]{1, 2, 8, 2, 8, 9, 11, 12, 17, 12, 17, 18, 11, 13, 14, 
//		13, 14, 15, 14, 15, 16, 15, 16, 17, 8, 9, 19, 9, 19, 20, 9, 10, 20,
//		10, 20, 21, 19, 28, 29, 19, 20, 29, 4, 5, 7, 4, 6, 7, 5, 7, 8,
//		19, 23, 25, 22, 23, 24, 23, 24, 25, 0, 1, 30, 1, 3, 30, 26, 31, 28,
//		31, 27, 28};
//	}
	
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
	
//	public Point[] generateHitbox(){
//		Point[] hitbox = new Point[]{
//			new Point(-40, 38, true),
//			new Point(-40, 26, true),
//			new Point(-40, 2, true),
//			new Point(-34, 22, true),
//			new Point(-26, 32, true),
//			new Point(-26, 14, true),
//			new Point(-22, 32, true),
//			new Point(-22, 24, true),
//			new Point(-12, 10, true),
//			new Point(-12, -14, true),
//			new Point(-12, -20, true),
//			new Point(-8, 40, true),
//			new Point(-8, 10, true),
//			new Point(-4, 52, true),
//			new Point(-4, 40, true),
//			new Point(4, 52, true),
//			new Point(4, 40, true),
//			new Point(8, 40, true),
//			new Point(8, 10, true),
//			new Point(12, 10, true), 
//			new Point(12, -14, true),
//			new Point(12, -20, true),
//			new Point(22, 32, true),
//			new Point(22, 24, true),
//			new Point(26, 32, true),
//			new Point(26, 14, true),
//			new Point(34, 22, true),
//			new Point(40, 38, true),
//			new Point(40, 26, true),
//			new Point(40, 2, true),
//			new Point(-36, 34, true),
//			new Point(36, 34, true)
//			
//		};
//		return hitbox;
//	}
	
	public Point[] generateHitbox(){
		Point[] hitbox = new Point[]{
			new Point(-40, 38, true),
			new Point(-40, 2, true),
			new Point(-12, -14, true),
			new Point(-12, -20, true),
			new Point(12, -20, true),
			new Point(12, -14, true),
			new Point(40, 2, true),
			new Point(40, 38, true),
			new Point(36, 34, true),
			new Point(34, 22, true),
			new Point(26, 14, true),
			new Point(26, 32, true),
			new Point(22, 32, true),
			new Point(22, 24, true),
			new Point(12, 10, true),
			new Point(8, 40, true),
			new Point(4, 40, true),
			new Point(4, 52, true),
			new Point(-4, 52, true),
			new Point(-4, 40, true),
			new Point(-8, 40, true),
			new Point(-12, 10, true),
			new Point(-22, 24, true),
			new Point(-22, 32, true),
			new Point(-26, 32, true),
			new Point(-26, 14, true),
			new Point(-34, 22, true),
			new Point(-36, 34, true),
			
			
		};
		return hitbox;
	}
	
	public void doRandomMovement(){
		changeDirection++;
		//check if the target is already dead
		if(target != null && target.getHealth() <= 0){
			target = null;
		}
		//get a new target if fighter has no target or target is far away
		if(target == null || game.distance(center.X(), center.Y(), target.getX(), target.getY()) >= scan_range / 2){
			getClosestEnemy();
		}
		//getClosestEnemy();
		if(target == null){
			if (locationTarget != null) {
				double distance = distance(this.getX(), this.getY(), locationTarget.x, locationTarget.y);
				if (distance > 50) {
					boolean positiveY = false;
					if (locationTarget.y >=  this.getY()) positiveY = true;
					double angle = Math.acos((locationTarget.x - this.getX()) / distance) * 180 / Math.PI;
					double targetAngle;
					if (positiveY) targetAngle = (270 + angle) % 360;
					else targetAngle = 270 - angle;
					//System.out.println(targetAngle);
					targeted_velocity = max_velocity / 2;
					if (this.angle == Math.round(targetAngle)) {
						current_turn_speed = 0;
						targeted_velocity = max_velocity;
					}
					else if ((Math.round(targetAngle) - this.angle + 360) % 360 < 180) current_turn_speed = (int) Math.min(max_turn_speed, (Math.round(targetAngle) - this.angle + 360) % 360);
					else current_turn_speed = (int) Math.max(-max_turn_speed, -((this.angle - Math.round(targetAngle) + 360) % 360));
				}
				else locationTarget = null;
			}
			else{
				current_turn_speed = 0;
				targeted_velocity = 0;
			}
		}
		else{
			//If ship has no location target and it locks onto an enemy,
			//give it a location target so it returns to where it was before it was attacked (so defensive line is unbroken)
			if (locationTarget == null) locationTarget = new Point(center.x, center.y);
			int relativeAngle = game.angleToPoint(center.X(), center.Y(), target.getX(), target.getY());
			double distanceToTarget = game.distance(center.X(), center.Y(), target.getX(), target.getY());
			int leftBearing = getTurnDistance(relativeAngle, true);
			int rightBearing = getTurnDistance(relativeAngle, false);
			//if interceptor is facing target
			if(angle > (relativeAngle - distanceToTarget / 5) && angle < (relativeAngle + distanceToTarget / 5)){
				targeted_velocity = max_velocity;
				//adjust course
				if(leftBearing <= rightBearing){ //turn left
					current_turn_speed = max_turn_speed;
				}
				else{ //turn right
					current_turn_speed = -max_turn_speed;
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
				//if inteceptor can outrun
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
		edgeGuard();
		if(current_turn_speed != 0 && targeted_velocity < min_turn_velocity){
			targeted_velocity = min_turn_velocity;
		}
		
		
		
		
		//TODO remove this code after testing
//		if (angle != 0) {
//			current_turn_speed = Math.min(max_turn_speed, 360 - angle);
//		}
//		else current_turn_speed = 0;
//		current_turn_speed = max_turn_speed;
	}
	
	//gets the closest enemy and changes target accordingly
	public void getClosestEnemy(){
		if(target != null && game.distance(center.X(), center.Y(), target.getX(), target.getY()) > scan_range){
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