import java.util.ArrayList;
import java.util.Random;

public class Starship {
	
	int damageDisplayDelay = 0;
	Starship target = null;
	Random random = new Random();
	StarshipArena game;
	Model model;
	static Texture tex = new Texture("WIP.png");
	//halo rendering variables
	Model haloModel;
	double[] haloVertices;
	Point[] haloPoints;
	int haloSize = 80;
	static Texture haloTexture = new Texture("ships_halo.png");
	
	double[] vertices;
	double[] textureCoords; 
	int[] indices;
	Point center;
	Point[] points;
	Point[] hitbox;
	
	String team;
	int angle;
	double targeted_velocity;
	double current_velocity = 0;
	int current_turn_speed;
	
	//Customizable variables
	//Ship specifications
	double max_health;
	double current_health;
	double acceleration;
	double max_velocity;
	double max_reverse_velocity;
	double min_turn_velocity;
	int max_turn_speed;
	int scan_range;
	int clickRadius;
	
	ArrayList<Turret> turrets = new ArrayList<>();
	
	int xOff = 0;
	int yOff = 0;
	Point spawnPoint;
	Point locationTarget;
	
	boolean selected = false;
	
	static Texture blueHalo = new Texture("blue_halo.png");
	static Texture redHalo = new Texture("red_halo.png");
	static Texture whiteHalo = new Texture("white_halo.png");
	
	//Screen bounds
	int x_min;
	int x_max;
	int y_min;
	int y_max;
	
	Starship(StarshipArena mygame, int spawnx, int spawny){
		this(mygame, "none", spawnx, spawny, 0);
	}
	
	Starship(StarshipArena mygame, int spawnx, int spawny, int spawnangle){
		this(mygame, "none", spawnx, spawny, spawnangle);
	}
	
	Starship(StarshipArena mygame, String newteam, int spawnx, int spawny, int spawnangle){
		game = mygame;
		team = newteam;
		setScreenBounds(game.getScreenBounds());
		if(!onScreen(spawnx, spawny)){
			try {
				throw new GameException("Ship spawned off screen");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		spawnPoint = new Point(spawnx, spawny);
		center = new Point(spawnx, spawny);
		angle = spawnangle;
		shipStats();
		shipTurrets();
		current_health = max_health;
		locationTarget = null;
		points = generatePoints();
		haloPoints = generateHaloPoints();
		hitbox = generateHitbox();
		vertices = new double[points.length * 2];
		haloVertices = new double[haloPoints.length * 2];
		setTextureCoords();
		setIndices();
		setPoints();
		model = new Model(vertices, textureCoords, indices);
		haloModel = new Model(haloVertices, textureCoords, indices);
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
		for (int i = 0; i < hitbox.length; i++) {
			hitbox[i].setX(center.X() + hitbox[i].getXOffset());
			hitbox[i].setY(center.Y() + hitbox[i].getYOffset());
			hitbox[i].rotatePoint(center.X(), center.Y(), angle);
		}
		moveTurrets();
		fireTurrets();
	}
	
	public void setHaloPoints(){
		Point trueCenter = new Point(center.X() + xOff, center.Y() + yOff);
		trueCenter.rotatePoint(center.X(), center.Y(), angle);
		haloPoints[0].setX(trueCenter.X() - haloSize);
		haloPoints[0].setY(trueCenter.Y() + haloSize);
		haloPoints[1].setX(trueCenter.X() - haloSize);
		haloPoints[1].setY(trueCenter.Y() - haloSize);
		haloPoints[2].setX(trueCenter.X() + haloSize);
		haloPoints[2].setY(trueCenter.Y() + haloSize);
		haloPoints[3].setX(trueCenter.X() + haloSize);
		haloPoints[3].setY(trueCenter.Y() - haloSize);
		int v_index = 0;
		for (int i = 0; i < haloPoints.length; i++) {
			v_index = 2*i;
			haloVertices[v_index] = haloPoints[i].X();
			haloVertices[v_index+1] = haloPoints[i].Y();	
		}
	}
	
	public void setHaloTexture(){
		if(team.equals("blue")){
			blueHalo.bind();
		}
		else if(team.equals("red")){
			redHalo.bind();
		}
		else{
			whiteHalo.bind();
		}
	}
	
	public void moveTurrets(){
		
	}
	
	public void fireTurrets(){
		for (int i = 0; i < turrets.size(); i++) {
			turrets.get(i).update();
		}
	}
	
	public void destroy(){
		model.destroy();
		game.removeShip(this);
	}
	
	public void setTexture(){
		tex.bind();
	}
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 1, 0.5, 0, 1, 1};
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2};
	}
	
	public boolean display(){
		if(current_health > 0){
			setTexture();
			model.render(vertices);
			if(selected){
				setHaloPoints();
				haloTexture.bind();
//				setHaloTexture();
				haloModel.render(haloVertices);
			}
			return true;
		}
		else {
			new Explosion(game, center.X(), center.Y());
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
	
	public Point[] generateHaloPoints(){
		Point[] points = new Point[]{
			new Point(),
			new Point(),
			new Point(),
			new Point()
		};
		return points;
	}
	
	public Point[] generateHitbox(){
		Point[] hitbox = new Point[]{
			new Point(-10, -20, true),
			new Point(0, 20, true),
			new Point(10, -20, true),
		};
		return hitbox;
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
		max_health = 1;
		//movement
		acceleration = 0.1;
		max_velocity = 5;
		max_reverse_velocity = -2;
		min_turn_velocity = 2;
		max_turn_speed = 2;
		scan_range = 200;
	}
	
	public void shipTurrets(){
		
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
		angle = game.normalizeAngle(angle);
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
		return hitbox;
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
	
	public double getHealth() {
		return current_health;
	}
	
	public void setHealth(double newhealth) {
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
	
	public double getMaxHealth(){
		return max_health;
	}
	
	public boolean getSelected() {
		return selected;
	}
	public void setSelected(boolean newSelected) {
		selected = newSelected;
	}
	
	public void setLocationTarget(Point newLocationTarget) {
		locationTarget = newLocationTarget;
	}
	
	public int getClickRadius(){
		return clickRadius;
	}
	
	public int getXOff(){
		return xOff;
	}

	public int getYOff(){
		return yOff;
	}
	
    public double distance(double x1, double y1, double x2, double y2){
    	return Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1),2));
    }

	public Point getSpawnPoint() {
		return spawnPoint;
	}
}
