import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
	private int id;
	private int width;
	private int height;
	
	public Texture(String filename){
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer comp = BufferUtils.createIntBuffer(1);
		
		ByteBuffer data = stbi_load("./res/"+filename,width,height,comp,4);

		id = glGenTextures();
		this.width = width.get();
		this.height = height.get();
		
		glBindTexture(GL_TEXTURE_2D, id);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
		stbi_image_free(data);
	}
	
//	protected void finalize() throws Throwable{
//		glDeleteTextures(id);
//		super.finalize();
//	}
	
	public void destroy(){
		glDeleteTextures(id);
	}
	
	public void bind(){
		glBindTexture(GL_TEXTURE_2D, id);
	}

}
