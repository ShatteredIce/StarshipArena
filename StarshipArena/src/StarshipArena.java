//Starship Arena - Created on 1/11/17 by Nathan Purwosumarto
//Example of LWJGL 3, displays ships that move randomly in the window

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.ArrayList;
import java.util.Random;


public class StarshipArena {

	//The window handle and size
	private long window;
	
	int WINDOW_WIDTH = 1300;
	int WINDOW_HEIGHT = 900;
	
	int WORLD_WIDTH = 2600;
    int WORLD_HEIGHT = 1800;

    int CURR_X = 0;
	int CURR_Y = 0;
	int CAMERA_SPEED = 10;
	int CAMERA_WIDTH = 2600;
	int CAMERA_HEIGHT = 1800;
	int zoomLevel = 3;
	
	int gameState = 1;
	int SLOW = 1;
    
    boolean panLeft = false;
    boolean panRight = false;
    boolean panUp = false;
    boolean panDown = false;
    
    DoubleBuffer oldMouseX;
    DoubleBuffer oldMouseY;
    DoubleBuffer newMouseX;
    DoubleBuffer newMouseY;
    
    int FIGHTER_COST = 5;
    int INTERCEPTOR_COST = 20;
    int TRANSPORT_COST = 10;
    int BATTLESHIP_COST = 100;
    
	Random random = new Random();
	
	ArrayList<Starship> ships = new ArrayList<>();
	ArrayList<Projectile> projectiles = new ArrayList<>();
	ArrayList<Planet> planets = new ArrayList<>();
	ArrayList<Tile> backgroundTiles = new ArrayList<>();
	ArrayList<Player> playerList = new ArrayList<>(); 
	ArrayList<BitmapFontLetter> text = new ArrayList<>();

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
	
	Layer boxSelect;
	boolean boxSelectCurrent = false;
	
	Layer settingsIcon;
	Button settingsButton = new Button(1250, 898, 1298, 850);
	
	Player player = new Player(this, "blue");
	Enemy enemy = new Enemy(this, new Player(this, "red"));
	
    
	public void run() {

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

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
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		
		// Create the window
		window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Starship Arena [WIP]", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ){
//				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
				CAMERA_WIDTH = 2600;
				CAMERA_HEIGHT = 1800;
				zoomLevel = 3;
				CURR_X = 0;
				CURR_Y = 0;
				gameState = 1;
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
			
			if ( key == GLFW_KEY_A && action == GLFW_RELEASE ) {
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
						if (ships.get(i).getTeam().equals(player.getTeam())) ships.get(i).setSelected(true);
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
			
			if ( key == GLFW_KEY_LEFT_BRACKET && action == GLFW_PRESS ) {
				if (SLOW == 1) SLOW = 5000;
				else if (SLOW == 5000) SLOW = 100000;
			}

			if ( key == GLFW_KEY_RIGHT_BRACKET && action == GLFW_PRESS ) {
				if (SLOW == 100000) SLOW = 5000;
				else if (SLOW == 5000) SLOW = 1;
			}
		
//			if ( key == GLFW_KEY_DOWN && action == GLFW_RELEASE )
//				SLOW = 1;
//			if ( key == GLFW_KEY_UP && action == GLFW_PRESS )
//				SLOW = 5000;
//			if ( key == GLFW_KEY_UP && action == GLFW_RELEASE )
//				SLOW = 1;
			if ( key == GLFW_KEY_1 && action == GLFW_PRESS)
				buyShips(player, 1);
			if ( key == GLFW_KEY_2 && action == GLFW_PRESS)
				buyShips(player, 2);
			if ( key == GLFW_KEY_3 && action == GLFW_PRESS)
				buyShips(player, 3);
			if ( key == GLFW_KEY_4 && action == GLFW_PRESS)
				buyShips(player, 4);
			if ( key == GLFW_KEY_ENTER && action == GLFW_PRESS)
				gameState = 3;
		
		});
		
		//Mouse clicks
		glfwSetMouseButtonCallback (window, (window, button, action, mods) -> {
			DoubleBuffer xpos = BufferUtils.createDoubleBuffer(3);
			DoubleBuffer ypos = BufferUtils.createDoubleBuffer(3);
			glfwGetCursorPos(window, xpos, ypos);
			//convert the glfw coordinate to our coordinate system
			//relative camera coordinates
			xpos.put(1, getWidthScalar() * xpos.get(0) + CURR_X);
			ypos.put(1, (getHeightScalar() * (WINDOW_HEIGHT - ypos.get(0)) + CURR_Y));
			//true window coordinates
			xpos.put(2, xpos.get(0));
			ypos.put(2, (WINDOW_HEIGHT - ypos.get(0)));
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
					}
				}
			}
			else if(gameState == 2){
				if ( button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
					if(settingsButton.isClicked(xpos.get(2), ypos.get(2))){
						gameState = 1;
					}
				}
				else if ( button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE) {
					//System.out.println(xpos.get(1) + " " + ypos.get(1));
					for (int i = 0; i < levelButtons.length; i++) {
						if(levelButtons[i].isClicked(xpos.get(2), ypos.get(2))){
							gameState = 3;
							loadLevel(i+1);
						}
					}
				}
			}
			else if(gameState == 3){
				boolean clickedOnSprite = false;
				if ( button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
					if(settingsButton.isClicked(xpos.get(2), ypos.get(2))){
						gameState = 1;
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
							else s.setSelected(false);
						}
						else if (distance(newMouseX.get(1), newMouseY.get(1), clickCenter.X(), clickCenter.Y()) <= s.getClickRadius()
								|| distance(newMouseX.get(1), oldMouseY.get(1), clickCenter.X(), clickCenter.Y()) <= s.getClickRadius()
								|| distance(oldMouseX.get(1), newMouseY.get(1), clickCenter.X(), clickCenter.Y()) <= s.getClickRadius()
								|| distance(oldMouseX.get(1), oldMouseY.get(1), clickCenter.X(), clickCenter.Y()) <= s.getClickRadius()) {
							if (shipsControllingTeam.equals("") || s.getTeam().equals(shipsControllingTeam)) {
								shipsControllingTeam = s.getTeam();
								if (!shipsControllingTeam.equals(player.getTeam())) {
									selectedUncontrolledShips.add(s);
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
							else s.setSelected(false);
						}
						else s.setSelected(false);
					}
					if (!clickedOnSprite && selectedUncontrolledShips.size() > 0) {
						for (int i = 0; i < selectedUncontrolledShips.size(); i++) {
							selectedUncontrolledShips.get(i).setSelected(true);
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
						if (s.getSelected() && s.getTeam().equals(player.getTeam())) {
							s.setLocationTarget(new Point(xpos.get(1), ypos.get(1)));
							//System.out.println(xpos.get(0) + ", " + ypos.get(0));
						}
					}
				}
			}
		});
		
		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		glMatrixMode(GL_PROJECTION);
        glLoadIdentity(); // Resets any previous projection matrices
        glOrtho(CURR_X, CURR_X + CAMERA_WIDTH, CURR_Y, CURR_Y + CAMERA_HEIGHT, 1, -1);
        glMatrixMode(GL_MODELVIEW);

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		//Enable transparency
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		playerList.add(player);
		
		createShips(200);
		
		new Planet(this, 1300, 900, 1);
		sidebar = new Sidebar(this, WINDOW_WIDTH / 2, WINDOW_HEIGHT / 18);
		titlePage = new Layer(1);
		levelSelect = new Layer(2);
		boxSelect = new Layer(3);
		//static layer on top of game
		settingsIcon = new Layer(4);
		settingsIcon.setTopLeft(WINDOW_WIDTH - 50, WINDOW_HEIGHT - 2);
		settingsIcon.setBottomRight(WINDOW_WIDTH - 2, WINDOW_HEIGHT - 50);
		settingsIcon.setPoints();
				
		genTiles();

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		int slowCounter = 0;
		int counter = 0;
		while ( !glfwWindowShouldClose(window) ) {
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			glEnable(GL_TEXTURE_2D);
			
			//display the title
			if(gameState == 1){
				projectTrueWindowCoordinates();
				titlePage.setTopLeft(250, WINDOW_HEIGHT - 150);
				titlePage.setBottomRight(WINDOW_WIDTH - 250, 150);
				titlePage.setPoints();
				titlePage.display();
				settingsIcon.display();
				glfwSwapBuffers(window); // swap the color buffers
			}
			if(gameState == 2){
				projectTrueWindowCoordinates();
				levelSelect.setTopLeft(200, WINDOW_HEIGHT - 50);
				levelSelect.setBottomRight(WINDOW_WIDTH - 200, 50);
				levelSelect.setPoints();
				levelSelect.display();
				settingsIcon.display();
				glfwSwapBuffers(window); // swap the color buffers
			}
			else if(gameState == 3){
				slowCounter++;
				if (slowCounter >= SLOW) {
					slowCounter = 0;
					//System.out.println(counter);
					counter++;
					
					projectRelativeCameraCoordinates();
					
					//Display background
					for (int t = 0; t < backgroundTiles.size(); t++) {
						backgroundTiles.get(t).display();
					}
					
					//Display planets
					for(int p = 0; p < planets.size(); p++){
						planets.get(p).checkCapturePoint();
						planets.get(p).updateResources();
						//System.out.println(player.getResources());
						planets.get(p).display();
					}
					
					//display ships
					for(int s = 0; s < ships.size(); s++){
						ships.get(s).doRandomMovement();
				    	ships.get(s).setPoints();
				    	ships.get(s).damageDisplayDelay--;
				    	if(ships.get(s).display() == false){
				    		s--;
				    	}
					}
					
					//display projectiles
					for(int p = 0; p < projectiles.size(); p++){
				    	projectiles.get(p).setPoints();
						if(projectiles.get(p).display() == false){
							p--;
						}
					}
				
				//display box select
				if(boxSelectCurrent){
					DoubleBuffer xpos = BufferUtils.createDoubleBuffer(2);
					DoubleBuffer ypos = BufferUtils.createDoubleBuffer(2);
					glfwGetCursorPos(window, xpos, ypos);
					//convert the glfw coordinate to our coordinate system
					xpos.put(1, getWidthScalar() * xpos.get(0) + CURR_X);
					ypos.put(1, (getHeightScalar() * (WINDOW_HEIGHT - ypos.get(0)) + CURR_Y));
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
							first.center = new Point(newFirstX, newFirstY);
							second.center = new Point(newSecondX, newSecondY);
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
					
					//onMouseEvent();
					
					projectTrueWindowCoordinates();
					
					//display settings icon
					settingsIcon.display();
					
					//Display sidebar and figure out what has been selected
					boolean sidebarIsDisplayed = false;
					int sumCurrentHP = 0;
					int sumMaxHP = 0;
					int numFightersSelected = 0;
					int numInterceptorsSelected = 0;
					int numTransportsSelected = 0;
					String shipStatus = "Idle";
					int selectedPlanetResources = Integer.MIN_VALUE;
					String planetControllingTeam = "none";
					String shipsControllingTeam = "none";
					for(int p = 0; p < planets.size(); p++){
						if(planets.get(p).getSelected()){
							sidebar.display();
							sidebarIsDisplayed = true;
							selectedPlanetResources = planets.get(p).getResources();
							planetControllingTeam = planets.get(p).getTeam();
						}
					}
					for (int s = 0; s < ships.size(); s++) {
						if (ships.get(s).getSelected()) {
							sidebar.display();
							sidebarIsDisplayed = true;
							sumCurrentHP += ships.get(s).current_health;
							sumMaxHP += ships.get(s).max_health;
							shipsControllingTeam = ships.get(s).getTeam();
							if (ships.get(s).locationTarget != null && shipStatus.equals("Idle"))shipStatus = "Moving";
							if (ships.get(s).target != null && !shipStatus.equals("Taking damage")) shipStatus = "Engaging enemy";
							if (ships.get(s).damageDisplayDelay > 0) shipStatus = "Taking damage";
							if (ships.get(s) instanceof Fighter) numFightersSelected++;
							else if (ships.get(s) instanceof Interceptor) numInterceptorsSelected++;
							else if (ships.get(s) instanceof Transport) numTransportsSelected++; 
						}
					}
					
					//Display bitmap font letters
					destroyAllText();
					if (sidebarIsDisplayed) {
						if (selectedPlanetResources > Integer.MIN_VALUE) {
							writeText("Planet resources:", 20, 40);
//							if (planetControllingTeam.equals(player.getTeam()))
								writeText("" + selectedPlanetResources, 20, 20);
//							else
//								writeText("??", 20, 20);
							
							writeText("Controlled by:", 20, 100);
							writeText(planetControllingTeam, 20, 80);
						}
						else if (numFightersSelected + numInterceptorsSelected + numTransportsSelected == 1) {
							if (numFightersSelected == 1) writeText("Fighter", 400, 15, 30);
							else if (numInterceptorsSelected == 1) writeText("Interceptor", 400, 15, 30);
							else if (numTransportsSelected == 1) writeText("Transport", 400, 15, 30);
//							if(shipsControllingTeam.equals(player.getTeam()))
								writeText("Armor:" + sumCurrentHP + "/" + sumMaxHP, 800, 20);
//							else
//								writeText("Armor:??/??", 800, 20);
							writeText("Faction:", 20, 100);
							writeText(shipsControllingTeam, 20, 80);
							writeText("Ship status:", 20, 40);
							writeText(shipStatus, 20, 20);
						}
						else if (numFightersSelected + numInterceptorsSelected + numTransportsSelected > 1) {
							writeText("Starfleet(" + (numFightersSelected + numInterceptorsSelected + numTransportsSelected) + ")", 400, 15, 30);
							writeText("Fighters:" + numFightersSelected, 1000, 100);
							writeText("Interceptors:" + numInterceptorsSelected, 1000, 80);
							writeText("Transports:" + numTransportsSelected, 1000, 60);
//							if (shipsControllingTeam.equals(player.getTeam()))
								writeText("Fleet armor:" + sumCurrentHP + "/" + sumMaxHP, 800, 20);
//							else
//								writeText("Fleet armor:??/??", 800, 20);
							writeText("Faction:", 20, 100);
							writeText(shipsControllingTeam, 20, 80);
							writeText("Fleet status:", 20, 40);
							writeText(shipStatus, 20, 20);
						}
						
						for (int i = 0; i < text.size(); i++) {
	//						text.get(i).setPoints();
							text.get(i).display();
						}
					}
					
					glDisable(GL_TEXTURE_2D);
					
					//Check which direction the camera should move, and move accordingly
					if (panLeft)
						CURR_X = Math.max(0, CURR_X - CAMERA_SPEED * WORLD_HEIGHT / 2600);
					if (panRight)
						CURR_X = Math.min(WORLD_WIDTH - CAMERA_WIDTH, CURR_X + CAMERA_SPEED * WORLD_HEIGHT / 2600);
					if (panDown)
						CURR_Y = Math.max(0, CURR_Y - CAMERA_SPEED * WORLD_HEIGHT / 2600);
					if (panUp)
						CURR_Y = Math.min(WORLD_HEIGHT - CAMERA_HEIGHT, CURR_Y + CAMERA_SPEED * WORLD_HEIGHT / 2600);
					
					glfwSwapBuffers(window); // swap the color buffers
		        
				}
			}
		}
	}

	public static void main(String[] args) {
		new StarshipArena().run();
	}
	
	public void projectRelativeCameraCoordinates(){
		glMatrixMode(GL_PROJECTION);
        glLoadIdentity(); // Resets any previous projection matrices
        glOrtho(CURR_X, CURR_X + CAMERA_WIDTH, CURR_Y, CURR_Y + CAMERA_HEIGHT, 1, -1);
        glMatrixMode(GL_MODELVIEW);
	}
	
	public void projectTrueWindowCoordinates(){
		glMatrixMode(GL_PROJECTION);
        glLoadIdentity(); // Resets any previous projection matrices
        glOrtho(0, WINDOW_WIDTH, 0, WINDOW_HEIGHT, 1, -1);
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
	
	//Creates the number of ships specified by the user
	//Each ship has a random starting location and angle
	public void createShips(int num){
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
//		new Fighter(this, "red", 1500, 400, 270, 5);
		new Fighter(this, "blue", 200, 500, 270);
		new Transport(this, "red", 400, 450, 0).setHealth(10000);
		new Transport(this, "red", 100, 450, 0).setHealth(10000);
		new Transport(this, "red", 5, 450, 0).setHealth(10000);
		new Transport(this, "r"
				+ "ed", 400, 600, 0).setHealth(10000);
		new Interceptor(this, "blue", 600, 500, 90);
		new Battleship(this, "blue", 700, 500, 0);
		new Fighter(this, "red", 1700, 1500, 0);
		new Fighter(this, "red", 1750, 1500, 0);
		new Fighter(this, "red", 1650, 1500, 0);
		new Fighter(this, "red", 1700, 1550, 0);
//		new Fighter(this, "blue", 200, 500, 270, 1);
//		new Interceptor(this, 500, 700, 0, 1);
	}
	
	public void buyShips(Player player, int type){
		Planet p = player.getSelectedPlanet();
		//if player has selected an allied planet
		if(p != null && p.getTeam().equals(player.getTeam())){
			//attempt to buy fighter
			if(type == 1 && p.getResources() >= FIGHTER_COST){
				p.setResources(p.getResources() - FIGHTER_COST);
				new Fighter(this, p.getTeam(), (int) p.getX() + random.nextInt(p.getSize() * 2) - p.getSize(), 
						(int) p.getY() + random.nextInt(p.getSize() * 2) - p.getSize(), 0);
			}
			//attempt to buy interceptor
			else if(type == 2 && p.getResources() >= INTERCEPTOR_COST){
				p.setResources(p.getResources() - INTERCEPTOR_COST);
				new Interceptor(this, p.getTeam(), (int) p.getX() + random.nextInt(p.getSize() * 2) - p.getSize(), 
						(int) p.getY() + random.nextInt(p.getSize() * 2) - p.getSize(), 0);
				
			}
			//attempt to buy transport
			else if(type == 3 && p.getResources() >= TRANSPORT_COST){
				p.setResources(p.getResources() - TRANSPORT_COST);
				new Transport(this, p.getTeam(), (int) p.getX() + random.nextInt(p.getSize() * 2) - p.getSize(), 
						(int) p.getY() + random.nextInt(p.getSize() * 2) - p.getSize(), 0);
				
			}
			//attempt to buy battleship
			else if(type == 4 && p.getResources() >= BATTLESHIP_COST){
				p.setResources(p.getResources() - BATTLESHIP_COST);
				new Battleship(this, p.getTeam(), (int) p.getX() + random.nextInt(p.getSize() * 2) - p.getSize(), 
						(int) p.getY() + random.nextInt(p.getSize() * 2) - p.getSize(), 0);
				
			}
		}
	}
	
	public void updateZoomLevel(boolean zoomOut){
		int ZOOM_WIDTH = 650;
		int ZOOM_HEIGHT = 450;
		if(zoomOut){
			if(zoomLevel < 5 && WORLD_WIDTH >= CAMERA_WIDTH + ZOOM_WIDTH && WORLD_HEIGHT >= CAMERA_HEIGHT + ZOOM_HEIGHT){
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
			if(zoomLevel > 1){
				zoomLevel--;
				CAMERA_WIDTH -= ZOOM_WIDTH;
				CAMERA_HEIGHT -= ZOOM_HEIGHT;
				CURR_X += ZOOM_WIDTH / 2;
				CURR_Y += ZOOM_HEIGHT / 2;
			}
		}
	}
	//TODO Maybe make consecutive levels related to each other (i.e. level 2 involves you starting with the two planets you controlled from level 1)
	public void loadLevel(int level){
		destroyAllShips();
		destroyAllPlanets();
		destroyAllProjectiles();
		destroyAllTiles();
		
		zoomLevel = 3;
		CAMERA_WIDTH = 2600;
		CAMERA_HEIGHT = 1800;
		if(level == 1){
			WORLD_WIDTH = 3900;
		    WORLD_HEIGHT = 2700;
		    CURR_X = 0;
			CURR_Y = 0;
			enemy = new Enemy(this, new Player(this, "red"));
			new Planet(this, 1350, 1000, 1);
			new Planet(this, 3000, 1500, 2).setTeam("red");
			new Fighter(this, "blue", 500, 400, 0);
			new Fighter(this, "blue", 600, 350, 0);
			new Fighter(this, "blue", 400, 350, 0);
//			new Fighter(this, "blue", 700, 600, 0);
//			new Fighter(this, "blue", 800, 550, 0);
//			new Fighter(this, "blue", 600, 550, 0);
//			new Fighter(this, "blue", 900, 400, 0);
//			new Fighter(this, "blue", 1000, 350, 0);
//			new Fighter(this, "blue", 800, 350, 0);
			
			new Fighter(this, "red", 2800, 1500, 135);
			new Fighter(this, "red", 3000, 1500, 90);
			new Fighter(this, "red", 3000, 1700, 80);
			new Fighter(this, "red", 3200, 1500, 150);
			new Fighter(this, "red", 3200, 1300, 160);
			new Fighter(this, "red", 3000, 1300, 150);
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
			new Fighter(this, "blue", 800, 550, 0);
			new Fighter(this, "blue", 600, 550, 0);
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
			new Planet(this, 350, 200, 1).setTeam("blue");
			new Planet(this, 2000, 700, 2).setTeam("blue");
			new Planet(this, 2250, 3000, 3).setTeam("red");
			new Planet(this, 4700, 2300, 4).setTeam("red");
			new Fighter(this, "blue", 350, 200, 0);
			new Fighter(this, "blue", 450, 150, 0);
			new Fighter(this, "blue", 250, 150, 0);
			
			new Fighter(this, "blue", 2000, 700, 0);
			new Fighter(this, "blue", 2100, 650, 0);
			new Fighter(this, "blue", 1900, 650, 0);
			
			new Fighter(this, "red", 2250, 3000, 0);
			new Fighter(this, "red", 2350, 2950, 0);
			new Fighter(this, "red", 2150, 2950, 0);
			
			new Fighter(this, "red", 4700, 2300, 0);
			new Fighter(this, "red", 4800, 2250, 0);
			new Fighter(this, "red", 4600, 2250, 0);
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
			new Planet(this, 350, 200, 1).setTeam("blue");
			new Planet(this, 2000, 700, 2).setTeam("blue");
			new Planet(this, 2250, 3000, 3).setTeam("blue");
			new Planet(this, 4700, 2300, 4).setTeam("blue");
			
			new Planet(this, 4350, 4200, 5).setTeam("red");
			new Planet(this, 9500, 1500, 6).setTeam("red");
			new Planet(this, 1500, 4000, 0).setTeam("red");
			new Planet(this, 2700, 6300, 0).setTeam("red");
			new Planet(this, 6700, 7500, 0).setTeam("red");
			
			new Fighter(this, "blue", 350, 200, 0);
			new Fighter(this, "blue", 450, 150, 0);
			new Fighter(this, "blue", 250, 150, 0);
			new Interceptor(this, "blue", 150, 100, 0);
			new Interceptor(this, "blue", 550, 100, 0);
			
			new Interceptor(this, "blue", 2000, 700, 0);
			new Fighter(this, "blue", 2100, 650, 0);
			new Fighter(this, "blue", 1900, 650, 0);
			
			new Fighter(this, "blue", 2250, 3000, 0);
			new Fighter(this, "blue", 2350, 2950, 0);
			new Fighter(this, "blue", 2150, 2950, 0);
			
			new Fighter(this, "blue", 4800, 2250, 0);
			new Fighter(this, "blue", 4600, 2250, 0);
			new Interceptor(this, "blue", 4900, 2050, 0);
			new Interceptor(this, "blue", 4700, 2050, 0);
			new Interceptor(this, "blue", 4500, 2050, 0);
			
			
			new Fighter(this, "red", 6700, 7500, 0);
			new Fighter(this, "red", 6800, 7450, 0);
			new Fighter(this, "red", 6600, 7450, 0);
			new Interceptor(this, "red", 6500, 7400, 0);
			new Interceptor(this, "red", 6900, 7400, 0);
			
			new Interceptor(this, "red", 1500, 4000, 0);
			new Interceptor(this, "red", 1600, 3950, 0);
			new Interceptor(this, "red", 1400, 3950, 0);
			
			new Fighter(this, "red", 9500, 1500, 0);
			new Fighter(this, "red", 9600, 1450, 0);
			new Fighter(this, "red", 9400, 1450, 0);
			new Interceptor(this, "red", 9700, 1400, 0);
			new Interceptor(this, "red", 9300, 1400, 0);
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
			new Planet(this, 350, 200, 1).setTeam("blue");
			new Planet(this, 2000, 700, 2).setTeam("blue");
			new Planet(this, 2250, 3000, 3).setTeam("blue");
			new Planet(this, 4700, 2300, 4).setTeam("blue");
			
			new Planet(this, 4350, 4200, 5).setTeam("red");
			
			new Planet(this, 9500, 1500, 6).setTeam("blue");
			new Planet(this, 1500, 4000, 0).setTeam("blue");
			new Planet(this, 2700, 6300, 0).setTeam("blue");
			new Planet(this, 6700, 7500, 0).setTeam("blue");
			
			new Fighter(this, "blue", 350, 200, 0);
			
			new Interceptor(this, "blue", 2000, 700, 0);
			
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
	
		genTiles();
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
		    			s.setHealth(s.getHealth()-p.getDamage());
		    			s.damageDisplayDelay = 100;
		    			p.destroy();
		    			projectiles.remove(p);
		    		}
				}
				else if ((!p.getTeam().equals(s.getTeam()) || p.getTeam().equals("none")) && 
							polygon_intersection(p.getPoints(), s.getPoints())) {
					s.setHealth(s.getHealth()-p.getDamage());
	    			s.damageDisplayDelay = 100;
	    			p.destroy();
	    			projectiles.remove(p);
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
	
	public int normalizeAngle(int angle){
		while(angle < 0){
			angle += 360;
		}
		while(angle >= 360){
			angle -= 360;
		}
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
    
    public void genTiles(){
    	for (int x = 360; x <= WORLD_WIDTH + 720; x+=720) {
			for (int y = 360; y <= WORLD_HEIGHT + 720; y+=720) {
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
			projectiles.get(i).destroy();
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
	
}