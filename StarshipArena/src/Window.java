import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

public class Window {
	
	private long window;
	private int width, height, fullscreenXOffset, fullscreenYOffset;
	
	public Window(int mywidth, int myheight){
		setSize(mywidth, myheight);
	}
	
	public void createWindow(String title){
		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//		window = glfwCreateWindow(width, height, title, NULL, NULL);
		window = glfwCreateWindow(vidmode.width(), vidmode.height(), title, glfwGetPrimaryMonitor(), NULL);
		if ( window == NULL ){
			throw new RuntimeException("Failed to create the GLFW window");
		}
		
		fullscreenXOffset = (vidmode.width() - width) / 2;
		fullscreenYOffset = (vidmode.height() - height) / 2;
		
//		// Center the window
//		glfwSetWindowPos(
//			window,
//			(vidmode.width() - width) / 2,
//			(vidmode.height() - height) / 2
//		);
		
		// Make the window visible
		glfwShowWindow(window);
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
	}
	
	public boolean shouldClose(){
		return glfwWindowShouldClose(window);
	}
	
	// swap the color buffers
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
	
	public int getXOffset(){
		return fullscreenXOffset;
	}
	
	public int getYOffset(){
		return fullscreenYOffset;
	}
	
	public long getWindowHandle(){
		return window;
	}

}
