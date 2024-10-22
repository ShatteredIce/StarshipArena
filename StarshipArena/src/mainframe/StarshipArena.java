package mainframe;
//Starship Arena - Created on 1/11/17 by Nathan Purwosumarto
//Example of LWJGL 3, displays ships that move randomly in the window

//ANGLE SYSTEM (DO NOT DELETE THESE, DUJIN NEEDS THESE FOR REFERENCE):
//I find these comments very useful, will not delete
//			 0
//		  90 + 270
//		    180
//Up is 0 degrees, and increases counter clockwise
//Standard/Java angle system:
//
//			90
//		 180 + 0
//		   270
//Right is 0 degrees, and increases counter clockwise

/**
 * USEFUL SHORTCUTS/FUNCTIONS
 * Right-clicking on a method in Outline, then try Open Call Hierarchy and Open Type Hierarchy. Useful for determining
 * inheritance and function redundancy
 * Press CTRL+SHIFT+L for a list of shortcuts, and press it twice to search for shortcuts and/or change them
 * 
 */

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import entities.BasicPod;
import entities.Battleship;
import entities.Explosion;
import entities.Fighter;
import entities.Interceptor;
import entities.MachineGunPod;
import entities.Missile;
import entities.MissilePod;
import entities.Missileship;
import entities.Planet;
import entities.PlanetLaser;
import entities.PlanetRadar;
import entities.Projectile;
import entities.Sniper;
import entities.Starship;
import entities.Transport;
import entities.Wallship;
import rendering.BitmapFontLetter;
import rendering.GameRenderer;
import rendering.Tile;
import rendering.Window;

import java.nio.*;
import java.time.Instant;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.sound.sampled.*;

import java.io.File;
import java.io.IOException;

public class StarshipArena {
	
	static GameRenderer gamerenderer;
	
	Window window;
	
	int WINDOW_WIDTH = 1300;
	int WINDOW_HEIGHT = 900;
	//TODO Downsized window to see if it works on Chromebook now
//	int WINDOW_WIDTH = 1011;
//	int WINDOW_HEIGHT = 700;
	
	int windowXOffset;
	int windowYOffset;
	
	//Game scale: In future, it can be changed in an options menu
	//TODO Reduce the default numbers by 10x, then set default levelScale to 10
	//Right now game scale is causing lag, I suspect because of the double multiplication. Thus, try ^
	public double levelScale = 0.5;
	
	
	public double WORLD_WIDTH = 260000 * levelScale;
	public double WORLD_HEIGHT = 180000 * levelScale;
    
    //TODO Get rid of all the hardcoded dimensions
	public double currentMin_X = 0;
	public double currentMin_Y = 0;
	public double currentMax_X = WORLD_WIDTH;
	public double currentMax_Y = WORLD_HEIGHT;
    
    public double spaceMin_X = 0;
    public double spaceMin_Y = 0;
    public double spaceMax_X = WORLD_WIDTH;
    public double spaceMax_Y = WORLD_HEIGHT;
    
    //previous camera view saved when zooming into planet
    public double spaceView_X = 0;
    public double spaceView_Y = 0;
    public double spaceViewWidth = 2600;
    public double spaceViewHeight = 1800;
	
    public double viewX = 0;
	public double viewY = 0;
	public double cameraSpeed = 10;
	public double cameraWidth = 2600;
	public double cameraHeight = 1800;
	int zoomLevel = 3;
	
	int gameState = 1;
	int SLOW = 1;
	int NUM_LEVELS = 10;
	
	int currentLevel = 0;
	final int endLevelDelay = 50;
	int endLevelTimer = 0;
	
	boolean staticFrame = false;
	boolean windowSelected = true;
    
    boolean panLeft = false;
    boolean panRight = false;
    boolean panUp = false;
    boolean panDown = false;
    
    boolean shiftPressed = false;
    boolean tPressed = false;
    boolean controlPressed = false;
    boolean altPressed = false;
    boolean f1Pressed = false;
    boolean lPressed = false;
    
    boolean shipTracking = false;
    
    DoubleBuffer oldMouseX;
    DoubleBuffer oldMouseY;
    DoubleBuffer newMouseX;
    DoubleBuffer newMouseY;
    
    public final static int FIGHTER_COST = 5;
    public final static int INTERCEPTOR_COST = 20;
    public final static int MISSILESHIP_COST = 40;
    public final static int WALLSHIP_COST = 30;
    public final static int SNIPER_COST = 60;
    public final static int BATTLESHIP_COST = 80;
    
    int PROXIMITY_SIZE = 300;
    
    int planetDisplayBorder = 4500;
    int shipDisplayBorder = 100;
    
    //Sound decibel offsets
    public float PLASMA_DB = -20.0f;
    public float MGUN_DB = -18.0f;
    public float MISSILE_DB = -4.0f;
    public float LASER_DB = -8.0f;
    public float SNIPER_DB = 3.0f;
    public float HITEX_DB = -8.0f;
    public float DEATHEX_DB = 1.0f;
    
	Random random = new Random();
	
	public ArrayList<Starship> ships = new ArrayList<>();
	public ArrayList<Projectile> projectiles = new ArrayList<>();
	public ArrayList<Explosion> explosions = new ArrayList<>();
	public ArrayList<Planet> planets = new ArrayList<>();
	public ArrayList<Tile> backgroundTiles = new ArrayList<>();
	public ArrayList<Player> playerList = new ArrayList<>(); 
	public ArrayList<BitmapFontLetter> text = new ArrayList<>();
	public Clip[] soundEffects = new Clip[50];
	
//	File temp = new File("sounds/music/Earth.wav");
	File temp = new File("sounds/music/Yin & Yang.wav");
	AudioInputStream BGM = AudioSystem.getAudioInputStream(temp);
//	File temp2 = new File("sounds/music/Journey to the Sky.wav");
	File temp2 = new File("sounds/music/Vague.wav");
	AudioInputStream menuBGM = AudioSystem.getAudioInputStream(temp2);
	Clip gameMusic;
	Clip menuMusic;
	
	public boolean mute = false;
	boolean fog = false;
	
	//Damage multipliers array;
	double[][] damageMultipliers = new double[7][8];
	

	Sidebar sidebar;
	//Would this work:
//	Button levelSelectButton = new Button(550 + (1300 - WINDOW_WIDTH) / 2, 555 - (900 - WINDOW_HEIGHT) / 2, 760 - (1300 - WINDOW_WIDTH) / 2, 465 + (900 - WINDOW_HEIGHT) / 2);
	Button levelSelectButton = new Button(550, 555, 760, 465);
	Button[] levelButtons = {
		new Button(300, 650, 400, 550),
		new Button(500, 650, 600, 550),
		new Button(700, 650, 800, 550),
		new Button(900, 650, 1000, 550),
		new Button(300, 450, 400, 350),
		new Button(500, 450, 600, 350),
		new Button(700, 450, 800, 350),
		new Button(900, 450, 1000, 350),
		new Button(300, 250, 400, 150),
		new Button(500, 250, 600, 150),
		new Button(700, 250, 800, 150),
		new Button(900, 250, 1000, 150),
	};
	Button controlsButton = new Button(550, 435, 760, 345);
	Button creditsButton = new Button(550, 315, 760, 225);
	
	double boxSelect_startx;
	double boxSelect_starty;
	boolean boxSelectCurrent = false;
	
	Button settingsButton = new Button(WINDOW_WIDTH - 50, WINDOW_HEIGHT - 2, WINDOW_WIDTH - 2, WINDOW_HEIGHT - 50);
	Button audioButton = new Button(WINDOW_WIDTH - 102, WINDOW_HEIGHT - 2, WINDOW_WIDTH - 54, WINDOW_HEIGHT - 50);
	
	Button planetBuyButton = new Button(WINDOW_WIDTH - 380, 80, WINDOW_WIDTH - 280, 20);
	Button planetIndustryButton = new Button(WINDOW_WIDTH - 250, 80, WINDOW_WIDTH - 150, 20);
	Button planetEconomyButton = new Button(WINDOW_WIDTH - 120, 80, WINDOW_WIDTH - 20, 20);
		
	Button nextLevelButton = new Button(WINDOW_WIDTH / 2 - 70, WINDOW_HEIGHT / 2 + 5, WINDOW_WIDTH / 2 + 70, WINDOW_HEIGHT / 2 - 25);
	Button restartLevelButton = new Button(WINDOW_WIDTH / 2 - 70, WINDOW_HEIGHT / 2 - 35, WINDOW_WIDTH / 2 + 70, WINDOW_HEIGHT / 2 - 65);
	
	Player player = new Player(this, "blue");
	Enemy enemy = new Enemy(this, new Player(this, "red"));
	
	//This empty constructor exists simply so I can do throws declarations. I have no idea how else to do it.
    public StarshipArena() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
    	
    }
	
	public void run() {

		init();
		loop();

//		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window.getWindowHandle());
		glfwDestroyWindow(window.getWindowHandle());

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable
		
		window = new Window(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.createWindow("Starship Arena [WIP]");
		windowXOffset = window.getXOffset();
		windowYOffset = window.getYOffset();
//		System.out.println(windowXOffset);
//		System.out.println(windowYOffset);
		
		//Set up damage multipliers array:
		for (int i = 0; i < damageMultipliers.length; i++) {
			Arrays.fill(damageMultipliers[i], 1);
		}
		//Fighters are vulnerable to missiles
		damageMultipliers[1][4] = 2;
		//Interceptors are resistant to missiles
		damageMultipliers[2][4] = 0.25;
		//Missileships are resistant to plasma (all three colors)
		damageMultipliers[3][0] = 0.5; damageMultipliers[3][1] = 0.5; damageMultipliers[3][2] = 0.5;
		damageMultipliers[3][3] = 2;
		//Missileships are vulnerable to snipers
		damageMultipliers[3][7] = 2;
		//Wallships are vulnerable to snipers
		damageMultipliers[4][7] = 3;
		//Wallships are resistant to missiles
		damageMultipliers[4][4] = 0.25;
		
		try {
			gameMusic = AudioSystem.getClip();
			menuMusic = AudioSystem.getClip();
			menuMusic.open(menuBGM);
			if (!mute)
				menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
			gameMusic.open(BGM);
			for (int i = 0; i < 5; i++) {
				soundEffects[i] = AudioSystem.getClip();
				AudioInputStream plasmaSfx = AudioSystem.getAudioInputStream(new File("sounds/effects/plasma.wav"));
				soundEffects[i].open(plasmaSfx);
//				FloatControl gainControl = (FloatControl) soundEffects[i].getControl(FloatControl.Type.MASTER_GAIN);
//				gainControl.setValue(-20.0f); // Reduce volume by a number of decibels.
			}
			for (int i = 5; i < 10; i++) {
				soundEffects[i] = AudioSystem.getClip();
				AudioInputStream mgunSfx = AudioSystem.getAudioInputStream(new File("sounds/effects/sd_emgv7.wav"));
				soundEffects[i].open(mgunSfx);
//				FloatControl gainControl = (FloatControl) soundEffects[i].getControl(FloatControl.Type.MASTER_GAIN);
//				gainControl.setValue(-18.0f); // Reduce volume by a number of decibels.
				
			}
			for (int i = 10; i < 15; i++) {
				soundEffects[i] = AudioSystem.getClip();
				AudioInputStream missileSfx = AudioSystem.getAudioInputStream(new File("sounds/effects/missile.wav"));
				soundEffects[i].open(missileSfx);
//				FloatControl gainControl = (FloatControl) soundEffects[i].getControl(FloatControl.Type.MASTER_GAIN);
//				gainControl.setValue(-4.0f); // Reduce volume by a number of decibels.
			}
			for (int i = 15; i < 20; i++) {
				soundEffects[i] = AudioSystem.getClip();
				AudioInputStream mExplosionSfx = AudioSystem.getAudioInputStream(new File("sounds/effects/ex_med5.wav"));
				soundEffects[i].open(mExplosionSfx);
//				FloatControl gainControl = (FloatControl) soundEffects[i].getControl(FloatControl.Type.MASTER_GAIN);
//				gainControl.setValue(-8.0f); // Reduce volume by a number of decibels.
			}
			for (int i = 20; i < 25; i++) {
				soundEffects[i] = AudioSystem.getClip();
				AudioInputStream deathSfx = AudioSystem.getAudioInputStream(new File("sounds/effects/ex_with_debri.wav"));
				soundEffects[i].open(deathSfx);
//				FloatControl gainControl = (FloatControl) soundEffects[i].getControl(FloatControl.Type.MASTER_GAIN);
//				gainControl.setValue(1.0f); // Increase volume by a number of decibels.
			}
			for (int i = 25; i < 30; i++) {
				soundEffects[i] = AudioSystem.getClip();
				AudioInputStream laserSfx = AudioSystem.getAudioInputStream(new File("sounds/effects/pulse_laser.wav"));
				soundEffects[i].open(laserSfx);
//				FloatControl gainControl = (FloatControl) soundEffects[i].getControl(FloatControl.Type.MASTER_GAIN);
//				gainControl.setValue(1.0f); // Increase volume by a number of decibels.
			}
			for (int i = 30; i < 35; i++) {
				soundEffects[i] = AudioSystem.getClip();
				AudioInputStream sniperSfx = AudioSystem.getAudioInputStream(new File("sounds/effects/cannon_fire2.wav"));
				soundEffects[i].open(sniperSfx);
//				FloatControl gainControl = (FloatControl) soundEffects[i].getControl(FloatControl.Type.MASTER_GAIN);
//				gainControl.setValue(1.0f); // Increase volume by a number of decibels.
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window.getWindowHandle(), (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ){
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
//				CAMERA_WIDTH = 2600;
//				CAMERA_HEIGHT = 1800;
//				zoomLevel = 3;
//				CURR_X = 0;
//				CURR_Y = 0;
//				gameState = 1;
			}
			//Figure out which arrow keys, if any, are depressed and tell the loop to pan the camera
			if ( key == GLFW_KEY_LEFT && action == GLFW_PRESS )
				panLeft = true;
			if ( key == GLFW_KEY_LEFT && action == GLFW_RELEASE )
				panLeft = false;
			
			if ( key == GLFW_KEY_RIGHT && action == GLFW_PRESS )
				panRight = true;
			if ( key == GLFW_KEY_RIGHT && action == GLFW_RELEASE )
				panRight = false;
			
			if ( key == GLFW_KEY_UP && action == GLFW_PRESS )
				panUp = true;
			if ( key == GLFW_KEY_UP && action == GLFW_RELEASE )
				panUp = false;
			
			if ( key == GLFW_KEY_DOWN && action == GLFW_PRESS )
				panDown = true;
			if ( key == GLFW_KEY_DOWN && action == GLFW_RELEASE )
				panDown = false;
			
			if ( key == GLFW_KEY_A && action == GLFW_PRESS )
				panLeft = true;
			if ( key == GLFW_KEY_A && action == GLFW_RELEASE )
				panLeft = false;
			
			if ( key == GLFW_KEY_D && action == GLFW_PRESS )
				panRight = true;
			if ( key == GLFW_KEY_D && action == GLFW_RELEASE )
				panRight = false;
			
			if ( key == GLFW_KEY_W && action == GLFW_PRESS )
				panUp = true;
			if ( key == GLFW_KEY_W && action == GLFW_RELEASE )
				panUp = false;
			
			if ( key == GLFW_KEY_S && action == GLFW_PRESS )
				panDown = true;
			if ( key == GLFW_KEY_S && action == GLFW_RELEASE )
				panDown = false;
			if ( key == GLFW_KEY_C && action == GLFW_PRESS )
				shipTracking = !shipTracking;
			
			//Stop a ship's movement
			if( key == GLFW_KEY_Z && action == GLFW_RELEASE ){
				for (int s = 0; s < ships.size(); s++) {
					if(ships.get(s).isSelected() && ships.get(s).getTeam().equals(player.getTeam())){
						ships.get(s).commands.clear();
						ships.get(s).locationTarget = null;
						ships.get(s).setAttackMove(false);
						ships.get(s).setLockPosition(false);
						ships.get(s).isDirectTarget(false);
						
					}
				}
			}
			
			if ( key == GLFW_KEY_L && action == GLFW_PRESS )
				if (player.getSelectedPlanet() != null) player.getSelectedPlanet().setLoop(!player.getSelectedPlanet().looping);
//			if ( key == GLFW_KEY_L && action == GLFW_RELEASE )
//				lPressed = false;
			
			//TODO Remove this testing thing for proximity groups after testing concludes
			if( key == GLFW_KEY_P && action == GLFW_RELEASE ){
				for (int s = 0; s < ships.size(); s++) {
					if(ships.get(s).isSelected()){
						ArrayList<Starship> temp = new ArrayList<Starship>();
						temp.add(ships.get(s));
						proximityGroup(temp, ships, ships.get(s));
						for (int i = 0; i < temp.size(); i++) {
							temp.get(i).setSelected(true);
						}
					}
				}
			}
			
			if ( key == GLFW_KEY_LEFT_SHIFT && action == GLFW_PRESS )
				shiftPressed = true;
			if ( key == GLFW_KEY_LEFT_SHIFT && action == GLFW_RELEASE )
				shiftPressed = false;
			if ( key == GLFW_KEY_RIGHT_SHIFT && action == GLFW_PRESS )
				shiftPressed = true;
			if ( key == GLFW_KEY_RIGHT_SHIFT && action == GLFW_RELEASE )
				shiftPressed = false;
			if ( key == GLFW_KEY_T && action == GLFW_PRESS )
				tPressed = true;
			if ( key == GLFW_KEY_T && action == GLFW_RELEASE )
				tPressed = false;
			if ( key == GLFW_KEY_LEFT_CONTROL && action == GLFW_PRESS )
				controlPressed = true;
			if ( key == GLFW_KEY_LEFT_CONTROL && action == GLFW_RELEASE )
				controlPressed = false;
			if ( key == GLFW_KEY_RIGHT_CONTROL && action == GLFW_PRESS )
				controlPressed = true;
			if ( key == GLFW_KEY_RIGHT_CONTROL && action == GLFW_RELEASE )
				controlPressed = false;
			
			if ( key == GLFW_KEY_LEFT_ALT && action == GLFW_PRESS )
				altPressed = true;
			if ( key == GLFW_KEY_LEFT_ALT && action == GLFW_RELEASE )
				altPressed = false;
			if ( key == GLFW_KEY_RIGHT_ALT && action == GLFW_PRESS )
				altPressed = true;
			if ( key == GLFW_KEY_RIGHT_ALT && action == GLFW_RELEASE )
				altPressed = false;
			if ( key == GLFW_KEY_F1 && action == GLFW_PRESS )
				f1Pressed = true;
			if ( key == GLFW_KEY_F1 && action == GLFW_RELEASE )
				f1Pressed = false;
			
			if ( key == GLFW_KEY_SPACE && action == GLFW_RELEASE ) {
				for (int i = 0; i < planets.size(); i++) {
					planets.get(i).setSelected(false);
				}
				ArrayList<Starship> typesSelected = new ArrayList<>();
				for (int i = 0; i < ships.size(); i++) {
					if (ships.get(i).selected) {
						boolean alreadyHave = false;
						for (int j = 0; j < typesSelected.size(); j++) {
							if (typesSelected.get(j).getClass().equals(ships.get(i).getClass())
									&& typesSelected.get(j).getTeam().equals(ships.get(i).getTeam())) alreadyHave = true;
						}
						if (!alreadyHave) typesSelected.add(ships.get(i));
					}
				}
				//If we already have ships selected, only select ships of the same type
				//Also check if the ships are on the same surface by checking X and Y bounds
				if (typesSelected.size() > 0) {
					for (int i = 0; i < ships.size(); i++) {
						for (int j = 0; j < typesSelected.size(); j++) {
							if (typesSelected.get(j).getClass().equals(ships.get(i).getClass())
									&& typesSelected.get(j).getTeam().equals(ships.get(i).getTeam())
									&& currentMin_Y == ships.get(i).y_min
									&& currentMin_X == ships.get(i).x_min)
								ships.get(i).setSelected(true);
						}
					}
				}
				//If no ships selected, select all allied ships
				else {
					for (int i = 0; i < ships.size(); i++) {
						if (ships.get(i).getTeam().equals(player.getTeam()) && !(ships.get(i) instanceof BasicPod) 
								&& currentMin_Y == ships.get(i).y_min
								&& currentMin_X == ships.get(i).x_min){
							ships.get(i).setSelected(true);
						}
					}
				}
			}
			
			if ( key == GLFW_KEY_MINUS && action == GLFW_PRESS )
				if(gameState == 3){
					updateZoomLevel(true);
				}
			if ( key == GLFW_KEY_EQUAL && action == GLFW_PRESS )
				if(gameState == 3){
					updateZoomLevel(false);
				}
			//Enter/exit planet views
			if ( key == GLFW_KEY_PERIOD && action == GLFW_PRESS )
				if (player.selectedPlanet != null) {
					loadSubLevel(player.selectedPlanet);
				}
			if ( key == GLFW_KEY_COMMA && action == GLFW_PRESS )
				loadSubLevel(null);
			
			
			if ( key == GLFW_KEY_F1 && action == GLFW_RELEASE ){
				if(player.getSelectedPlanet() != null /*&& player.getSelectedPlanet().getTeam().equals(player.getTeam())*/){
					player.getSelectedPlanet().setResources(player.getSelectedPlanet().getResources() + 100);
				}
			}
			if ( key == GLFW_KEY_LEFT_BRACKET && action == GLFW_PRESS ) {
				if (SLOW == 1) SLOW = 20000;
				else if (SLOW == 20000) SLOW = 150000;
			}

			if ( key == GLFW_KEY_RIGHT_BRACKET && action == GLFW_PRESS ) {
				if (SLOW == 150000) SLOW = 20000;
				else if (SLOW == 20000) SLOW = 1;
			}
		
//			if ( key == GLFW_KEY_DOWN && action == GLFW_RELEASE )
//				SLOW = 1;
//			if ( key == GLFW_KEY_UP && action == GLFW_PRESS )
//				SLOW = 5000;
//			if ( key == GLFW_KEY_UP && action == GLFW_RELEASE )
//				SLOW = 1;
			if ( key == GLFW_KEY_0 && action == GLFW_PRESS){
				
				if (player.getSelectedPlanet() != null && player.getTeam().equals(player.getSelectedPlanet().getTeam())) {
					player.getSelectedPlanet().clearBuildOrder();
				}
			}
			if ( key == GLFW_KEY_1 && action == GLFW_PRESS) {
				numberKeyPressed(1);
			}
			if ( key == GLFW_KEY_2 && action == GLFW_PRESS) {
				numberKeyPressed(2);
			}
			if ( key == GLFW_KEY_3 && action == GLFW_PRESS) {
				numberKeyPressed(3);
			}
			if ( key == GLFW_KEY_4 && action == GLFW_PRESS) {
				numberKeyPressed(4);
			}
			if ( key == GLFW_KEY_5 && action == GLFW_PRESS) {
				numberKeyPressed(5);
			}
			if ( key == GLFW_KEY_6 && action == GLFW_PRESS) {
				numberKeyPressed(6);
			}
			
			//Some cheat commands!!! Useful for testing purposes
			//H heals selected ships to full health
			if (key == GLFW_KEY_H && action == GLFW_PRESS)
				for (int i = 0; i < ships.size(); i++) {
					Starship s = ships.get(i);
					if (s.isSelected()) s.current_health = s.max_health;
				}
			//K insta destroyes selected ships
			if (key == GLFW_KEY_K && action == GLFW_PRESS)
				for (int i = 0; i < ships.size(); i++) {
					Starship s = ships.get(i);
					if (s.isSelected()) s.current_health = -1;
				}
			
			if ( key == GLFW_KEY_ENTER && action == GLFW_PRESS) {
				if (gameState != 3) {
					gameState = 3;
					menuMusic.stop();
					gameMusic.setFramePosition(0);
					if (!mute)
						gameMusic.loop(Clip.LOOP_CONTINUOUSLY);
				}
			}
		
		});
		
		//Mouse clicks
		glfwSetMouseButtonCallback (window.getWindowHandle(), (window, button, action, mods) -> {
			DoubleBuffer xpos = BufferUtils.createDoubleBuffer(3);
			DoubleBuffer ypos = BufferUtils.createDoubleBuffer(3);
			glfwGetCursorPos(window, xpos, ypos);
			//check if the mouse click is in the game frame
//			if(xpos.get(0) > windowXOffset && xpos.get(0) < WINDOW_WIDTH + windowXOffset &&
//					ypos.get(0) > windowYOffset && ypos.get(0) < WINDOW_HEIGHT + windowYOffset){
				//convert the glfw coordinate to our coordinate system
				xpos.put(0, Math.min(Math.max(xpos.get(0), windowXOffset), WINDOW_WIDTH + windowXOffset));
				ypos.put(0, Math.min(Math.max(ypos.get(0), windowYOffset), WINDOW_HEIGHT + windowYOffset));
				//relative camera coordinates
				xpos.put(1, getWidthScalar() * (xpos.get(0) - windowXOffset) + viewX);
				ypos.put(1, (getHeightScalar() * (WINDOW_HEIGHT - ypos.get(0) + windowYOffset) + viewY));
				//true window coordinates
				xpos.put(2, xpos.get(0) - windowXOffset);
				ypos.put(2, (WINDOW_HEIGHT - ypos.get(0) + windowYOffset));
				if(gameState == 1){
					if ( button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
						if(settingsButton.isClicked(xpos.get(2), ypos.get(2))){
							System.exit(1);
						}
						else if(audioButton.isClicked(xpos.get(2), ypos.get(2))){
							toggleAudio();
							staticFrame = false;
						}
					}
					else if ( button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE) {
					//System.out.println(xpos.get(1) + " " + ypos.get(1));
						if(levelSelectButton.isClicked(xpos.get(2), ypos.get(2))){
							gameState = 2;
							staticFrame = false;
						}
						else if(controlsButton.isClicked(xpos.get(2), ypos.get(2))){
							ProcessBuilder pb = new ProcessBuilder("notepad.exe", "controls.txt");
							try {
								pb.start();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						else if(creditsButton.isClicked(xpos.get(2), ypos.get(2))){
							ProcessBuilder pb = new ProcessBuilder("notepad.exe", "credits.txt");
							try {
								pb.start();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				else if(gameState == 2){
					if ( button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
						if(settingsButton.isClicked(xpos.get(2), ypos.get(2))){
							gameState = 1;
							staticFrame = false;
							return;
						}
						else if(audioButton.isClicked(xpos.get(2), ypos.get(2))){
							toggleAudio();
							staticFrame = false;
						}
					}
					else if ( button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE) {
						//System.out.println(xpos.get(1) + " " + ypos.get(1));
						for (int i = 0; i < NUM_LEVELS; i++) {
							if(levelButtons[i].isClicked(xpos.get(2), ypos.get(2))){
								gameState = 3;
								loadLevel(i+1);
							}
						}
					}
				}
				//player has beaten or lost the level buttons
				else if(gameState == 4 || gameState == 5){
					if ( button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
						if(settingsButton.isClicked(xpos.get(2), ypos.get(2))){
							gameState = 1;
							
							gameMusic.stop();
							menuMusic.setFramePosition(0);
							if (!mute)
								menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
						
							staticFrame = false;
							return;
						}
						else if(audioButton.isClicked(xpos.get(2), ypos.get(2))){
							toggleAudio();
							staticFrame = false;
						}
					}
					if ( button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE){
						if(nextLevelButton.isClicked(xpos.get(2), ypos.get(2))){
							if (currentLevel + 1 <= NUM_LEVELS) {
								loadLevel(currentLevel + 1);
								gameState = 3;
							}
						}
						else if(restartLevelButton.isClicked(xpos.get(2), ypos.get(2))){
							loadLevel(currentLevel);
							gameState = 3;
						}
					}
				}
				else if(gameState == 3){
					boolean clickedOnSprite = false;
					if ( button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
						if(settingsButton.isClicked(xpos.get(2), ypos.get(2))){
							gameState = 1;
							staticFrame = false;
							gameMusic.stop();
							menuMusic.setFramePosition(0);
							if (!mute)
								menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
							return;
						}
						else if(audioButton.isClicked(xpos.get(2), ypos.get(2))){
							toggleAudio();
						}
						boxSelect_startx = xpos.get(1);
						boxSelect_starty = ypos.get(1);
						oldMouseX = xpos;
						oldMouseY = ypos;
						boxSelectCurrent = true;
					}
					if ( button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE) {
						newMouseX = xpos;
						newMouseY = ypos;
						boxSelectCurrent = false;
						//remove selected planet
						if(player.getSelectedPlanet() != null){
							player.getSelectedPlanet().setSelected(false);
							player.setSelectedPlanet(null);
						}
						//If shift is pressed, we don't unselect currently selected ships
						String shipsControllingTeam = "";
						ArrayList<Starship> selectedUncontrolledShips = new ArrayList<>();
						for (int i = 0; i < ships.size(); i++) {
							Starship s = ships.get(i);
							Point clickCenter = new Point(s.getX() + s.getXOff(), s.getY() + s.getYOff());
							clickCenter.rotatePoint(s.getX(), s.getY(), s.getAngle());
							if (clickCenter.X() < Math.max(newMouseX.get(1), oldMouseX.get(1)) + s.getClickRadius()
									&& clickCenter.X() > Math.min(oldMouseX.get(1), newMouseX.get(1)) - s.getClickRadius()
									&& clickCenter.Y() < Math.max(newMouseY.get(1), oldMouseY.get(1))
									&& clickCenter.Y() > Math.min(oldMouseY.get(1), newMouseY.get(1))
									|| clickCenter.X() < Math.max(newMouseX.get(1), oldMouseX.get(1))
									&& clickCenter.X() > Math.min(oldMouseX.get(1), newMouseX.get(1))
									&& clickCenter.Y() < Math.max(newMouseY.get(1), oldMouseY.get(1)) + s.getClickRadius()
									&& clickCenter.Y() > Math.min(oldMouseY.get(1), newMouseY.get(1)) - s.getClickRadius()) {
								if (shipsControllingTeam.equals("") || s.getTeam().equals(shipsControllingTeam)) {
									shipsControllingTeam = s.getTeam();
									if (!shipsControllingTeam.equals(player.getTeam())) {
										selectedUncontrolledShips.add(s);
										if (!shiftPressed && !controlPressed)
											s.setSelected(false);
									}
									else {
										s.setSelected(true);
										clickedOnSprite = true;
									}
								}
								else if (s.getTeam().equals(player.getTeam())) {
									shipsControllingTeam = s.getTeam();
									s.setSelected(true);
									clickedOnSprite = true;
								}
								else if(!shiftPressed && !controlPressed) s.setSelected(false);
							}
							else if (distance(newMouseX.get(1), newMouseY.get(1), clickCenter.X(), clickCenter.Y()) <= s.getClickRadius()
									|| distance(newMouseX.get(1), oldMouseY.get(1), clickCenter.X(), clickCenter.Y()) <= s.getClickRadius()
									|| distance(oldMouseX.get(1), newMouseY.get(1), clickCenter.X(), clickCenter.Y()) <= s.getClickRadius()
									|| distance(oldMouseX.get(1), oldMouseY.get(1), clickCenter.X(), clickCenter.Y()) <= s.getClickRadius()) {
								if (shipsControllingTeam.equals("") || s.getTeam().equals(shipsControllingTeam)) {
									shipsControllingTeam = s.getTeam();
									if (!shipsControllingTeam.equals(player.getTeam())) {
										selectedUncontrolledShips.add(s);
										if (!shiftPressed && !controlPressed)
											s.setSelected(false);
									}
									else {
										s.setSelected(true);
										clickedOnSprite = true;
									}
								}
								else if (s.getTeam().equals(player.getTeam())) {
									shipsControllingTeam = s.getTeam();
									s.setSelected(true);
									clickedOnSprite = true;
								}
								else if (!shiftPressed && !controlPressed) s.setSelected(false);
							}
							else if (!shiftPressed && !controlPressed) s.setSelected(false);
						}
						if (!clickedOnSprite && selectedUncontrolledShips.size() > 0) {
							for (int i = 0; i < selectedUncontrolledShips.size(); i++) {
								if(isVisible(selectedUncontrolledShips.get(i), player)){
									selectedUncontrolledShips.get(i).setSelected(true);
								}
							}
							clickedOnSprite = true;
						}
						//if we haven't clicked on a ship, check if we clicked on a planet
						if(!clickedOnSprite){
							for (int i = 0; i < planets.size(); i++) {
								Planet p = planets.get(i);
								//allows box select of planet
		//						if (p.getX() < Math.max(newMouseX.get(1), oldMouseX.get(1)) + p.getSize() - 30
		//								&& p.getX() > Math.min(oldMouseX.get(1), newMouseX.get(1)) - p.getSize() + 30
		//								&& p.getY() < Math.max(newMouseY.get(1), oldMouseY.get(1))- 30
		//								&& p.getY() > Math.min(oldMouseY.get(1), newMouseY.get(1))+ 30
		//								|| p.getX() < Math.max(newMouseX.get(1), oldMouseX.get(1))- 30
		//								&& p.getX() > Math.min(oldMouseX.get(1), newMouseX.get(1))+ 30
		//								&& p.getY() < Math.max(newMouseY.get(1), oldMouseY.get(1)) + p.getSize() - 30
		//								&& p.getY() > Math.min(oldMouseY.get(1), newMouseY.get(1)) - p.getSize() + 30) {
		//							System.out.println("check1");
		//							if(player.getSelectedPlanet() != null){
		//								player.getSelectedPlanet().setSelected(false);
		//							}
		//							player.setSelectedPlanet(p);
		//							p.setSelected(true);
		//							clickedOnSprite = true;
		//							break;
		//						}
								if (Math.abs(newMouseX.get(2) - oldMouseX.get(2)) < 5 && Math.abs(newMouseY.get(2) - oldMouseY.get(2)) < 5 && (distance(newMouseX.get(1), newMouseY.get(1), p.getX(), p.getY()) <= p.getSize() - 30
										|| distance(newMouseX.get(1), oldMouseY.get(1), p.getX(), p.getY()) <= p.getSize() - 30
										|| distance(oldMouseX.get(1), newMouseY.get(1), p.getX(), p.getY()) <= p.getSize() - 30
										|| distance(oldMouseX.get(1), oldMouseY.get(1), p.getX(), p.getY()) <= p.getSize() - 30)) {
									if(player.getSelectedPlanet() != null){
										player.getSelectedPlanet().setSelected(false);
									}
									player.setSelectedPlanet(p);
									p.setSelected(true);
									clickedOnSprite = true;
									break;
								}
							}
						}
					}
					if ( button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS) {
						//For each ship, if it is selected and owned by player...
						for (int i = 0; i < ships.size(); i++) {
							Starship s = ships.get(i);
							//TODO Enemy ships can be given orders (testing)
							if (s.isSelected() /*&& s.getTeam().equals(player.getTeam())*/) {
								//If shift is not pressed, player is not queueing commands. Clear the current queue
								if (!shiftPressed) s.commands.clear();
								ArrayList<Starship> visibleShips = player.visibleShips;
								Starship targetShip = null;
								//Check if the right click was on top of a ship
								for (int j = 0; j < visibleShips.size(); j++) {
									Starship ship = visibleShips.get(j);
									if (ship.team == player.team) continue;
									Point clickCenter = new Point(ship.getX() + ship.getXOff(), ship.getY() + ship.getYOff());
									clickCenter.rotatePoint(ship.getX(), ship.getY(), ship.getAngle());
									if (distance(xpos.get(1), ypos.get(1), clickCenter.X(), clickCenter.Y()) < ship.getClickRadius()) {
										targetShip = ship;
										break;
									}
								}
								//If the click was not on a ship, move to location. Else, attack the clicked ship
								if (targetShip == null) {
//									s.addCommand(shiftPressed, altPressed, controlPressed, tPressed, null, new Point(Math.max(Math.min(xpos.get(1), WORLD_WIDTH), 0), Math.max(Math.min(ypos.get(1), WORLD_HEIGHT), 0)));
									s.addCommand(shiftPressed, altPressed, controlPressed, tPressed, null, new Point(Math.max(Math.min(xpos.get(1), currentMax_X - s.getClickRadius()), currentMin_X + s.getClickRadius()), Math.max(Math.min(ypos.get(1), currentMax_Y - s.getClickRadius()), currentMin_Y + s.getClickRadius())));
								}
								else {
									s.addCommand(shiftPressed, false, controlPressed, false, targetShip, null);
									s.target = null;
									s.locationTarget = null;
									
								}
							}	
							//System.out.println(xpos.get(0) + ", " + ypos.get(0));
						}
					}
				}
//			}
			else{
				if(boxSelectCurrent){
					boxSelectCurrent = false;
				}
			}
			
		});
		
		glfwSetScrollCallback(window.getWindowHandle(), (window, xoffset, yoffset) -> {
			//If they scrolled up, zoom in. If they scrolled down, zoom out.
			if (yoffset > 0) {
				updateZoomLevel(false);
			}
			if (yoffset < 0) {
				updateZoomLevel(true);
			}
		});
		
//		// Get the thread stack and push a new frame
//		try ( MemoryStack stack = stackPush() ) {
//			IntBuffer pWidth = stack.mallocInt(1); // int*
//			IntBuffer pHeight = stack.mallocInt(1); // int*
//
//			// Get the window size passed to glfwCreateWindow
//			glfwGetWindowSize(window, pWidth, pHeight);
//
//			// Get the resolution of the primary monitor
//			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//
//			// Center the window
//			glfwSetWindowPos(
//				window,
//				(vidmode.width() - pWidth.get(0)) / 2,
//				(vidmode.height() - pHeight.get(0)) / 2
//			);
//		} // the stack frame is popped automatically

		// Enable v-sync
		glfwSwapInterval(1);
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		projectRelativeCameraCoordinates();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		//Enable transparency
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		playerList.add(player);
		playerList.add(enemy.getPlayer());
		
//		createShips(200);
		
		sidebar = new Sidebar(this, WINDOW_WIDTH / 2, WINDOW_HEIGHT / 18);
		//static layer on top of game
		
		gamerenderer = new GameRenderer();
				
		genTiles();

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		int slowCounter = 0;
		//Will counter be used for game time???
		int counter = 0;
		while ( !window.shouldClose()) {
			if(glfwGetWindowAttrib(window.getWindowHandle(), GLFW_FOCUSED) == GLFW_FALSE){
				if(windowSelected == true){
					glfwIconifyWindow(window.getWindowHandle());
					windowSelected = false;
				}
			}
			else{
				if(windowSelected == false){
					window.setCurrent();
					staticFrame = false;
					windowSelected = true;
				}
			}
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			glEnable(GL_TEXTURE_2D);
			
			//display the title
			if(gameState == 1){
				if(currentLevel != 0){
					currentLevel = 0;
				}
				if(staticFrame == false){
					glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
					projectTrueWindowCoordinates();
					//draw title page
					gamerenderer.loadTexture(1);
					gamerenderer.setTextureCoords(0, 0, 1, 1);
					gamerenderer.setModel(250, WINDOW_HEIGHT - 150, WINDOW_WIDTH - 250, 150);
					drawUIButtons();
					displayBorders();
					window.swapBuffers();
					staticFrame = true;
				}
			}
			//display level select	
			if(gameState == 2){
				if(staticFrame == false){
					glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
					projectTrueWindowCoordinates();
					//draw level select
					gamerenderer.loadTexture(2);
					gamerenderer.setTextureCoords(0, 0, 1, 1);
					gamerenderer.setModel(200, WINDOW_HEIGHT - 50, WINDOW_WIDTH - 200, 50);
					//draw settings icon
					drawUIButtons();
					displayBorders();
					window.swapBuffers();
					staticFrame = true;
				}
			}
			else if(gameState == 4){
				if(staticFrame == false){
					projectTrueWindowCoordinates();
					gamerenderer.loadTexture(7);
					gamerenderer.setTextureCoords(0, 0, 1, 1);
					gamerenderer.setModel(WINDOW_WIDTH / 2 - 100, WINDOW_HEIGHT / 2 + 70, WINDOW_WIDTH / 2 + 100, WINDOW_HEIGHT / 2 - 70);
					window.swapBuffers();
					staticFrame = true;
				}
			}
			else if(gameState == 5){
				if(staticFrame == false){
					projectTrueWindowCoordinates();
					gamerenderer.loadTexture(8);
					gamerenderer.setTextureCoords(0, 0, 1, 1);
					gamerenderer.setModel(WINDOW_WIDTH / 2 - 100, WINDOW_HEIGHT / 2 + 70, WINDOW_WIDTH / 2 + 100, WINDOW_HEIGHT / 2 - 70);
					window.swapBuffers();
					staticFrame = true;
				}
			}
			//MAIN GAME RENDERING LOOP
			else if(gameState == 3){

				if(staticFrame == true){
					staticFrame = false;
				}
				slowCounter++;
				//NOTE: This comment is here to make it easy to Ctrl-F to this part of the code
				//Game loop
				if (slowCounter >= SLOW) {
					slowCounter = 0;
					//System.out.println(counter);
					counter++;
					
					glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
					
					projectRelativeCameraCoordinates();
														
					//Precompute isVisible()
					for (int i = 0; i < playerList.size(); i++) {
						Player p = playerList.get(i);
						p.checkVisible(p.getControlledPlanets(), p.getControlledShips());
					}
					
					//Show FOW
					gamerenderer.drawAllFOW(player.getControlledShips(), player.getControlledPlanets());
					
					projectTrueWindowCoordinates();
					
					//Display background
					for (int t = 0; t < backgroundTiles.size(); t++) {
						backgroundTiles.get(t).display();
					}
					
					projectRelativeCameraCoordinates();

					
					//Update planets
					for(int p = 0; p < planets.size(); p++){
						planets.get(p).checkCapturePoint();
						planets.get(p).update();
						//only display if planet is in camera window
						if(planets.get(p).getX() > viewX - (planetDisplayBorder * getWidthScalar()) && planets.get(p).getX() < viewX + cameraWidth + (planetDisplayBorder * getWidthScalar()) 
								&& planets.get(p).getY() > viewY - (planetDisplayBorder * getHeightScalar()) && planets.get(p).getY() < viewY + cameraHeight + (planetDisplayBorder * getHeightScalar())){
							gamerenderer.drawPlanet(planets.get(p), isVisible(planets.get(p), player));
						}
					}
					
					gamerenderer.drawAllBuildBars(player.getControlledPlanets());
					gamerenderer.drawAllCaptureBars(planets);

					
					//heal ships in planet range
			    	for (int i = 0; i < planets.size(); i++) {
			    		ArrayList<Starship> orbitingShips = planets.get(i).getShips();
						for (int j = 0; j < orbitingShips.size(); j++) {
							if (orbitingShips.get(j).getTeam().equals(planets.get(i).getTeam()) && orbitingShips.get(j).damageDisplayDelay < 200)
								orbitingShips.get(j).damageDisplayDelay -= 2;
						}
					}
			    	
			    	//update ships
					for(int s = 0; s < ships.size(); s++){
						ships.get(s).doRandomMovement();
				    	ships.get(s).setPoints();
				    	ships.get(s).damageDisplayDelay--;
				    	if (ships.get(s).damageDisplayDelay < 0) {
				    		ships.get(s).current_health = Math.min(ships.get(s).current_health + 1, ships.get(s).max_health);
				    		ships.get(s).damageDisplayDelay = 200;
				    	}
				    	if(ships.get(s).checkHealth() == false){
				    		s--;
				    	}
					}
					
					//update projectiles
					for(int p = 0; p < projectiles.size(); p++){
						if(projectiles.get(p).setPoints() == false){
							p--;
						}
					}
					
					//update explosions
					for(int e = 0; e < explosions.size(); e++){
				    	explosions.get(e).update();
					}
					
					if(cameraWidth < 10400 && cameraHeight < 7200){
						//display ships
						for(int s = 0; s < ships.size(); s++){
							if(ships.get(s).getX() > viewX - (shipDisplayBorder * getWidthScalar()) && ships.get(s).getX() < viewX + cameraWidth + (shipDisplayBorder * getWidthScalar())
									&& ships.get(s).getY() > viewY - (shipDisplayBorder * getHeightScalar()) && ships.get(s).getY() < viewY + cameraHeight + (shipDisplayBorder * getHeightScalar())){
								if(isVisible(ships.get(s), player)){
									gamerenderer.drawShip(ships.get(s));
								}
							}
						}
						ArrayList<Starship> selectedShips = player.getSelectedShips();
						gamerenderer.drawAllShipHalos(selectedShips);
						gamerenderer.drawAllHPBars(selectedShips);
					}
					else{
						//display ship icons
						for(int s = 0; s < ships.size(); s++){
							if(ships.get(s).getX() > viewX - (shipDisplayBorder * getWidthScalar()) && ships.get(s).getX() < viewX + cameraWidth + (shipDisplayBorder * getWidthScalar())
									&& ships.get(s).getY() > viewY - (shipDisplayBorder * getHeightScalar()) && ships.get(s).getY() < viewY + cameraHeight + (shipDisplayBorder * getHeightScalar())){
								if(isVisible(ships.get(s), player)){
									gamerenderer.drawShipIcon(ships.get(s));
								}
							}
						}
					}
					//display projectiles
					for(int p = 0; p < projectiles.size(); p++){
				    	projectiles.get(p).display();
					}
					//update explosions
					for(int e = 0; e < explosions.size(); e++){
				    	explosions.get(e).display();
					}
					
					//display weapons range of selected ships
					if(f1Pressed){
						gamerenderer.drawAllScan(player.getControlledShips());
					}
					
					if(shipTracking) {
						updateShipTracking(player);
					}
						
					
				
				//display box select
				if(boxSelectCurrent){
					DoubleBuffer xpos = BufferUtils.createDoubleBuffer(2);
					DoubleBuffer ypos = BufferUtils.createDoubleBuffer(2);
					glfwGetCursorPos(window.getWindowHandle(), xpos, ypos);
					//convert the glfw coordinate to our coordinate system
					xpos.put(1, getWidthScalar() * (xpos.get(0) - windowXOffset) + viewX);
					ypos.put(1, (getHeightScalar() * ((WINDOW_HEIGHT) - ypos.get(0) + windowYOffset) + viewY));
					//draw box select
					gamerenderer.loadTexture(3);
					gamerenderer.setTextureCoords(0, 0, 1, 1);
					gamerenderer.setModel(boxSelect_startx, boxSelect_starty, xpos.get(1), ypos.get(1));
				}

				Instant time = Instant.now();
				
				//Make ships drift apart if they're too close
				for (int s = 0; s < ships.size(); s++) {
					Starship first = ships.get(s);
					for (int s1 = s + 1; s1 < ships.size(); s1++) {
						Starship second = ships.get(s1);
						if (first.getTeam().equals(second.getTeam()) && distance(first.getX(), first.getY(), second.getX(), second.getY()) < first.getClickRadius() * 2 + second.getClickRadius() * 2) {
							double angle = Math.acos((second.getX() - first.getX()) / distance(first.getX(), first.getY(), second.getX(), second.getY()));
							if (Double.isNaN(angle)) angle = 180;
							double newFirstX, newFirstY, newSecondX, newSecondY;
							if (second.getY() > first.getY()) {
								newFirstX = Math.min(Math.max(first.center.X() - Math.cos(angle) / first.weight, first.x_min + first.getClickRadius()), first.x_max - first.getClickRadius());
								newFirstY = Math.min(Math.max(first.center.Y() - Math.sin(angle) / first.weight, first.y_min + first.getClickRadius()), first.y_max - first.getClickRadius());
								newSecondX = Math.min(Math.max(second.center.X() + Math.cos(angle) / second.weight, second.x_min + second.getClickRadius()), second.x_max - second.getClickRadius());
								newSecondY = Math.min(Math.max(second.center.Y() + Math.sin(angle) / second.weight, second.y_min + second.getClickRadius()), second.y_max - second.getClickRadius());
//								newFirstX = Math.min(Math.max(first.center.X() - Math.cos(angle) / first.weight, first.getClickRadius()), WORLD_WIDTH - first.getClickRadius());
//								newFirstY = Math.min(Math.max(first.center.Y() - Math.sin(angle) / first.weight, first.getClickRadius()), WORLD_HEIGHT - first.getClickRadius());
//								newSecondX = Math.min(Math.max(second.center.X() + Math.cos(angle) / second.weight, second.getClickRadius()), WORLD_WIDTH - second.getClickRadius());
//								newSecondY = Math.min(Math.max(second.center.Y() + Math.sin(angle) / second.weight, second.getClickRadius()), WORLD_HEIGHT - second.getClickRadius());
							}
							else {
								newFirstX = Math.min(Math.max(first.center.X() - Math.cos(angle) / first.weight, first.x_min + first.getClickRadius()), first.x_max - first.getClickRadius());
								newFirstY = Math.min(Math.max(first.center.Y() + Math.sin(angle) / first.weight, first.y_min + first.getClickRadius()), first.y_max - first.getClickRadius());
								newSecondX = Math.min(Math.max(second.center.X() + Math.cos(angle) / second.weight, second.x_min + second.getClickRadius()), second.x_max - second.getClickRadius());
								newSecondY = Math.min(Math.max(second.center.Y() - Math.sin(angle) / second.weight, second.y_min + second.getClickRadius()), second.y_max - second.getClickRadius());
//								newFirstX = Math.min(Math.max(first.center.X() - Math.cos(angle) / first.weight, first.getClickRadius()), WORLD_WIDTH - first.getClickRadius());
//								newFirstY = Math.min(Math.max(first.center.Y() + Math.sin(angle) / first.weight, first.getClickRadius()), WORLD_HEIGHT - first.getClickRadius());
//								newSecondX = Math.min(Math.max(second.center.X() + Math.cos(angle) / second.weight, second.getClickRadius()), WORLD_WIDTH - second.getClickRadius());
//								newSecondY = Math.min(Math.max(second.center.Y() - Math.sin(angle) / second.weight, second.getClickRadius()), WORLD_HEIGHT - second.getClickRadius());
							}
							if(!(first instanceof BasicPod || first instanceof PlanetRadar || first instanceof PlanetLaser)){
								first.center = new Point(newFirstX, newFirstY);
							}
							if(!(second instanceof BasicPod || second instanceof PlanetRadar || second instanceof PlanetLaser)){
								second.center = new Point(newSecondX, newSecondY);
							}
//							if (first.locationTarget != null) {
//								if (second.locationTarget != null) {
//									first.setLocationTarget(new Point(first.locationTarget.x + newFirstX - first.center.X()
//											, first.locationTarget.y + newFirstY - first.center.Y()));
//									second.setLocationTarget(new Point(second.locationTarget.x + newSecondX - second.center.X()
//											, second.locationTarget.y + newSecondY - second.center.Y()));
//								}
////								else first.setLocationTarget(new Point(second.center.X() + newFirstX - first.center.X()
////											, second.center.Y() + newFirstY - first.center.Y()));
//							}
//							else {
//								if (second.locationTarget != null)
//									second.setLocationTarget(new Point(first.center.X() + newSecondX - second.center.X()
//										, first.center.Y() + newSecondY - second.center.Y()));
//							}
							//This change involving the command queue has the potential to be buggy
							if (first.locationTarget != null && distance(first.center.X(), first.center.Y(), first.locationTarget.x, first.locationTarget.y) < first.getClickRadius() * 4 && !first.commands.isEmpty()) {
								first.commands.remove(0);
								first.locationTarget = null;
							}

							if (second.locationTarget != null && distance(second.center.X(), second.center.Y(), second.locationTarget.x, second.locationTarget.y) < second.getClickRadius() * 4 && !second.commands.isEmpty()) {
								second.commands.remove(0);
								second.locationTarget = null;
							}
						}
					}
				}
				checkProjectiles();
				
				enemy.move();
				
				
				projectTrueWindowCoordinates();
				
				drawUIButtons();
				
				//Display sidebar and figure out what has been selected
				boolean sidebarIsDisplayed = false;
				sidebar.display();
				int sumCurrentHP = 0;
				int sumMaxHP = 0;
				int numFightersSelected = 0;
				int numInterceptorsSelected = 0;
				int numTransportsSelected = 0;
				int numMissileshipsSelected = 0;
				int numWallshipsSelected = 0;
				int numSnipersSelected = 0;
				int numBattleshipsSelected = 0;
				int numPodsSelected = 0;
				String shipStatus = "Idle";
				Planet selectedPlanet = null;
				int selectedPlanetResources = Integer.MIN_VALUE;
				boolean alliedPlanetSelected = false;
				String planetControllingTeam = "none";
				String shipsControllingTeam = "none";
				String podTypeSelected = "none";
				for(int p = 0; p < planets.size(); p++){
					if(planets.get(p).getSelected()){
						sidebarIsDisplayed = true;
						selectedPlanet = planets.get(p);
						selectedPlanetResources = selectedPlanet.getResources();
						planetControllingTeam = selectedPlanet.getTeam();
					}
				}
				for (int s = 0; s < ships.size(); s++) {
					if (ships.get(s).isSelected()) {
						sidebarIsDisplayed = true;
						sumCurrentHP += ships.get(s).current_health;
						sumMaxHP += ships.get(s).max_health;
						shipsControllingTeam = ships.get(s).getTeam();
						if (ships.get(s).locationTarget != null && shipStatus.equals("Idle"))shipStatus = "Moving";
						if (ships.get(s).target != null && !shipStatus.equals("Taking damage")) shipStatus = "Engaging enemy";
						if (ships.get(s).damageDisplayDelay > 900) shipStatus = "Taking damage";
						if (ships.get(s) instanceof Fighter) numFightersSelected++;
						else if (ships.get(s) instanceof Interceptor) numInterceptorsSelected++;
						else if (ships.get(s) instanceof Transport) numTransportsSelected++;
						else if (ships.get(s) instanceof Missileship) numMissileshipsSelected++;
						else if (ships.get(s) instanceof Wallship) numWallshipsSelected++;
						else if (ships.get(s) instanceof Sniper) numSnipersSelected++;
						else if (ships.get(s) instanceof Battleship) numBattleshipsSelected++;
						else if (ships.get(s) instanceof BasicPod) {
							numPodsSelected++;
							if (ships.get(s) instanceof MissilePod) podTypeSelected = "Missile Turret";
							else if (ships.get(s) instanceof MachineGunPod) podTypeSelected = "Machinegun Turret";
							else if (ships.get(s) instanceof BasicPod) podTypeSelected = "Basic Turret";
						}
					}
				}
				
				//Display bitmap font letters
				destroyAllText();
				if (sidebarIsDisplayed) {
					if (selectedPlanetResources > Integer.MIN_VALUE) {
						writeText("Planet resources:", 20, 40);
						if (planetControllingTeam.equals(player.getTeam())){
//								writeText("" + selectedPlanetResources, 20, 20);
							alliedPlanetSelected = true;
						}
						else{
//								writeText("??", 20, 20);
//								writeText("" + selectedPlanetResources, 20, 20);
						}
						writeText("Controlled by:", 20, 100);
						//if planet is visible
						if(isVisible(selectedPlanet, player)){
							writeText("" + selectedPlanetResources, 20, 20);
							writeText(planetControllingTeam, 20, 80);
							for (int i = 0; i < Math.min(selectedPlanet.buildOrder.size(), 20); i++) {
								writeText(selectedPlanet.buildOrder.get(i) + "", 500 + i * 20, 20);
							}
						}
						else{
							writeText("???", 20, 20);
							writeText("???", 20, 80);
						}
					}
					else if (numFightersSelected + numInterceptorsSelected + numTransportsSelected
							+ numMissileshipsSelected + numWallshipsSelected + numSnipersSelected
							+ numBattleshipsSelected + numPodsSelected == 1) {
						if (numFightersSelected == 1) writeText("Fighter", 400, 15, 30);
						else if (numInterceptorsSelected == 1) writeText("Interceptor", 400, 15, 30);
						else if (numTransportsSelected == 1) writeText("Transport", 400, 15, 30);
						else if (numMissileshipsSelected == 1) writeText("Missileship", 400, 15, 30);
						else if (numWallshipsSelected == 1) writeText("Wallship", 400, 15, 30);
						else if (numSnipersSelected == 1) writeText("Sniper", 400, 15, 30);
						else if (numBattleshipsSelected == 1) writeText("Battleship", 400, 15, 30);
						else if (numPodsSelected == 1) writeText(podTypeSelected, 300, 15, 30);
//							if(shipsControllingTeam.equals(player.getTeam()))
						if (sumCurrentHP - (int)sumCurrentHP > 0)
							sumCurrentHP++;
							writeText("Armor:" + sumCurrentHP + "/" + sumMaxHP, 800, 20);
//							else
//								writeText("Armor:??/??", 800, 20);
						writeText("Faction:", 20, 100);
						writeText(shipsControllingTeam, 20, 80);
						writeText("Status:", 20, 40);
						writeText(shipStatus, 20, 20);
					}
					else if (numFightersSelected + numInterceptorsSelected + numTransportsSelected
							+ numMissileshipsSelected + numWallshipsSelected + numSnipersSelected
							+ numBattleshipsSelected + numPodsSelected > 1) {
						writeText("Starfleet(" + (numFightersSelected + numInterceptorsSelected + numTransportsSelected
								+ numMissileshipsSelected + numWallshipsSelected + numSnipersSelected
								+ numBattleshipsSelected + numPodsSelected) + ")", 400, 15, 30);
						writeText("F:" + numFightersSelected, 1000, 120);
						writeText("I:" + numInterceptorsSelected, 1000, 100);
//							writeText("Transports:" + numTransportsSelected, 1000, 60);
						writeText("M:" + numMissileshipsSelected, 1000, 80);
						writeText("W:" + numWallshipsSelected, 1000, 60);
						writeText("S:" + numSnipersSelected, 1200, 120);
						writeText("B:" + numBattleshipsSelected, 1200, 100);
						writeText("T:" + numPodsSelected, 1200, 80);
//							if (shipsControllingTeam.equals(player.getTeam()))
						if (sumCurrentHP - (int)sumCurrentHP > 0)		
							sumCurrentHP++;
							writeText("Fleet armor:" + sumCurrentHP + "/" + sumMaxHP, 800, 20);
//							else
//								writeText("Fleet armor:??/??", 800, 20);
						writeText("Faction:", 20, 100);
						writeText(shipsControllingTeam, 20, 80);
						writeText("Fleet status:", 20, 40);
						writeText(shipStatus, 20, 20);
					}
					if(alliedPlanetSelected){
						gamerenderer.loadTexture(5);
						gamerenderer.setTextureCoords(0, 0, 1, 1);
						gamerenderer.setModel(WINDOW_WIDTH - 400, 100, WINDOW_WIDTH, -50);
					}
					
					for (int i = 0; i < text.size(); i++) {
//						text.get(i).setPoints();
						text.get(i).display();
					}
				}
				else {
					//Given that we know that no ships are selected, set shipTracking to false.
					shipTracking = false;
					writeText("Blue", 100, 15, 30);
					writeText("Red", 1100, 15, 30);
					int blueResources = 0;
					int redResources = 0;
					int bluePlanets = 0;
					int redPlanets = 0;
					for (int i = 0; i < planets.size(); i++) {
						if (planets.get(i).getTeam().equals("red")) {
							redPlanets++;
							redResources += planets.get(i).storedResources;
						}
						else if (planets.get(i).getTeam().equals("blue")) {
							bluePlanets++;
							blueResources += planets.get(i).storedResources;
						}
					}
					int redShips = 0;
					int blueShips = 0;
					for (int i = 0; i < ships.size(); i++) {
						if (ships.get(i).getTeam().equals("red")) {
							redShips++;
						}
						else if (ships.get(i).getTeam().equals("blue")) {
							blueShips++;
						}
					}
					writeText("Ships: " + blueShips, 20, 100);
					writeText("Ships: " + redShips, 1000, 100);
					writeText("Planets: " + bluePlanets, 20, 80);
					writeText("Planets: " + redPlanets, 1000, 80);
					writeText("Resources: " + blueResources, 20, 60);
					writeText("Resources: " + redResources, 1000, 60);
				}
				
				displayBorders();
				
				//Check win
				if (player.getControlledPlanets().size() == 0 && player.getControlledShips().size() == 0) {
					if(endLevelTimer >= endLevelDelay){
						gameState = 5;
						endLevelTimer = 0;
					}
					else{
						endLevelTimer++;
					}
				}
				else if (enemy.getPlayer().getControlledPlanets().size() == 0 && enemy.getPlayer().getControlledShips().size() == 0) {
					if(endLevelTimer >= endLevelDelay){
						gameState = 4;
						endLevelTimer = 0;
					}
					else{
						endLevelTimer++;
					}
				}
				
				for (int i = 0; i < text.size(); i++) {
//						text.get(i).setPoints();
					text.get(i).display();
				}
				
				glDisable(GL_TEXTURE_2D);
				
				//mouse on edge scrolling
				DoubleBuffer xpos = BufferUtils.createDoubleBuffer(3);
				DoubleBuffer ypos = BufferUtils.createDoubleBuffer(3);
				glfwGetCursorPos(window.getWindowHandle(), xpos, ypos);
					//glfw coordinates bounded by window offsets
					xpos.put(0, Math.min(Math.max(xpos.get(0), windowXOffset), WINDOW_WIDTH + windowXOffset));
					ypos.put(0, Math.min(Math.max(ypos.get(0), windowYOffset), WINDOW_HEIGHT + windowYOffset));
					//relative camera coordinates based on camera position and scale
					xpos.put(1, getWidthScalar() * (xpos.get(0) - windowXOffset) + viewX);
					ypos.put(1, (getHeightScalar() * (WINDOW_HEIGHT - ypos.get(0) + windowYOffset) + viewY));
					//true window coordinates
					xpos.put(2, xpos.get(0) - windowXOffset);
					ypos.put(2, (WINDOW_HEIGHT - ypos.get(0) + windowYOffset));
//				System.out.println(xpos.get(2) + " " + ypos.get(2));
				//Check which direction the camera should move, and move accordingly
				if (panLeft || (xpos.get(2) == 0)) {
					viewX = Math.max(currentMin_X, viewX - cameraWidth / 30);
					shipTracking = false;
				}
				if (panRight || (xpos.get(2) == WINDOW_WIDTH)) {
					viewX = Math.min(currentMax_X - cameraWidth, viewX + cameraWidth / 30);
					shipTracking = false;
				}
				if (panDown || (ypos.get(2) == 0)) {
					viewY = Math.max((int) (currentMin_Y - 150 * getHeightScalar()), viewY - cameraHeight / 30);
					shipTracking = false;
				}
				if (panUp || (ypos.get(2) == WINDOW_HEIGHT)) {
					viewY = Math.min(currentMax_Y - cameraHeight, viewY + cameraHeight / 30);
					shipTracking = false;
				}
				window.swapBuffers();
//				System.out.println(Instant.now().compareTo(time) / 1000000);
				}

			}
			
		}
	}
	
	public void drawUIButtons() {
		//display settings icon
		gamerenderer.loadTexture(4);
		gamerenderer.setTextureCoords(0, 0, 1, 1);
		gamerenderer.setModel(WINDOW_WIDTH - 50, WINDOW_HEIGHT - 2, WINDOW_WIDTH - 2, WINDOW_HEIGHT - 50);
		if(!mute) {
			gamerenderer.loadTexture(9);
		}
		else {
			gamerenderer.loadTexture(10);
		}
		gamerenderer.setModel(WINDOW_WIDTH - 102, WINDOW_HEIGHT - 2, WINDOW_WIDTH - 54, WINDOW_HEIGHT - 50);
	}
	
	public void toggleAudio() {
		mute = !mute;
		if(mute) { //stop audio
			menuMusic.stop();
			gameMusic.stop();
		}
		else { //start audio
			if(gameState == 3) {
				gameMusic.start();
			}
			else {
				menuMusic.start();
			}
		}
	}

	public static void main(String[] args) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
		new StarshipArena().run();
	}
	
	public void projectRelativeCameraCoordinates(){
		glMatrixMode(GL_PROJECTION);
        glLoadIdentity(); // Resets any previous projection matrices
        glOrtho((-windowXOffset * getWidthScalar()) + viewX, viewX + cameraWidth + (windowXOffset * getWidthScalar()), viewY + ((-windowYOffset) * getHeightScalar()), viewY + cameraHeight + ((windowYOffset)* getHeightScalar()), 1, -1);
        glMatrixMode(GL_MODELVIEW);
	}
	
	public void projectTrueWindowCoordinates(){
		glMatrixMode(GL_PROJECTION);
        glLoadIdentity(); // Resets any previous projection matrices
        glOrtho(-windowXOffset, WINDOW_WIDTH + windowXOffset, -windowYOffset, WINDOW_HEIGHT + windowYOffset, 1, -1);
        glMatrixMode(GL_MODELVIEW);
	}
	
	public double[] getScreenBounds(){
    	double[] bounds = new double[4];
    	bounds[0] = 0;
    	bounds[1] = WORLD_WIDTH;
    	bounds[2] = 0;
    	bounds[3] = WORLD_HEIGHT;
//    	bounds[0] = currentMin_X;
//    	bounds[1] = currentMax_X;
//    	bounds[2] = currentMin_Y;
//    	bounds[3] = currentMax_Y;
    	
    	return bounds;
    }
	
	public void displayBorders(){
		gamerenderer.loadTexture(6);
		gamerenderer.setTextureCoords(0, 0, 1, 1);
		gamerenderer.setModel(-windowXOffset, WINDOW_HEIGHT + windowYOffset, 0, -windowYOffset);
		gamerenderer.setModel(-windowXOffset, WINDOW_HEIGHT + windowYOffset, WINDOW_WIDTH + windowXOffset, WINDOW_HEIGHT);
		gamerenderer.setModel(WINDOW_WIDTH, WINDOW_HEIGHT + windowYOffset, WINDOW_WIDTH + windowXOffset * 2, -windowYOffset);
		gamerenderer.setModel(-windowXOffset, 0, WINDOW_WIDTH + windowXOffset, -windowYOffset);
	}
	
	//calls whenever a number key is pressed 
	public void numberKeyPressed(int key) {
		if(shiftPressed){
			assignControlGroup(player, key);
		}
		else if(controlPressed) {
			removeControlGroup(player, key);
		}
		else if(player.getSelectedPlanet() == null){
			displayControlGroup(player, key);
		}
		else {
			buyShips(player, key);
		}
	}
	
	//Creates the number of ships specified by the user
	//Each ship has a random starting location and angle
	public void createShips(int num){
		new Planet(this, 23000 * levelScale, 10000 * levelScale, 1).setTeam("blue");
		for (int i = 0; i < planets.size(); i++) {
			Planet p = planets.get(i);
			p.surfaceX = -100000 + i * 100000;
			p.surfaceY = -100000;
			p.dimensionX = 10000;
			p.dimensionY = 10000;
			loadSubLevel(p);
			for (int j = 0; j < 10; j++) {
				new Fighter(this, "blue", p.surfaceX + 500, p.surfaceY + 500, 0);
			}
			loadSubLevel(null);
		}
//		int startx;
//		int starty;
//		int angle;
//		for(int i = 0; i < num; i++){
//			startx = random.nextInt(WORLD_WIDTH - 100) + 50;
//			starty = random.nextInt(WORLD_HEIGHT - 100) + 50;
//			angle = random.nextInt(360);
//			new Fighter(this, "none", startx, starty, angle);
//			if(i % 2 == 0){
//				new Interceptor(this, "none", startx , starty, angle);
//			}
//		}
//		new Missileship(this, "red", 100, 450, 0);
//		new Transport(this, "red", 100, 450, 0);
//		new Transport(this, "red", 5, 450, 0);
//		new Transport(this, "red", 400, 600, 0);
//		new Fighter(this, "blue", 200, 500, 270);
//		new Interceptor(this, "blue", 600, 500, 90);
//		new Missileship(this, "blue", 700, 500, 0);
//		new Fighter(this, "red", 1700, 400, 0);
//		new Fighter(this, "red", 1750, 400, 0);
//		new Fighter(this, "red", 1650, 400, 0);
//		new Fighter(this, "red", 1700, 450, 0);
		
		new MachineGunPod(this, "red", 3000 * levelScale, 15000 * levelScale, 270);
		new MachineGunPod(this, "red", 3500, 14000, 270);
		new MachineGunPod(this, "red", 3000, 13000, 270);
//		
		new Wallship(this, "blue", 20000 * levelScale, 10000 * levelScale, 270);
		new Wallship(this, "blue", 20000 * levelScale, 10000 * levelScale, 270);
		new Wallship(this, "blue", 20000 * levelScale, 10000 * levelScale, 270);
		new Sniper(this, "blue", 20000 * levelScale, 10000 * levelScale, 270);
		new Sniper(this, "blue", 20000 * levelScale, 10000 * levelScale, 270);
		new Sniper(this, "blue", 20000 * levelScale, 10000 * levelScale, 270);
		new Battleship(this, "blue", 20000 * levelScale, 10000 * levelScale, 270);
		new Battleship(this, "blue", 20000 * levelScale, 10000 * levelScale, 270);
		new Battleship(this, "blue", 20000 * levelScale, 10000 * levelScale, 270);
		
		new MachineGunPod(this, "red", 4000, 15000, 270);
		new MachineGunPod(this, "red", 4500, 14000, 270);
		new MachineGunPod(this, "red", 4000, 13000, 270);
		new MachineGunPod(this, "red", 5000, 15000, 270);
		new MachineGunPod(this, "red", 5500, 14000, 270);
		new MachineGunPod(this, "red", 5000, 13000, 270);
		new MachineGunPod(this, "red", 6000, 15000, 270);
		new MachineGunPod(this, "red", 6500, 14000, 270);
		new MachineGunPod(this, "red", 6000, 13000, 270);
//		new Fighter(this, "blue", 2000, 5000, 270, 1);
//		new Interceptor(this, 500, 700, 0, 1);
	}
	
	public void buyShips(Player player, int type){
		Planet p = player.getSelectedPlanet();
		//if player has selected an allied planet
		int spawnangle = 0;
		if(p != null && p.getTeam().equals(player.getTeam())){
			if (p.getTeam().equals("red")) spawnangle = 180;
			//attempt to buy fighter
			if(type == 1){
//				p.setResources(p.getResources() - FIGHTER_COST);
				p.queueShip(1);
			}
			//attempt to buy interceptor
			else if(type == 2){
//				p.setResources(p.getResources() - INTERCEPTOR_COST);
				p.queueShip(2);
				
			}
			//attempt to buy missileship
			else if(type ==3){
//				p.setResources(p.getResources() - MISSILESHIP_COST);
				p.queueShip(3);
				
			}
			
			//NEW SHIP CLASSES:
			//Attempt to buy Wallship
			else if (type == 4) {
//				p.setResources(p.getResources() - WALLSHIP_COST);
				p.queueShip(4);
			}
			//Attempt to buy Sniper
			else if (type == 5) {
//				p.setResources(p.getResources() - SNIPER_COST);
				p.queueShip(5);
			}
			//Attempt to buy Battleship
			else if (type == 6) {
//				p.setResources(p.getResources() - BATTLESHIP_COST);
				p.queueShip(6);
			}
		}
	}
	
	//assigns selected ships to specified control group
	public void assignControlGroup(Player player, int group){
		ArrayList<Starship> playerShips = player.getControlledShips();
		for(int i = 0; i < playerShips.size(); i++){
			if(playerShips.get(i).isSelected()){
				playerShips.get(i).addControlGroup(group);
			}
		}
	}
	
	//removes selected ships from specified control groups
	public void removeControlGroup(Player player, int group){
		ArrayList<Starship> playerShips = player.getControlledShips();
		for(int i = 0; i < playerShips.size(); i++){
			if(playerShips.get(i).isSelected()){
				playerShips.get(i).removeControlGroup(group);
			}
		}
	}
	
	public void displayControlGroup(Player player, int group){
		ArrayList<Starship> allShips = getAllShips();
		for(int i = 0; i < allShips.size(); i++){
			if(allShips.get(i).inControlGroup(group)){
				allShips.get(i).setSelected(true);
			}
			else{
				allShips.get(i).setSelected(false);
			}
		}
	}
	
	//fog of war - planets
	public boolean isVisible(Planet entity, Player p){
		if (p.visiblePlanets.contains(entity)) return true;
		return false;
	}
	public boolean isVisible(Planet entity, String team){
		for (int i = 0; i < playerList.size(); i++) {
			if (team.equals(playerList.get(i).team)) return isVisible(entity, playerList.get(i));
		}
		return false;
	}
	
	//fog of war - ships
		public boolean isVisible(Starship entity, Player p){
			if (p.visibleShips.contains(entity)) return true;
			return false;
		}
		public boolean isVisible(Starship entity, String team){
			for (int i = 0; i < playerList.size(); i++) {
				if (team.equals(playerList.get(i).team)) return isVisible(entity, playerList.get(i));
			}
			return false;
		}
	
	public void updateZoomLevel(boolean zoomOut){
		DoubleBuffer xpos = BufferUtils.createDoubleBuffer(3);
		DoubleBuffer ypos = BufferUtils.createDoubleBuffer(3);
		glfwGetCursorPos(window.getWindowHandle(), xpos, ypos);
		xpos.put(0, Math.min(Math.max(xpos.get(0), windowXOffset), WINDOW_WIDTH + windowXOffset));
		ypos.put(0, Math.min(Math.max(ypos.get(0), windowYOffset), WINDOW_HEIGHT + windowYOffset));
		//relative camera coordinates
		xpos.put(1, getWidthScalar() * (xpos.get(0) - windowXOffset) + viewX);
		ypos.put(1, (getHeightScalar() * (WINDOW_HEIGHT - ypos.get(0) + windowYOffset) + viewY));
		//true window coordinates
		xpos.put(2, xpos.get(0) - windowXOffset);
		ypos.put(2, (WINDOW_HEIGHT - ypos.get(0) + windowYOffset));
		boolean mouseInFrame = false;
		
		double oldX = xpos.get(1);
		double oldY = ypos.get(1);
		double xAxisDistance = 0;
		double yAxisDistance = 0;
		
		if(xpos.get(2) > 0 && xpos.get(2) < WINDOW_WIDTH && ypos.get(2) > 0 && ypos.get(2) < WINDOW_HEIGHT){
			mouseInFrame = true;
			xAxisDistance = xpos.get(2)/WINDOW_WIDTH;
			yAxisDistance = ypos.get(2)/WINDOW_HEIGHT;
		}
		
		int MIN_WIDTH = 650;
		int MIN_HEIGHT = 450;
		if(zoomOut){
			if(/*zoomLevel < 5 && */currentMax_X - currentMin_X >= cameraWidth * 1.5 && currentMax_Y - currentMin_Y >= cameraHeight * 1.5){
				zoomLevel++;
				cameraWidth *= 1.5;
				cameraHeight *= 1.5;
				if(mouseInFrame){
					viewX = oldX - cameraWidth * xAxisDistance;
					viewY = oldY - cameraHeight * yAxisDistance;
				}
				else{
					viewX -= cameraWidth / 6;
					viewY -= cameraHeight / 6;
				}
				if(viewX + cameraWidth > currentMax_X){
					viewX = currentMax_X - cameraWidth;
				}
				if(viewY + cameraHeight > currentMax_Y){
					viewY = currentMax_Y - cameraHeight;
				}
				if(viewX < currentMin_X){
					viewX = currentMin_X;
				}
				if(viewY < currentMin_Y){
					viewY = currentMin_Y;
				}
			}
		}
		else{
			if(/*zoomLevel > 1*/ cameraWidth - 2 * MIN_WIDTH > 0 && cameraHeight - 2 * MIN_HEIGHT > 0){
				zoomLevel--;
				cameraWidth /= 1.5;
				cameraHeight /= 1.5;
				if(mouseInFrame){
					viewX = oldX - cameraWidth * xAxisDistance;
					viewY = oldY - cameraHeight * yAxisDistance;
				}
				else{
					viewX += cameraWidth / 4;
					viewY += cameraHeight / 4;
				}
			}
		}
	}
	
	public void updateShipTracking(Player p){
		double avgX = 0;
		double avgY = 0;
		ArrayList<Starship> selected = p.getSelectedShips();
		if(selected.size() == 0) {
			return;
		}
		for (int i = 0; i < selected.size(); i++) {
			avgX += selected.get(i).getX();
			avgY += selected.get(i).getY();
		}
		avgX /= selected.size();
		avgY /= selected.size();
		viewX = avgX - cameraWidth/2;
		viewY = avgY - cameraHeight/2;
	}
	
	public void loadLevel(int level){
		boolean trueMuteState = mute;
		mute = true;
		destroyAllShips();
		destroyAllPlanets();
		destroyAllProjectiles();
		destroyAllExplosions();
		playerList.clear();
		loadSubLevel(null);
		mute = trueMuteState;
		menuMusic.stop();
		if (!gameMusic.isActive()) {
			gameMusic.setFramePosition(0);
			if (!mute)
				gameMusic.loop(Clip.LOOP_CONTINUOUSLY);
		}
		
		currentLevel = level;
		zoomLevel = 3;
		cameraWidth = 26000 * levelScale;
		cameraHeight = 18000 * levelScale;
		if(level == 1){
			WORLD_WIDTH = 39000 * levelScale;
		    WORLD_HEIGHT = 27000 * levelScale;
		    viewX = 0 * levelScale;
			viewY = 0 * levelScale;
			cameraWidth = 26000 * levelScale;
			cameraHeight = 18000 * levelScale;
			enemy = new Enemy(this, new Player(this, "red"));
			//TODO Enemy Fighters are commented out so I can test new ships, and left Planet is auto-given to blue. Reverse these changes after testing concludes.
			new Planet(this, 13500 * levelScale, 10000 * levelScale, 1).setTeam("blue");;
//			new PlanetRadar(this, "blue", 13500 * levelScale, 10000 * levelScale, 45);
			new Planet(this, 30000 * levelScale, 15000 * levelScale, 2).setTeam("red");
//			new PlanetLaser(this, "red", 30000 * levelScale, 15000 * levelScale, 45);
			
			new Wallship(this, "red", 30000 * levelScale, 15000 * levelScale, 45);
			new Wallship(this, "red", 30000 * levelScale, 15000 * levelScale, 45);
			new Wallship(this, "red", 30000 * levelScale, 15000 * levelScale, 45);
			
			new Sniper(this, "red", 30000 * levelScale, 15000 * levelScale, 45);
			new Sniper(this, "red", 30000 * levelScale, 15000 * levelScale, 45);
			new Sniper(this, "red", 30000 * levelScale, 15000 * levelScale, 45);
			
			new Battleship(this, "red", 30000 * levelScale, 15000 * levelScale, 45);
			new Battleship(this, "red", 30000 * levelScale, 15000 * levelScale, 45);
			new Battleship(this, "red", 30000 * levelScale, 15000 * levelScale, 45);
			
			
//			for (int i = 0; i < 320; i++) {
//				if (i % 20 == 0) {
//					new Missileship(this, "blue", 13500 * levelScale, 10000 * levelScale, 45);
//				}
//				new Fighter(this, "red", 30000 * levelScale, 15000 * levelScale, 45);
////				new Fighter(this, "blue", 13500 * levelScale, 10000 * levelScale, 45);
//			}
//			for (int i = 0; i < 32; i++) {
//				new Interceptor(this, "red", 30000 * levelScale, 15000 * levelScale, 45);
//			}
			
			
//			new Fighter(this, "blue", 5000 * levelScale, 4000 * levelScale, 0);
//			new Fighter(this, "blue", 6000 * levelScale, 3500 * levelScale, 0);
//			new Fighter(this, "blue", 4000 * levelScale, 3500 * levelScale, 0);
////			new Fighter(this, "blue", 7000 * levelScale, 6000 * levelScale, 0);
////			new Fighter(this, "blue", 8000 * levelScale, 5500 * levelScale, 0);
////			new Fighter(this, "blue", 6000 * levelScale, 5500 * levelScale, 0);
////			new Fighter(this, "blue", 9000 * levelScale, 4000 * levelScale, 0);
////			new Fighter(this, "blue", 10000 * levelScale, 3500 * levelScale, 0);
////			new Fighter(this, "blue", 8000 * levelScale, 3500 * levelScale, 0);
//			
//			new Fighter(this, "red", 28000 * levelScale, 15000 * levelScale, 135);
//			new Fighter(this, "red", 29000 * levelScale, 15000 * levelScale, 90);
//			new Fighter(this, "red", 30000 * levelScale, 17000 * levelScale, 80);
//			new Fighter(this, "red", 32000 * levelScale, 15000 * levelScale, 150);
//			new Fighter(this, "red", 32000 * levelScale, 13000 * levelScale, 160);
//			new Fighter(this, "red", 30000 * levelScale, 13000 * levelScale, 150);
		}
		else if(level == 2){
			WORLD_WIDTH = 39000 * levelScale;
		    WORLD_HEIGHT = 27000 * levelScale;
		    viewX = 0 * levelScale;
			viewY = 0 * levelScale;
			cameraWidth = 26000 * levelScale;
			cameraHeight = 18000 * levelScale;
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			new Planet(this, 13500 * levelScale, 10000 * levelScale, 1);
			new Planet(this, 30000 * levelScale, 15000 * levelScale, 2).setTeam("red");
			new Fighter(this, "blue", 5000 * levelScale, 4000 * levelScale, 0);
			new Fighter(this, "blue", 6000 * levelScale, 3500 * levelScale, 0);
			new Fighter(this, "blue", 4000 * levelScale, 3500 * levelScale, 0);
			new Fighter(this, "blue", 7000 * levelScale, 6000 * levelScale, 0);
//			new Fighter(this, "blue", 8000 * levelScale, 5500 * levelScale, 0);
//			new Fighter(this, "blue", 6000 * levelScale, 5500 * levelScale, 0);
			new Fighter(this, "blue", 9000 * levelScale, 4000 * levelScale, 0);
			new Fighter(this, "blue", 10000 * levelScale, 3500 * levelScale, 0);
			new Fighter(this, "blue", 8000 * levelScale, 3500 * levelScale, 0);
			
			new Fighter(this, "red", 28000 * levelScale, 15000 * levelScale, 135);
			new Fighter(this, "red", 30000 * levelScale, 15000 * levelScale, 90);
			new Fighter(this, "red", 30000 * levelScale, 17000 * levelScale, 80);
			new Fighter(this, "red", 32000 * levelScale, 15000 * levelScale, 150);
			new Fighter(this, "red", 32000 * levelScale, 13000 * levelScale, 160);
			new Fighter(this, "red", 30000 * levelScale, 13000 * levelScale, 150);
		}
		
		else if (level == 3) {
			WORLD_WIDTH = 50000 * levelScale;
		    WORLD_HEIGHT = 40000 * levelScale;
		    viewX = 2000 * levelScale;
			viewY = 2000 * levelScale;
			zoomLevel = 2;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			Planet temp;
			temp = new Planet(this, 13500 * levelScale, 10000 * levelScale, 1);
			temp.setTeam("blue"); temp.setResources(40);
			temp = new Planet(this, 30000 * levelScale, 15000 * levelScale, 2);
			temp.setTeam("blue"); temp.setResources(40);		
			temp = new Planet(this, 22500 * levelScale, 30000 * levelScale, 3);		
			temp.setTeam("red"); temp.setResources(40);		
			temp = new Planet(this, 47000 * levelScale, 23000 * levelScale, 4);		
			temp.setTeam("red"); temp.setResources(40);
			new Fighter(this, "blue", 13500 * levelScale, 10000 * levelScale, 0);
			new Fighter(this, "blue", 14500 * levelScale, 9500 * levelScale, 0);
			new Fighter(this, "blue", 12500 * levelScale, 9500 * levelScale, 0);
			
			new Fighter(this, "blue", 30000 * levelScale, 15000 * levelScale, 0);
			new Fighter(this, "blue", 31000 * levelScale, 14500 * levelScale, 0);
			new Fighter(this, "blue", 29000 * levelScale, 14500 * levelScale, 0);
			
			new Fighter(this, "red", 22500 * levelScale, 30000 * levelScale, 180);
			new Fighter(this, "red", 23500 * levelScale, 29500 * levelScale, 180);
			new Fighter(this, "red", 21500 * levelScale, 29500 * levelScale, 180);
			
			new Fighter(this, "red", 47000 * levelScale, 23000 * levelScale, 180);
			new Fighter(this, "red", 48000 * levelScale, 22500 * levelScale, 180);
			new Fighter(this, "red", 46000 * levelScale, 22500 * levelScale, 180);
		}
		
		else if (level == 4) {
			WORLD_WIDTH = 100000 * levelScale;
		    WORLD_HEIGHT = 80000 * levelScale;
		    viewX = 2000 * levelScale;
			viewY = 2000 * levelScale;
			zoomLevel = 3;
			cameraWidth = 52000 * levelScale;
			cameraHeight = 36000 * levelScale;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			new Planet(this, 13500 * levelScale, 10000 * levelScale, 1).setTeam("blue");
			new Planet(this, 30000 * levelScale, 15000 * levelScale, 2).setTeam("blue");
			new Planet(this, 22500 * levelScale, 30000 * levelScale, 3).setTeam("blue");
			new Planet(this, 47000 * levelScale, 23000 * levelScale, 4).setTeam("blue");
			
			new Planet(this, 43500 * levelScale, 42000 * levelScale, 5).setTeam("red");
			new Planet(this, 95000 * levelScale, 15000 * levelScale, 6).setTeam("red");
			new Planet(this, 15000 * levelScale, 40000 * levelScale, 4).setTeam("red");
			new Planet(this, 27000 * levelScale, 63000 * levelScale, 3).setTeam("red");
			new Planet(this, 67000 * levelScale, 75000 * levelScale, 2).setTeam("red");
			
			new Fighter(this, "blue", 13500 * levelScale, 10000 * levelScale, 0);
			new Fighter(this, "blue", 14500 * levelScale, 9500 * levelScale, 0);
			new Fighter(this, "blue", 12500 * levelScale, 9500 * levelScale, 0);
			new Interceptor(this, "blue", 11500 * levelScale, 9000 * levelScale, 0);
			new Interceptor(this, "blue", 15500 * levelScale, 9000 * levelScale, 0);
			
			new Interceptor(this, "blue", 30000 * levelScale, 15000 * levelScale, 0);
			new Fighter(this, "blue", 31000 * levelScale, 14500 * levelScale, 0);
			new Fighter(this, "blue", 29000 * levelScale, 14500 * levelScale, 0);
			
			new Fighter(this, "blue", 22500 * levelScale, 30000 * levelScale, 0);
			new Fighter(this, "blue", 23500 * levelScale, 29500 * levelScale, 0);
			new Fighter(this, "blue", 21500 * levelScale, 29500 * levelScale, 0);
			
			new Fighter(this, "blue", 48000 * levelScale, 22500 * levelScale, 0);
			new Fighter(this, "blue", 46000 * levelScale, 22500 * levelScale, 0);
			new Interceptor(this, "blue", 49000 * levelScale, 20500 * levelScale, 0);
			new Interceptor(this, "blue", 47000 * levelScale, 20500 * levelScale, 0);
			new Interceptor(this, "blue", 45000 * levelScale, 20500 * levelScale, 0);
			
			
			new Fighter(this, "red", 67000 * levelScale, 75000 * levelScale, 180);
			new Fighter(this, "red", 68000 * levelScale, 74500 * levelScale, 180);
			new Fighter(this, "red", 66000 * levelScale, 74500 * levelScale, 180);
			new Interceptor(this, "red", 65000 * levelScale, 74000 * levelScale, 180);
			new Interceptor(this, "red", 69000 * levelScale, 74000 * levelScale, 180);
			
			new Interceptor(this, "red", 15000 * levelScale, 40000 * levelScale, 180);
			new Interceptor(this, "red", 16000 * levelScale, 39500 * levelScale, 180);
			new Interceptor(this, "red", 14000 * levelScale, 39500 * levelScale, 180);
			
			new Fighter(this, "red", 95000 * levelScale, 15000 * levelScale, 180);
			new Fighter(this, "red", 96000 * levelScale, 14500 * levelScale, 180);
			new Fighter(this, "red", 94000 * levelScale, 14500 * levelScale, 180);
			new Interceptor(this, "red", 97000 * levelScale, 14000 * levelScale, 180);
			new Interceptor(this, "red", 93000 * levelScale, 14000 * levelScale, 180);
		}
		else if (level == 5) {
			WORLD_WIDTH = 100000 * levelScale;
		    WORLD_HEIGHT = 80000 * levelScale;
		    viewX = 17500 * levelScale;
			viewY = 24000 * levelScale;
			zoomLevel = 3;
			cameraWidth = 52000 * levelScale;
			cameraHeight = 36000 * levelScale;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			new Planet(this, 13500 * levelScale, 10000 * levelScale, 1).setTeam("blue");
			new Planet(this, 30000 * levelScale, 15000 * levelScale, 2).setTeam("blue");
			new Planet(this, 22500 * levelScale, 30000 * levelScale, 3).setTeam("blue");
			new Planet(this, 47000 * levelScale, 23000 * levelScale, 4).setTeam("blue");
			
			new Planet(this, 43500 * levelScale, 42000 * levelScale, 5).setTeam("red");
			
			new Planet(this, 95000 * levelScale, 15000 * levelScale, 6).setTeam("blue");
			new Planet(this, 15000 * levelScale, 40000 * levelScale, 4).setTeam("blue");
			new Planet(this, 27000 * levelScale, 63000 * levelScale, 3).setTeam("blue");
			new Planet(this, 67000 * levelScale, 75000 * levelScale, 2).setTeam("blue");
			
			new Fighter(this, "blue", 13500 * levelScale, 10000 * levelScale, 0);
			
			new Interceptor(this, "blue", 30000 * levelScale, 15000 * levelScale, 0);
			
			new Fighter(this, "blue", 22500 * levelScale, 30000 * levelScale, 0);
			
			new Interceptor(this, "blue", 47000 * levelScale, 23000 * levelScale, 0);
			new Interceptor(this, "blue", 47000 * levelScale, 21000 * levelScale, 0);
			
			
			new Fighter(this, "blue", 67000 * levelScale, 75000 * levelScale, 0);
			
			new Fighter(this, "blue", 15000 * levelScale, 40000 * levelScale, 0);
			
			
			new Interceptor(this, "red", 43500 * levelScale, 42000 * levelScale, 0);
			new Interceptor(this, "red", 44500 * levelScale, 42000 * levelScale, 0);
			new Interceptor(this, "red", 43500 * levelScale, 41000 * levelScale, 0);
			new Interceptor(this, "red", 42500 * levelScale, 42000 * levelScale, 0);
			new Interceptor(this, "red", 43500 * levelScale, 43000 * levelScale, 0);
			new Interceptor(this, "red", (43500 * levelScale + 500 * levelScale * Math.sqrt(2)), (42000 * levelScale + 500 * levelScale * Math.sqrt(2)), 315);
			new Interceptor(this, "red", (43500 * levelScale - 500 * levelScale * Math.sqrt(2)), (42000 * levelScale + 500 * levelScale * Math.sqrt(2)), 45);
			new Interceptor(this, "red", (43500 * levelScale - 500 * levelScale * Math.sqrt(2)), (42000 * levelScale - 500 * levelScale * Math.sqrt(2)), 135);
			new Interceptor(this, "red", (43500 * levelScale + 500 * levelScale * Math.sqrt(2)), (42000 * levelScale - 500 * levelScale * Math.sqrt(2)), 225);
			new Fighter(this, "red", 45500 * levelScale, 42000 * levelScale, 0);
			new Fighter(this, "red", 43500 * levelScale, 40000 * levelScale, 0);
			new Fighter(this, "red", 41500 * levelScale, 42000 * levelScale, 0);
			new Fighter(this, "red", 43500 * levelScale, 44000 * levelScale, 0);
			new Fighter(this, "red", (43500 * levelScale + 1000 * levelScale * Math.sqrt(2)), (42000 * levelScale + 1000 * levelScale * Math.sqrt(2)), 315);
			new Fighter(this, "red", (43500 * levelScale - 1000 * levelScale * Math.sqrt(2)), (42000 * levelScale + 1000 * levelScale * Math.sqrt(2)), 45);
			new Fighter(this, "red", (43500 * levelScale - 1000 * levelScale * Math.sqrt(2)), (42000 * levelScale - 1000 * levelScale * Math.sqrt(2)), 135);
			new Fighter(this, "red", (43500 * levelScale + 1000 * levelScale * Math.sqrt(2)), (42000 * levelScale - 1000 * levelScale * Math.sqrt(2)), 225);
			new Fighter(this, "red", 46500 * levelScale, 42000 * levelScale, 0);
			new Fighter(this, "red", 43500 * levelScale, 39000 * levelScale, 0);
			new Fighter(this, "red", 40500 * levelScale, 42000 * levelScale, 0);
			new Fighter(this, "red", 43500 * levelScale, 45000 * levelScale, 0);
			new Fighter(this, "red", (43500 * levelScale + 1500 * levelScale * Math.sqrt(2)), (42000 * levelScale + 1500 * levelScale * Math.sqrt(2)), 315);
			new Fighter(this, "red", (43500 * levelScale - 1500 * levelScale * Math.sqrt(2)), (42000 * levelScale + 1500 * levelScale * Math.sqrt(2)), 45);
			new Fighter(this, "red", (43500 * levelScale - 1500 * levelScale * Math.sqrt(2)), (42000 * levelScale - 1500 * levelScale * Math.sqrt(2)), 135);
			new Fighter(this, "red", (43500 * levelScale + 1500 * levelScale * Math.sqrt(2)), (42000 * levelScale - 1500 * levelScale * Math.sqrt(2)), 225);
		}

		else if(level == 6){
			WORLD_WIDTH = 75000 * levelScale;
		    WORLD_HEIGHT = 60000 * levelScale;
		    viewX = 16000 * levelScale;
			viewY = 7000 * levelScale;
			zoomLevel = 2;
			cameraWidth = 45500 * levelScale;
			cameraHeight = 31500 * levelScale;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			
			new Planet(this, 35000 * levelScale, 40000 * levelScale, 4).setTeam("red");
			new Fighter(this, "red", 34000 * levelScale, 39500 * levelScale, 180);
			new Fighter(this, "red", 36000 * levelScale, 39500 * levelScale, 180);
			new Planet(this, 53000 * levelScale, 50000 * levelScale, 4).setTeam("red");
			new Fighter(this, "red", 52000 * levelScale, 49500 * levelScale, 180);
			new Fighter(this, "red", 54000 * levelScale, 49500 * levelScale, 180);
			
			new MissilePod(this, "red", 33000 * levelScale, 36000 * levelScale, 180);
			new MissilePod(this, "red", 35000 * levelScale, 36000 * levelScale, 180);
			new MissilePod(this, "red", 37000 * levelScale, 36000 * levelScale, 180);
			
			new MissilePod(this, "red", 31000 * levelScale, 38000 * levelScale, 90);
			new MissilePod(this, "red", 31000 * levelScale, 40000 * levelScale, 90);
			new MissilePod(this, "red", 31000 * levelScale, 42000 * levelScale, 90);
			
			new MissilePod(this, "red", 39000 * levelScale, 38000 * levelScale, 270);
			new MissilePod(this, "red", 39000 * levelScale, 40000 * levelScale, 270);
			new MissilePod(this, "red", 39000 * levelScale, 42000 * levelScale, 270);
			
			new MissilePod(this, "red", 33000 * levelScale, 44000 * levelScale, 0);
			new MissilePod(this, "red", 35000 * levelScale, 44000 * levelScale, 0);
			new MissilePod(this, "red", 37000 * levelScale, 44000 * levelScale, 0);
	
			new Planet(this, 34000 * levelScale, 10000 * levelScale, 2).setTeam("blue");
			
			new Interceptor(this, "blue", 33000 * levelScale, 11000 * levelScale, 0);
			new Interceptor(this, "blue", 33000 * levelScale, 9000 * levelScale, 0);
			new Interceptor(this, "blue", 35000 * levelScale, 11000 * levelScale, 0);
			new Interceptor(this, "blue", 35000 * levelScale, 9000 * levelScale, 0);
			
			new Planet(this, 63000 * levelScale, 13000 * levelScale, 3);
			
			new Planet(this, 17500 * levelScale, 19000 * levelScale, 5);
			
		}

		else if(level == 7){
			WORLD_WIDTH = 60000 * levelScale;
		    WORLD_HEIGHT = 80000 * levelScale;
		    viewX = 4000 * levelScale;
			viewY = 3000 * levelScale;
			zoomLevel = 3;
			cameraWidth = 52000 * levelScale;
			cameraHeight = 36000 * levelScale;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			
			new Planet(this, 31000 * levelScale, 70000 * levelScale, 6).setTeam("red");
			new Fighter(this, "red", 31000 * levelScale, 69000 * levelScale, 180);
			new Fighter(this, "red", 29500 * levelScale, 70000 * levelScale, 180);
			new Fighter(this, "red", 32500 * levelScale, 70000 * levelScale, 180);
			
			new Planet(this, 50000 * levelScale, 60000 * levelScale, 6).setTeam("red");
			new Fighter(this, "red", 50000 * levelScale, 59000 * levelScale, 180);
			new Fighter(this, "red", 48500 * levelScale, 60000 * levelScale, 180);
			new Fighter(this, "red", 51500 * levelScale, 60000 * levelScale, 180);
			
			new Planet(this, 24000 * levelScale, 65000 * levelScale, 6).setTeam("red");
			new Fighter(this, "red", 24000 * levelScale, 64000 * levelScale, 180);
			new Fighter(this, "red", 22500 * levelScale, 65000 * levelScale, 180);
			new Fighter(this, "red", 25500 * levelScale, 65000 * levelScale, 180);
			
			new Planet(this, 34000 * levelScale, 60000 * levelScale, 6).setTeam("red");
			new Fighter(this, "red", 34000 * levelScale, 59000 * levelScale, 180);
			new Fighter(this, "red", 32500 * levelScale, 60000 * levelScale, 180);
			new Fighter(this, "red", 35500 * levelScale, 60000 * levelScale, 180);
			
			new MissilePod(this, "red", 37000 * levelScale, 73000 * levelScale, 180);
			new MissilePod(this, "red", 18000 * levelScale, 65000 * levelScale, 180);
			
			new Missileship(this, "red", 42000 * levelScale, 65000 * levelScale, 180);
			new Interceptor(this, "red", 40000 * levelScale, 65000 * levelScale, 180);
			new Interceptor(this, "red", 44000 * levelScale, 65000 * levelScale, 180);
			
			new Missileship(this, "red", 50000 * levelScale, 68000 * levelScale, 180);
			new Interceptor(this, "red", 48000 * levelScale, 68000 * levelScale, 180);
			new Interceptor(this, "red", 52000 * levelScale, 68000 * levelScale, 180);
			
			new Missileship(this, "red", 15000 * levelScale, 69000 * levelScale, 180);
			new Interceptor(this, "red", 13000 * levelScale, 69000 * levelScale, 180);
			new Interceptor(this, "red", 17000 * levelScale, 69000 * levelScale, 180);
			
			new Planet(this, 19000 * levelScale, 45000 * levelScale, 2);
			new Planet(this, 28000 * levelScale, 45000 * levelScale, 2);
			new Planet(this, 51000 * levelScale, 39000 * levelScale, 1);
			
			new Planet(this, 30000 * levelScale, 20000 * levelScale, 5).setTeam("blue");
			new Interceptor(this, "blue", 28000 * levelScale, 14500 * levelScale, 0);
			new Interceptor(this, "blue", 30000 * levelScale, 14500 * levelScale, 0);
			new Interceptor(this, "blue", 32000 * levelScale, 14500 * levelScale, 0);
			new Fighter(this, "blue", 29000 * levelScale, 16000 * levelScale, 0);
			new Fighter(this, "blue", 31000 * levelScale, 16000 * levelScale, 0);
			new MissilePod(this, "blue", 27000 * levelScale, 23000 * levelScale, 0);
			new MissilePod(this, "blue", 33000 * levelScale, 23000 * levelScale, 0);
			
			new Planet(this, 14000 * levelScale, 15000 * levelScale, 5).setTeam("blue");
			new Fighter(this, "blue", 22000 * levelScale, 14000 * levelScale, 0);
			new Fighter(this, "blue", 20500 * levelScale, 13000 * levelScale, 0);
			new Fighter(this, "blue", 23500 * levelScale, 13000 * levelScale, 0);
			new Fighter(this, "blue", 22000 * levelScale, 19000 * levelScale, 0);
			new Fighter(this, "blue", 20500 * levelScale, 18000 * levelScale, 0);
			new Fighter(this, "blue", 23500 * levelScale, 18000 * levelScale, 0);
			new MissilePod(this, "blue", 11000 * levelScale, 18000 * levelScale, 0);
			new MissilePod(this, "blue", 17000 * levelScale, 18000 * levelScale, 0);
			
			new Planet(this, 46000 * levelScale, 15000 * levelScale, 5).setTeam("blue");
			new Fighter(this, "blue", 38000 * levelScale, 14000 * levelScale, 0);
			new Fighter(this, "blue", 36500 * levelScale, 13000 * levelScale, 0);
			new Fighter(this, "blue", 39500 * levelScale, 13000 * levelScale, 0);
			new Fighter(this, "blue", 38000 * levelScale, 19000 * levelScale, 0);
			new Fighter(this, "blue", 36500 * levelScale, 18000 * levelScale, 0);
			new Fighter(this, "blue", 39500 * levelScale, 18000 * levelScale, 0);
			new MissilePod(this, "blue", 43000 * levelScale, 18000 * levelScale, 0);
			new MissilePod(this, "blue", 49000 * levelScale, 18000 * levelScale, 0);
			
			
		}
		
		else if (level == 8) {
			WORLD_WIDTH = 100000 * levelScale;
		    WORLD_HEIGHT = 80000 * levelScale;
		    viewX = 17500 * levelScale;
			viewY = 24000 * levelScale;
			zoomLevel = 3;
			cameraWidth = 52000 * levelScale;
			cameraHeight = 36000 * levelScale;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			new Planet(this, 13500 * levelScale, 10000 * levelScale, 1).setTeam("red");
			new Planet(this, 30000 * levelScale, 15000 * levelScale, 2).setTeam("red");
			new Planet(this, 22500 * levelScale, 30000 * levelScale, 3).setTeam("red");
			new Planet(this, 47000 * levelScale, 23000 * levelScale, 4).setTeam("red");
			
			new Planet(this, 43500 * levelScale, 42000 * levelScale, 5).setTeam("blue");
			
			new Planet(this, 95000 * levelScale, 15000 * levelScale, 6).setTeam("red");
			new Planet(this, 15000 * levelScale, 40000 * levelScale, 4).setTeam("red");
			new Planet(this, 27000 * levelScale, 63000 * levelScale, 3).setTeam("red");
			new Planet(this, 67000 * levelScale, 75000 * levelScale, 2).setTeam("red");
			
			new Fighter(this, "red", 13500 * levelScale, 10000 * levelScale, 0);
			new Fighter(this, "red", 12500 * levelScale, 9500 * levelScale, 0);
			new Fighter(this, "red", 14500 * levelScale, 9500 * levelScale, 0);
			
			new Interceptor(this, "red", 30000 * levelScale, 15000 * levelScale, 0);
			
			new Fighter(this, "red", 22500 * levelScale, 30000 * levelScale, 0);
			
			new Interceptor(this, "red", 47000 * levelScale, 23000 * levelScale, 0);
			new Interceptor(this, "red", 47000 * levelScale, 21000 * levelScale, 0);
			
			
			new Fighter(this, "red", 67000 * levelScale, 75000 * levelScale, 0);
			
			new Fighter(this, "red", 15000 * levelScale, 40000 * levelScale, 0);
			
			new Missileship(this, "red", 95000 * levelScale, 15000 * levelScale, 0);
			new Interceptor(this, "red", 94000 * levelScale, 14000 * levelScale, 0);
			new Interceptor(this, "red", 96000 * levelScale, 14000 * levelScale, 0);
			
			
			new Interceptor(this, "blue", 43500 * levelScale, 42000 * levelScale, 0);
			new Interceptor(this, "blue", 44500 * levelScale, 42000 * levelScale, 0);
			new Interceptor(this, "blue", 43500 * levelScale, 41000 * levelScale, 0);
			new Interceptor(this, "blue", 42500 * levelScale, 42000 * levelScale, 0);
			new Interceptor(this, "blue", 43500 * levelScale, 43000 * levelScale, 0);
			new Interceptor(this, "blue", (43500 * levelScale + 500 * levelScale * Math.sqrt(2)), (42000 * levelScale + 500 * levelScale * Math.sqrt(2)), 315);
			new Interceptor(this, "blue", (43500 * levelScale - 500 * levelScale * Math.sqrt(2)), (42000 * levelScale + 500 * levelScale * Math.sqrt(2)), 45);
			new Interceptor(this, "blue", (43500 * levelScale - 500 * levelScale * Math.sqrt(2)), (42000 * levelScale - 500 * levelScale * Math.sqrt(2)), 135);
			new Interceptor(this, "blue", (43500 * levelScale + 500 * levelScale * Math.sqrt(2)), (42000 * levelScale - 500 * levelScale * Math.sqrt(2)), 225);
			new Fighter(this, "blue", 45500 * levelScale, 42000 * levelScale, 0);
			new Fighter(this, "blue", 41500 * levelScale, 42000 * levelScale, 0);
			new Fighter(this, "blue", (43500 * levelScale + 1000 * levelScale * Math.sqrt(2)), (42000 * levelScale + 1000 * levelScale * Math.sqrt(2)), 315);
			new Fighter(this, "blue", (43500 * levelScale - 1000 * levelScale * Math.sqrt(2)), (42000 * levelScale + 1000 * levelScale * Math.sqrt(2)), 45);
			new Fighter(this, "blue", (43500 * levelScale - 1000 * levelScale * Math.sqrt(2)), (42000 * levelScale - 1000 * levelScale * Math.sqrt(2)), 135);
			new Fighter(this, "blue", (43500 * levelScale + 1000 * levelScale * Math.sqrt(2)), (42000 * levelScale - 1000 * levelScale * Math.sqrt(2)), 225);
			new Fighter(this, "blue", 46500 * levelScale, 42000 * levelScale, 0);
			new Fighter(this, "blue", 43500 * levelScale, 45000 * levelScale, 0);
			new Fighter(this, "blue", (43500 * levelScale + 1500 * levelScale * Math.sqrt(2)), (42000 * levelScale + 1500 * levelScale * Math.sqrt(2)), 315);
			new Fighter(this, "blue", (43500 * levelScale - 1500 * levelScale * Math.sqrt(2)), (42000 * levelScale + 1500 * levelScale * Math.sqrt(2)), 45);
			new Fighter(this, "blue", (43500 * levelScale - 1500 * levelScale * Math.sqrt(2)), (42000 * levelScale - 1500 * levelScale * Math.sqrt(2)), 135);
			new Fighter(this, "blue", (43500 * levelScale + 1500 * levelScale * Math.sqrt(2)), (42000 * levelScale - 1500 * levelScale * Math.sqrt(2)), 225);
		}

		else if(level == 9){
			WORLD_WIDTH = 125000 * levelScale;
		    WORLD_HEIGHT = 50000 * levelScale;
		    viewX = 37250 * levelScale;
			viewY = 9250 * levelScale;
			zoomLevel = 3;
			cameraWidth = 45500 * levelScale;
			cameraHeight = 31500 * levelScale;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			new Planet(this, 57500 * levelScale, 30000 * levelScale, 3).setTeam("blue");
			new MachineGunPod(this, "blue", 62000 * levelScale, 31000 * levelScale, 270);
			new MachineGunPod(this, "blue", 62000 * levelScale, 29000 * levelScale, 270);
			new MachineGunPod(this, "blue", 53000 * levelScale, 31000 * levelScale, 90);
			new MachineGunPod(this, "blue", 53000 * levelScale, 29000 * levelScale, 90);
			new Fighter(this, "blue", 56800 * levelScale, 30700 * levelScale, 45);
			new Fighter(this, "blue", 56800 * levelScale, 29300 * levelScale, 135);
			new Fighter(this, "blue", 58200 * levelScale, 30700 * levelScale, 315);
			new Fighter(this, "blue", 58200 * levelScale, 29300 * levelScale, 225);
			new Planet(this, 62500 * levelScale, 20000 * levelScale, 4).setTeam("blue");
			new MachineGunPod(this, "blue", 67000 * levelScale, 21000 * levelScale, 270);
			new MachineGunPod(this, "blue", 67000 * levelScale, 19000 * levelScale, 270);
			new MachineGunPod(this, "blue", 58000 * levelScale, 21000 * levelScale, 90);
			new MachineGunPod(this, "blue", 58000 * levelScale, 19000 * levelScale, 90);
			new Fighter(this, "blue", 61800 * levelScale, 20700 * levelScale, 45);
			new Fighter(this, "blue", 61800 * levelScale, 19300 * levelScale, 135);
			new Fighter(this, "blue", 63200 * levelScale, 20700 * levelScale, 315);
			new Fighter(this, "blue", 63200 * levelScale, 19300 * levelScale, 225);
			new Planet(this, 40000 * levelScale, 25000 * levelScale, 3);
			new Planet(this, 75000 * levelScale, 25000 * levelScale, 6);
			
			new Planet(this, 10000 * levelScale, 38000 * levelScale, 3).setTeam("red");
			new Fighter(this, "red", 10000 * levelScale, 38600 * levelScale, 270);
			new Fighter(this, "red", 10000 * levelScale, 37400 * levelScale, 270);
			new BasicPod(this, "red", 12000 * levelScale, 38600 * levelScale, 270);
			new BasicPod(this, "red", 12000 * levelScale, 37400 * levelScale, 270);
			new Planet(this, 12000 * levelScale, 26000 * levelScale, 4).setTeam("red");
			new Fighter(this, "red", 12000 * levelScale, 26600 * levelScale, 270);
			new Fighter(this, "red", 12000 * levelScale, 25400 * levelScale, 270);
			new BasicPod(this, "red", 14000 * levelScale, 26600 * levelScale, 270);
			new BasicPod(this, "red", 14000 * levelScale, 25400 * levelScale, 270);
			new Planet(this, 9000 * levelScale, 13000 * levelScale, 5).setTeam("red");
			new Fighter(this, "red", 9000 * levelScale, 13600 * levelScale, 270);
			new Fighter(this, "red", 9000 * levelScale, 12400 * levelScale, 270);
			new BasicPod(this, "red", 11000 * levelScale, 13600 * levelScale, 270);
			new BasicPod(this, "red", 11000 * levelScale, 12400 * levelScale, 270);
			
			new Planet(this, 119000 * levelScale, 27000 * levelScale, 4).setTeam("red");
			new Missileship(this, "red", 119000 * levelScale, 27000 * levelScale, 90);
			new Planet(this, 119000 * levelScale, 36000 * levelScale, 4).setTeam("red");
			new Missileship(this, "red", 119000 * levelScale, 36000 * levelScale, 90);
			new Planet(this, 96000 * levelScale, 41000 * levelScale, 3).setTeam("red");
			new Fighter(this, "red", 96000 * levelScale, 41600 * levelScale, 90);
			new Fighter(this, "red", 96000 * levelScale, 40400 * levelScale, 90);
			new Planet(this, 94600 * levelScale, 31500 * levelScale, 5).setTeam("red");
			new Fighter(this, "red", 94600 * levelScale, 32100 * levelScale, 90);
			new Fighter(this, "red", 94600 * levelScale, 30900 * levelScale, 90);
			new Planet(this, 110000 * levelScale, 16000 * levelScale, 3).setTeam("red");
			new Interceptor(this, "red", 109500 * levelScale, 16500 * levelScale, 80);
			new Interceptor(this, "red", 109500 * levelScale, 15500 * levelScale, 80);
			new Interceptor(this, "red", 110500 * levelScale, 16500 * levelScale, 80);
			new Interceptor(this, "red", 110500 * levelScale, 15500 * levelScale, 80);
			
			
		}
		else if(level == 10){
			WORLD_WIDTH = 120000 * levelScale;
		    WORLD_HEIGHT = 40000 * levelScale;
		    viewX = 250 * levelScale;
			viewY = 0 * levelScale;
			zoomLevel = 3;
			cameraWidth = 45500 * levelScale;
			cameraHeight = 31500 * levelScale;
			new Planet(this, 13000 * levelScale, 25000 * levelScale, 2).setTeam("blue");
			new Planet(this, 12000 * levelScale, 13000 * levelScale, 2).setTeam("blue");
			new Planet(this, 56000 * levelScale, 32000 * levelScale, 2).setTeam("red");
			new Planet(this, 100000 * levelScale, 30000 * levelScale, 2).setTeam("red");
			new Planet(this, 90000 * levelScale, 7000 * levelScale, 2).setTeam("red");
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));

			new MachineGunPod(this, "red", 37000 * levelScale, 24000 * levelScale, 90);
			new MachineGunPod(this, "red", 37000 * levelScale, 26000 * levelScale, 90);
			new MachineGunPod(this, "red", 37000 * levelScale, 28000 * levelScale, 90);
			new MissilePod(this, "red", 39000 * levelScale, 24000 * levelScale, 90);
			new MissilePod(this, "red", 39000 * levelScale, 26000 * levelScale, 90);
			new MissilePod(this, "red", 39000 * levelScale, 28000 * levelScale, 90);
			new MachineGunPod(this, "red", 38500 * levelScale, 31000 * levelScale, 90);
			new MachineGunPod(this, "red", 38500 * levelScale, 33000 * levelScale, 90);
			new MachineGunPod(this, "red", 38500 * levelScale, 35000 * levelScale, 90);
			new MissilePod(this, "red", 40500 * levelScale, 31000 * levelScale, 90);
			new MissilePod(this, "red", 40500 * levelScale, 33000 * levelScale, 90);
			new MissilePod(this, "red", 40500 * levelScale, 35000 * levelScale, 90);
			new MachineGunPod(this, "red", 38500 * levelScale, 21000 * levelScale, 90);
			new MachineGunPod(this, "red", 38500 * levelScale, 19000 * levelScale, 90);
			new MachineGunPod(this, "red", 38500 * levelScale, 17000 * levelScale, 90);
			new MissilePod(this, "red", 40500 * levelScale, 21000 * levelScale, 90);
			new MissilePod(this, "red", 40500 * levelScale, 19000 * levelScale, 90);
			new MissilePod(this, "red", 40500 * levelScale, 17000 * levelScale, 90);
			
			
			new MachineGunPod(this, "red", 55000 * levelScale, 10000 * levelScale, 90);
			new MachineGunPod(this, "red", 55000 * levelScale, 12000 * levelScale, 90);
			new MachineGunPod(this, "red", 55000 * levelScale, 14000 * levelScale, 90);
			new MissilePod(this, "red", 57000 * levelScale, 10000 * levelScale, 90);
			new MissilePod(this, "red", 57000 * levelScale, 12000 * levelScale, 90);
			new MissilePod(this, "red", 57000 * levelScale, 14000 * levelScale, 90);
			new MachineGunPod(this, "red", 56500 * levelScale, 17000 * levelScale, 90);
			new MachineGunPod(this, "red", 56500 * levelScale, 19000 * levelScale, 90);
			new MachineGunPod(this, "red", 56500 * levelScale, 21000 * levelScale, 90);
			new MissilePod(this, "red", 58500 * levelScale, 17000 * levelScale, 90);
			new MissilePod(this, "red", 58500 * levelScale, 19000 * levelScale, 90);
			new MissilePod(this, "red", 58500 * levelScale, 21000 * levelScale, 90);
			new MachineGunPod(this, "red", 56500 * levelScale, 7000 * levelScale, 90);
			new MachineGunPod(this, "red", 56500 * levelScale, 5000 * levelScale, 90);
			new MachineGunPod(this, "red", 56500 * levelScale, 3000 * levelScale, 90);
			new MissilePod(this, "red", 58500 * levelScale, 7000 * levelScale, 90);
			new MissilePod(this, "red", 58500 * levelScale, 5000 * levelScale, 90);
			new MissilePod(this, "red", 58500 * levelScale, 3000 * levelScale, 90);
			
			new MachineGunPod(this, "red", 73000 * levelScale, 24000 * levelScale, 90);
			new MachineGunPod(this, "red", 73000 * levelScale, 26000 * levelScale, 90);
			new MachineGunPod(this, "red", 73000 * levelScale, 28000 * levelScale, 90);
			new MissilePod(this, "red", 75000 * levelScale, 24000 * levelScale, 90);
			new MissilePod(this, "red", 75000 * levelScale, 26000 * levelScale, 90);
			new MissilePod(this, "red", 75000 * levelScale, 28000 * levelScale, 90);
			new MachineGunPod(this, "red", 74500 * levelScale, 31000 * levelScale, 90);
			new MachineGunPod(this, "red", 74500 * levelScale, 33000 * levelScale, 90);
			new MachineGunPod(this, "red", 74500 * levelScale, 35000 * levelScale, 90);
			new MissilePod(this, "red", 76500 * levelScale, 31000 * levelScale, 90);
			new MissilePod(this, "red", 76500 * levelScale, 33000 * levelScale, 90);
			new MissilePod(this, "red", 76500 * levelScale, 35000 * levelScale, 90);
			new MachineGunPod(this, "red", 74500 * levelScale, 21000 * levelScale, 90);
			new MachineGunPod(this, "red", 74500 * levelScale, 19000 * levelScale, 90);
			new MachineGunPod(this, "red", 74500 * levelScale, 17000 * levelScale, 90);
			new MissilePod(this, "red", 76500 * levelScale, 21000 * levelScale, 90);
			new MissilePod(this, "red", 76500 * levelScale, 19000 * levelScale, 90);
			new MissilePod(this, "red", 76500 * levelScale, 17000 * levelScale, 90);
			

			
		}
		//Define current dimensions of the map being observed (space by default)
		//TODO Try making some of the below (e.g. plant surface creation) functions, so they
		//can be conveniently called in the above if statements
		currentMin_X = 0;
		currentMin_Y = 0;
		currentMax_X = WORLD_WIDTH;
		currentMax_Y = WORLD_HEIGHT;
		//TODO Doing setScreenBounds within the constructor is causing hellish problems. I'll have to do it here again
		//TODO Note, this will not work if one plans to generate surface ships within the level definition.
		//TODO Solution is to never spawn surface ships
//		for (int i = 0; i < ships.size(); i++) {
//			ships.get(i).setScreenBounds(getScreenBounds());
//		}
		
		
//		//Define dimensions of the space map (which will be used to return to space when needed)
//	    spaceMin_X = 0;
//	    spaceMin_Y = 0;
//	    spaceMax_X = WORLD_WIDTH;
//	    spaceMax_Y = WORLD_HEIGHT;
//	    
//	    //Create planet surfaces
//	    for (int i = 0; i < planets.size(); i++) {
//			Planet p = planets.get(i);
//			p.surfaceX = -100000 + i * 100000;
//			p.surfaceY = -100000;
//			p.dimensionX = 10000;
//			p.dimensionY = 10000;
//			
//			//TODO Hardcoding tile size isn't probably a good idea
//			Tile temp = new Tile(this, p.surfaceX + p.dimensionX / 2, p.surfaceY + p.dimensionY / 2, 15000);
//			//TODO Ask Nathan to get some proper backgrounds for planet surfaces and store them in Tile the same way
//			//TODO that Planet stores planet textures (needs transparency, or no FOG, or even better, make all tiles opaque but use glDraw so that it's set to 90%)
//			temp.setSpecialTexture(Planet.textures[p.texId]);
//			
//			loadSubLevel(p);
//			//Testing with 10 ships
//			for (int j = 0; j < 10; j++) {
//				new Fighter(this, "blue", p.surfaceX + 500, p.surfaceY + 500, 0);
//			}
//			loadSubLevel(null);
//		}
	    
		playerList.add(player);
		playerList.add(enemy.getPlayer());
	}
	
	//Loads a sublevel, changing START_X and END_X and stuff. If parameter is null, load space
	//Save values of space dimensions in order to make future loading of space easier
	public void loadSubLevel(Planet p) {
		if (p == null) {
			currentMin_X = spaceMin_X;
			currentMin_Y = spaceMin_Y;
			currentMax_X = spaceMax_X;
			currentMax_Y = spaceMax_Y;
			viewX = spaceView_X;
			viewY = spaceView_Y;
			cameraWidth = spaceViewWidth;
			cameraHeight = spaceViewHeight;
		}
		else {
			spaceView_X = viewX;
			spaceView_Y = viewY;
			spaceViewWidth = cameraWidth;
			spaceViewHeight = cameraHeight;
			currentMin_X = p.surfaceX;
			currentMin_Y = p.surfaceY;
			currentMax_X = p.surfaceX + p.dimensionX;
			currentMax_Y = p.surfaceY + p.dimensionY;
			viewX = currentMin_X;
			viewY = currentMin_Y;
			cameraWidth = 2600;
			cameraHeight = 1800;
		}
		
		if(player.selectedPlanet != null) player.selectedPlanet.setSelected(false);
		player.selectedPlanet = null;
		for (int i = 0; i < ships.size(); i++) {
			ships.get(i).setSelected(false);
		}
	}
	
	//check projectile collisions
	public void checkProjectiles(){
    	for (int i = 0; i < projectiles.size(); i++) {
    		Projectile p = projectiles.get(i);
			for (int j = 0; j < ships.size(); j++) {
				Starship s = ships.get(j);
				//Check all ships to see if a collision with projectile is valid
				if(polygon_intersection(p.getPoints(), s.getPoints()) && (p.getOwner() == null || !p.getOwner().equals(s))
						&& (!p.getTeam().equals(s.getTeam()) || p.getTeam().equals("none"))){
					//Figure out what type of ship this is and what type of projectile this is
					int shipType = getShipType(s);
					int projectileType = p.texId;
					//If this projectile has not hit this ship before, hit it.
					if (!p.pierced.contains(s)) {
						s.setHealth(s.getHealth() - damageMultipliers[shipType][projectileType] * p.getDamage());
					}
	    			s.damageDisplayDelay = 1000;
	    			//Special destructor is called for missiles
	    			if (p instanceof Missile) {
	    				Missile m = (Missile)p;
	    				m.destroy(s);
	    			}
					//If projectile is splash, do damage to all ships within radius that are enemies
					if (p.splash) {
						for (int k = 0; k < ships.size(); k++) {
							Starship splashCheck = ships.get(k);
							if (distance(p.center.X(), p.center.Y(), splashCheck.getX(), splashCheck.getY()) < p.splashSize
									&& (!p.getTeam().equals(s.getTeam()) || p.getTeam().equals("null"))) {
								shipType = getShipType(splashCheck);
								//Piercing checks not performed with splashCheck, hopefully won't bite later
								splashCheck.setHealth(splashCheck.getHealth() - damageMultipliers[shipType][projectileType] * p.getDamage());
								splashCheck.damageDisplayDelay = 1000;
							}
						}
					}
	    			//If projectile is not piercing, destroy it. Else, allow it to hit targets not already pierced
					if (!p.piercing) {
						p.destroy();
						projectiles.remove(p);
					} else if (!p.pierced.contains(s)) {
						new Explosion(this, p.center.X()
								+ random.nextInt(21) - 10, p.center.Y()
								+ random.nextInt(21) - 10, 30);
						p.pierced.add(s);
					}
	    		}
			}
		}
    }
	

	
	private int getShipType(Starship s) {
		if (s instanceof Fighter) return 1;
		if (s instanceof Interceptor) return 2;
		if (s instanceof Missileship) return 3;
		if (s instanceof Wallship) return 4;
		if (s instanceof Sniper) return 5;
		if (s instanceof Battleship) return 6;
		//Do we have to differentiate between Pods, Radar, etc? Or all 0 could be fine.
		return 0;
	}

	//returns an int from 0 to 359
	public int angleToPoint(double center_x, double center_y, double target_x, double target_y){
    	//first quadrant
    	if(target_x >= center_x && target_y >= center_y){
    		return 360 - (int) Math.round(Math.toDegrees(Math.atan((target_x - center_x)/(target_y - center_y))));
    	}
    	//second quadrant
    	else if(target_x <= center_x && target_y >= center_y){
    		return -(int) Math.round(Math.toDegrees(Math.atan((target_x - center_x)/(target_y - center_y))));
    	}
    	//third quadrant
    	else if(target_x <= center_x && target_y <= center_y){
    		return 180 - (int) Math.round(Math.toDegrees(Math.atan((target_x - center_x)/(target_y - center_y))));
    	}
    	//fourth quadrant
    	else if(target_x >= center_x && target_y <= center_y){
    		return 180 - (int) Math.round(Math.toDegrees(Math.atan((target_x - center_x)/(target_y - center_y))));
    	}
    	else{
    		return 0;
    	}
    }
	
	public double normalizeAngle(double angle){
		while(angle < 0){
			angle += 360;
		}
		while(angle >= 360){
			angle -= 360;
		}
		//convert to two decimal places
		angle = ((double) Math.round(angle * 100)) / 100;
		return angle;
	}
    
    public double distance(double x1, double y1, double x2, double y2){
    	return Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1),2));
    }
	
	//Checks whether two lines intersect
    //a1 and a2 are endpoints of line a and b1 and b2 are endpoints of line b
    public boolean line_intersection(Point a1, Point a2, Point b1, Point b2){
    	double intersect_calc = ((b2.Y() - b1.Y()) * (a2.X() - a1.X())) - ((b2.X() - b1.X()) * (a2.Y() - a1.Y()));
	    if(intersect_calc == 0){
	    	return false;
	    }

	    else{
	        double ua = (((b2.X() - b1.X())*(a1.Y() - b1.Y()) - (b2.Y() - b1.Y())*(a1.X() - b1.X()))
	        		/ ((b2.Y() - b1.Y())*(a2.X() - a1.X()) - (b2.X() - b1.X())*(a2.Y() - a1.Y())));
	        double ub = (((a2.X() - a1.X())*(a1.Y() - b1.Y()) - (a2.Y() - a1.Y())*(a1.X() - b1.X()))
	              / ((b2.Y() - b1.Y())*(a2.X() - a1.X()) - (b2.X() - b1.X())*(a2.Y() - a1.Y())));
	        if(ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1){
	            return true;
	        }
	    }
	    return false;
    }
    
    //Checks whether two lines intersect
    //a1 and a2 are endpoints of line a and b1 and b2 are endpoints of line b
    public double[] line_intersection_coordinate(Point a1, Point a2, Point b1, Point b2){
    	double[] isct = {-1, -1};
    	double intersect_calc = ((b2.Y() - b1.Y()) * (a2.X() - a1.X())) - ((b2.X() - b1.X()) * (a2.Y() - a1.Y()));
	    if(intersect_calc == 0){
	    	return isct;
	    }

	    else{
	        double ua = (((b2.X() - b1.X())*(a1.Y() - b1.Y()) - (b2.Y() - b1.Y())*(a1.X() - b1.X()))
	        		/ ((b2.Y() - b1.Y())*(a2.X() - a1.X()) - (b2.X() - b1.X())*(a2.Y() - a1.Y())));
	        double ub = (((a2.X() - a1.X())*(a1.Y() - b1.Y()) - (a2.Y() - a1.Y())*(a1.X() - b1.X()))
	              / ((b2.Y() - b1.Y())*(a2.X() - a1.X()) - (b2.X() - b1.X())*(a2.Y() - a1.Y())));
	        if(ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1){
	        	isct[0] = (a1.X()*ua);
	        	isct[1] = (a1.Y()*ua);
	            return isct;
	        }
	    }
	    return isct;
    }
    
    //Checks if two shapes are intersecting by checking all the lines for an intersection
    public boolean polygon_intersection(Point[] shapeA, Point[] shapeB){
    	int next_shapeA_index, next_shapeB_index;
    	for (int i = 0; i < shapeA.length; i++) {
    		//set the index for the end point of the line being checked (shapeA)
    		if(i == shapeA.length - 1){
    			next_shapeA_index = 0;
    		}
    		else{
    			next_shapeA_index = i + 1;
    		}
			for (int j = 0; j < shapeB.length; j++) {
				//set the index for the end point of the line being checked (shapeB)
	    		if(j == shapeB.length - 1){
	    			next_shapeB_index = 0;
	    		}
	    		else{
	    			next_shapeB_index = j + 1;
	    		}
				boolean hit = line_intersection(shapeA[i], shapeA[next_shapeA_index], shapeB[j], shapeB[next_shapeB_index]);
				if(hit){
					return true;
				}
			}
		}
        return false;
    }
    
    public double[] polygon_intersection_coordinate(Point[] shapeA, Point[] shapeB){
    	int next_shapeA_index, next_shapeB_index;
    	for (int i = 0; i < shapeA.length; i++) {
    		//set the index for the end point of the line being checked (shapeA)
    		if(i == shapeA.length - 1){
    			next_shapeA_index = 0;
    		}
    		else{
    			next_shapeA_index = i + 1;
    		}
			for (int j = 0; j < shapeB.length; j++) {
				//set the index for the end point of the line being checked (shapeB)
	    		if(j == shapeB.length - 1){
	    			next_shapeB_index = 0;
	    		}
	    		else{
	    			next_shapeB_index = j + 1;
	    		}
				double[] hit = line_intersection_coordinate(shapeA[i], shapeA[next_shapeA_index], shapeB[j], shapeB[next_shapeB_index]);
				if(hit[0] != -1){
					return hit;
				}
			}
		}
        return null;
    }
    
    public void genTiles() {
    	int tileSize = 800;
    	for (int x = (int)(-tileSize); x <= WINDOW_WIDTH + tileSize*2; x += tileSize*2) {
    		for (int y = (int)(-tileSize); y <= WINDOW_HEIGHT + tileSize*2; y += tileSize*2) {
				new Tile(this, x, y, tileSize);
			}
		}
    }
    
    public void destroyAllShips(){
    	for (int i = ships.size()-1; i >= 0; i--) {
			ships.get(i).destroy();
		}
    }
    
    public void destroyAllPlanets(){
    	for (int i = planets.size()-1; i >= 0; i--) {
			planets.get(i).destroy();
		}
    }
    
    public void destroyAllProjectiles(){
    	for (int i = projectiles.size()-1; i >= 0; i--) {
    		if (projectiles.get(i) instanceof Missile) projectiles.get(i).center = new Point(100000, 100000);
			projectiles.get(i).destroy();
		}
    }
    
    public void destroyAllExplosions(){
    	for (int i = explosions.size()-1; i >= 0; i--) {
			explosions.get(i).destroy();
		}
    }
    
    public void destroyAllText(){
    	for (int i = text.size()-1; i >= 0; i--) {
			text.get(i).destroy();
		}
    }
    
    public ArrayList<Starship> getAllShips(){
    	return ships;
    }
    
    public ArrayList<Planet> getAllPlanets(){
    	return planets;
    }
    
    public ArrayList<Player> getPlayers(){
    	return playerList;
    }
	
    public void addShip(Starship s){
    	ships.add(s);
	}
	    
	public void removeShip(Starship s){
		ships.remove(s);
	}
	 
	public void addProjectile(Projectile p){
		projectiles.add(p);
	}
	
	public void removeProjectile(Projectile p){
		projectiles.remove(p);
	}
	
	public void addExplosion(Explosion e){
		explosions.add(e);
	}
	
	public void removeExplosion(Explosion e){
		explosions.remove(e);
	}
	
	public void addPlanet(Planet p){
    	planets.add(p);
	}
	    
	public void removePlanet(Planet p){
		planets.remove(p);
	}
	
	public void addTile(Tile t){
    	backgroundTiles.add(t);
	}
	    
	public void removeTile(Tile t){
		backgroundTiles.remove(t);
	}
	
	public void addLetter(BitmapFontLetter l){
		text.add(l);
	}
	
	public void removeLetter(BitmapFontLetter l){
		text.remove(l);
	}
	
	public double getCameraX(){
		return viewX;
	}
	
	public double getCameraY(){
		return viewY;
	}
	
	public double getCameraWidth(){
		return cameraWidth;
	}
	
	public double getCameraHeight(){
		return cameraHeight;
	}
	
	public int getWindowWidth(){
		return WINDOW_WIDTH;
	}
	
	public int getWindowHeight(){
		return WINDOW_HEIGHT;
	}
	
	public double getWidthScalar(){
		return(double) cameraWidth / (double) WINDOW_WIDTH;
	}
	
	public double getHeightScalar(){
		return(double) cameraHeight / (double) WINDOW_HEIGHT;
	}
	
	//Text is written such that the first letter of the text has center at startx, starty
	public void writeText(String newText, int startx, int starty) {
		writeText(newText, startx, starty, 20);
	}
	
	public void writeText(String newText, int startx, int starty, int textSize) {
		for (int i = 0; i < newText.length(); i++) {
			new BitmapFontLetter(this, newText.charAt(i), startx + i * textSize, starty, textSize);
		 }
	}
	
	//Add audio clip. Used for explosions.
//	public void addClip(String fileName, float modifier) {
//		if(mute){
//			return;
//		}
//		try {
//			Clip clip = AudioSystem.getClip();
//			File file = new File(fileName);
//			AudioInputStream sound = AudioSystem.getAudioInputStream(file);
//			clip.open(sound);
//			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
//			gainControl.setValue(modifier); // Reduce volume by a number of decibels.
//			clip.start();
//			explosionSounds.add(clip);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	//Recursively get a proximity group of allied ships. Used to find approximate fleet strength and/or do proximity damage
	public void proximityGroup(ArrayList<Starship> chosen, ArrayList<Starship> allShips, Starship current) {
		for (int i = 0; i < allShips.size(); i++) {
			Starship s = allShips.get(i);
			if (distance(current.center.X(), current.center.Y(), s.center.X(), s.center.Y()) < PROXIMITY_SIZE
					&& current.team.equals(s.team) && !chosen.contains(s)) {
				chosen.add(s);
				proximityGroup(chosen, allShips, s);
			}
		}
	}
	
}