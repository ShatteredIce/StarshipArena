
public class Fighter extends Starship{
	
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
		int v = random.nextInt(4);
		int t = random.nextInt(3);
		int f = random.nextInt(5);
		boolean turning = false;
		if(v == 0){
			targeted_velocity = max_velocity;
		}
		else if(v == 1){
			targeted_velocity = 0;
		}
		if(t == 0){
			current_turn_speed = max_turn_speed;
			turning = true;
		}
		else if(t == 1){
			current_turn_speed = -max_turn_speed;
			turning = true;
		}
		if(turning == true && targeted_velocity < min_turn_velocity){
			targeted_velocity = min_turn_velocity;
		}
		primary_fire = true;
		
	}

}
