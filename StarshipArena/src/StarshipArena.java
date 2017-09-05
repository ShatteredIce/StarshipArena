//Starship Arena - Created on 1/11/17 by Nathan Purwosumarto
//Example of LWJGL 3, displays ships that move randomly in the window

//ANGLE SYSTEM (DO NOT DELETE THESE, DUJIN NEEDS THESE FOR REFERENCE):
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

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.*;

import java.io.File;
import java.io.IOException;

public class StarshipArena {
	
	Window window;
	
	int WINDOW_WIDTH = 1300;
	int WINDOW_HEIGHT = 900;
	
	int windowXOffset;
	int windowYOffset;
	
	int WORLD_WIDTH = 26000;
    int WORLD_HEIGHT = 18000;

    int CURR_X = 0;
	int CURR_Y = 0;
	int CAMERA_SPEED = 10;
	int CAMERA_WIDTH = 2600;
	int CAMERA_HEIGHT = 1800;
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
    boolean f1Pressed = false;
    boolean lPressed = false;
    
    DoubleBuffer oldMouseX;
    DoubleBuffer oldMouseY;
    DoubleBuffer newMouseX;
    DoubleBuffer newMouseY;
    
    int FIGHTER_COST = 5;
    int INTERCEPTOR_COST = 20;
    int TRANSPORT_COST = 10;
    int MISSILESHIP_COST = 40;
    
    int PROXIMITY_SIZE = 150;
    
    int planetDisplayBorder = 400;
    int shipDisplayBorder = 100;
    
    float PLASMA_DB = -20.0f;
    float MGUN_DB = -18.0f;
    float MISSILE_DB = -4.0f;
    float LASER_DB = -8.0f;
    float HITEX_DB = -8.0f;
    float DEATHEX_DB = 1.0f;
    
	Random random = new Random();
	
	ArrayList<Starship> ships = new ArrayList<>();
	ArrayList<Projectile> projectiles = new ArrayList<>();
	ArrayList<Explosion> explosions = new ArrayList<>();
	ArrayList<Planet> planets = new ArrayList<>();
	ArrayList<Tile> backgroundTiles = new ArrayList<>();
	ArrayList<Player> playerList = new ArrayList<>(); 
	ArrayList<BitmapFontLetter> text = new ArrayList<>();
	Clip[] soundEffects = new Clip[50];
	
//	File temp = new File("sounds/music/Earth.wav");
	File temp = new File("sounds/music/Yin & Yang.wav");
	AudioInputStream BGM = AudioSystem.getAudioInputStream(temp);
//	File temp2 = new File("sounds/music/Journey to the Sky.wav");
	File temp2 = new File("sounds/music/Vague.wav");
	AudioInputStream menuBGM = AudioSystem.getAudioInputStream(temp2);
	Clip gameMusic;
	Clip menuMusic;
	
	//TODO it's not a true mute yet
	//TODO Actually, it might be now. I forgot, check soon.
	boolean mute = false;
	boolean fog = true;
	
	

	Sidebar sidebar;
	Layer titlePage;
	Layer levelSelect;
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
	
	Layer boxSelect;
	boolean boxSelectCurrent = false;
	
	Layer settingsIcon;
	Button settingsButton = new Button(1250, 898, 1298, 850);
	
	Layer planetMainMenu;
	Button planetBuyButton = new Button(WINDOW_WIDTH - 380, 80, WINDOW_WIDTH - 280, 20);
	Button planetIndustryButton = new Button(WINDOW_WIDTH - 250, 80, WINDOW_WIDTH - 150, 20);
	Button planetEconomyButton = new Button(WINDOW_WIDTH - 120, 80, WINDOW_WIDTH - 20, 20);
	
	Layer border1;
	Layer border2;
	Layer border3;
	Layer border4;
	
	Layer victoryMessage;
	Layer defeatMessage;
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
		System.out.println(windowXOffset);
		System.out.println(windowYOffset);
		
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
			
			if( key == GLFW_KEY_Z && action == GLFW_RELEASE ){
				for (int s = 0; s < ships.size(); s++) {
					if(ships.get(s).getTeam().equals(player.getTeam())){
						ships.get(s).setLocationTarget(null);
					}
				}
			}
			
			if ( key == GLFW_KEY_L && action == GLFW_PRESS )
				lPressed = true;
			if ( key == GLFW_KEY_L && action == GLFW_RELEASE )
				lPressed = false;
			
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
				if (typesSelected.size() > 0) {
					for (int i = 0; i < ships.size(); i++) {
						for (int j = 0; j < typesSelected.size(); j++) {
							if (typesSelected.get(j).getClass().equals(ships.get(i).getClass())
									&& typesSelected.get(j).getTeam().equals(ships.get(i).getTeam()))
								ships.get(i).setSelected(true);
						}
					}
				}
				else {
					for (int i = 0; i < ships.size(); i++) {
						if (ships.get(i).getTeam().equals(player.getTeam()) && !(ships.get(i) instanceof BasicPod)){
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
				if(shiftPressed){
					assignControlGroup(player, 0);
				}
			}
			if ( key == GLFW_KEY_1 && action == GLFW_PRESS){
				if(shiftPressed){
					assignControlGroup(player, 1);
				}
				else if(player.getSelectedPlanet() == null){
					displayControlGroup(player, 1);
				}
				else if (lPressed){
					player.getSelectedPlanet().setLoop(player.getTeam(), "1");
				}
				else {
					buyShips(player, 1);
				}
			}
			if ( key == GLFW_KEY_2 && action == GLFW_PRESS)
				if(shiftPressed){
					assignControlGroup(player, 2);
				}
				else if(player.getSelectedPlanet() == null){
					displayControlGroup(player, 2);
				}
				else if (lPressed){
					player.getSelectedPlanet().setLoop(player.getTeam(), "2");
				}
				else {
					buyShips(player, 2);
				}
			if ( key == GLFW_KEY_3 && action == GLFW_PRESS)
				if(shiftPressed){
					assignControlGroup(player, 3);
				}
				else if(player.getSelectedPlanet() == null){
					displayControlGroup(player, 3);
				}
				else if (lPressed){
					player.getSelectedPlanet().setLoop(player.getTeam(), "3");
				}
				else {
					buyShips(player, 3);
				}
//			if ( key == GLFW_KEY_4 && action == GLFW_PRESS)
//				buyShips(player, 4);
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
				//relative camera coordinates
				xpos.put(0, Math.min(Math.max(xpos.get(0), windowXOffset), WINDOW_WIDTH + windowXOffset));
				ypos.put(0, Math.min(Math.max(ypos.get(0), windowYOffset), WINDOW_HEIGHT + windowYOffset));
				xpos.put(1, getWidthScalar() * (xpos.get(0) - windowXOffset) + CURR_X);
				ypos.put(1, (getHeightScalar() * (WINDOW_HEIGHT - ypos.get(0) + windowYOffset) + CURR_Y));
				//true window coordinates
				xpos.put(2, xpos.get(0) - windowXOffset);
				ypos.put(2, (WINDOW_HEIGHT - ypos.get(0) + windowYOffset));
				if(gameState == 1){
					if ( button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
						if(settingsButton.isClicked(xpos.get(2), ypos.get(2))){
							System.exit(1);
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
						boxSelect.setTopLeft(xpos.get(1), ypos.get(1));
						//boxSelect.setBottomRight(xpos.get(0), ypos.get(0));
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
						for (int i = 0; i < ships.size(); i++) {
							Starship s = ships.get(i);
							if (s.isSelected() && s.getTeam().equals(player.getTeam())) {
								if(tPressed){
									s.setLockPosition(true);
								}
								else{
									s.setLockPosition(false);
								}
								if(controlPressed || shiftPressed){
									s.setAttackMove(false);
								}
								else{
									s.setAttackMove(true);
								}
								s.setLocationTarget(new Point(Math.max(Math.min(xpos.get(1), WORLD_WIDTH), 0), Math.max(Math.min(ypos.get(1), WORLD_HEIGHT), 0)));
								//System.out.println(xpos.get(0) + ", " + ypos.get(0));
							}
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
		
		createShips(200);
		
		sidebar = new Sidebar(this, WINDOW_WIDTH / 2, WINDOW_HEIGHT / 18);
		titlePage = new Layer(1);
		titlePage.setTopLeft(250, WINDOW_HEIGHT - 150);
		titlePage.setBottomRight(WINDOW_WIDTH - 250, 150);
		titlePage.setPoints();
		levelSelect = new Layer(2);
		levelSelect.setTopLeft(200, WINDOW_HEIGHT - 50);
		levelSelect.setBottomRight(WINDOW_WIDTH - 200, 50);
		levelSelect.setPoints();
		boxSelect = new Layer(3);
		//static layer on top of game
		settingsIcon = new Layer(4);
		settingsIcon.setTopLeft(WINDOW_WIDTH - 50, WINDOW_HEIGHT - 2);
		settingsIcon.setBottomRight(WINDOW_WIDTH - 2, WINDOW_HEIGHT - 50);
		settingsIcon.setPoints();
		
		planetMainMenu = new Layer(5);
		planetMainMenu.setTopLeft(WINDOW_WIDTH - 400, 100);
		planetMainMenu.setBottomRight(WINDOW_WIDTH, -50);
		planetMainMenu.setPoints();
		
		border1 = new Layer(6);
		border1.setTopLeft(-windowXOffset, WINDOW_HEIGHT + windowYOffset);
		border1.setBottomRight(0, -windowYOffset);
		border1.setPoints();
		
		border2 = new Layer(6);
		border2.setTopLeft(-windowXOffset, WINDOW_HEIGHT + windowYOffset);
		border2.setBottomRight(WINDOW_WIDTH + windowXOffset, WINDOW_HEIGHT);
		border2.setPoints();
		
		border3 = new Layer(6);
		border3.setTopLeft(WINDOW_WIDTH, WINDOW_HEIGHT + windowYOffset);
		border3.setBottomRight(WINDOW_WIDTH + windowXOffset * 2, -windowYOffset);
		border3.setPoints();
		
		border4 = new Layer(6);
		border4.setTopLeft(-windowXOffset, 0);
		border4.setBottomRight(WINDOW_WIDTH + windowXOffset, -windowYOffset);
		border4.setPoints();
		
		victoryMessage = new Layer(7);
		victoryMessage.setTopLeft(WINDOW_WIDTH / 2 - 100, WINDOW_HEIGHT / 2 + 70);
		victoryMessage.setBottomRight(WINDOW_WIDTH / 2 + 100, WINDOW_HEIGHT / 2 - 70);
		victoryMessage.setPoints();
		
		defeatMessage = new Layer(8);
		defeatMessage.setTopLeft(WINDOW_WIDTH / 2 - 100, WINDOW_HEIGHT / 2 + 70);
		defeatMessage.setBottomRight(WINDOW_WIDTH / 2 + 100, WINDOW_HEIGHT / 2 - 70);
		defeatMessage.setPoints();
				
		genTiles();

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		int slowCounter = 0;
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
					titlePage.display();
					settingsIcon.display();
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
					levelSelect.display();
					settingsIcon.display();
					displayBorders();
					window.swapBuffers();
					staticFrame = true;
				}
			}
			else if(gameState == 4){
				if(staticFrame == false){
					projectTrueWindowCoordinates();
					victoryMessage.display();
					window.swapBuffers();
					staticFrame = true;
				}
			}
			else if(gameState == 5){
				if(staticFrame == false){
					projectTrueWindowCoordinates();
					defeatMessage.display();
					window.swapBuffers();
					staticFrame = true;
				}
			}
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
						playerList.get(i).checkVisible();
					}
					
					//Show FOW
					for (int i = 0; i < ships.size(); i++) {
						if (ships.get(i).getTeam().equals(player.getTeam()))
							ships.get(i).showView();
					}
					
					
					//Display background
					for (int t = 0; t < backgroundTiles.size(); t++) {
						backgroundTiles.get(t).display();
					}
					
					
					//Update planets
					for(int p = 0; p < planets.size(); p++){
						planets.get(p).checkCapturePoint();
						planets.get(p).updateResources();
						//only display if planet is in camera window
						if(planets.get(p).getX() > CURR_X - (planetDisplayBorder * getWidthScalar()) && planets.get(p).getX() < CURR_X + CAMERA_WIDTH + (planetDisplayBorder * getWidthScalar()) 
								&& planets.get(p).getY() > CURR_Y - (planetDisplayBorder * getHeightScalar()) && planets.get(p).getY() < CURR_Y + CAMERA_HEIGHT + (planetDisplayBorder * getHeightScalar())){
							if(isVisible(planets.get(p), player)){
								planets.get(p).display(true);
							}
							else{
								planets.get(p).display(false);
							}
						}
					}
					
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
					
					if(CAMERA_WIDTH < 5200 && CAMERA_HEIGHT < 3600){
						//display ships
						for(int s = 0; s < ships.size(); s++){
							if(ships.get(s).getX() > CURR_X - (shipDisplayBorder * getWidthScalar()) && ships.get(s).getX() < CURR_X + CAMERA_WIDTH + (shipDisplayBorder * getWidthScalar())
									&& ships.get(s).getY() > CURR_Y - (shipDisplayBorder * getHeightScalar()) && ships.get(s).getY() < CURR_Y + CAMERA_HEIGHT + (shipDisplayBorder * getHeightScalar())){
								if(isVisible(ships.get(s), player)){
									ships.get(s).display();
								}
							}
						}
					}
					else{
						//display ship icons
						for(int s = 0; s < ships.size(); s++){
							if(ships.get(s).getX() > CURR_X - (shipDisplayBorder * getWidthScalar()) && ships.get(s).getX() < CURR_X + CAMERA_WIDTH + (shipDisplayBorder * getWidthScalar())
									&& ships.get(s).getY() > CURR_Y - (shipDisplayBorder * getHeightScalar()) && ships.get(s).getY() < CURR_Y + CAMERA_HEIGHT + (shipDisplayBorder * getHeightScalar())){
								if(isVisible(ships.get(s), player)){
									ships.get(s).displayIcon();
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
						for (int s = 0; s < ships.size(); s++) {
							if(ships.get(s).isSelected() && ships.get(s).getTeam().equals(player.getTeam())){
								ships.get(s).showRadar();
							}
						}
					}
						
					
				
				//display box select
				if(boxSelectCurrent){
					DoubleBuffer xpos = BufferUtils.createDoubleBuffer(2);
					DoubleBuffer ypos = BufferUtils.createDoubleBuffer(2);
					glfwGetCursorPos(window.getWindowHandle(), xpos, ypos);
					//convert the glfw coordinate to our coordinate system
					xpos.put(1, getWidthScalar() * (xpos.get(0) - windowXOffset) + CURR_X);
					ypos.put(1, (getHeightScalar() * ((WINDOW_HEIGHT) - ypos.get(0) + windowYOffset) + CURR_Y));
					boxSelect.setBottomRight(xpos.get(1), ypos.get(1));
					boxSelect.setPoints();
					boxSelect.display();
				}
				
				
				//Make ships drift apart if they're too close
				for (int s = 0; s < ships.size(); s++) {
					Starship first = ships.get(s);
					for (int s1 = s + 1; s1 < ships.size(); s1++) {
						Starship second = ships.get(s1);
						if (first.getTeam().equals(second.getTeam()) && distance(first.getX(), first.getY(), second.getX(), second.getY()) < first.getClickRadius() + second.getClickRadius()) {
							double angle = Math.acos((second.getX() - first.getX()) / distance(first.getX(), first.getY(), second.getX(), second.getY()));
							double newFirstX, newFirstY, newSecondX, newSecondY;
							if (second.getY() > first.getY()) {
								newFirstX = Math.min(Math.max(first.center.x - Math.cos(angle), first.getClickRadius()), WORLD_WIDTH - first.getClickRadius());
								newFirstY = Math.min(Math.max(first.center.y - Math.sin(angle), first.getClickRadius()), WORLD_HEIGHT - first.getClickRadius());
								newSecondX = Math.min(Math.max(second.center.x + Math.cos(angle), second.getClickRadius()), WORLD_WIDTH - second.getClickRadius());
								newSecondY = Math.min(Math.max(second.center.y + Math.sin(angle), second.getClickRadius()), WORLD_HEIGHT - second.getClickRadius());
							}
							else {
								newFirstX = Math.min(Math.max(first.center.x - Math.cos(angle), first.getClickRadius()), WORLD_WIDTH - first.getClickRadius());
								newFirstY = Math.min(Math.max(first.center.y + Math.sin(angle), first.getClickRadius()), WORLD_HEIGHT - first.getClickRadius());
								newSecondX = Math.min(Math.max(second.center.x + Math.cos(angle), second.getClickRadius()), WORLD_WIDTH - second.getClickRadius());
								newSecondY = Math.min(Math.max(second.center.y - Math.sin(angle), second.getClickRadius()), WORLD_HEIGHT - second.getClickRadius());
							}
							if(!(first instanceof BasicPod || first instanceof PlanetRadar || first instanceof PlanetLaser)){
								first.center = new Point(newFirstX, newFirstY);
							}
							if(!(second instanceof BasicPod || second instanceof PlanetRadar || second instanceof PlanetLaser)){
								second.center = new Point(newSecondX, newSecondY);
							}
//							if (first.locationTarget != null) {
//								if (second.locationTarget != null) {
//									first.setLocationTarget(new Point(first.locationTarget.x + newFirstX - first.center.x
//											, first.locationTarget.y + newFirstY - first.center.y));
//									second.setLocationTarget(new Point(second.locationTarget.x + newSecondX - second.center.x
//											, second.locationTarget.y + newSecondY - second.center.y));
//								}
////								else first.setLocationTarget(new Point(second.center.x + newFirstX - first.center.x
////											, second.center.y + newFirstY - first.center.y));
//							}
//							else {
//								if (second.locationTarget != null)
//									second.setLocationTarget(new Point(first.center.x + newSecondX - second.center.x
//										, first.center.y + newSecondY - second.center.y));
//							}
							
							if (first.locationTarget != null 
									&& distance(first.center.x, first.center.y, first.locationTarget.x, first.locationTarget.y) < first.getClickRadius() * 4)
								first.locationTarget = null;
							if (second.locationTarget != null 
									&& distance(second.center.x, second.center.y, second.locationTarget.x, second.locationTarget.y) < second.getClickRadius() * 4)
								second.locationTarget = null;
							}
						}
					}
					checkProjectiles();
					
//					enemy.buyShips();
					enemy.move();
					
					
					projectTrueWindowCoordinates();
					
					//display settings icon
					settingsIcon.display();
					
					//Display sidebar and figure out what has been selected
					boolean sidebarIsDisplayed = false;
					sidebar.display();
					int sumCurrentHP = 0;
					int sumMaxHP = 0;
					int numFightersSelected = 0;
					int numInterceptorsSelected = 0;
					int numTransportsSelected = 0;
					int numMissileshipsSelected = 0;
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
							}
							else{
								writeText("???", 20, 20);
								writeText("???", 20, 80);
							}
						}
						else if (numFightersSelected + numInterceptorsSelected + numTransportsSelected + numMissileshipsSelected + numPodsSelected == 1) {
							if (numFightersSelected == 1) writeText("Fighter", 400, 15, 30);
							else if (numInterceptorsSelected == 1) writeText("Interceptor", 400, 15, 30);
							else if (numTransportsSelected == 1) writeText("Transport", 400, 15, 30);
							else if (numMissileshipsSelected == 1) writeText("Missileship", 400, 15, 30);
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
						else if (numFightersSelected + numInterceptorsSelected + numTransportsSelected  + numMissileshipsSelected + numPodsSelected > 1) {
							writeText("Starfleet(" + (numFightersSelected + numInterceptorsSelected + numTransportsSelected + numMissileshipsSelected + numPodsSelected) + ")", 400, 15, 30);
							writeText("Fighters:" + numFightersSelected, 1000, 120);
							writeText("Interceptors:" + numInterceptorsSelected, 1000, 100);
//							writeText("Transports:" + numTransportsSelected, 1000, 60);
							writeText("Missileships:" + numMissileshipsSelected, 1000, 80);
							writeText("Turrets:" + numPodsSelected, 1000, 60);
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
							planetMainMenu.display();
						}
						
						for (int i = 0; i < text.size(); i++) {
	//						text.get(i).setPoints();
							text.get(i).display();
						}
					}
					else {
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
					
					//Check which direction the camera should move, and move accordingly
					if (panLeft)
						CURR_X = Math.max(0, CURR_X - CAMERA_WIDTH / 20);
					if (panRight)
						CURR_X = Math.min(WORLD_WIDTH - CAMERA_WIDTH, CURR_X + CAMERA_WIDTH / 20);
					if (panDown)
						CURR_Y = Math.max((int) (-150 * getHeightScalar()), CURR_Y - CAMERA_HEIGHT / 20);
					if (panUp)
						CURR_Y = Math.min(WORLD_HEIGHT - CAMERA_HEIGHT, CURR_Y + CAMERA_HEIGHT / 20);
					
					
					window.swapBuffers();
		        
				}
			}
		}
	}

	public static void main(String[] args) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
		new StarshipArena().run();
	}
	
	public void projectRelativeCameraCoordinates(){
		glMatrixMode(GL_PROJECTION);
        glLoadIdentity(); // Resets any previous projection matrices
        glOrtho((-windowXOffset * getWidthScalar()) + CURR_X, CURR_X + CAMERA_WIDTH + (windowXOffset * getWidthScalar()), CURR_Y + ((-windowYOffset) * getHeightScalar()), CURR_Y + CAMERA_HEIGHT + ((windowYOffset)* getHeightScalar()), 1, -1);
        glMatrixMode(GL_MODELVIEW);
	}
	
	public void projectTrueWindowCoordinates(){
		glMatrixMode(GL_PROJECTION);
        glLoadIdentity(); // Resets any previous projection matrices
        glOrtho(-windowXOffset, WINDOW_WIDTH + windowXOffset, -windowYOffset, WINDOW_HEIGHT + windowYOffset, 1, -1);
        glMatrixMode(GL_MODELVIEW);
	}
	
	public int[] getScreenBounds(){
    	int[] bounds = new int[4];
    	bounds[0] = 0;
    	bounds[1] = WORLD_WIDTH;
    	bounds[2] = 0;
    	bounds[3] = WORLD_HEIGHT;
    	return bounds;
    }
	
	public void displayBorders(){
		border1.display();
		border2.display();
		border3.display();
		border4.display();
	}
	
	//Creates the number of ships specified by the user
	//Each ship has a random starting location and angle
	public void createShips(int num){
		new Planet(this, 2300, 1000, 1).setTeam("blue");
		int startx;
		int starty;
		int angle;
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
		
		new MachineGunPod(this, "red", 300, 1500, 270);
		new MachineGunPod(this, "red", 350, 1400, 270);
		new MachineGunPod(this, "red", 300, 1300, 270);
		
		new MachineGunPod(this, "red", 400, 1500, 270);
		new MachineGunPod(this, "red", 450, 1400, 270);
		new MachineGunPod(this, "red", 400, 1300, 270);
		new MachineGunPod(this, "red", 500, 1500, 270);
		new MachineGunPod(this, "red", 550, 1400, 270);
		new MachineGunPod(this, "red", 500, 1300, 270);
		new MachineGunPod(this, "red", 600, 1500, 270);
		new MachineGunPod(this, "red", 650, 1400, 270);
		new MachineGunPod(this, "red", 600, 1300, 270);
//		new Fighter(this, "blue", 200, 500, 270, 1);
//		new Interceptor(this, 500, 700, 0, 1);
	}
	
	public void buyShips(Player player, int type){
		Planet p = player.getSelectedPlanet();
		//if player has selected an allied planet
		int spawnangle = 0;
		if(p != null && p.getTeam().equals(player.getTeam())){
			if (p.getTeam().equals("red")) spawnangle = 180;
			//attempt to buy fighter
			if(type == 1 && p.getResources() >= FIGHTER_COST){
				p.setResources(p.getResources() - FIGHTER_COST);
				new Fighter(this, p.getTeam(), (int) p.getX() + random.nextInt(p.getSize() * 2) - p.getSize(), 
						(int) p.getY() + random.nextInt(p.getSize() * 2) - p.getSize(), spawnangle);
			}
			//attempt to buy interceptor
			else if(type == 2 && p.getResources() >= INTERCEPTOR_COST){
				p.setResources(p.getResources() - INTERCEPTOR_COST);
				new Interceptor(this, p.getTeam(), (int) p.getX() + random.nextInt(p.getSize() * 2) - p.getSize(), 
						(int) p.getY() + random.nextInt(p.getSize() * 2) - p.getSize(), spawnangle);
				
			}
//			//attempt to buy transport
//			else if(type == 3 && p.getResources() >= TRANSPORT_COST){
//				p.setResources(p.getResources() - TRANSPORT_COST);
//				new Transport(this, p.getTeam(), (int) p.getX() + random.nextInt(p.getSize() * 2) - p.getSize(), 
//						(int) p.getY() + random.nextInt(p.getSize() * 2) - p.getSize(), 0);
//				
//			}
			//attempt to buy missileship
			else if(type ==3 && p.getResources() >= MISSILESHIP_COST){
				p.setResources(p.getResources() - MISSILESHIP_COST);
				new Missileship(this, p.getTeam(), (int) p.getX() + random.nextInt(p.getSize() * 2) - p.getSize(), 
						(int) p.getY() + random.nextInt(p.getSize() * 2) - p.getSize(), spawnangle);
				
			}
		}
	}
	
	public void assignControlGroup(Player player, int group){
		ArrayList<Starship> playerShips = player.getControlledShips();
		for(int i = 0; i < playerShips.size(); i++){
			if(playerShips.get(i).isSelected()){
				playerShips.get(i).setControlGroup(group);
			}
		}
	}
	
	public void displayControlGroup(Player player, int group){
		ArrayList<Starship> allShips = getAllShips();
		for(int i = 0; i < allShips.size(); i++){
			if(allShips.get(i).getControlGroup() == group){
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
		int ZOOM_WIDTH = 650;
		int ZOOM_HEIGHT = 450;
		if(zoomOut){
			if(/*zoomLevel < 5 && */WORLD_WIDTH >= CAMERA_WIDTH + ZOOM_WIDTH && WORLD_HEIGHT >= CAMERA_HEIGHT + ZOOM_HEIGHT){
				zoomLevel++;
				CAMERA_WIDTH += ZOOM_WIDTH;
				CAMERA_HEIGHT += ZOOM_HEIGHT;
				CURR_X -= ZOOM_WIDTH / 2;
				CURR_Y -= ZOOM_HEIGHT / 2;
				if(CURR_X + CAMERA_WIDTH > WORLD_WIDTH){
					CURR_X = WORLD_WIDTH - CAMERA_WIDTH;
				}
				if(CURR_Y + CAMERA_HEIGHT > WORLD_HEIGHT){
					CURR_Y = WORLD_HEIGHT - CAMERA_HEIGHT;
				}
				if(CURR_X < 0){
					CURR_X = 0;
				}
				if(CURR_Y < 0){
					CURR_Y = 0;
				}
			}
		}
		else{
			if(/*zoomLevel > 1*/ CAMERA_WIDTH - 2 * ZOOM_WIDTH > 0 && CAMERA_HEIGHT - 2 * ZOOM_HEIGHT > 0){
				zoomLevel--;
				CAMERA_WIDTH -= ZOOM_WIDTH;
				CAMERA_HEIGHT -= ZOOM_HEIGHT;
				CURR_X += ZOOM_WIDTH / 2;
				CURR_Y += ZOOM_HEIGHT / 2;
			}
		}
	}
	public void loadLevel(int level){
		boolean trueMuteState = mute;
		mute = true;
		destroyAllShips();
		destroyAllPlanets();
		destroyAllProjectiles();
		destroyAllExplosions();
		destroyAllTiles();
		playerList.clear();
		mute = trueMuteState;
		menuMusic.stop();
		if (!gameMusic.isActive()) {
			gameMusic.setFramePosition(0);
			if (!mute)
				gameMusic.loop(Clip.LOOP_CONTINUOUSLY);
		}
		
		currentLevel = level;
		zoomLevel = 3;
		CAMERA_WIDTH = 2600;
		CAMERA_HEIGHT = 1800;
		if(level == 1){
			WORLD_WIDTH = 3900;
		    WORLD_HEIGHT = 2700;
		    CURR_X = 0;
			CURR_Y = 0;
			enemy = new Enemy(this, new Player(this, "red"));
			//TODO Enemy Fighters are commented out so I can test PlanetLaser, and left Planet is auto-given to blue. Reverse these changes after testing concludes.
			new Planet(this, 1350, 1000, 1).setTeam("blue");;
			new PlanetRadar(this, "blue", 1350, 1000, 45);
			new Planet(this, 3000, 1500, 2).setTeam("red");
			new PlanetLaser(this, "red", 3000, 1500, 45);
//			new Fighter(this, "blue", 500, 400, 0);
//			new Fighter(this, "blue", 600, 350, 0);
//			new Fighter(this, "blue", 400, 350, 0);
////			new Fighter(this, "blue", 700, 600, 0);
////			new Fighter(this, "blue", 800, 550, 0);
////			new Fighter(this, "blue", 600, 550, 0);
////			new Fighter(this, "blue", 900, 400, 0);
////			new Fighter(this, "blue", 1000, 350, 0);
////			new Fighter(this, "blue", 800, 350, 0);
//			
//			new Fighter(this, "red", 2800, 1500, 135);
//			new Fighter(this, "red", 2900, 1500, 90);
//			new Fighter(this, "red", 3000, 1700, 80);
//			new Fighter(this, "red", 3200, 1500, 150);
//			new Fighter(this, "red", 3200, 1300, 160);
//			new Fighter(this, "red", 3000, 1300, 150);
		}
		else if(level == 2){
			WORLD_WIDTH = 3900;
		    WORLD_HEIGHT = 2700;
		    CURR_X = 0;
			CURR_Y = 0;
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			new Planet(this, 1350, 1000, 1);
			new Planet(this, 3000, 1500, 2).setTeam("red");
			new Fighter(this, "blue", 500, 400, 0);
			new Fighter(this, "blue", 600, 350, 0);
			new Fighter(this, "blue", 400, 350, 0);
			new Fighter(this, "blue", 700, 600, 0);
//			new Fighter(this, "blue", 800, 550, 0);
//			new Fighter(this, "blue", 600, 550, 0);
			new Fighter(this, "blue", 900, 400, 0);
			new Fighter(this, "blue", 1000, 350, 0);
			new Fighter(this, "blue", 800, 350, 0);
			
			new Fighter(this, "red", 2800, 1500, 135);
			new Fighter(this, "red", 3000, 1500, 90);
			new Fighter(this, "red", 3000, 1700, 80);
			new Fighter(this, "red", 3200, 1500, 150);
			new Fighter(this, "red", 3200, 1300, 160);
			new Fighter(this, "red", 3000, 1300, 150);
		}
		
		else if (level == 3) {
			WORLD_WIDTH = 5000;
		    WORLD_HEIGHT = 4000;
		    CURR_X = 200;
			CURR_Y = 200;
			zoomLevel = 2;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			Planet temp;
			temp = new Planet(this, 1350, 1000, 1);
			temp.setTeam("blue"); temp.setResources(40);
			temp = new Planet(this, 3000, 1500, 2);
			temp.setTeam("blue"); temp.setResources(40);		
			temp = new Planet(this, 2250, 3000, 3);		
			temp.setTeam("red"); temp.setResources(40);		
			temp = new Planet(this, 4700, 2300, 4);		
			temp.setTeam("red"); temp.setResources(40);
			new Fighter(this, "blue", 1350, 1000, 0);
			new Fighter(this, "blue", 1450, 950, 0);
			new Fighter(this, "blue", 1250, 950, 0);
			
			new Fighter(this, "blue", 3000, 1500, 0);
			new Fighter(this, "blue", 3100, 1450, 0);
			new Fighter(this, "blue", 2900, 1450, 0);
			
			new Fighter(this, "red", 2250, 3000, 180);
			new Fighter(this, "red", 2350, 2950, 180);
			new Fighter(this, "red", 2150, 2950, 180);
			
			new Fighter(this, "red", 4700, 2300, 180);
			new Fighter(this, "red", 4800, 2250, 180);
			new Fighter(this, "red", 4600, 2250, 180);
		}
		
		else if (level == 4) {
			WORLD_WIDTH = 10000;
		    WORLD_HEIGHT = 8000;
		    CURR_X = 200;
			CURR_Y = 200;
			zoomLevel = 3;
			CAMERA_WIDTH = 5200;
			CAMERA_HEIGHT = 3600;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			new Planet(this, 1350, 1000, 1).setTeam("blue");
			new Planet(this, 3000, 1500, 2).setTeam("blue");
			new Planet(this, 2250, 3000, 3).setTeam("blue");
			new Planet(this, 4700, 2300, 4).setTeam("blue");
			
			new Planet(this, 4350, 4200, 5).setTeam("red");
			new Planet(this, 9500, 1500, 6).setTeam("red");
			new Planet(this, 1500, 4000, 4).setTeam("red");
			new Planet(this, 2700, 6300, 3).setTeam("red");
			new Planet(this, 6700, 7500, 2).setTeam("red");
			
			new Fighter(this, "blue", 1350, 1000, 0);
			new Fighter(this, "blue", 1450, 950, 0);
			new Fighter(this, "blue", 1250, 950, 0);
			new Interceptor(this, "blue", 1150, 900, 0);
			new Interceptor(this, "blue", 1550, 900, 0);
			
			new Interceptor(this, "blue", 3000, 1500, 0);
			new Fighter(this, "blue", 3100, 1450, 0);
			new Fighter(this, "blue", 2900, 1450, 0);
			
			new Fighter(this, "blue", 2250, 3000, 0);
			new Fighter(this, "blue", 2350, 2950, 0);
			new Fighter(this, "blue", 2150, 2950, 0);
			
			new Fighter(this, "blue", 4800, 2250, 0);
			new Fighter(this, "blue", 4600, 2250, 0);
			new Interceptor(this, "blue", 4900, 2050, 0);
			new Interceptor(this, "blue", 4700, 2050, 0);
			new Interceptor(this, "blue", 4500, 2050, 0);
			
			
			new Fighter(this, "red", 6700, 7500, 180);
			new Fighter(this, "red", 6800, 7450, 180);
			new Fighter(this, "red", 6600, 7450, 180);
			new Interceptor(this, "red", 6500, 7400, 180);
			new Interceptor(this, "red", 6900, 7400, 180);
			
			new Interceptor(this, "red", 1500, 4000, 180);
			new Interceptor(this, "red", 1600, 3950, 180);
			new Interceptor(this, "red", 1400, 3950, 180);
			
			new Fighter(this, "red", 9500, 1500, 180);
			new Fighter(this, "red", 9600, 1450, 180);
			new Fighter(this, "red", 9400, 1450, 180);
			new Interceptor(this, "red", 9700, 1400, 180);
			new Interceptor(this, "red", 9300, 1400, 180);
		}
		
		else if (level == 5) {
			WORLD_WIDTH = 10000;
		    WORLD_HEIGHT = 8000;
		    CURR_X = 1750;
			CURR_Y = 2400;
			zoomLevel = 3;
			CAMERA_WIDTH = 5200;
			CAMERA_HEIGHT = 3600;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			new Planet(this, 1350, 1000, 1).setTeam("blue");
			new Planet(this, 3000, 1500, 2).setTeam("blue");
			new Planet(this, 2250, 3000, 3).setTeam("blue");
			new Planet(this, 4700, 2300, 4).setTeam("blue");
			
			new Planet(this, 4350, 4200, 5).setTeam("red");
			
			new Planet(this, 9500, 1500, 6).setTeam("blue");
			new Planet(this, 1500, 4000, 4).setTeam("blue");
			new Planet(this, 2700, 6300, 3).setTeam("blue");
			new Planet(this, 6700, 7500, 2).setTeam("blue");
			
			new Fighter(this, "blue", 1350, 1000, 0);
			
			new Interceptor(this, "blue", 3000, 1500, 0);
			
			new Fighter(this, "blue", 2250, 3000, 0);
			
			new Interceptor(this, "blue", 4700, 2300, 0);
			new Interceptor(this, "blue", 4700, 2100, 0);
			
			
			new Fighter(this, "blue", 6700, 7500, 0);
			
			new Fighter(this, "blue", 1500, 4000, 0);
			
			
			new Interceptor(this, "red", 4350, 4200, 0);
			new Interceptor(this, "red", 4450, 4200, 0);
			new Interceptor(this, "red", 4350, 4100, 0);
			new Interceptor(this, "red", 4250, 4200, 0);
			new Interceptor(this, "red", 4350, 4300, 0);
			new Interceptor(this, "red", (int)(4350 + 50 * Math.sqrt(2)), (int)(4200 + 50 * Math.sqrt(2)), 315);
			new Interceptor(this, "red", (int)(4350 - 50 * Math.sqrt(2)), (int)(4200 + 50 * Math.sqrt(2)), 45);
			new Interceptor(this, "red", (int)(4350 - 50 * Math.sqrt(2)), (int)(4200 - 50 * Math.sqrt(2)), 135);
			new Interceptor(this, "red", (int)(4350 + 50 * Math.sqrt(2)), (int)(4200 - 50 * Math.sqrt(2)), 225);
			new Fighter(this, "red", 4550, 4200, 0);
			new Fighter(this, "red", 4350, 4000, 0);
			new Fighter(this, "red", 4150, 4200, 0);
			new Fighter(this, "red", 4350, 4400, 0);
			new Fighter(this, "red", (int)(4350 + 100 * Math.sqrt(2)), (int)(4200 + 100 * Math.sqrt(2)), 315);
			new Fighter(this, "red", (int)(4350 - 100 * Math.sqrt(2)), (int)(4200 + 100 * Math.sqrt(2)), 45);
			new Fighter(this, "red", (int)(4350 - 100 * Math.sqrt(2)), (int)(4200 - 100 * Math.sqrt(2)), 135);
			new Fighter(this, "red", (int)(4350 + 100 * Math.sqrt(2)), (int)(4200 - 100 * Math.sqrt(2)), 225);
			new Fighter(this, "red", 4650, 4200, 0);
			new Fighter(this, "red", 4350, 3900, 0);
			new Fighter(this, "red", 4050, 4200, 0);
			new Fighter(this, "red", 4350, 4500, 0);
			new Fighter(this, "red", (int)(4350 + 150 * Math.sqrt(2)), (int)(4200 + 150 * Math.sqrt(2)), 315);
			new Fighter(this, "red", (int)(4350 - 150 * Math.sqrt(2)), (int)(4200 + 150 * Math.sqrt(2)), 45);
			new Fighter(this, "red", (int)(4350 - 150 * Math.sqrt(2)), (int)(4200 - 150 * Math.sqrt(2)), 135);
			new Fighter(this, "red", (int)(4350 + 150 * Math.sqrt(2)), (int)(4200 - 150 * Math.sqrt(2)), 225);
		}
		
		else if(level == 6){
			WORLD_WIDTH = 7500;
		    WORLD_HEIGHT = 6000;
		    CURR_X = 1600;
			CURR_Y = 700;
			zoomLevel = 2;
			CAMERA_WIDTH = 4550;
			CAMERA_HEIGHT = 3150;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			
			new Planet(this, 3500, 4000, 4).setTeam("red");
			new Fighter(this, "red", 3400, 3950, 180);
			new Fighter(this, "red", 3600, 3950, 180);
			new Planet(this, 5300, 5000, 4).setTeam("red");
			new Fighter(this, "red", 5200, 4950, 180);
			new Fighter(this, "red", 5400, 4950, 180);
			
			new MissilePod(this, "red", 3300, 3600, 180);
			new MissilePod(this, "red", 3500, 3600, 180);
			new MissilePod(this, "red", 3700, 3600, 180);
			
			new MissilePod(this, "red", 3100, 3800, 90);
			new MissilePod(this, "red", 3100, 4000, 90);
			new MissilePod(this, "red", 3100, 4200, 90);
			
			new MissilePod(this, "red", 3900, 3800, 270);
			new MissilePod(this, "red", 3900, 4000, 270);
			new MissilePod(this, "red", 3900, 4200, 270);
			
			new MissilePod(this, "red", 3300, 4400, 0);
			new MissilePod(this, "red", 3500, 4400, 0);
			new MissilePod(this, "red", 3700, 4400, 0);
	
			new Planet(this, 3400, 1000, 2).setTeam("blue");
			
			new Interceptor(this, "blue", 3300, 1100, 0);
			new Interceptor(this, "blue", 3300, 900, 0);
			new Interceptor(this, "blue", 3500, 1100, 0);
			new Interceptor(this, "blue", 3500, 900, 0);
			
			new Planet(this, 6300, 1300, 3);
			
			new Planet(this, 1750, 1900, 5);
			
		}
		
		else if(level == 7){
			WORLD_WIDTH = 6000;
		    WORLD_HEIGHT = 8000;
		    CURR_X = 400;
			CURR_Y = 300;
			zoomLevel = 3;
			CAMERA_WIDTH = 5200;
			CAMERA_HEIGHT = 3600;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			
			new Planet(this, 3100, 7000, 6).setTeam("red");
			new Fighter(this, "red", 3100, 6900, 180);
			new Fighter(this, "red", 2950, 7000, 180);
			new Fighter(this, "red", 3250, 7000, 180);
			new Planet(this, 5000, 6000, 6).setTeam("red");
			new Fighter(this, "red", 5000, 5900, 180);
			new Fighter(this, "red", 4850, 6000, 180);
			new Fighter(this, "red", 5150, 6000, 180);
			new Planet(this, 2400, 6500, 6).setTeam("red");
			new Fighter(this, "red", 2400, 6400, 180);
			new Fighter(this, "red", 2250, 6500, 180);
			new Fighter(this, "red", 2550, 6500, 180);
			new Planet(this, 3400, 6000, 6).setTeam("red");
			new Fighter(this, "red", 3400, 5900, 180);
			new Fighter(this, "red", 3250, 6000, 180);
			new Fighter(this, "red", 3550, 6000, 180);
			
			new MissilePod(this, "red", 3700, 7300, 180);
			new MissilePod(this, "red", 1800, 6500, 180);
			
			new Missileship(this, "red", 4200, 6500, 180);
			new Interceptor(this, "red", 4000, 6500, 180);
			new Interceptor(this, "red", 4400, 6500, 180);
			
			new Missileship(this, "red", 5000, 6800, 180);
			new Interceptor(this, "red", 4800, 6800, 180);
			new Interceptor(this, "red", 5200, 6800, 180);
			
			new Missileship(this, "red", 1500, 6900, 180);
			new Interceptor(this, "red", 1300, 6900, 180);
			new Interceptor(this, "red", 1700, 6900, 180);
			
			new Planet(this, 1900, 4500, 2);
			new Planet(this, 2800, 4500, 2);
			new Planet(this, 5100, 3900, 1);
			
			new Planet(this, 3000, 2000, 5).setTeam("blue");
			new Interceptor(this, "blue", 2800, 1450, 0);
			new Interceptor(this, "blue", 3000, 1450, 0);
			new Interceptor(this, "blue", 3200, 1450, 0);
			new Fighter(this, "blue", 2900, 1600, 0);
			new Fighter(this, "blue", 3100, 1600, 0);
			new MissilePod(this, "blue", 2700, 2300, 0);
			new MissilePod(this, "blue", 3300, 2300, 0);
			
			new Planet(this, 1400, 1500, 5).setTeam("blue");
			new Fighter(this, "blue", 2200, 1400, 0);
			new Fighter(this, "blue", 2050, 1300, 0);
			new Fighter(this, "blue", 2350, 1300, 0);
			new Fighter(this, "blue", 2200, 1900, 0);
			new Fighter(this, "blue", 2050, 1800, 0);
			new Fighter(this, "blue", 2350, 1800, 0);
			new MissilePod(this, "blue", 1100, 1800, 0);
			new MissilePod(this, "blue", 1700, 1800, 0);
			
			new Planet(this, 4600, 1500, 5).setTeam("blue");
			new Fighter(this, "blue", 3800, 1400, 0);
			new Fighter(this, "blue", 3650, 1300, 0);
			new Fighter(this, "blue", 3950, 1300, 0);
			new Fighter(this, "blue", 3800, 1900, 0);
			new Fighter(this, "blue", 3650, 1800, 0);
			new Fighter(this, "blue", 3950, 1800, 0);
			new MissilePod(this, "blue", 4300, 1800, 0);
			new MissilePod(this, "blue", 4900, 1800, 0);
			
			
		}
		
		else if (level == 8) {
			WORLD_WIDTH = 10000;
		    WORLD_HEIGHT = 8000;
		    CURR_X = 1750;
			CURR_Y = 2400;
			zoomLevel = 3;
			CAMERA_WIDTH = 5200;
			CAMERA_HEIGHT = 3600;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			new Planet(this, 1350, 1000, 1).setTeam("red");
			new Planet(this, 3000, 1500, 2).setTeam("red");
			new Planet(this, 2250, 3000, 3).setTeam("red");
			new Planet(this, 4700, 2300, 4).setTeam("red");
			
			new Planet(this, 4350, 4200, 5).setTeam("blue");
			
			new Planet(this, 9500, 1500, 6).setTeam("red");
			new Planet(this, 1500, 4000, 4).setTeam("red");
			new Planet(this, 2700, 6300, 3).setTeam("red");
			new Planet(this, 6700, 7500, 2).setTeam("red");
			
			new Fighter(this, "red", 1350, 1000, 0);
			new Fighter(this, "red", 1250, 950, 0);
			new Fighter(this, "red", 1450, 950, 0);
			
			new Interceptor(this, "red", 3000, 1500, 0);
			
			new Fighter(this, "red", 2250, 3000, 0);
			
			new Interceptor(this, "red", 4700, 2300, 0);
			new Interceptor(this, "red", 4700, 2100, 0);
			
			
			new Fighter(this, "red", 6700, 7500, 0);
			
			new Fighter(this, "red", 1500, 4000, 0);
			
			new Missileship(this, "red", 9500, 1500, 0);
			new Interceptor(this, "red", 9400, 1400, 0);
			new Interceptor(this, "red", 9600, 1400, 0);
			
			
			new Interceptor(this, "blue", 4350, 4200, 0);
			new Interceptor(this, "blue", 4450, 4200, 0);
			new Interceptor(this, "blue", 4350, 4100, 0);
			new Interceptor(this, "blue", 4250, 4200, 0);
			new Interceptor(this, "blue", 4350, 4300, 0);
			new Interceptor(this, "blue", (int)(4350 + 50 * Math.sqrt(2)), (int)(4200 + 50 * Math.sqrt(2)), 315);
			new Interceptor(this, "blue", (int)(4350 - 50 * Math.sqrt(2)), (int)(4200 + 50 * Math.sqrt(2)), 45);
			new Interceptor(this, "blue", (int)(4350 - 50 * Math.sqrt(2)), (int)(4200 - 50 * Math.sqrt(2)), 135);
			new Interceptor(this, "blue", (int)(4350 + 50 * Math.sqrt(2)), (int)(4200 - 50 * Math.sqrt(2)), 225);
			new Fighter(this, "blue", 4550, 4200, 0);
			new Fighter(this, "blue", 4150, 4200, 0);
			new Fighter(this, "blue", (int)(4350 + 100 * Math.sqrt(2)), (int)(4200 + 100 * Math.sqrt(2)), 315);
			new Fighter(this, "blue", (int)(4350 - 100 * Math.sqrt(2)), (int)(4200 + 100 * Math.sqrt(2)), 45);
			new Fighter(this, "blue", (int)(4350 - 100 * Math.sqrt(2)), (int)(4200 - 100 * Math.sqrt(2)), 135);
			new Fighter(this, "blue", (int)(4350 + 100 * Math.sqrt(2)), (int)(4200 - 100 * Math.sqrt(2)), 225);
			new Fighter(this, "blue", 4650, 4200, 0);
			new Fighter(this, "blue", 4350, 4500, 0);
			new Fighter(this, "blue", (int)(4350 + 150 * Math.sqrt(2)), (int)(4200 + 150 * Math.sqrt(2)), 315);
			new Fighter(this, "blue", (int)(4350 - 150 * Math.sqrt(2)), (int)(4200 + 150 * Math.sqrt(2)), 45);
			new Fighter(this, "blue", (int)(4350 - 150 * Math.sqrt(2)), (int)(4200 - 150 * Math.sqrt(2)), 135);
			new Fighter(this, "blue", (int)(4350 + 150 * Math.sqrt(2)), (int)(4200 - 150 * Math.sqrt(2)), 225);
		}
		else if(level == 9){
			WORLD_WIDTH = 12500;
		    WORLD_HEIGHT = 5000;
		    CURR_X = 3725;
			CURR_Y = 925;
			zoomLevel = 3;
			CAMERA_WIDTH = 4550;
			CAMERA_HEIGHT = 3150;
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));
			new Planet(this, 5750, 3000, 3).setTeam("blue");
			new MachineGunPod(this, "blue", 6200, 3100, 270);
			new MachineGunPod(this, "blue", 6200, 2900, 270);
			new MachineGunPod(this, "blue", 5300, 3100, 90);
			new MachineGunPod(this, "blue", 5300, 2900, 90);
			new Fighter(this, "blue", 5680, 3070, 45);
			new Fighter(this, "blue", 5680, 2930, 135);
			new Fighter(this, "blue", 5820, 3070, 315);
			new Fighter(this, "blue", 5820, 2930, 225);
			new Planet(this, 6250, 2000, 4).setTeam("blue");
			new MachineGunPod(this, "blue", 6700, 2100, 270);
			new MachineGunPod(this, "blue", 6700, 1900, 270);
			new MachineGunPod(this, "blue", 5800, 2100, 90);
			new MachineGunPod(this, "blue", 5800, 1900, 90);
			new Fighter(this, "blue", 6180, 2070, 45);
			new Fighter(this, "blue", 6180, 1930, 135);
			new Fighter(this, "blue", 6320, 2070, 315);
			new Fighter(this, "blue", 6320, 1930, 225);
			new Planet(this, 4000, 2500, 3);
			new Planet(this, 7500, 2500, 6);
			
			new Planet(this, 1000, 3800, 3).setTeam("red");
			new Fighter(this, "red", 1000, 3860, 270);
			new Fighter(this, "red", 1000, 3740, 270);
			new BasicPod(this, "red", 1200, 3860, 270);
			new BasicPod(this, "red", 1200, 3740, 270);
			new Planet(this, 1200, 2600, 4).setTeam("red");
			new Fighter(this, "red", 1200, 2660, 270);
			new Fighter(this, "red", 1200, 2540, 270);
			new BasicPod(this, "red", 1400, 2660, 270);
			new BasicPod(this, "red", 1400, 2540, 270);
			new Planet(this, 900, 1300, 5).setTeam("red");
			new Fighter(this, "red", 900, 1360, 270);
			new Fighter(this, "red", 900, 1240, 270);
			new BasicPod(this, "red", 1100, 1360, 270);
			new BasicPod(this, "red", 1100, 1240, 270);
			
			new Planet(this, 11900, 2700, 4).setTeam("red");
			new Missileship(this, "red", 11900, 2700, 90);
			new Planet(this, 11900, 3600, 4).setTeam("red");
			new Missileship(this, "red", 11900, 3600, 90);
			new Planet(this, 9600, 4100, 3).setTeam("red");
			new Fighter(this, "red", 9600, 4160, 90);
			new Fighter(this, "red", 9600, 4040, 90);
			new Planet(this, 9460, 3150, 5).setTeam("red");
			new Fighter(this, "red", 9460, 3210, 90);
			new Fighter(this, "red", 9460, 3090, 90);
			new Planet(this, 11000, 1600, 3).setTeam("red");
			new Interceptor(this, "red", 10950, 1650, 80);
			new Interceptor(this, "red", 10950, 1550, 80);
			new Interceptor(this, "red", 11050, 1650, 80);
			new Interceptor(this, "red", 11050, 1550, 80);
			
			
		}
		else if(level == 10){
			WORLD_WIDTH = 12000;
		    WORLD_HEIGHT = 4000;
		    CURR_X = 25;
			CURR_Y = 0;
			zoomLevel = 3;
			CAMERA_WIDTH = 4550;
			CAMERA_HEIGHT = 3150;
			new Planet(this, 1300, 2500, 2).setTeam("blue");
			new Planet(this, 1200, 1300, 2).setTeam("blue");
			new Planet(this, 5600, 3200, 2).setTeam("red");
			new Planet(this, 10000, 3000, 2).setTeam("red");
			new Planet(this, 9000, 700, 2).setTeam("red");
			
			enemy = new AdvancedEnemy(this, new Player(this, "red"));

			new MachineGunPod(this, "red", 3700, 2400, 90);
			new MachineGunPod(this, "red", 3700, 2600, 90);
			new MachineGunPod(this, "red", 3700, 2800, 90);
			new MissilePod(this, "red", 3900, 2400, 90);
			new MissilePod(this, "red", 3900, 2600, 90);
			new MissilePod(this, "red", 3900, 2800, 90);
			new MachineGunPod(this, "red", 3850, 3100, 90);
			new MachineGunPod(this, "red", 3850, 3300, 90);
			new MachineGunPod(this, "red", 3850, 3500, 90);
			new MissilePod(this, "red", 4050, 3100, 90);
			new MissilePod(this, "red", 4050, 3300, 90);
			new MissilePod(this, "red", 4050, 3500, 90);
			new MachineGunPod(this, "red", 3850, 2100, 90);
			new MachineGunPod(this, "red", 3850, 1900, 90);
			new MachineGunPod(this, "red", 3850, 1700, 90);
			new MissilePod(this, "red", 4050, 2100, 90);
			new MissilePod(this, "red", 4050, 1900, 90);
			new MissilePod(this, "red", 4050, 1700, 90);
			
			
			new MachineGunPod(this, "red", 5500, 1000, 90);
			new MachineGunPod(this, "red", 5500, 1200, 90);
			new MachineGunPod(this, "red", 5500, 1400, 90);
			new MissilePod(this, "red", 5700, 1000, 90);
			new MissilePod(this, "red", 5700, 1200, 90);
			new MissilePod(this, "red", 5700, 1400, 90);
			new MachineGunPod(this, "red", 5650, 1700, 90);
			new MachineGunPod(this, "red", 5650, 1900, 90);
			new MachineGunPod(this, "red", 5650, 2100, 90);
			new MissilePod(this, "red", 5850, 1700, 90);
			new MissilePod(this, "red", 5850, 1900, 90);
			new MissilePod(this, "red", 5850, 2100, 90);
			new MachineGunPod(this, "red", 5650, 700, 90);
			new MachineGunPod(this, "red", 5650, 500, 90);
			new MachineGunPod(this, "red", 5650, 300, 90);
			new MissilePod(this, "red", 5850, 700, 90);
			new MissilePod(this, "red", 5850, 500, 90);
			new MissilePod(this, "red", 5850, 300, 90);
			
			new MachineGunPod(this, "red", 7300, 2400, 90);
			new MachineGunPod(this, "red", 7300, 2600, 90);
			new MachineGunPod(this, "red", 7300, 2800, 90);
			new MissilePod(this, "red", 7500, 2400, 90);
			new MissilePod(this, "red", 7500, 2600, 90);
			new MissilePod(this, "red", 7500, 2800, 90);
			new MachineGunPod(this, "red", 7450, 3100, 90);
			new MachineGunPod(this, "red", 7450, 3300, 90);
			new MachineGunPod(this, "red", 7450, 3500, 90);
			new MissilePod(this, "red", 7650, 3100, 90);
			new MissilePod(this, "red", 7650, 3300, 90);
			new MissilePod(this, "red", 7650, 3500, 90);
			new MachineGunPod(this, "red", 7450, 2100, 90);
			new MachineGunPod(this, "red", 7450, 1900, 90);
			new MachineGunPod(this, "red", 7450, 1700, 90);
			new MissilePod(this, "red", 7650, 2100, 90);
			new MissilePod(this, "red", 7650, 1900, 90);
			new MissilePod(this, "red", 7650, 1700, 90);
			

			
		}
	
		genTiles();
		playerList.add(player);
		playerList.add(enemy.getPlayer());
	}
	
	//check projectile collisions
	public void checkProjectiles(){
    	for (int i = 0; i < projectiles.size(); i++) {
    		Projectile p = projectiles.get(i);
			for (int j = 0; j < ships.size(); j++) {
				Starship s = ships.get(j);
				if (p.getOwner() != null ) {
					if(!p.getOwner().equals(s) && (!p.getTeam().equals(s.getTeam()) || p.getTeam().equals("none")) && 
							polygon_intersection(p.getPoints(), s.getPoints())){
						//fighters have a weakness to missiles
						if (p instanceof Missile && s instanceof Fighter && !p.pierced.contains(s))
							s.setHealth(s.getHealth()-p.getDamage()*8);
						//interceptor has a resistance to missiles
						else if (p instanceof Missile && s instanceof Interceptor && !p.pierced.contains(s))
							s.setHealth(s.getHealth()-p.getDamage()/2);
						//Missileships have resistance to plasma
						else if (p.texId < 3 && s instanceof Missileship && !p.pierced.contains(s))
							s.setHealth(s.getHealth()-p.getDamage() / 2);
						//Missileships are vulnerable to machineguns
						else if (p.texId == 3 && s instanceof Missileship && !p.pierced.contains(s))
							s.setHealth(s.getHealth()-p.getDamage()*2);
						else if (!p.pierced.contains(s))
							s.setHealth(s.getHealth()-p.getDamage());
		    			s.damageDisplayDelay = 1000;
		    			if (p instanceof Missile) {
		    				Missile m = (Missile)p;
		    				m.destroy(s);
		    			}
		    			else{
			    			if(!p.piercing){
			    				p.destroy();
			    				projectiles.remove(p);
			    			}
			    			else if (!p.pierced.contains(s)){
			    				new Explosion(this, p.center.X()+random.nextInt(21)-10, p.center.Y()+random.nextInt(21)-10, 30);
			    				p.pierced.add(s);
			    			}
		    			}
		    		}
				}
				else if ((!p.getTeam().equals(s.getTeam()) || p.getTeam().equals("none")) && 
							polygon_intersection(p.getPoints(), s.getPoints())) {
					//fighters have a weakness to missiles
					if (p instanceof Missile && s instanceof Fighter && !p.pierced.contains(s))
						s.setHealth(s.getHealth()-p.getDamage()*4);
					//interceptor has a high resistance to missiles
					else if (p instanceof Missile && s instanceof Interceptor && !p.pierced.contains(s))
						s.setHealth(s.getHealth()-p.getDamage()/5);
//					//Missileships have resistance to plasma
//					else if (p.texId < 3 && s instanceof Missileship)
//						s.setHealth(s.getHealth()-p.getDamage() / 2);
					//Missileships are vulnerable to machineguns
					else if (p.texId == 3 && s instanceof Missileship && !p.pierced.contains(s))
						s.setHealth(s.getHealth()-p.getDamage()*2);
					else if (!p.pierced.contains(s))
						s.setHealth(s.getHealth()-p.getDamage());
	    			s.damageDisplayDelay = 1000;
	    			if (p instanceof Missile) {
	    				Missile m = (Missile)p;
	    				m.destroy(s);
	    			}
	    			else{
		    			if(p.getType() != 5 && p.getType() != 6){
		    				p.destroy();
		    				projectiles.remove(p);
		    			}
		    			else{
		    				new Explosion(this, p.center.X()+random.nextInt(21)-10, p.center.Y()+random.nextInt(21)-10, 20);
		    			}
	    			}
				}
			}
		}
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
    
    public void genTiles(){
    	for (int x = -360; x <= WORLD_WIDTH + 720; x+=720) {
			for (int y = -360; y <= WORLD_HEIGHT + 720; y+=720) {
				new Tile(this, x, y);
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
    
    public void destroyAllTiles(){
    	for (int i = backgroundTiles.size()-1; i >= 0; i--) {
			backgroundTiles.get(i).destroy();
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
	
	public int getCameraX(){
		return CURR_X;
	}
	
	public int getCameraY(){
		return CURR_Y;
	}
	
	public int getCameraWidth(){
		return CAMERA_WIDTH;
	}
	
	public int getCameraHeight(){
		return CAMERA_HEIGHT;
	}
	
	public int getWindowWidth(){
		return WINDOW_WIDTH;
	}
	
	public int getWindowHeight(){
		return WINDOW_HEIGHT;
	}
	
	public double getWidthScalar(){
		return(double) CAMERA_WIDTH / (double) WINDOW_WIDTH;
	}
	
	public double getHeightScalar(){
		return(double) CAMERA_HEIGHT / (double) WINDOW_HEIGHT;
	}
	
	//Text is written such that the first letter of the text has center at startx, starty
	public void writeText(String newText, int startx, int starty) {
		writeText(newText, startx, starty, 20);
	}
	
	public void writeText(String newText, int startx, int starty, int textSize) {
		for (int i = 0; i < newText.length(); i++) {
			BitmapFontLetter newLetter = new BitmapFontLetter(this, newText.charAt(i), startx + i * textSize, starty, textSize);
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
	
	//Recursively get a proximity group of allied ships. Used to find approximate fleet strength.
	public void proximityGroup(ArrayList<Starship> chosen, ArrayList<Starship> allShips, Starship current) {
		for (int i = 0; i < allShips.size(); i++) {
			Starship s = allShips.get(i);
			if (distance(current.center.x, current.center.y, s.center.x, s.center.y) < PROXIMITY_SIZE
					&& current.team.equals(s.team) && !chosen.contains(s)) {
				chosen.add(s);
				proximityGroup(chosen, allShips, s);
			}
		}
	}
	
}