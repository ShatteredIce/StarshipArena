import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.FloatControl;

public class Starship {
	
	//Command queues: String commands paired with Point and Starship arrays
	ArrayList<Command> commands = new ArrayList<Command>();
	
	int damageDisplayDelay = 0;
	Starship target = null;
	Random random = new Random();
	StarshipArena game;
	static Texture tex = new Texture("WIP.png");
	//halo rendering variables
	int haloSize = 80;
	static Texture blueCircle = new Texture("blue_circle.png");
	static Texture redCircle = new Texture("red_circle.png");
	static Texture blueSelectedCircle = new Texture("blue_selected_circle.png");
	static Texture redSelectedCircle = new Texture("red_selected_circle.png");
		
	double[] vertices;
	double[] textureCoords = new double[4]; 
	int[] indices;
	Point center;
	Point[] points;
	Point[] hitbox;
	
	//Change scale of ships
	static double scaleFactor = 1;
	
	String team;
	int control_group = 0;
	double angle;
	double move_angle;
	double targeted_velocity;
	double current_velocity = 0;
	double current_turn_speed;
	
	//Customizable variables
	//Ship specifications
	double max_health;
	double current_health;
	double acceleration;
	double max_velocity;
	double max_reverse_velocity;
	double min_turn_velocity;
	double max_turn_speed;
	int scan_range;
	int radar_range;
	int clickRadius;
	//Weight determines how much a ship drifts when drifting. The lower the weight, the higher the drift speed
	double weight = 0.25;
	
	ArrayList<Turret> turrets = new ArrayList<>();
	
	int xOff = 0;
	int yOff = 0;
	Point spawnPoint;
	Point locationTarget;
	
	boolean selected = false;
	boolean lockPosition = false;
	boolean attackMove = true;
	boolean directTarget = false;
	
	
	//Screen bounds
	double x_min;
	double x_max;
	double y_min;
	double y_max;
	
	Starship(StarshipArena mygame, double spawnx, double spawny){
		this(mygame, "none", spawnx, spawny, 0);
	}
	
	Starship(StarshipArena mygame, double spawnx, double spawny, double spawnangle){
		this(mygame, "none", spawnx, spawny, spawnangle);
	}
	
	Starship(StarshipArena mygame, String newteam, double spawnx, double spawny, double spawnangle){
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
		move_angle = spawnangle;
		shipStats();
		shipTurrets();
		current_health = max_health;
		locationTarget = null;
		points = generatePoints();
		hitbox = generateHitbox();
		applyScaleFactor();
		vertices = new double[points.length * 2];
		setPoints();
		game.addShip(this);
	}
	
	public void setPoints(){
		updateCurrentVelocity();
		//set the center point if not offscreen
		Point newcenter = new Point(center.X(), center.Y()+current_velocity);
		newcenter.rotatePoint(center.X(), center.Y(), move_angle);
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
	
	
	public void destroy(){
		if (this instanceof Missileship) {
			new Explosion(game, center.X(), center.Y(), 220);
			for (int i = 0; i < 4; i++) {
				int x_rand = random.nextInt(5) - 2;
				int y_rand = random.nextInt(5) - 2;
				int rand_angle = random.nextInt(360);
				
				new Projectile(game, null, "none", center.x + x_rand, center.y + y_rand, 0, rand_angle, 100, 18, 7, 0);
				new Projectile(game, null, "none", center.x + x_rand, center.y + y_rand, 0, rand_angle, 100, 18, 10, 0);
				new Projectile(game, null, "none", center.x + x_rand, center.y + y_rand, 0, rand_angle, 100, 18, 13, 0);
				new Projectile(game, null, "none", center.x + x_rand, center.y + y_rand, 0, rand_angle, 100, 18, 16, 0);
			}
		}
		else {
			new Explosion(game, center.X(), center.Y());
			for (int i = 0; i < 4; i++) {
				int x_rand = random.nextInt(5) - 2;
				int y_rand = random.nextInt(5) - 2;
				int rand_angle = random.nextInt(360);
				
				new Projectile(game, null, "none", center.x + x_rand, center.y + y_rand, 0, rand_angle, 100, 6, 9, 3);
				new Projectile(game, null, "none", center.x + x_rand, center.y + y_rand, 0, rand_angle, 100, 6, 12, 3);
				new Projectile(game, null, "none", center.x + x_rand, center.y + y_rand, 0, rand_angle, 100, 6, 15, 3);
				new Projectile(game, null, "none", center.x + x_rand, center.y + y_rand, 0, rand_angle, 100, 6, 18, 3);
			}
		}
		for (int i = 20; i < 25; i++) {
			if (game.mute) break;
			if (!game.soundEffects[i].isRunning()) {
				double cameraX = game.viewX + game.cameraWidth / 2;
				double cameraY = game.viewY + game.cameraHeight / 2;
				//This formula decrease the volume the further away the player is from the weapon event, but increase volume for high levels of zoom
				float dbDiff = (float)(game.distance(cameraX, cameraY, center.X(), center.Y()) / game.cameraWidth * -10 + 10000 / game.cameraWidth);
				FloatControl gainControl = (FloatControl) game.soundEffects[i].getControl(FloatControl.Type.MASTER_GAIN);
				gainControl.setValue(Math.max(-80, Math.min(6, game.DEATHEX_DB + dbDiff))); // Increase volume by a number of decibels.
				game.soundEffects[i].setFramePosition(0);
				game.soundEffects[i].start();
				break;
			}
		}
		//Close turret sound clips, save memory hopefully
//		for (int i = 0; i < turrets.size(); i++) {
//			turrets.get(i).clip.close();
//		}
		game.removeShip(this);
	}
	
	public void setTexture(){
		tex.bind();
		setTextureCoords(0,0,1,1);
	}
	
	public double[] getTextureCoords(){
		return textureCoords;
	}
	
	public void setTextureCoords(double x1, double y1, double x2, double y2){
		textureCoords[0] = x1;
		textureCoords[1] = y1;
		textureCoords[2] = x2;
		textureCoords[3] = y2;
	}
	
	public boolean checkHealth(){
		if(current_health > 0){
			return true;
		}
		else {
			destroy();
			return false;
		}
	}
	
	public void setIconTexture(){
		setTextureCoords(0, 0, 1, 1);
		if(team == "blue"){
			if(selected){
				blueSelectedCircle.bind();
			}
			else{
				blueCircle.bind();
			}
		}
		else{
			if(selected){
				redSelectedCircle.bind();
			}
			else{
				redCircle.bind();
			}
		}
	}
	
	//The superclass' doRandomMovement makes sure every ship class processes its command queue before executing its default behavior.
	//TODO For every ship, I must teach it how to deal with directTarget
	public void doRandomMovement(){
		//If ship has commands:
		if (!commands.isEmpty()) {
			Command command = commands.get(0);
			//If the command is a location target, tell the ship to move there (apply modifiers e.g. alt for attack-move).
			if (command.isLocationTarget) {
				locationTarget = command.locationTarget;
				isDirectTarget(false);
				setAttackMove(command.alt);
				setLockPosition(command.t);
				//If the command is complete (we have moved to the location/turned to face the direction if it's a turn command), remove it.
				if (!lockPosition && distance(this.getX(), this.getY(), locationTarget.X(), locationTarget.Y()) < 50)
					commands.remove(0);
				else if (lockPosition) {
					double relativeAngle = game.angleToPoint(this.getX(), this.getY(), locationTarget.X(), locationTarget.Y());
					double leftBearing = getTurnDistance(relativeAngle, true);
					double rightBearing = getTurnDistance(relativeAngle, false);
					if(leftBearing < max_turn_speed || rightBearing < max_turn_speed){
						commands.remove(0);
					}
				}
			}
			else {
				//Otherwise, we are targeting a ship. Direct target it.
				target = command.target;
				isDirectTarget(true);
				//If command is a direct target, in theory we can ignore modifiers. But read them in just in case.
				setAttackMove(command.alt);
				setLockPosition(command.t);
				if (target == null || target.getHealth() <= 0 || !game.isVisible(target, this.getTeam()))
					commands.remove(0);
				//Check if target is dead/out of radar, otherwise target it
			}
		}
		else {
			setAttackMove(false);
			setLockPosition(false);
			isDirectTarget(false);
			//Will this help or screw up things?
			locationTarget = null;
		}
	}
	
	public void edgeGuard(){
		int BORDER = 100;
		//left edge
		if(center.X() < x_min + BORDER && angle <= 90){
			current_turn_speed = -max_turn_speed;
		}
		else if(center.X() < x_min + BORDER && angle <= 180){
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
		else if(center.Y() < y_min + BORDER && angle >= 90 && angle <= 180){
			current_turn_speed = -max_turn_speed;
		}
		else if(center.Y() < y_min + BORDER && angle > 180 && angle <= 270){
			current_turn_speed = max_turn_speed;
		}
		//top edge
		else if(center.Y() > y_max - BORDER && angle <= 90){
			current_turn_speed = max_turn_speed;
		}
		else if(center.Y() > y_max - BORDER && angle >= 270){
			current_turn_speed = -max_turn_speed;
		}
//		if (hitEdge) {
//			locationTarget = null;
//		}
	}
	
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			new Point(-10, -20, true),
			new Point(0, 20, true),
			new Point(10, -20, true),
		};
		return points;
	}
	
	public Point[] generateEmptyPoints(){
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
		ArrayList<Starship> shipsList = null;
		for (int i = 0; i < game.playerList.size(); i++) {
			if (this.team.equals(game.playerList.get(i).team)) {
				shipsList = game.playerList.get(i).visibleShips;
				break;
			}
		}
		if (shipsList == null) shipsList = game.getAllShips();
		ArrayList<Starship> scanned = new ArrayList<Starship>();
		for(int i = 0; i < shipsList.size(); i++){
			if(!shipsList.get(i).equals(this) && (game.distance(center.X(), center.Y(), shipsList.get(i).getX(), shipsList.get(i).getY()) <= scan_range)){
				scanned.add(shipsList.get(i));
			}
		}
		return scanned;
	}
	
	public void moveToLocation(){
		if(locationTarget != null){
			double relativeAngle = game.angleToPoint(this.getX(), this.getY(), locationTarget.X(), locationTarget.Y());
			
			double distance = distance(this.getX(), this.getY(), locationTarget.X(), locationTarget.Y());
			double leftBearing = getTurnDistance(relativeAngle, true);
			double rightBearing = getTurnDistance(relativeAngle, false);
			if (lockPosition == true) {
				if(leftBearing < max_turn_speed || rightBearing < max_turn_speed){
					lockPosition = false;
					locationTarget = null;
					current_turn_speed = 0;
				}
				//adjust angle
				else if(leftBearing <= rightBearing){ //turn left
					current_turn_speed = Math.min((Math.round(relativeAngle) - angle + 3600) % 360, max_turn_speed);
				}
				else{ //turn right
					current_turn_speed = Math.max(-((angle - Math.round(relativeAngle) + 3600) % 360), -max_turn_speed);
				}
				//Missileship and similar thinks that drifting like crazy is cool. It's not.
				targeted_velocity = 0;
			}
			else if (lockPosition == false) {
				if (distance > 50) {
					if(this instanceof Missileship || this instanceof BasicPod){
						targeted_velocity = max_velocity / 8;
					}
					else{
						targeted_velocity = max_velocity;
					}
					
					//If the ship is within 1 angle of the proper movement angle, don't turn in order to save wobbling headaches
					double howFarOff = Math.abs(this.move_angle - relativeAngle);
					double slowShipHowFarOff = Math.abs(this.angle - relativeAngle);
					if (howFarOff <= 1 || howFarOff >= 359) {
					//if(Math.min(leftBearing, rightBearing) < 2){
						current_turn_speed = 0;
						targeted_velocity = max_velocity;
					}
					else if ((this instanceof Missileship || this instanceof BasicPod) && (slowShipHowFarOff <= 1 || slowShipHowFarOff >= 359)) {
						targeted_velocity = max_velocity;
						move_angle = angle;
					}
					else if (leftBearing <= rightBearing){ //turn left
						current_turn_speed = Math.min(max_turn_speed, (Math.round(relativeAngle) - this.angle + 360) % 360);
					}
					else{ //turn right
						current_turn_speed = Math.max(-max_turn_speed, -((this.angle - Math.round(relativeAngle) + 360) % 360));
					}
				}
				else{
					locationTarget = null;
					targeted_velocity = 0;
				}
			}
		}
		else{
			current_turn_speed = 0;
			targeted_velocity = 0;
			if(attackMove == false){
				attackMove = true;
			}
		}
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
		radar_range = 300;
	}
	
	public void shipTurrets(){
		
	}
	
	public void setScreenBounds(double[] bounds){
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
	public double getTurnDistance(double relativeAngle, boolean toLeft){
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
	public double getClosestBearing(Starship s){
		double relativeAngle = game.angleToPoint(center.X(), center.Y(), s.getX(), s.getY());
		double leftBearing = getTurnDistance(relativeAngle, true);
		double rightBearing = getTurnDistance(relativeAngle, false);
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
		double glide_turn = max_turn_speed / 1.95;
		angle += current_turn_speed;
		angle = game.normalizeAngle(angle);
		double leftBearing = 0;
		double rightBearing = 0;
		if(angle >= move_angle){
			leftBearing = angle - move_angle;
			rightBearing = 360 - angle + move_angle;
		}
		else if(angle < move_angle){
			rightBearing = move_angle - angle;
			leftBearing = 360 - move_angle + angle;
		}
		//update move angle
		if(Math.min(leftBearing, rightBearing) < glide_turn * 1.1){
			move_angle = angle;
		}
		else if(leftBearing <= rightBearing){
			move_angle += glide_turn;
		}
		else if(rightBearing < leftBearing){
			move_angle -= glide_turn;
		}
		move_angle = game.normalizeAngle(move_angle);
	}
	
	
	public double normalizeValue(double i){
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
	
	public double getAngle() {
		return angle;
	}
	
	public void setAngle(double newangle) {
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
	
	public double getTSpeed(){
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
	
	public boolean isSelected() {
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
	
	public int getRadarRange(){
		return radar_range;
	}
	
	public int getScanRange(){
		return scan_range;
	}
	
	public int getHaloSize(){
		return haloSize;
	}
	
	
    public double distance(double x1, double y1, double x2, double y2){
    	return Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1),2));
    }

	public Point getSpawnPoint() {
		return spawnPoint;
	}
	
	public void setLockPosition(boolean b){
		lockPosition = b;
	}
	
	public void setAttackMove(boolean b){
		attackMove = b;
	}
	
	//This boolean is set to true if the target was acquired by a direct right click.
	//If this is true, ships do not lose target as long as target is within radar range
	public void isDirectTarget(boolean b){
		directTarget = b;
	}
	
	public void setControlGroup(int group){
		control_group = group;
	}
	
	public int getControlGroup(){
		return control_group;
	}
	
	//When calling this command, put a valid Starship/Point as one input and null for the other one
	public void addCommand(boolean shift, boolean alt, boolean control, boolean t, Starship newTarget, Point newLocation) {
		commands.add(new Command(shift, alt, control, t, newTarget, newLocation));
	}
	
	public void applyScaleFactor(){
		for (int i = 0; i < points.length; i++) {
			Point p = points[i];
			p.setXOffset(p.getXOffset()*scaleFactor);
			p.setYOffset(p.getYOffset()*scaleFactor);
		}
		for (int i = 0; i < hitbox.length; i++) {
			Point p = hitbox[i];
			p.setXOffset(p.getXOffset()*scaleFactor);
			p.setYOffset(p.getYOffset()*scaleFactor);
		}
	}
}
