
public class Layer {
	
	Model model;
	static Texture tex1 = new Texture("title_page.png");
	static Texture tex2 = new Texture("level_select.png");
	static Texture tex3 = new Texture("box_select.png");
	static Texture tex4 = new Texture("settings_icon.png");
	static Texture tex5 = new Texture("planet_buyshipsmenu.png");
	static Texture tex6 = new Texture("border.png");
	static Texture tex7 = new Texture("victory_screen.png");
	static Texture tex8 = new Texture("defeat_screen.png");
	
	double[] vertices;
	double[] textureCoords; 
	int[] indices;
	Point topLeft;
	Point bottomRight;
	Point[] points;
	int id;
	
	
	Layer(int layerId){
		id = layerId;
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
	
	public void setTexture(){
		if(id == 1){
			tex1.bind();
		}
		else if(id == 2){
			tex2.bind();
		}
		else if(id == 3){
			tex3.bind();
		}
		else if(id == 4){
			tex4.bind();
		}
		else if(id == 5){
			tex5.bind();
		}
		else if(id == 6){
			tex6.bind();
		}
		else if(id == 7){
			tex7.bind();
		}
		else if(id == 8){
			tex8.bind();
		}
	}
	
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 0, 0, 1, 1, 0, 1, 1};
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2, 2, 1, 3};
	}
	
	public void display(){
		setTexture();
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
