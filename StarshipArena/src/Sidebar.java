
public class Sidebar {
	
	StarshipArena game;
	Model model;
	Texture tex;
	
	double[] vertices;
	double[] textureCoords; 
	int[] indices;
	Point center;
	Point[] points;
	
	int true_X;
	int true_Y;
	
	Sidebar(StarshipArena mygame, int spawnx, int spawny){
		game = mygame;
		center = new Point(spawnx, spawny);
		true_X = spawnx;
		true_Y = spawny;
		points = generatePoints();
		vertices = new double[points.length * 2];
		setTextureCoords();
		setIndices();
		setTexture();
		setPoints();
		model = new Model(vertices, textureCoords, indices);

	}
	
	public void setPoints(){
		int v_index = 0;
		center.setX(game.getWidthScalar()*true_X + game.getCameraX());
		center.setY(game.getHeightScalar()*true_Y + game.getCameraY());
		for (int i = 0; i < points.length; i++) {
			points[i].setX(center.X() + (points[i].getXOffset()*game.getWidthScalar()));
			points[i].setY(center.Y() + (points[i].getYOffset()*game.getHeightScalar()));
			v_index = 2*i;
			vertices[v_index] = points[i].X();
			vertices[v_index+1] = points[i].Y();	
		}
	}
	
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			new Point(-125, -450, true),
			new Point(-125, 450, true),
			new Point(125, 450, true),
			new Point(125, -450, true),
		};
		return points;
	}
	
	public void setTexture(){
		tex = new Texture("planet_sidebar.png");
	}
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 1, 0, 0, 1, 0, 1, 1};
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2, 2, 3, 0};
	}
	
	public void display(){
		tex.bind();
		model.render(vertices);
	}
	
	public double getX() {
		return center.X();
	}
	
	public double getY() {
		return center.Y();
	}

}
