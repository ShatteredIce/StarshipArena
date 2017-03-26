import java.util.ArrayList;

public class Interceptor extends Starship{
	
	Starship target = null;
	int changeDirection = 0;
	int changeDirectionCooldown = 0;
	Texture tex1 = new Texture("interceptor1.png");
	Texture tex2 = new Texture("interceptor2.png");
	Texture tex3 = new Texture("interceptor3.png");
	Texture tex4 = new Texture("interceptor4.png");

	
	public Interceptor(StarshipArena mygame, int spawnx, int spawny){
		super(mygame, spawnx, spawny);
	}
	
	public Interceptor(StarshipArena mygame, int spawnx, int spawny, int spawnangle, int spawnhealth){
		super(mygame, "none", spawnx, spawny, spawnangle, spawnhealth);
	}

	public Interceptor(StarshipArena mygame, String newteam, int spawnx, int spawny, int spawnangle, int spawnhealth){
		super(mygame, newteam, spawnx, spawny, spawnangle, spawnhealth);
	}
	
	public void shipStats(){
		//movement
		acceleration = 0.5;
		max_velocity = 8;
		max_reverse_velocity = -2;
		min_turn_velocity = 3;
		max_turn_speed = 6;
		//weaponry
		primary_cooldown = 50;
		primary_current_cooldown = 0;
		primary_speed = 20;
		primary_lifetime = 450/primary_speed;
		primary_accuracy = 95;
		scan_range = 500;
		//other
		clickRadius = 40;
		xOff = 0;
		yOff = 15;
	}
	
	public void setTexture(){
		if(current_velocity > 7){
			tex4.bind();
		}
		else if(current_velocity > 4){
			tex3.bind();
		}
		else if(current_velocity > 0){
			tex2.bind();
		}
		else{
			tex1.bind();
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
		if (locationTarget != null) {
			double distance = distance(this.getX(), this.getY(), locationTarget.x, locationTarget.y);
			primary_fire = false;
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
		else {
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
				//random movement if fighter has no target
	//			int t = random.nextInt(4);
	//			targeted_velocity = max_velocity / 2;
	//			//turn left
	//			if(t == 0){
	//				current_turn_speed = max_turn_speed;
	//			}
	//			//turn right
	//			else if(t == 1){
	//				current_turn_speed = -max_turn_speed;
	//			}
				current_turn_speed = 0;
				targeted_velocity = 0;
				primary_fire = false;
			}
			else{
				int relativeAngle = game.angleToPoint(center.X(), center.Y(), target.getX(), target.getY());
				double distanceToTarget = game.distance(center.X(), center.Y(), target.getX(), target.getY());
				int leftBearing = getTurnDistance(relativeAngle, true);
				int rightBearing = getTurnDistance(relativeAngle, false);
				primary_fire = false;
				//if interceptor is facing target
				if(angle > (relativeAngle - distanceToTarget / 5) && angle < (relativeAngle + distanceToTarget / 5)){
					targeted_velocity = max_velocity;
					if(distanceToTarget <= 400 && angle > relativeAngle - 5 && angle < relativeAngle + 5){
						primary_fire = true;
					}
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