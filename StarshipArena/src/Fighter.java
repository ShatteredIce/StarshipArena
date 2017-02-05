import java.util.ArrayList;

public class Fighter extends Starship{
	
	Starship target = null;
	
	public Fighter(StarshipArena mygame, int spawnx, int spawny){
		super(mygame, spawnx, spawny);
	}

	public Fighter(StarshipArena mygame, int spawnx, int spawny, int spawnangle, int spawnhealth) {
		super(mygame, spawnx, spawny, spawnangle, spawnhealth);
	}
	
	public void shipStats(){
		//movement
		acceleration = 1;
		max_velocity = 5;
		max_reverse_velocity = -2;
		min_turn_velocity = 2;
		max_turn_speed = 5;
		//weaponry
		primary_cooldown = 50;
		primary_current_cooldown = 0;
		primary_speed = 5.5;
		primary_lifetime = 450/primary_speed;
		primary_accuracy = 95;
	}
	
	public void setTexture(){
		tex = new Texture("fighter_spaceship.png");
	}
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 1, 0.5, 0, 1, 1};
	}
	
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			new Point(-40, -40, true),
			new Point(0, 40, true),
			new Point(40, -40, true),
		};
		return points;
	}
	
	public void doRandomMovement(){
		//check if the target is already dead
		if(target != null && target.getHealth() <= 0){
			target = null;
		}
		//get a new target
		if(target == null){
			ArrayList<Starship> shipsList = game.getAllShips();
			for(int i = 0; i < shipsList.size(); i++){
				if(!shipsList.get(i).equals(this)){
					target = shipsList.get(i);
					break;
				}
			}
			int v = random.nextInt(4);
			int t = random.nextInt(3);
			int f = random.nextInt(5);
			if(v == 0){
				targeted_velocity = max_velocity / 2;
			}
			else if(v == 1){
				targeted_velocity = 0;
			}
			if(t == 0){
				current_turn_speed = max_turn_speed;
			}
			else if(t == 1){
				current_turn_speed = -max_turn_speed;
			}
			primary_fire = false;
		}
		else{
			int relativeAngle = game.angleToPoint(center.X(), center.Y(), target.getX(), target.getY());
			if(game.distance(center.X(), center.Y(), target.getX(), target.getY()) > 100){
				targeted_velocity = max_velocity;
				int leftBearing = 0;
				int rightBearing = 0;
				//don't turn if angle is already pointed toward player
				if(angle > relativeAngle - 5 && angle < relativeAngle + 5){
					current_turn_speed = 0;
				}
				else{
					//Find which direction to turn is shortest
					if(angle >= relativeAngle){
						rightBearing = angle - relativeAngle;
						leftBearing = 360 - angle + relativeAngle;
					}
					else if(angle < relativeAngle){
						leftBearing = relativeAngle - angle;
						rightBearing = angle + 360 - relativeAngle;
					}
					//turn appropriate way 
					if(leftBearing <= rightBearing){
						current_turn_speed = max_turn_speed;
					}
					else{
						current_turn_speed = -max_turn_speed;
					}
				}
			}
			relativeAngle = game.angleToPoint(center.X(), center.Y(), target.getX(), target.getY());
			if(angle > relativeAngle - 5 && angle < relativeAngle + 5){
				//if pointed toward player and gun cooldown is zero, fire
				primary_fire = true;
			}
			else{
				primary_fire = false;
			}
		}
		if(current_turn_speed != 0 && targeted_velocity < min_turn_velocity){
			targeted_velocity = min_turn_velocity;
		}
		
	}

}