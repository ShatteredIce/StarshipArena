import java.util.Random;

public class Projectile {
	
	Random random = new Random();
	StarshipArena game;
	Model model;
	Texture tex;
	
	double[] vertices;
	double[] textureCoords; 
	Point center;
	Point[] points;
	
	int damage;
	int angle;
	double speed;
	double lifetime;
	double current_lifetime = 0;
	
	Projectile(StarshipArena mygame, double spawnx, double spawny, int newdamage, int spawnangle, int accuracy, double newspeed, double newlifetime){
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
		setPoints();
		setTexture();
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
		updateLifetime();
	}
	
	public void updateLifetime(){
		current_lifetime += 1;
		if(current_lifetime >= lifetime){
			game.removeProjectile(this);
		}
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
			new Point(3, 10, true),
			new Point(3, -10, true),
			new Point(-3, -10, true)
		};
		return points;
	}
	
	public void setTexture(){
		tex = new Texture("torpedo.png");
	}
	
	public void setTextureCoords(){
		textureCoords = new double[]{1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1};
	}
	
	public void display(){
		model = new Model(vertices, textureCoords);
		tex.bind();
		model.render();
	}
	
	public int getDamage(){
		return damage;
	}
}
