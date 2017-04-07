import java.util.ArrayList;

public class Battleship extends Starship{
	
	Starship target = null;
	int changeDirection = 0;
	int changeDirectionCooldown = 0;
	static Texture blue_tex1 = new Texture("blue_transport1.png");
	static Texture blue_tex2 = new Texture("blue_transport2.png");
	static Texture blue_tex3 = new Texture("blue_transport3.png");
	static Texture blue_tex4 = new Texture("blue_transport4.png");
	static Texture red_tex1 = new Texture("red_transport1.png");
	static Texture red_tex2 = new Texture("red_transport2.png");
	static Texture red_tex3 = new Texture("red_transport3.png");
	static Texture red_tex4 = new Texture("red_transport4.png");
	
	static double primary_damage = 10;
	static int primary_cooldown = 800;
	static int primary_spread = 60;
	static int primary_accuracy = 95;
	static int primary_range = 2000;
	static int primary_speed = 15;
	static int primary_lifetime = 2500;
	static int primary_xoffset = 0;
	static int primary_yoffset = 10;
	int primary_id = 4;
	
	static double secondary_damage = 0.5;
	static int secondary_cooldown = 80;
	static int secondary_spread = 30;
	static int secondary_accuracy = 97;
	static int secondary_range = 300;
	static int secondary_speed = 30; 
	static int secondary_lifetime = 400;
	static int secondary_xoffset = 0;
	static int secondary_yoffset = 0;
	static int secondary_id = 3;
	
	public Battleship(StarshipArena mygame, int spawnx, int spawny){
		super(mygame, spawnx, spawny);
	}
	
	public Battleship(StarshipArena mygame, int spawnx, int spawny, int spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public Battleship(StarshipArena mygame, String newteam, int spawnx, int spawny, int spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	
	public void shipStats(){
		max_health = 50;
		//movement
		acceleration = 0.2;
		max_velocity = 5;
		max_reverse_velocity = -2;
		min_turn_velocity = 1;
		max_turn_speed = 1;
		//weaponry
		scan_range = 300;
		//other
		clickRadius = 55;
		xOff = 0;
		yOff = 0;
	}
	
	public void shipTurrets(){
//		if(team.equals("blue")){
//			primary_id = 1;
//		}
//		else if(team.equals("red")){
//			primary_id = 2;
//		}
		Turret primaryTurret = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, 4, 2, 0);
		primaryTurret.setOffset(primary_xoffset, primary_yoffset);
		turrets.add(primaryTurret);
		
		Turret secondaryTurret1 = new Turret(game, this, team, 0, 0, angle, secondary_damage, secondary_cooldown, 
				secondary_spread, secondary_accuracy, secondary_range, secondary_speed, secondary_lifetime, secondary_id, 1, 90);
		secondaryTurret1.setOffset(-secondary_xoffset, 50);
		turrets.add(secondaryTurret1);
		
		Turret secondaryTurret2 = new Turret(game, this, team, 0, 0, angle, secondary_damage, secondary_cooldown, 
				secondary_spread, secondary_accuracy, secondary_range, secondary_speed, secondary_lifetime, secondary_id, 1, 90);
		secondaryTurret2.setOffset(-secondary_xoffset, 0);
		turrets.add(secondaryTurret2);
		
		Turret secondaryTurret3 = new Turret(game, this, team, 0, 0, angle, secondary_damage, secondary_cooldown, 
				secondary_spread, secondary_accuracy, secondary_range, secondary_speed, secondary_lifetime, secondary_id, 1, 90);
		secondaryTurret3.setOffset(-secondary_xoffset, -50);
		turrets.add(secondaryTurret3);
		
		Turret secondaryTurret4 = new Turret(game, this, team, 0, 0, angle, secondary_damage, secondary_cooldown, 
				secondary_spread, secondary_accuracy, secondary_range, secondary_speed, secondary_lifetime, secondary_id, 1, 270);
		secondaryTurret4.setOffset(secondary_xoffset, 50);
		turrets.add(secondaryTurret4);
		
		Turret secondaryTurret5 = new Turret(game, this, team, 0, 0, angle, secondary_damage, secondary_cooldown, 
				secondary_spread, secondary_accuracy, secondary_range, secondary_speed, secondary_lifetime, secondary_id, 1, 270);
		secondaryTurret5.setOffset(secondary_xoffset, 0);
		turrets.add(secondaryTurret5);
		
		Turret secondaryTurret6 = new Turret(game, this, team, 0, 0, angle, secondary_damage, secondary_cooldown, 
				secondary_spread, secondary_accuracy, secondary_range, secondary_speed, secondary_lifetime, secondary_id, 1, 270);
		secondaryTurret6.setOffset(secondary_xoffset, -50);
		turrets.add(secondaryTurret6);
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
			new Point(-96, 144, true),
			new Point(-96, -144, true),
			new Point(96, 144, true),
			new Point(96, -144, true)
		};
		return points;
	}
	
	public Point[] generateHitbox(){
		Point[] hitbox = new Point[]{
			new Point(-9, 120, true),
			new Point(-18, 114, true),
			new Point(-24, 90, true),
			new Point(-30, 66, true),
			
			new Point(-30, 45, true),
			new Point(-36, 39, true),
			new Point(-39, 12, true),
			new Point(-39, -57, true),
			
			new Point(-39, -81, true),
			new Point(-18, -90, true),
			new Point(18, -90, true),
			new Point(36, -81, true),
			
			new Point(39, -57, true),
			new Point(39, 12, true),
			new Point(36, 39, true),
			new Point(30, 45, true),
			
			new Point(30, 66, true),
			new Point(24, 90, true),
			new Point(18, 114, true),
			new Point(6, 120, true),
			
		};
		return hitbox;
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
				if (Math.abs(this.angle - Math.round(targetAngle)) < 2) {
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
		moveTurrets();
		edgeGuard();
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