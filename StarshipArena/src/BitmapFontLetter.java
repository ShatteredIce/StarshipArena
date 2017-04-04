
public class BitmapFontLetter {
	
	StarshipArena game;
	Model model;
	
	
	//Bitmap is 16 by 16
	static Texture bitmap_font = new Texture("bitmap_font.png");
	double[] vertices;
	double[] textureCoords; 
	int[] indices;
	Point center;
	Point[] points;
	
	public BitmapFontLetter(StarshipArena myGame, int spawnx, int spawny) {
		this(myGame, ' ', spawnx, spawny);
	}
	
	public BitmapFontLetter(StarshipArena myGame, char letter, int spawnx, int spawny) {
		game = myGame;
		center = new Point(spawnx, spawny);
		points = generatePoints();
		setTextureCoords(letter);
		setIndices();
		setPoints();
		model = new Model(vertices, textureCoords, indices);
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
			new Point(-5, 5, true),
			new Point(-5, -5, true),
			new Point(5, 5, true),
			new Point(5, -5, true),
		};
		return points;
	}
}
