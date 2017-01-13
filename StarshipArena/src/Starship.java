import java.util.Random;

public class Starship {
	
	Random random = new Random();
	StarshipArena game;
	Model model;
	Texture tex;
	
	double[] vertices;
	double[] textureCoords; 
	Point center;
	Point[] points;
	
	int angle;
	double targeted_velocity;
	double current_velocity = 0;
	int current_turn_speed;

	
	//Customizable variables
	//Ship specifications
	int max_health;
	int current_health;
	double acceleration;
	double max_velocity;
	double max_reverse_velocity;
	double min_turn_velocity;
	int max_turn_speed;
	boolean primary_fire = false;
	int primary_cooldown;
	int primary_current_cooldown;
	double primary_speed;
	double primary_lifetime;
	int primary_accuracy;
	
	//Screen bounds
	int x_min;
	int x_max;
	int y_min;
	int y_max;
	
	Starship(StarshipArena mygame, int spawnx, int spawny){
		this(mygame, spawnx, spawny, 0, 10);
	}
	
	Starship(StarshipArena mygame, int spawnx, int spawny, int spawnangle, int spawnhealth){
		game = mygame;
		setScreenBounds(game.getScreenBounds());
		if(!onScreen(spawnx, spawny)){
			try {
				throw new GameException("Ship spawned off screen");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		max_health = spawnhealth;
		current_health = spawnhealth;
		angle = spawnangle;
		shipStats();
		center = new Point(spawnx, spawny);
		points = generatePoints();
		vertices = new double[points.length * 2];
		setTextureCoords();
		setPoints();
		game.addShip(this);
	}
	
	public void setPoints(){
		updateCurrentVelocity();
		Point newcenter = new Point(center.X(), center.Y()+current_velocity);
		newcenter.rotatePoint(center.X(), center.Y(), angle);
		if(onScreen(newcenter.X(), newcenter.Y())){
			center.setX(newcenter.X());
			center.setY(newcenter.Y()); 
		}
		else{
			targeted_velocity = 0;
			current_velocity = 0;
		}
		angle += current_turn_speed;
		normalizeAngle();
		//Set points for ship facing upward, then rotate points to player angle
		int v_index = 0;
		for (int i = 0; i < points.length; i++) {
			points[i].setX(center.X() + points[i].getXOffset());
			points[i].setY(center.Y() + points[i].getYOffset());
			points[i].rotatePoint(center.X(), center.Y(), angle);
			v_index = 2*i;
			vertices[v_index] = points[i].X();
			vertices[v_index+1] = points[i].Y();	
		}
		if(primary_fire == true && primary_current_cooldown == 0){
			primary_current_cooldown = primary_cooldown;
			new Projectile(game,center.X(),center.Y(),1,angle,primary_accuracy,primary_speed,primary_lifetime);
		}
		else if(primary_current_cooldown > 0){
			primary_current_cooldown -= 1;
			if(primary_current_cooldown < 0){
				primary_current_cooldown = 0;
			}
		}
		if(current_health <= 0){
			//destroy();
		}
	}
	
//	public void destroy(){
//		game.setPlayerAlive(false);
//		int explosion_projectiles = 20;
//        int drawingangle = 0;
//        while(drawingangle < 360){
//            int x_shift = random.nextInt(4) - 2;
//            int y_shift = random.nextInt(4) - 2;
//            int color = random.nextInt(3);
//            if(color == 1){
//            	new Projectile(game, center.X()+x_shift,center.Y()+y_shift,1, drawingangle, 90, 2, 10, 1.0, 0.6, 0);
//            }
//            else if(color == 2){
//            	new Projectile(game, center.X()+x_shift,center.Y()+y_shift,1, drawingangle, 90, 2, 10, 1.0, 0.3, 0);
//            }
//            else{
//            	new Projectile(game, center.X()+x_shift,center.Y()+y_shift,1, drawingangle, 90, 2, 10, 1.0, 0, 0);
//            }
//            drawingangle += (360/explosion_projectiles);
//        }
//
//	}
	
	public void setTexture(){
		tex = new Texture("triangle_spaceship.jpg");
	}
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 1, 0.5, 0, 1, 1};
	}
	
	public void display(){
		model = new Model(vertices, textureCoords);
		tex.bind();
		model.render();
	}

	public void doRandomMovement(){
		int v = random.nextInt(4);
		int t = random.nextInt(3);
		boolean turning = false;
		if(v == 0){
			targeted_velocity = max_velocity;
		}
		else if(v == 1){
			targeted_velocity = 0;
		}
		if(t == 0){
			current_turn_speed = max_turn_speed;
			turning = true;
		}
		else if(t == 1){
			current_turn_speed = -max_turn_speed;
			turning = true;
		}
		if(turning == true && targeted_velocity < min_turn_velocity){
			targeted_velocity = min_turn_velocity;
		}
		
	}
	
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			new Point(-10, -20, true),
			new Point(0, 20, true),
			new Point(10, -20, true),
		};
		return points;
	}
	
	public void shipStats(){
		//movement
		acceleration = 0.1;
		max_velocity = 5;
		max_reverse_velocity = -2;
		min_turn_velocity = 2;
		max_turn_speed = 2;
		//weaponry
		primary_cooldown = 20;
		primary_current_cooldown = 0;
		primary_speed = 5.5;
		primary_lifetime = 450/primary_speed;
		primary_accuracy = 95;
	}
	
	public void setScreenBounds(int[] bounds){
		x_min = bounds[0];
		x_max = bounds[1];
		y_min = bounds[2];
		y_max = bounds[3];
		
	}
	
	public boolean onScreen(double x, double y){
		if((x_min < x) && (x_max > x) &&
				(y_min < y) && (y_max > y)){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void updateCurrentVelocity(){
		if(targeted_velocity > current_velocity){
			current_velocity += acceleration;
			if(current_velocity > targeted_velocity){
				current_velocity = targeted_velocity;
			}
		}
		else if(targeted_velocity < current_velocity){
			current_velocity -= acceleration;
			if(current_velocity < targeted_velocity){
				current_velocity = targeted_velocity;
			}
		}
	}
	
	public void normalizeAngle(){
		while(angle < 0){
			angle += 360;
		}
		while(angle >= 360){
			angle -= 360;
		}
	}
	
	public double getX() {
		return center.X();
	}
	
	public double getY() {
		return center.Y();
	}
	
	public void setX(int newx) {
		center.setX(newx);
	}
	
	public void setY(int newy) {
		center.setY(newy);
	}
	
	public int getAngle() {
		return angle;
	}
	
	public void setAngle(int newangle) {
		angle = newangle;
	}
	
	public int getHealth() {
		return current_health;
	}
	
	public void setHealth(int newhealth) {
		current_health = newhealth;
	}
	
	public double getMVelocity(){
		return targeted_velocity;
	}
	
	public int getTSpeed(){
		return current_turn_speed;
	}
	
	public void setMVelocity(double newvelocity){
		targeted_velocity = newvelocity;
	}
	
	public void setTVelocity(int newvelocity){
		current_turn_speed = newvelocity;
	}
	
	public void setPrimaryFired(boolean state){
		primary_fire = state;
	}
	
	public boolean getPrimaryState(){
		return primary_fire;
	}
	
	public int getMaxHealth(){
		return max_health;
	}

}
