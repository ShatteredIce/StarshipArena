import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.Random;

public class Starship {
	
	Random random = new Random();
	StarshipArena game;
	Model model;
	Texture tex;
	
	double[] vertices = new double[6];
	double[] textureCoords; 
	Point center = new Point();
	//Left vertex
	Point p1 = new Point();
	//Front vertex
	Point p2 = new Point();
	//Right vertex
	Point p3 = new Point();
	
	int angle;
	double targeted_velocity;
	double current_velocity = 0;
	int current_turn_speed;

	
	//Customizable variables
	//Ship specifications
	int max_health;
	int current_health;
	double acceleration = 0.1;
	double max_velocity = 5;
	double max_reverse_velocity = -2;
	double min_turn_velocity = 2;
	int max_turn_speed = 2;
	//Weaponry
	boolean primary_fired = false;
	int primary_cooldown = 20;
	int primary_current_cooldown = 0;
	double primary_speed = 5.5;
	double primary_lifetime = 450/primary_speed;
	int primary_accuracy = 95;
	
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
		center.setX(spawnx);
		center.setY(spawny);
		angle = spawnangle;
		max_health = spawnhealth;
		current_health = spawnhealth;
		setPoints();
		textureCoords = new double[]{0, 1, 0.5, 0, 1, 1};
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
		p1.setX(center.X()-10);
		p1.setY(center.Y()-20);
		p2.setX(center.X());
		p2.setY(center.Y()+20);
		p3.setX(center.X()+10);
		p3.setY(center.Y()-20);
		p1.rotatePoint(center.X(), center.Y(), angle);
		p2.rotatePoint(center.X(), center.Y(), angle);
		p3.rotatePoint(center.X(), center.Y(), angle);
		vertices[0] = p1.X();
		vertices[1] = p1.Y(); 
		vertices[2] = p2.X();
		vertices[3] = p2.Y();
		vertices[4] = p3.X();
		vertices[5] = p3.Y();
//		if(primary_fired == true && primary_current_cooldown == 0){
//			primary_current_cooldown = primary_cooldown;
//			//new Projectile(game,p1.X(),p1.Y(),1,angle,primary_accuracy,primary_speed,primary_lifetime,1,0,0);
//		}
//		else if(primary_current_cooldown > 0){
//			primary_current_cooldown -= 1;
//			if(primary_current_cooldown < 0){
//				primary_current_cooldown = 0;
//			}
//		}
//		if(current_health <= 0){
//			destroy();
//		}
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
	
	public Point getP1() {
		return p1;
	}
	
	public Point getP2() {
		return p2;
	}
	
	public Point getP3() {
		return p3;
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
		primary_fired = state;
	}
	
	public boolean getPrimaryFired(){
		return primary_fired;
	}
	
	public int getMaxHealth(){
		return max_health;
	}

}
