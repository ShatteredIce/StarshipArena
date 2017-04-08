import java.util.ArrayList;

public class MissilePod extends Starship{
	
	static Texture tex1 = new Texture("red_missilepod.png");
	static Texture tex2 = new Texture("blue_missilepod.png");
	
	static double primary_damage = 10;
	static int primary_cooldown = 3000;
	static int primary_spread = 55;
	static int primary_accuracy = 95;
	static int primary_range = 2000;
	static int primary_speed = 15;
	static int primary_lifetime = 2100;
	static int primary_xoffset = 0;
	static int primary_yoffset = 20;
	int primary_id = 4;
	
	
	public MissilePod(StarshipArena mygame, int spawnx, int spawny){
		super(mygame, spawnx, spawny);
	}
	
	public MissilePod(StarshipArena mygame, int spawnx, int spawny, int spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public MissilePod(StarshipArena mygame, String newteam, int spawnx, int spawny, int spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	
	public void shipStats(){
		max_health = 50;
		//other
		clickRadius = 30;
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