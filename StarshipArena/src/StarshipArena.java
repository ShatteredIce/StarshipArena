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
	int WIDTH = 1300;
    int HEIGHT = 900;
	int CURR_X = WIDTH / 4;
	int CURR_Y = HEIGHT / 4;
    
    
    boolean panLeft = false;
    boolean panRight = false;
    boolean panUp = false;
    boolean panDown = false;
    
	Random random = new Random();
	
	ArrayList<Projectile> projectiles = new ArrayList<>();
	ArrayList<Starship> ships = new ArrayList<>();
	ArrayList<Planet> planets = new ArrayList<>();
	
	Planet selectedPlanet = null;
	Sidebar sidebar;
    
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
		window = glfwCreateWindow(WIDTH, HEIGHT, "Starship Arena [WIP]", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			
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
        glOrtho(CURR_X, CURR_X + WIDTH / 2, CURR_Y, CURR_Y + HEIGHT / 2, 1, -1);
        glMatrixMode(GL_MODELVIEW);

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		//Enable transparency
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		createShips(20);
		
		new Planet(this, 500, 500);
		sidebar = new Sidebar(this, 1175, 450);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			
			glEnable(GL_TEXTURE_2D);
			
			//Display planets
			for(int p = 0; p < planets.size(); p++){
				planets.get(p).display();
			}
			
			//display ships
			for(int s = 0; s < ships.size(); s++){
				ships.get(s).doRandomMovement();
		    	ships.get(s).setPoints();
		    	ships.get(s).display();
			}
			
			//display projectiles
			for(int p = 0; p < projectiles.size(); p++){
		    	projectiles.get(p).setPoints();
		    	if(projectiles.size() == 0){
		    		break;
		    	}
				projectiles.get(p).display();
			}
			
			//Display sidebar
			for(int p = 0; p < planets.size(); p++){
				if(planets.get(p).getSelected() == true){
					sidebar.display();
				}
			}
		
			checkProjectiles();
			
			onMouseEvent();
			
			glDisable(GL_TEXTURE_2D);
			
			if (panLeft)
				CURR_X = Math.max(0, CURR_X - 3);
			if (panRight)
				CURR_X = Math.min(WIDTH / 2, CURR_X + 3);
			if (panDown)
				CURR_Y = Math.max(0, CURR_Y - 3);
			if (panUp)
				CURR_Y = Math.min(HEIGHT / 2, CURR_Y + 3);
			
			glfwSwapBuffers(window); // swap the color buffers
			glMatrixMode(GL_PROJECTION);
	        glLoadIdentity(); // Resets any previous projection matrices
	        glOrtho(CURR_X, CURR_X + WIDTH / 2, CURR_Y, CURR_Y + HEIGHT / 2, 1, -1);
	        glMatrixMode(GL_MODELVIEW);
		}
	}

	public static void main(String[] args) {
		new StarshipArena().run();
	}
	
	public int[] getScreenBounds(){
    	int[] bounds = new int[4];
    	bounds[0] = 0;
    	bounds[1] = WIDTH;
    	bounds[2] = 0;
    	bounds[3] = HEIGHT;
    	return bounds;
    }
	
	//Creates the number of ships specified by the user
	//Each ship has a random starting location and angle
	public void createShips(int num){
		int startx;
		int starty;
		int angle;
		for(int i = 0; i < num; i++){
			startx = random.nextInt(WIDTH - 100) + 50;
			starty = random.nextInt(HEIGHT - 100) + 50;
			angle = random.nextInt(360);
			new Starship(this, startx, starty, angle, 1);
		}
		new Fighter(this, "none", 300, 300, 0, 10);
	}
	
	//check projectile collisions
	public void checkProjectiles(){
    	for (int i = 0; i < projectiles.size(); i++) {
    		Projectile p = projectiles.get(i);
			for (int j = 0; j < ships.size(); j++) {
				Starship s = ships.get(j);
				if(!p.getOwner().equals(s) && polygon_intersection(p.getPoints(), s.getPoints())){
	    			s.setHealth(s.getHealth()-p.getDamage());
	    			projectiles.remove(p);
	    		}
			}
		}
    }
	
	//responds to mouse clicks
	public void onMouseEvent(){
		if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == 1){
			boolean clickedOnSprite = false;
			DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer ypos = BufferUtils.createDoubleBuffer(1);
			glfwGetCursorPos(window, xpos, ypos);
			//convert the glfw coordinate to our coordinate system
			ypos.put(0, HEIGHT - ypos.get(0));
			//System.out.println(xpos.get(0) + " " +ypos.get(0));

			for (int i = 0; i < planets.size(); i++) {
				Planet p = planets.get(i);
				if(distance(p.getX(), p.getY(), xpos.get(0), ypos.get(0)) <= p.getSize() - 30){
					if(selectedPlanet != null){
						selectedPlanet.setSelected(false);
					}
					selectedPlanet = p;
					p.setSelected(true);
					clickedOnSprite = true;
					break;
				}
			}
			if(clickedOnSprite == false){
				if(selectedPlanet != null){
					selectedPlanet.setSelected(false);
					selectedPlanet = null;
				}
			}
		}
	}
	
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
    
    public ArrayList<Starship> getAllShips(){
    	return ships;
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
}