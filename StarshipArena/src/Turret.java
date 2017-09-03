import java.util.ArrayList;

import javax.sound.sampled.FloatControl;

public class Turret {
	
	StarshipArena game;
	Starship owner;
	String team;
	
	//turret firing variables
	int cooldown;
	int current_cooldown = 0;
	int spread;
	int accuracy;
	int scan_range;
	double projectile_damage;
	int projectile_speed;
	int projectile_lifetime;
	int projectile_type;
	Point center;
	//turret turning variables
	int turn_speed;
	double angle;
	double xOff = 0;
	double yOff = 0;
	double angleOff = 0;
	boolean autoAiming = false;
	int angle_offset = 0;
	
	//Sound effects
//	Clip clip;
//	AudioInputStream weapon;
	//TODO: Someone should make this function call this() instead of literally copy pasting the other constructor
	//TODO: That would clean up code a bit.
	Turret(StarshipArena mygame, Starship newowner, String myteam, double spawnx, double spawny, double newangle, double newdamage, int newcooldown, int newspread, int newaccuracy, int newscanrange, int newspeed, int newlifetime, int newid){
		game = mygame;
		owner = newowner;
		team = myteam;
		center = new Point(spawnx, spawny);
		angle = newangle;
		cooldown = newcooldown;
		spread = newspread;
		accuracy = newaccuracy;
		scan_range = newscanrange;
		projectile_damage = newdamage / 4;
		projectile_speed = newspeed;
		projectile_lifetime = newlifetime / projectile_speed;
		projectile_type = newid;
		
//		try {
//			if (this.projectile_textureId < 3)
//				weapon = AudioSystem.getAudioInputStream(new File("sounds/effects/plasma.wav"));
//			else if (this.projectile_textureId == 3)
//				weapon = AudioSystem.getAudioInputStream(new File("sounds/effects/sd_emgv7.wav"));
//			else
//				weapon = AudioSystem.getAudioInputStream(new File("sounds/effects/missile.wav"));
//				
//			clip = AudioSystem.getClip();
//			clip.open(weapon);
//			if (this.projectile_textureId < 3) {
//				FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
//				gainControl.setValue(-20.0f); // Reduce volume by a number of decibels.
//			}
//			else if (this.projectile_textureId == 3) {
//				FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
//				gainControl.setValue(-15.0f); // Reduce volume by a number of decibels.
//			}
//			else {
//				FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
//				gainControl.setValue(-12.0f); // Reduce volume by a number of decibels.
//			}
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	Turret(StarshipArena mygame, Starship newowner, String myteam, double spawnx, double spawny, double newangle, double newdamage, int newcooldown, int newspread, int newaccuracy, int newscanrange, int newspeed, int newlifetime, int newid, int newangle_offset, boolean autoaim){
		game = mygame;
		owner = newowner;
		team = myteam;
		center = new Point(spawnx, spawny);
		angle = (newangle + newangle_offset) % 360;
		cooldown = newcooldown;
		spread = newspread;
		accuracy = newaccuracy;
		scan_range = newscanrange;
		projectile_damage = newdamage / 4;
		projectile_speed = newspeed;
		projectile_lifetime = newlifetime / projectile_speed;
		projectile_type = newid;
		autoAiming = autoaim;
		angle_offset = newangle_offset;
		
//		try {
//			if (this.projectile_textureId < 3)
//				weapon = AudioSystem.getAudioInputStream(new File("sounds/effects/plasma.wav"));
//			else if (this.projectile_textureId == 3)
//				weapon = AudioSystem.getAudioInputStream(new File("sounds/effects/sd_emgv7.wav"));
//			else
//				weapon = AudioSystem.getAudioInputStream(new File("sounds/effects/missile.wav"));
//				
//			clip = AudioSystem.getClip();
//			clip.open(weapon);
//			if (this.projectile_textureId < 3) {
//				FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
//				gainControl.setValue(-20.0f); // Reduce volume by a number of decibels.
//			}
//			else if (this.projectile_textureId == 3) {
//				FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
//				gainControl.setValue(-15.0f); // Reduce volume by a number of decibels.
//			}
//			else {
//				FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
//				gainControl.setValue(-12.0f); // Reduce volume by a number of decibels.
//			}
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	public void setOffset(double newx, double newy) {
		setOffset(newx, newy, 0);
	}
	public void setOffset(double newx, double newy, double newAngle){
		xOff = newx;
		yOff = newy;
		angleOff = newAngle;
	}
	
	//TODO Lag is possibly caused by the need for update() to for-loop through every single enemy ship.
	public void update(){
		if(current_cooldown == 0){
			ArrayList<Starship> enemyShips = getEnemyShips();
			int relativeAngle;
			for (int i = 0; i < enemyShips.size(); i++) {
				relativeAngle = game.angleToPoint(center.X(), center.Y(), enemyShips.get(i).getX(), enemyShips.get(i).getY());
				if(Math.abs(relativeAngle - angle) < spread){
					boolean fired = false;
					if (!autoAiming) {
						fire(angle);
						fired = true;
					}
					//fire projectile not based on turret angle
					else if (enemyShips.get(i).equals(owner.target) || spread < 180) {
						fire(relativeAngle);
						fired = true;
					}
					if (fired == true) {
						current_cooldown = cooldown;
						break;
					}
				}
			}
		}
		else{
			current_cooldown--;
		}
	}
	
	public void fire(double newAngle){
		//Get position of the center of the camera, to determine distance from the sound event
		int cameraX = game.CURR_X + game.CAMERA_WIDTH / 2;
		int cameraY = game.CURR_Y + game.CAMERA_HEIGHT / 2;
		//This formula decrease the volume the further away the player is from the weapon event, but increase volume for high levels of zoom
		float dbDiff = (float)(game.distance(cameraX, cameraY, center.X(), center.Y()) / game.CAMERA_WIDTH * -20 + 10000 / game.CAMERA_WIDTH);
		//launch plasma or mgun
		if (projectile_type == 1 || projectile_type == 2 || projectile_type == 3 || projectile_type == 5 || projectile_type == 6) {
			new Projectile(game, owner, team, center.X(), center.Y(), projectile_damage, (newAngle + angleOff + 360) % 360, accuracy, projectile_speed, projectile_lifetime, projectile_type);
			//plasma sfx
			if (projectile_type < 3) {
				for (int i = 0; i < 5; i++) {
					if (!game.soundEffects[i].isRunning()) {
						FloatControl gainControl = (FloatControl) game.soundEffects[i].getControl(FloatControl.Type.MASTER_GAIN);
						gainControl.setValue(Math.max(-80, Math.min(6, game.PLASMA_DB + dbDiff))); // Increase volume by a number of decibels.
						game.soundEffects[i].setFramePosition(0);
						game.soundEffects[i].start();
						
						break;
					}
				}
			}
			//mgun sfx
			else if (projectile_type == 3) {
				for (int i = 5; i < 10; i++) {
					if (game.soundEffects[i].getFramePosition() > 1000 || !game.soundEffects[i].isRunning()) {
						FloatControl gainControl = (FloatControl) game.soundEffects[i].getControl(FloatControl.Type.MASTER_GAIN);
						gainControl.setValue(Math.max(-80, Math.min(6, game.MGUN_DB + dbDiff))); // Increase volume by a number of decibels.
						game.soundEffects[i].setFramePosition(0);
						game.soundEffects[i].start();
						break;
					}
				}
			}
		}
		//launch missile and sfx
		else if (projectile_type == 4){
			new Missile(game, owner, team, center.X(), center.Y(), projectile_damage, (newAngle + angleOff + 360) % 360, accuracy, projectile_speed, projectile_lifetime, projectile_type);
			for (int i = 10; i < 15; i++) {
				if (!game.soundEffects[i].isRunning()) {
					FloatControl gainControl = (FloatControl) game.soundEffects[i].getControl(FloatControl.Type.MASTER_GAIN);
					gainControl.setValue(Math.max(-80, Math.min(6, game.MISSILE_DB + dbDiff))); // Increase volume by a number of decibels.
					game.soundEffects[i].setFramePosition(0);
					game.soundEffects[i].start();
					break;
				}
				else if (game.soundEffects[i].getFramePosition() < 500) {
					break;
				}
			}
		}
	}
	
	public ArrayList<Starship> getEnemyShips(){
		ArrayList<Starship> allShips = game.getAllShips();
		ArrayList<Starship> enemyShips = new ArrayList<>();
		if(team == "none"){
			for (int i = 0; i < allShips.size(); i++) {
				if(game.distance(center.X(), center.Y(), allShips.get(i).getX(), allShips.get(i).getY()) <= scan_range && game.isVisible(allShips.get(i), team)){
					enemyShips.add(allShips.get(i));
				}
			}
		}
		else {
			for (int i = 0; i < allShips.size(); i++) {
				if(!allShips.get(i).getTeam().equals(team) && game.distance(center.X(), center.Y(), allShips.get(i).getX(), allShips.get(i).getY()) <= scan_range && game.isVisible(allShips.get(i), team)){
					enemyShips.add(allShips.get(i));
				}
			}
		}
		return enemyShips;
	}
	
	public double getAngle(){
		return angle;
	}
	
	public void setAngle(double newangle){
		angle = (newangle + angle_offset) % 360;
	}
	
	public void setCenter(Point p){
		center = p;
	}
	
	public void setX(double newx){
		center.setX(newx);
	}
	
	public void setY(double newy){
		center.setY(newy);
	}
	
	public double getXOffset(){
		return xOff;
	}
	
	public double getYOffset(){
		return yOff;
	}

}
