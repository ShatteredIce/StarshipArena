
public class Layer {
	
	Model model;
	static Texture tex = new Texture("box_select.png");
	
	double[] vertices;
	double[] textureCoords; 
	int[] indices;
	Point topLeft;
	Point bottomRight;
	Point[] points;
	
	
	Layer(){
		topLeft = new Point();
		bottomRight = new Point();
		points = generatePoints();
		vertices = new double[points.length * 2];
		setTextureCoords();
		setIndices();
		setPoints();
		model = new Model(vertices, textureCoords, indices);

	}
	
	public void setPoints(){
		int v_index = 0;
		points[0] = topLeft;
		points[1].setX(topLeft.X());
		points[1].setY(bottomRight.Y());
		points[2].setX(bottomRight.X());
		points[2].setY(topLeft.Y());
		points[3] = bottomRight;
		for (int i = 0; i < points.length; i++) {
			//System.out.println(points[i].X() + " " + points[i].Y());
			v_index = 2*i;
			vertices[v_index] = points[i].X();
			vertices[v_index+1] = points[i].Y();	
		}
	}
	
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			topLeft,
			new Point(topLeft.X(), bottomRight.Y()),
			new Point(bottomRight.X(), topLeft.Y()),
			bottomRight,
		};
		return points;
	}
	
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 0, 0, 1, 1, 0, 1, 1};
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2, 2, 1, 3};
	}
	
	public void display(){
		tex.bind();
		model.render(vertices);
	}
	
	public void setTopLeft(double newx, double newy){
		topLeft.setX(newx);
		topLeft.setY(newy);
	}
	
	public void setBottomRight(double newx, double newy){
		bottomRight.setX(newx);
		bottomRight.setY(newy);
	}
	
}
