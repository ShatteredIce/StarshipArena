
public class BasicPod extends Starship{
	
	static Texture tex1 = new Texture("red_basicpod.png");
	static Texture tex2 = new Texture("blue_basicpod.png");
	
	static double primary_damage = 5;
	static int primary_cooldown = 100;
	static int primary_spread = 10;
	static int primary_accuracy = 99;
	static int primary_range = 1500;
	static int primary_speed = 15;
	static int primary_lifetime = 1500;
	static int primary_xoffset = 0;
	static int primary_yoffset = 20;
	int primary_id = 0;
	
	
	public BasicPod(StarshipArena mygame, int spawnx, int spawny){
		super(mygame, spawnx, spawny);
	}
	
	public BasicPod(StarshipArena mygame, int spawnx, int spawny, int spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public BasicPod(StarshipArena mygame, String newteam, int spawnx, int spawny, int spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	
	public void shipStats(){
		max_health = 40;
		//movement
		acceleration = 0.1;
		max_velocity = 0.5;
		min_turn_velocity = 0;
		max_turn_speed = 0.5;
		//other
		clickRadius = 45;
		xOff = 0;
		yOff = -10;
	}
	
	public void shipTurrets(){
		if(team.equals("blue")){
			primary_id = 1;
		}
		else if(team.equals("red")){
			primary_id = 2;
		}
		Turret primaryTurret = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id, 1, 0);
		primaryTurret.setOffset(primary_xoffset, primary_yoffset);
		turrets.add(primaryTurret);
	}
	
	public void doRandomMovement(){
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
				else if ((Math.round(targetAngle) - this.angle + 360) % 360 < 180) current_turn_speed = Math.min(max_turn_speed, (Math.round(targetAngle) - this.angle + 360) % 360);
				else current_turn_speed = Math.max(-max_turn_speed, -((this.angle - Math.round(targetAngle) + 360) % 360));
			}
			else locationTarget = null;
		}
		else{
			current_turn_speed = 0;
			targeted_velocity = 0;
		}
	}
	
	public void setTexture(){
		if(team.equals("red")){
			tex1.bind();
		}
		else{
			tex2.bind();
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
			new Point(-52, 72, true),
			new Point(-52, -72, true),
			new Point(52, 72, true),
			new Point(52, -72, true)
		};
		return points;
	}
	
	public Point[] generateHitbox(){
		Point[] hitbox = new Point[]{
			new Point(-52, 72, true),
			new Point(-52, -72, true),
			new Point(52, 72, true),
			new Point(52, -72, true)
		};
		return hitbox;
	}
	
	
}