
public class Explosion {
	
	StarshipArena game;
	
	Model model;
	static Texture explosion_sprites = new Texture("explosion_sprites.png");
	
	double[] vertices;
	double[] textureCoords; 
	int[] indices;
	Point center;
	Point[] points;
	
	int size = 100;
	int ticksPerFrame = 2;
	int lifetime = -1;
	
	Explosion(StarshipArena mygame, double spawnx, double spawny) {
		this(mygame, spawnx, spawny, 100);
	}
	Explosion(StarshipArena mygame, double spawnx, double spawny, int newsize){
		game = mygame;
		center = new Point(spawnx, spawny);
		size = newsize;
		points = generatePoints();
		vertices = new double[points.length * 2];
		setIndices();
		setPoints();
		setTextureCoords();
		model = new Model(vertices, textureCoords, indices);
		game.addExplosion(this);

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
			new Point(-size, size, true),
			new Point(-size, -size, true),
			new Point(size, size, true),
			new Point(size, -size, true),
		};
		return points;
	}
	
	public void setTexture(){
		explosion_sprites.bind();
		if(lifetime < ticksPerFrame){
			setTextureCoords(0, 0, 0, 0.5, 0.25, 0, 0.25, 0.5);
		}
		else if(lifetime < ticksPerFrame * 2){
			setTextureCoords(0.125, 0, 0.125, 0.5, 0.25, 0, 0.25, 0.5);
		}
		else if(lifetime < ticksPerFrame * 3){
			setTextureCoords(0.25, 0, 0.25, 0.5, 0.375, 0, 0.375, 0.5);
		}
		else if(lifetime < ticksPerFrame * 4){
			setTextureCoords(0.375, 0, 0.375, 0.5, 0.5, 0, 0.5, 0.5);
		}
		else if(lifetime < ticksPerFrame * 5){
			setTextureCoords(0.5, 0, 0.5, 0.5, 0.625, 0, 0.625, 0.5);
		}
		else if(lifetime < ticksPerFrame * 6){
			setTextureCoords(0.625, 0, 0.625, 0.5, 0.75, 0, 0.75, 0.5);
		}
		else if(lifetime < ticksPerFrame * 7){
			setTextureCoords(0.75, 0, 0.75, 0.5, 0.875, 0, 0.875, 0.5);
		}
		else if(lifetime < ticksPerFrame * 8){
			setTextureCoords(0.875, 0, 0.875, 0.5, 1, 0, 1, 0.5);
		}
		else if(lifetime < ticksPerFrame * 9){
			setTextureCoords(0, 0.5, 0, 1, 0.125, 0.5, 0.125, 1);
		}
		else if(lifetime < ticksPerFrame * 10){
			setTextureCoords(0.125, 0.5, 0.125, 1, 0.25, 0.5, 0.25, 1);
		}
		else if(lifetime < ticksPerFrame * 11){
			setTextureCoords(0.25, 0.5, 0.25, 1, 0.375, 0.5, 0.375, 1);
		}
		else{
			destroy();
		}
		model.setTextureCoords(textureCoords);
	
	}
	
	public void update(){
		lifetime++;
	}
	
	public void destroy(){
		model.destroy();
		game.removeExplosion(this);
	}
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 0, 0, 1, 1, 0, 1, 1};
	}
	
	public void setTextureCoords(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4){
		textureCoords[0] = x1;
		textureCoords[1] = y1;
		textureCoords[2] = x2;
		textureCoords[3] = y2;
		textureCoords[4] = x3;
		textureCoords[5] = y3;
		textureCoords[6] = x4;
		textureCoords[7] = y4;
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2, 2, 1, 3};
	}
	
	public void display(){
		setTexture();
		model.render(vertices);
	}
	
	public void setSize(int newsize) {
		size = newsize;
	}
	
}
