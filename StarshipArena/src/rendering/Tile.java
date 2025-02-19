package rendering;
import mainframe.Point;
import mainframe.StarshipArena;

public class Tile {
	
	StarshipArena game;
	Model model;
	static Texture backgroundTexture = new Texture("background_semitransparent.png");
	//Texture for planet surface
	Texture specialTexture = null;
	
	double[] vertices;
	double[] textureCoords; 
	int[] indices;
	Point center;
	Point[] points;
	int size;
	//Default (space) tiles
	Tile(StarshipArena mygame, double spawnx, double spawny){
		this(mygame, spawnx, spawny, 3600);
	}
	//Custom (planet) tiles
	public Tile(StarshipArena mygame, double spawnx, double spawny, int newsize){
		game = mygame;
		size = (int)(newsize);
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
		if (specialTexture != null) specialTexture.bind();
		else backgroundTexture.bind();
		model.render(vertices);
	}
	
	public void destroy(){
		model.destroy();
		game.removeTile(this);
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
	
	public void setSpecialTexture(Texture tex) {
		specialTexture = tex;
	}

}
