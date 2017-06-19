import java.util.ArrayList;

public class BasicPod extends Starship{
	
	static Texture tex1 = new Texture("red_basicpod.png");
	static Texture tex2 = new Texture("blue_basicpod.png");
	
	static double primary_damage = 2;
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
	
	public BasicPod(StarshipArena mygame, int spawnx, int spawny, double spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public BasicPod(StarshipArena mygame, String newteam, int spawnx, int spawny, double spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	
	public void shipStats(){
		max_health = 40;
		scan_range = primary_range * 3/4;
		radar_range = primary_range;
		//movement
		acceleration = 0.1;
		max_velocity = 0.5;
		min_turn_velocity = 0;
		max_turn_speed = 3;
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
		if(locationTarget != null){
			moveToLocation();
		}
		else{
			current_velocity = 0;
			Starship target = null;
			double closestBearing = 360;
			ArrayList<Starship> scanned = scan();
			if(scanned.size() != 0){
				for (int i = 0; i < scanned.size(); i++) {
					Starship s = scanned.get(i);
					//if turret has no team, or scanned enemy is on another team 
					if(team.equals("none") || !s.getTeam().equals(team)){
						if(getClosestBearing(s) < closestBearing){
							closestBearing = getClosestBearing(s);
							target = s;
						}
					}
				}
			}
			if(target != null){
				double relativeAngle = game.angleToPoint(center.X(), center.Y(), target.getX(), target.getY());
				double leftBearing = getTurnDistance(relativeAngle, true);
				double rightBearing = getTurnDistance(relativeAngle, false);
				if(leftBearing <= rightBearing){ //turn left
					current_turn_speed = max_turn_speed;
				}
				else{ //turn right
					current_turn_speed = -max_turn_speed;
				}
			}
			else{
				current_turn_speed = 0;
			}
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