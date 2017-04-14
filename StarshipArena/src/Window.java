import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWVidMode;

public class Window {
	private long window;
	
	private int width, height;
	
	public Window(int mywidth, int myheight){
		setSize(mywidth, myheight);
	}
	
	public void createWindow(String title){
		window = glfwCreateWindow(width, height, title, NULL, NULL);
		if ( window == NULL ){
			throw new RuntimeException("Failed to create the GLFW window");
		}
		
		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center the window
		glfwSetWindowPos(
			window,
			(vidmode.width()) / 2,
			(vidmode.height()) / 2
		);
		
		// Make the window visible
		glfwShowWindow(window);
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
	}
	
	public boolean shouldClose(){
		return glfwWindowShouldClose(window);
	}
	
	public void swapBuffers(){
		glfwSwapBuffers(window);
	}
	
	public void setSize(int newwidth, int newheight){
		width = newwidth;
		height = newheight;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}

}
