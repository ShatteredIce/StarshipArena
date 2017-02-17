
public class Tile {
	
	StarshipArena game;
	Model model;
	
	double[] vertices;
	double[] textureCoords; 
	int[] indices;
	Point center;
	Point[] points;
	int size = 360;
	
	Tile(StarshipArena mygame, int spawnx, int spawny){
		game = mygame;
		center = new Point(spawnx, spawny);
		points = generatePoints();
		vertices = new double[points.length * 2];
		setTextureCoords();
		setIndices();
		setPoints();
		model = new Model(vertices, textureCoords, indices);
		game.addTile(this);
	}
	
	public void setPoints(){
		int v_index = 0;
		for (int i = 0; i < points.length; i++) {
			points[i].setX(center.X() + points[i].getXOffset());
			points[i].setY(center.Y() + points[i].getYOffset());
			v_index = 2*i;
			vertices[v_index] = points[i].X();
			vertices[v_index+1] = points[i].Y();	
		}
	}
	
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			new Point(-size, -size, true),
			new Point(-size, size, true),
			new Point(size, size, true),
			new Point(size, -size, true),
		};
		return points;
	}
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 1, 0, 0, 1, 0, 1, 1};
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2, 2, 3, 0};
	}
	
	public void display(){
		model.render(vertices);
	}
	
	public int getSize(){
		return size;
	}
	
	public double getX() {
		return center.X();
	}
	
	public double getY() {
		return center.Y();
	}

}
