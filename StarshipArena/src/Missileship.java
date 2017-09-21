import java.util.ArrayList;

public class Missileship extends Starship{
	//I have doubled all Missileship dimensions to create a larger ship
	int changeDirection = 0;
	int changeDirectionCooldown = 0;
	static Texture missileship_sprites = new Texture("missileship_sprites.png");
	
	static double primary_damage = 5;
	static int primary_cooldown = 560;
	static int primary_spread = 360;
	static int primary_accuracy = 95;
	static int primary_range = 4000;
	static int primary_speed = 15;
	static int primary_lifetime = 4300;
	static int primary_xoffset = 0;
	static int primary_yoffset = -100;
	static int primary_id = 4;
	
	public Missileship(StarshipArena mygame, int spawnx, int spawny){
		super(mygame, spawnx, spawny);
	}
	
	public Missileship(StarshipArena mygame, int spawnx, int spawny, int spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public Missileship(StarshipArena mygame, String newteam, int spawnx, int spawny, int spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	
	public void shipStats(){
		max_health = 50;
		//movement
		acceleration = 0.1;
		max_velocity = 5;
		max_reverse_velocity = -2;
		min_turn_velocity = 1;
		max_turn_speed = 1;
		//weaponry
		scan_range = 4000;
		radar_range = 2500;
		//other
		clickRadius = 110;
		xOff = 0;
		yOff = 0;
		
		//TODO Missileship needs a bigger halo and bigger weight. The new classes might need this too
		haloSize = 160;
		weight = 2;
	}
	
	public void shipTurrets(){
//		if(team.equals("blue")){
//			primary_id = 1;
//		}
//		else if(team.equals("red")){
//			primary_id = 2;
//		}
		Turret primaryTurret1 = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id, 0, 1);
//		primaryTurret1.setOffset(primary_xoffset + 25, primary_yoffset + 20, -15);
		primaryTurret1.setOffset(primary_xoffset, primary_yoffset, -20);
		turrets.add(primaryTurret1);
		
		Turret primaryTurret2 = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id, 0, 1);
//		primaryTurret2.setOffset(primary_xoffset - 25, primary_yoffset + 20, -5);
		primaryTurret2.setOffset(primary_xoffset, primary_yoffset, -14);
		turrets.add(primaryTurret2);
		
		Turret primaryTurret3 = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id, 0, 1);
//		primaryTurret3.setOffset(primary_xoffset + 25, primary_yoffset - 20, 5);
		primaryTurret3.setOffset(primary_xoffset, primary_yoffset, 14);
		turrets.add(primaryTurret3);
		
		Turret primaryTurret4 = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id, 0, 1);
//		primaryTurret4.setOffset(primary_xoffset - 25, primary_yoffset - 20, 15);
		primaryTurret4.setOffset(primary_xoffset, primary_yoffset, 20);
		turrets.add(primaryTurret4);
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
		missileship_sprites.bind();
		if(team.equals("blue")){
			if(current_velocity > 4){
				setTextureCoords(0.75, 0, 0.75, 0.5, 1, 0, 1, 0.5);
			}
			else if(current_velocity > 2){
				setTextureCoords(0.5, 0, 0.5, 0.5, 0.75, 0, 0.75, 0.5);
			}
			else if(current_velocity > 0){
				setTextureCoords(0.25, 0, 0.25, 0.5, 0.5, 0, 0.5, 0.5);
			}
			else{
				setTextureCoords(0, 0, 0, 0.5, 0.25, 0, 0.25, 0.5);
			}
		}
		else{
			if(current_velocity > 4){
				setTextureCoords(0.75, 0.5, 0.75, 1, 1, 0.5, 1, 1);
			}
			else if(current_velocity > 2){
				setTextureCoords(0.5, 0.5, 0.5, 1, 0.75, 0.5, 0.75, 1);
			}
			else if(current_velocity > 0){
				setTextureCoords(0.25, 0.5, 0.25, 1, 0.5, 0.5, 0.5, 1);
			}
			else{
				setTextureCoords(0, 0.5, 0, 1, 0.25, 0.5, 0.25, 1);
			}
		}
		model.setTextureCoords(textureCoords);
	}
	
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 0, 0, 1, 1, 0, 1, 1};
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2, 2, 1, 3};
	}
	
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			new Point(-192, 288, true),
			new Point(-192, -288, true),
			new Point(192, 288, true),
			new Point(192, -288, true)
		};
		return points;
	}
	
	public Point[] generateHitbox(){
		Point[] hitbox = new Point[]{
			//Why is this -18, but at the button it's +12 and not +18?
			new Point(-18, 240, true),
			new Point(-36, 228, true),
			new Point(-48, 180, true),
			new Point(-60, 132, true),
			
			new Point(-60, 90, true),
			new Point(-72, 78, true),
			new Point(-78, 24, true),
			new Point(-78, -104, true),
			
			new Point(-78, -162, true),
			new Point(-36, -180, true),
			new Point(36, -180, true),
			new Point(72, -162, true),
			
			new Point(78, -104, true),
			new Point(78, 24, true),
			new Point(72, 78, true),
			new Point(60, 90, true),
			
			new Point(60, 132, true),
			new Point(48, 180, true),
			new Point(36, 228, true),
			new Point(12, 240, true),
			
		};
		return hitbox;
	}
	
	
	
	public void doRandomMovement(){
		super.doRandomMovement();
		if(target != null && target.getHealth() <= 0){
			target = null;
		}
		getClosestEnemy();
		moveToLocation();
			
		moveTurrets();
		edgeGuard();
		getClosestEnemy();
	}
	
	//gets the closest enemy and changes target accordingly
	public void getClosestEnemy(){
		if (target != null && game.distance(center.x, center.y, target.getX(), target.getY()) > scan_range) target = null;
		ArrayList<Starship> scanned = scan();
		if(scanned.size() != 0){
			for (int i = 0; i < scanned.size(); i++) {
				Starship s = scanned.get(i);
				//if fighter has no team, or scanned enemy is on another team or closer than current target
				//Missileship is a special case; it shoots further than it can see, so it is the only ship where
				//we must check whether it can even see target.
				if(game.isVisible(s, team) && (team.equals("none") || !s.getTeam().equals(team)) && (target == null ||
					game.distance(center.X(), center.Y(), s.getX(), s.getY()) - getClosestBearing(s) < 
					game.distance(center.X(), center.Y(), target.getX(), target.getY()) - getClosestBearing(target))){
					target = scanned.get(i);
				}
			}
		}
	}
	
}