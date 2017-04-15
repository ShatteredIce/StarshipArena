import java.util.Random;

public class Projectile {
	
	Random random = new Random();
	StarshipArena game;
	Starship owner;
	String team;
	Model model;
	
	static Texture tex0 = new Texture("projectile_green.png");
	static Texture tex1 = new Texture("projectile_blue.png");
	static Texture tex2 = new Texture("projectile_red.png");
	static Texture tex3 = new Texture("projectile_machinegun.png");
	static Texture tex4 = new Texture("missile.png");
	
	double[] vertices;
	double[] textureCoords;
	int[] indices;
	Point center;
	Point[] points;
	
	double damage;
	double angle;
	double speed;
	double lifetime;
	double current_lifetime = 0;
	int texId;
	
	Projectile(StarshipArena mygame, Starship newowner, String myteam, double spawnx, double spawny, double newdamage, double spawnangle, int accuracy, double newspeed, double newlifetime, int id){
		game = mygame;
		owner = newowner;
		team = myteam;
		damage = newdamage;
		angle = spawnangle;
		speed = newspeed;
		lifetime = newlifetime;
		texId = id;
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
		Point[] points = null;
		if(texId == 0 || texId == 1 || texId == 2){
			points = new Point[]{
				new Point(-7, 14, true),
				new Point(-7, -14, true),
				new Point(7, 14, true),
				new Point(7, -14, true),
			};
		}
		else if(texId == 3){
			points = new Point[]{
				new Point(-2, 10, true),
				new Point(-2, -10, true),
				new Point(2, 10, true),
				new Point(2, -10, true),
			};
		}
		else if (texId == 4) {
			points = new Point[]{
				new Point(-16, 20, true),
				new Point(-16, -20, true),
				new Point(16, 20, true),
				new Point(16, -20, true),
			};
		}
		return points;
	}
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 0, 0, 1, 1, 0, 1, 1};
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2, 2, 1, 3};
	}
	
	public void setTexture(){
		if(texId == 1){
			tex1.bind();
		}
		else if(texId == 2){
			tex2.bind();
		}
		else if(texId == 3){
			tex3.bind();
		}
		else if (texId == 4){
			tex4.bind();
		}
		else{
			tex0.bind();
		}
	}
	
	public boolean display(){
		setTexture();
		if(updateLifetime()){
			model.render(vertices);
			return true;
		}
		else{
			destroy();
			return false;
		}
	}
	
	public void destroy(){
		model.destroy();
		game.removeProjectile(this);
		if (texId < 3) new Explosion(game, center.X(), center.Y(), 40);
		else if (texId == 3) new Explosion(game, center.X(), center.Y(), 20);
	}
	
	public Starship getOwner(){
		return owner;
	}
	
	public String getTeam(){
		return team;
	}
	
	public Point[] getPoints(){
		return points;
	}
	
	public double getDamage(){
		return damage;
	}
}