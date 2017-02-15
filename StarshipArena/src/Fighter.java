import java.util.ArrayList;

public class Fighter extends Starship{
	
	Starship target = null;
	int changeDirection = 0;
	int changeDirectionCooldown = 0;
	
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
		acceleration = 1;
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
		scan_range = 300;
	}
	
	public void setTexture(){
		tex = new Texture("fighter_spaceship.png");
	}
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 1, 0.5, 0, 1, 1};
	}
	
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			new Point(-30, -30, true),
			new Point(0, 30, true),
			new Point(30, -30, true),
		};
		return points;
	}
	
	public void doRandomMovement(){
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
			//random movement if fighter has no target
			int t = random.nextInt(4);
			targeted_velocity = max_velocity / 2;
			//turn left
			if(t == 0){
				current_turn_speed = max_turn_speed;
			}
			//turn right
			else if(t == 1){
				current_turn_speed = -max_turn_speed;
			}
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
		
	}
	
	//gets the closest enemy and changes target accordingly
	public void getClosestEnemy(){
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