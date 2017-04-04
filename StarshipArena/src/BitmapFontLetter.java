
public class BitmapFontLetter {
	
	StarshipArena game;
	Model model;
	
	
	//Bitmap is 16 by 16
	static Texture bitmap_font = new Texture("bitmap_font.png");
	double[] vertices;
	double[] textureCoords; 
	int[] indices;
	Point center;
	int trueX;
	int trueY;
	static int size = 30;
	Point[] points;
	//Spawnx and spawny represent the distance from the left bottom corner of the window that the text should appear
	public BitmapFontLetter(StarshipArena myGame, int spawnx, int spawny) {
		this(myGame, ' ', spawnx, spawny);
	}
	
	public BitmapFontLetter(StarshipArena myGame, char letter, int spawnx, int spawny) {
		game = myGame;
		trueX = spawnx;
		trueY = spawny;
		center = new Point(spawnx, spawny);
		points = generatePoints();
		vertices = new double[points.length * 2];
		setTextureCoords(letter);
		setIndices();
		setPoints();
		model = new Model(vertices, textureCoords, indices);
		game.text.add(this);
	}
	
	public void setTexture(){
		bitmap_font.bind();
	}
	
	public void setTextureCoords(char letter) {
		int code = letter;
		textureCoords = new double[]{(code % 16) / 16.0, (code / 16) / 16.0
			, (code % 16) / 16.0, (code / 16) / 16.0 + 1.0 / 16
			, (code % 16) / 16.0 + 1.0 / 16, (code / 16) / 16.0
			, (code % 16) / 16.0 + 1.0 / 16, (code / 16) / 16.0 + 1.0 / 16};
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2, 2, 1, 3};
	}
	
	//TODO change this from the Sidebar copy paste into something else that works
	public void setPoints(){
		int v_index = 0;
		center.setX(game.getWidthScalar() * trueX + game.getCameraX());
		center.setY(game.getHeightScalar() * trueY + game.getCameraY());
		for (int i = 0; i < points.length; i++) {
			points[i].setX(center.X() + (points[i].getXOffset() * game.getWidthScalar()));
			points[i].setY(center.Y() + (points[i].getYOffset() * game.getHeightScalar()));
			v_index = 2*i;
			vertices[v_index] = points[i].X();
			vertices[v_index+1] = points[i].Y();	
		}
//		for (int i = 0; i < points.length; i++) {
//			points[i].setX(center.X() + points[i].getXOffset());
//			points[i].setY(center.Y() + points[i].getYOffset());
//			points[i].rotatePoint(center.X(), center.Y(), 0);
//			v_index = 2*i;
//			vertices[v_index] = points[i].X();
//			vertices[v_index+1] = points[i].Y();
//		}
	}
	
	public Point[] generatePoints(){
		Point[] points = new Point[]{
			new Point(-size / 2, size / 2, true),
			new Point(-size / 2, -size / 2, true),
			new Point(size / 2, size / 2, true),
			new Point(size / 2, -size / 2, true),
		};
		return points;
	}
	
	public void display() {
		setTexture();
		model.render(vertices);
	}
}
