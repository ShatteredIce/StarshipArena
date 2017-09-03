import java.util.ArrayList;

public class PlanetLaser extends Starship{
	
	static Texture tex1 = new Texture("WIP.png");
	
	static double primary_damage = 5;
	static int primary_cooldown = 250;
	static int primary_spread = 10;
	static int primary_accuracy = 99;
	static int primary_range = 1500;
	static int primary_speed = 20;
	static int primary_lifetime = 1500;
	static int primary_xoffset = 0;
	static int primary_yoffset = 0;
	int primary_id = 0;
	
	public PlanetLaser(StarshipArena mygame, int spawnx, int spawny){
		super(mygame, spawnx, spawny);
	}
	
	public PlanetLaser(StarshipArena mygame, int spawnx, int spawny, double spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public PlanetLaser(StarshipArena mygame, String newteam, int spawnx, int spawny, double spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	
	public void shipStats(){
		max_health = 100;
		scan_range = primary_range;
		radar_range = primary_range;
		//movement
		acceleration = 0;
		max_velocity = 0;
		min_turn_velocity = 0;
		max_turn_speed = 4;
		//other
		clickRadius = 45;
		xOff = 0;
		yOff = 0;
	}
	
	public void shipTurrets(){
		if(team.equals("blue")){
			primary_id = 5;
		}
		else if(team.equals("red")){
			primary_id = 6;
		}
		Turret primaryTurret = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id);
		turrets.add(primaryTurret);
	}
	
	public void doRandomMovement(){
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
	
	public void setTexture(){
		tex1.bind();
	}
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 0, 0, 1, 1, 0, 1, 1};
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2, 2, 1, 3};
	}
	
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			new Point(-32, 32, true),
			new Point(-32, -32, true),
			new Point(32, 32, true),
			new Point(32, -32, true)
		};
		return points;
	}
	
	public Point[] generateHitbox(){
		Point[] hitbox = new Point[]{
			new Point(-32, 32, true),
			new Point(-32, -32, true),
			new Point(32, 32, true),
			new Point(32, -32, true)
		};
		return hitbox;
	}
}