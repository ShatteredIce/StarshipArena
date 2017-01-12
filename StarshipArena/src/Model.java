import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;


public class Model {
	
	private int draw_count;
	private int v_id;
	private int t_id;
	
	public Model(double[] vertices, double[] textureCoords){
		draw_count = vertices.length / 2;

		v_id = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, v_id);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(vertices), GL_DYNAMIC_DRAW);
		
		t_id = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, t_id);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(textureCoords), GL_DYNAMIC_DRAW);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);

	}
	
	public void render() {
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		
		glBindBuffer(GL_ARRAY_BUFFER, v_id);
		glVertexPointer(2, GL_DOUBLE, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, t_id);
		glTexCoordPointer(2, GL_DOUBLE, 0, 0);
		
		glDrawArrays(GL_TRIANGLES, 0, draw_count);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	}

	private DoubleBuffer createBuffer(double[] data) {
		DoubleBuffer buffer = BufferUtils.createDoubleBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
