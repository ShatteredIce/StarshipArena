package entities;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.FloatControl;

import mainframe.Point;
import mainframe.StarshipArena;
import rendering.Model;
import rendering.Texture;

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
	public Point center;
	Point[] points;
	public ArrayList<Starship> pierced = new ArrayList<Starship>();
	
	public double damage;
	public double angle;
	public double speed;
	public double lifetime;
	public double current_lifetime = 0;
	public int texId;
	
	//Bonuses:
	int SPLASH = 8;
	int PIERCING = 1;
	
	public boolean splash = false;
	public boolean piercing = false;
	
	public int splashSize = 300;
	
	Projectile(StarshipArena mygame, Starship newowner, String myteam, double spawnx, double spawny, double newdamage,
			double spawnangle, int accuracy, double newspeed, double newlifetime, int id){
		
		this (mygame, newowner, myteam, spawnx, spawny, newdamage, spawnangle, accuracy, newspeed, newlifetime, id, 0);
	}
	
	
	//Bonuses for Projectiles work similarly to bonuses for turrets
	Projectile(StarshipArena mygame, Starship newowner, String myteam, double spawnx, double spawny, double newdamage,
			double spawnangle, int accuracy, double newspeed, double newlifetime, int id, int bonuses){
		
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
		if (bonuses % SPLASH != bonuses) {
			splash = true;
			bonuses %= SPLASH;
		}
		if (bonuses % PIERCING != bonuses) {
			piercing = true;
			bonuses %= PIERCING;
		}
		game.addProjectile(this);
	}
	
	
	public boolean setPoints(){
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
		if(updateLifetime()){
			return true;
		}
		else{
			destroy();
			return false;
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
		if (random.nextInt(100) >= accuracy) {
			int randomized_angle = random.nextInt(8);
			int direction = random.nextInt(2);
			if(direction == 0){
				angle += randomized_angle;
			}
			else{
				angle -= randomized_angle;
			}
		}
	}
	
	public Point[] generatePoints(){
		Point[] points = null;
		//plasma
		if(texId == 0 || texId == 1 || texId == 2){
			points = new Point[]{
				new Point(-7, 14, true),
				new Point(-7, -14, true),
				new Point(7, 14, true),
				new Point(7, -14, true),
			};
		}
		//mgun
		else if(texId == 3){
			points = new Point[]{
				new Point(-2, 10, true),
				new Point(-2, -10, true),
				new Point(2, 10, true),
				new Point(2, -10, true),
			};
		}
		//missile
		else if (texId == 4) {
			points = new Point[]{
				new Point(-16, 20, true),
				new Point(-16, -20, true),
				new Point(16, 20, true),
				new Point(16, -20, true),
			};
		}
		//laser
		else if (texId == 5 || texId == 6) {
			points = new Point[]{
				new Point(-8, 34, true),
				new Point(-8, -180, true),
				new Point(8, 34, true),
				new Point(8, -180, true),
			};
		}
		//Sniper shot
		else if(texId == 7){
			points = new Point[]{
				new Point(-2, 200, true),
				new Point(-2, -200, true),
				new Point(2, 200, true),
				new Point(2, -200, true),
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
		//TODO Some weapons are using other weapon's textures right now.
		else if (texId == 5){
			tex1.bind();
		}
		else if (texId == 6){
			tex2.bind();
		}
		else if (texId == 7) {
			tex3.bind();
		}
		else{
			tex0.bind();
		}
	}
	
	public void display(){
		setTexture();
		model.render(vertices);
	}
	
	public void destroy(){
		model.destroy();
		game.removeProjectile(this);
		if (texId < 3) new Explosion(game, center.X(), center.Y(), 40);
		else if (texId == 3) new Explosion(game, center.X(), center.Y(), 20);
		else if (texId == 7) {
			new Explosion(game, center.X(), center.Y(), 120);
			for (int i = 15; i < 20; i++) {
				if (game.mute) break;
				if (!game.soundEffects[i].isRunning()) {
					double cameraX = game.viewX + game.cameraWidth / 2;
					double cameraY = game.viewY + game.cameraHeight / 2;
					//This formula decrease the volume the further away the player is from the weapon event, but increase volume for high levels of zoom
					float dbDiff = (float)(game.distance(cameraX, cameraY, center.X(), center.Y()) / game.cameraWidth * -20 + 10000 / game.cameraWidth);
					FloatControl gainControl = (FloatControl) game.soundEffects[i].getControl(FloatControl.Type.MASTER_GAIN);
					gainControl.setValue(Math.max(-80, Math.min(6, game.HITEX_DB + dbDiff))); // Increase volume by a number of decibels.
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
	
	public int getType(){
		return texId;
	}
}