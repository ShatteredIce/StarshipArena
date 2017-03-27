import java.util.ArrayList;

public class Fighter extends Starship{
	
	Starship target = null;
	int changeDirection = 0;
	int changeDirectionCooldown = 0;
	Texture blue_tex1 = new Texture("blue_fighter1.png");
	Texture blue_tex2 = new Texture("blue_fighter2.png");
	Texture blue_tex3 = new Texture("blue_fighter3.png");
	Texture blue_tex4 = new Texture("blue_fighter4.png");
	Texture red_tex1 = new Texture("red_fighter1.png");
	Texture red_tex2 = new Texture("red_fighter2.png");
	Texture red_tex3 = new Texture("red_fighter3.png");
	Texture red_tex4 = new Texture("red_fighter4.png");
	
	public Fighter(StarshipArena mygame, int spawnx, int spawny){
		super(mygame, spawnx, spawny);
	}
	
	public Fighter(StarshipArena mygame, int spawnx, int spawny, int spawnangle, int spawnhealth){
		super(mygame, "none", spawnx, spawny, spawnangle, spawnhealth);
	}

	public Fighter(StarshipArena mygame, String newteam, int spawnx, int spawny, int spawnangle, int spawnhealth){
		super(mygame, newteam, spawnx, spawny, spawnangle, spawnhealth);
	}
	
	public void shipStats(){
		//movement
		acceleration = 0.5;
		max_velocity = 5;
		max_reverse_velocity = -2;
		min_turn_velocity = 3;
		max_turn_speed = 3;
		//weaponry
		primary_cooldown = 50;
		primary_current_cooldown = 0;
		primary_speed = 15;
		primary_lifetime = 450/primary_speed;
		primary_accuracy = 95;
		scan_range = 400;
		//other
		clickRadius = 45;
		xOff = 0;
		yOff = 10;
	}
	
	public void setTexture(){
		if(team.equals("red")){
			if(current_velocity > 4){
				red_tex4.bind();
			}
			else if(current_velocity > 2){
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
			if(current_velocity > 4){
				blue_tex4.bind();
			}
			else if(current_velocity > 2){
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
//		textureCoords = new double[]{0.1094, 0.5313, 0.1094, 0.6406, 0.375, 0.2188,
//		0.375, 0.3906, 0.375, 0.5313, 0.375, 0.6406, 0.4063, 0.2188, 0.4063, 0.6406,
//		0.4063, 0.7188, 0.4531, 0.125, 0.4531, 0.2188, 0.5469, 0.125, 0.5469, 0.2188,
//		0.5938, 0.2188, 0.5938, 0.6406, 0.5938, 0.7188, 0.625, 0.2188, 0.625, 0.3906, 
//		0.625, 0.5313, 0.625, 0.6406, 0.8906, 0.5313, 0.8906, 0.6406};
//	}
//	
//	public void setIndices(){
//		indices = new int[]{0, 1, 4, 1, 4, 5, 0, 3, 4, 2, 5, 19, 2, 16, 19, 
//				6, 9, 10, 9, 10, 12, 9, 11, 12, 11, 12, 13, 5, 7, 8, 7, 8, 15,
//				7, 14, 15, 14, 15, 19, 17, 18, 20, 18, 19, 21, 18, 20, 21};
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
//			new Point(-50, -4, true),
//			new Point(-50, -18, true),
//			new Point(-16, 36, true),
//			new Point(-16, 14, true),
//			new Point(-16, -18, true),
//			new Point(-12, 36, true),
//			new Point(-12, -18, true),
//			new Point(-12, -28, true),
//			new Point(-6, 48, true),
//			new Point(-6, 36, true),
//			new Point(6, 48, true),
//			new Point(6, 36, true),
//			new Point(12, 36, true),
//			new Point(12, -18, true),
//			new Point(12, -28, true),
//			new Point(16, 36, true),
//			new Point(16, 14, true),
//			new Point(16, -18, true),
//			new Point(50, -4, true),
//			new Point(50, -18, true)
//		};
//		return hitbox;
//	}
	
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
			if(target == null || game.distance(center.X(), center.Y(), target.getX(), target.getY()) >= scan_range / 3){
				getClosestEnemy();
			}
			//getClosestEnemy();
			if(target == null){
	//			//random movement if fighter has no target
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
				targeted_velocity = max_velocity;
				int relativeAngle = game.angleToPoint(center.X(), center.Y(), target.getX(), target.getY());
				if(game.distance(center.X(), center.Y(), target.getX(), target.getY()) > 100){
					//don't turn if angle is already pointed toward target
					if(angle > relativeAngle - 5 && angle < relativeAngle + 5){
						current_turn_speed = 0;
					}
					else{
						int leftBearing = getTurnDistance(relativeAngle, true);
						int rightBearing = getTurnDistance(relativeAngle, false);
						//System.out.println(team + " " +relativeAngle + " " + leftBearing + " " + rightBearing);
						if(changeDirection > primary_cooldown * 4){
							if(changeDirectionCooldown == 0){
								current_turn_speed *= -1;
								changeDirectionCooldown = 100;
							}
							else{
								changeDirectionCooldown--;
							}
						}
						else if(leftBearing <= rightBearing){
							current_turn_speed = max_turn_speed;
						}
						else{
							current_turn_speed = -max_turn_speed;
						}
					}
				}
				if(angle > relativeAngle - 5 && angle < relativeAngle + 5){
					//if pointed toward target, fire
					changeDirection = 0;
					changeDirectionCooldown = 0;
					primary_fire = true;
				}
				else{
					primary_fire = false;
				}
			}
			edgeGuard();
			if(current_turn_speed != 0 && targeted_velocity < min_turn_velocity){
				targeted_velocity = min_turn_velocity;
			}
			
			
			
			//TODO remove this code after testing
//			if (angle != 0) {
//				current_turn_speed = Math.min(max_turn_speed, 360 - angle);
//			}
//			else current_turn_speed = 0;
			//current_turn_speed = max_turn_speed;
		}
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