import java.util.ArrayList;

public class PlanetRadar extends Starship{
	
	static Texture tex1 = new Texture("WIP.png");
	
	public PlanetRadar(StarshipArena mygame, int spawnx, int spawny){
		super(mygame, spawnx, spawny);
	}
	
	public PlanetRadar(StarshipArena mygame, int spawnx, int spawny, double spawnangle){
		super(mygame, "none", spawnx, spawny, spawnangle);
	}

	public PlanetRadar(StarshipArena mygame, String newteam, int spawnx, int spawny, double spawnangle){
		super(mygame, newteam, spawnx, spawny, spawnangle);
	}
	
	public void shipStats(){
		max_health = 100;
		scan_range = 2500;
		radar_range = 2500;
		//movement
		acceleration = 0;
		max_velocity = 0;
		min_turn_velocity = 0;
		max_turn_speed = 1;
		current_turn_speed = -max_turn_speed;
		//other
		clickRadius = 45;
		xOff = 0;
		yOff = 0;
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