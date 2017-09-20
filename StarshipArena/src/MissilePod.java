import java.util.ArrayList;

public class MissilePod extends BasicPod{
	
	static Texture tex1 = new Texture("red_missilepod.png");
	static Texture tex2 = new Texture("blue_missilepod.png");
	
	static double primary_damage = 5;
	static int primary_cooldown = 200;
	static int primary_spread = 55;
	static int primary_accuracy = 95;
	static int primary_range = 3000;
	static int primary_speed = 15;
	static int primary_lifetime = 3300;
	static int primary_xoffset = 20;
	static int primary_yoffset = 30;
	static int primary_id = 4;
	
	
	public MissilePod(StarshipArena mygame, int spawnx, int spawny){
		super(mygame, spawnx, spawny);
	}
	
	public MissilePod(StarshipArena mygame, int spawnx, int spawny, double spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public MissilePod(StarshipArena mygame, String newteam, int spawnx, int spawny, double spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	
	public void shipStats(){
		max_health = 80;
		scan_range = primary_range * 3/4;
		radar_range = primary_range;
		//movement
		acceleration = 0.1;
		max_velocity = 0.5;
		min_turn_velocity = 0;
		max_turn_speed = 1;
		//other
		clickRadius = 65;
		xOff = 0;
		yOff = -5;
	}
	
	public void shipTurrets(){
//		if(team.equals("blue")){
//			primary_id = 1;
//		}
//		else if(team.equals("red")){
//			primary_id = 2;
//		}
		Turret primaryTurret1 = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id);
		primaryTurret1.setOffset(primary_xoffset, primary_yoffset);
		turrets.add(primaryTurret1);
		Turret primaryTurret2 = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id);
		primaryTurret2.setOffset(-primary_xoffset, primary_yoffset);
		turrets.add(primaryTurret2);
	}
	
//	public void moveTurrets(){
//		for (int i = 0; i < turrets.size(); i++) {
//			Point p = new Point();
//			p.setX(turrets.get(i).getXOffset() + center.X());
//			p.setY(turrets.get(i).getYOffset() + center.Y());
//			p.rotatePoint(center.X(), center.Y(), angle);
//			turrets.get(i).setCenter(p);
//			turrets.get(i).setAngle(angle);
//			turrets.get(i).update();
//		}
//	}
	
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