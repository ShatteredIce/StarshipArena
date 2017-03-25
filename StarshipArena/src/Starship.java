import java.util.ArrayList;
import java.util.Random;

public class Starship {
	
	Random random = new Random();
	StarshipArena game;
	Model model;
	Texture tex;
	
	double[] vertices;
	double[] textureCoords; 
	int[] indices;
	Point center;
	Point[] points;
	
	String team;
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
	int scan_range;
	int radius;
	Point target;
	
	boolean selected = false;
	
	//Screen bounds
	int x_min;
	int x_max;
	int y_min;
	int y_max;
	
	Starship(StarshipArena mygame, int spawnx, int spawny){
		this(mygame, "none", spawnx, spawny, 0, 10);
	}
	
	Starship(StarshipArena mygame, int spawnx, int spawny, int spawnangle, int spawnhealth){
		this(mygame, "none", spawnx, spawny, spawnangle, spawnhealth);
	}
	
	Starship(StarshipArena mygame, String newteam, int spawnx, int spawny, int spawnangle, int spawnhealth){
		game = mygame;
		team = newteam;
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
		setIndices();
		setTexture();
		setPoints();
		model = new Model(vertices, textureCoords, indices);
		game.addShip(this);
	}
	
	public void setPoints(){
		updateCurrentVelocity();
		//set the center point if not offscreen
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
		updateCurrentAngle();
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
			new Projectile(this, game,center.X(),center.Y(),1,angle,primary_accuracy,primary_speed,primary_lifetime);
		}
		else if(primary_current_cooldown > 0){
			primary_current_cooldown -= 1;
			if(primary_current_cooldown < 0){
				primary_current_cooldown = 0;
			}
		}
	}
	
	public void destroy(){
		tex.destroy();
		model.destroy();
		game.removeShip(this);
	}
	
	public void setTexture(){
		tex = new Texture("triangle_spaceship.jpg");
	}
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 1, 0.5, 0, 1, 1};
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2};
	}
	
	public boolean display(){
		if(current_health > 0){
			tex.bind();
			model.render(vertices);
			return true;
		}
		else {
			destroy();
			return false;
		}
	}

	public void doRandomMovement(){
		int v = random.nextInt(4);
		int t = random.nextInt(3);
		if(v == 0){
			targeted_velocity = max_velocity;
		}
		else if(v == 1){
			targeted_velocity = 0;
		}
		if(t == 0){
			current_turn_speed = max_turn_speed;
		}
		else if(t == 1){
			current_turn_speed = -max_turn_speed;
		}
		if(current_turn_speed != 0 && targeted_velocity < min_turn_velocity){
			targeted_velocity = min_turn_velocity;
		}
		edgeGuard();
//		targeted_velocity = max_velocity;
//		current_turn_speed = max_turn_speed;
	}
	
	public void edgeGuard(){
		int BORDER = 100;
		//left edge
		if(center.X() < BORDER && angle <= 90){
			current_turn_speed = -max_turn_speed;
		}
		else if(center.X() < BORDER && angle <= 180){
			current_turn_speed = max_turn_speed;
		}
		//right edge
		else if(center.X() > x_max - BORDER && angle >= 270){
			current_turn_speed = max_turn_speed;
		}
		else if(center.X() > x_max - BORDER && angle >= 180){
			current_turn_speed = -max_turn_speed;
		}
		//bottom edge
		else if(center.Y() < BORDER && angle >= 90 && angle <= 180){
			current_turn_speed = -max_turn_speed;
		}
		else if(center.Y() < BORDER && angle > 180 && angle <= 270){
			current_turn_speed = max_turn_speed;
		}
		//top edge
		else if(center.Y() > y_max - BORDER && angle <= 90){
			current_turn_speed = max_turn_speed;
		}
		else if(center.Y() > y_max - BORDER && angle >= 270){
			current_turn_speed = -max_turn_speed;
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
	
	public ArrayList<Starship> scan(){
		ArrayList<Starship> shipsList = game.getAllShips();
		ArrayList<Starship> scanned = new ArrayList<Starship>();
		for(int i = 0; i < shipsList.size(); i++){
			if(!shipsList.get(i).equals(this) && (game.distance(center.X(), center.Y(), shipsList.get(i).getX(), shipsList.get(i).getY()) <= scan_range)){
				scanned.add(shipsList.get(i));
			}
		}
		return scanned;
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
		scan_range = 200;
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
	
	//get turn distance to angle depending on which direction to turn, 
	public int getTurnDistance(int relativeAngle, boolean toLeft){
		//find which direction to turn is shortest to target
		if(angle >= relativeAngle){
			if(toLeft){
				return 360 - angle + relativeAngle; //left bearing
			}
			else{
				return angle - relativeAngle; //right bearing
			}
		}
		else if(angle < relativeAngle){
			if(toLeft){
				return relativeAngle - angle; //left bearing
			}
			else{
				 return angle + 360 - relativeAngle; //right bearing
			}
		}
		else{
			try {
				throw new GameException("error in function Fighter.getTurnDistance");
			} catch (GameException e) {
				e.printStackTrace();
			}
			return 0;
		}
	}
		
	//finds the closest angle to turn to be facing starship s
	public int getClosestBearing(Starship s){
		int relativeAngle = game.angleToPoint(center.X(), center.Y(), s.getX(), s.getY());
		int leftBearing = getTurnDistance(relativeAngle, true);
		int rightBearing = getTurnDistance(relativeAngle, false);
		if(leftBearing <= rightBearing){
			return leftBearing;
		}
		else{
			return rightBearing;
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
	
	public void updateCurrentAngle(){
		angle += current_turn_speed;
		normalizeAngle();
	}
	
	public void normalizeAngle(){
		while(angle < 0){
			angle += 360;
		}
		while(angle >= 360){
			angle -= 360;
		}
	}
	
	public int normalizeValue(int i){
		while(i < 0){
			i += 360;
		}
		while(i >= 360){
			i -= 360;
		}
		return i;
	}
	
	public Point[] getPoints(){
		return points;
	}
	
	public String getTeam(){
		return team;
	}
	
	public void setX(int newx) {
		center.setX(newx);
	}
	
	public void setY(int newy) {
		center.setY(newy);
	}
	
	public double getX() {
		return center.X();
	}
	
	public double getY() {
		return center.Y();
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
	
	public boolean getSelected() {
		return selected;
	}
	public void setSelected(boolean newSelected) {
		selected = newSelected;
	}
	
	public void setTarget(Point newTarget) {
		target = newTarget;
	}

}
