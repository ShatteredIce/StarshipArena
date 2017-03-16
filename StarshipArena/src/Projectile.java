import java.util.Random;

public class Projectile {
	
	Random random = new Random();
	StarshipArena game;
	Starship owner;
	Model model;
	
	double[] vertices;
	double[] textureCoords;
	int[] indices;
	Point center;
	Point[] points;
	
	int damage;
	int angle;
	double speed;
	double lifetime;
	double current_lifetime = 0;
	
	Projectile(Starship newowner, StarshipArena mygame, double spawnx, double spawny, int newdamage, int spawnangle, int accuracy, double newspeed, double newlifetime){
		owner = newowner;
		game = mygame;
		damage = newdamage;
		angle = spawnangle;
		speed = newspeed;
		lifetime = newlifetime;
		setAccuracy(accuracy);
		center = new Point(spawnx, spawny);
		points = generatePoints();
		vertices = new double[points.length * 2];
		setTextureCoords();
		setIndices();
		setPoints();
		model = new Model(vertices, textureCoords, indices);
		game.addProjectile(this);
	}
	
	public void setPoints(){
		Point newcenter = new Point(center.X(), center.Y()+speed);
		newcenter.rotatePoint(center.X(), center.Y(), angle);
		center.setX(newcenter.X());
		center.setY(newcenter.Y()); 
		int v_index = 0;
		for (int i = 0; i < points.length; i++) {
			points[i].setX(center.X() + points[i].getXOffset());
			points[i].setY(center.Y() + points[i].getYOffset());
			points[i].rotatePoint(center.X(), center.Y(), angle);
			v_index = 2*i;
			vertices[v_index] = points[i].X();
			vertices[v_index+1] = points[i].Y();	
		}
	}
	
	//returns false if projectile is destroyed
	public boolean updateLifetime(){
		current_lifetime += 1;
		if(current_lifetime >= lifetime){
			return false;
		}
		return true;
	}
	
	public void setAccuracy(int accuracy){
		int randomized_angle = random.nextInt(100-accuracy+1);
		int direction = random.nextInt(2);
		if(direction == 0){
			angle += randomized_angle;
		}
		else{
			angle -= randomized_angle;
		}
	}
	
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			new Point(-3, -10, true),
			new Point(-3, 10, true),
			new Point(3, 10, true),
			new Point(3, -10, true),
		};
		return points;
	}
	
	public void setTextureCoords(){
		textureCoords = new double[]{1, 1, 0, 1, 0, 0, 1, 0};
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2, 2, 3, 0};
	}
	
	public boolean display(){
		if(updateLifetime()){
			model.render(vertices);
			return true;
		}
		else{
			model.destroy();
			game.removeProjectile(this);
			return false;
		}
	}
	
	public Starship getOwner(){
		return owner;
	}
	
	public Point[] getPoints(){
		return points;
	}
	
	public int getDamage(){
		return damage;
	}
}