
public class MachineGunPod extends BasicPod{
	
	static Texture tex1 = new Texture("red_machinegunpod.png");
	static Texture tex2 = new Texture("blue_machinegunpod.png");
	
	static double primary_damage = 1;
	static int primary_cooldown = 4;
	static int primary_spread = 20;
	static int primary_accuracy = 99;
	static int primary_range = 1600;
	static int primary_speed = 15;
	static int primary_lifetime = 1700;
	static int primary_xoffset = 15;
	static int primary_yoffset = 20;
	static int primary_id = 3;
	
	
	public MachineGunPod(StarshipArena mygame, double spawnx, double spawny){
		super(mygame, spawnx, spawny);
	}
	
	public MachineGunPod(StarshipArena mygame, double spawnx, double spawny, double spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public MachineGunPod(StarshipArena mygame, String newteam, double spawnx, double spawny, double spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	
	public void shipStats(){
		max_health = 45;
		scan_range = primary_range - 20;
		radar_range = primary_range;
		//movement
		acceleration = 0.1;
		max_velocity = 0.5;
		min_turn_velocity = 0;
		max_turn_speed = 6;
		//other
		clickRadius = 45;
		xOff = 0;
		yOff = -15;
	}
	
	public void shipTurrets(){
		Turret primaryTurret1 = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id);
		primaryTurret1.setOffset(primary_xoffset, primary_yoffset);
		turrets.add(primaryTurret1);
		Turret primaryTurret2 = new Turret(game, this, team, 0, 0, angle, primary_damage, primary_cooldown, 
				primary_spread, primary_accuracy, primary_range, primary_speed, primary_lifetime, primary_id);
		primaryTurret2.setOffset(-primary_xoffset, primary_yoffset);
		turrets.add(primaryTurret2);
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