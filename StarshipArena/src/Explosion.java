
public class Explosion {
	
	StarshipArena game;
	
	Model model;
	static Texture tex1 = new Texture("explosionframe1.png");
	static Texture tex2 = new Texture("explosionframe2.png");
	static Texture tex3 = new Texture("explosionframe3.png");
	static Texture tex4 = new Texture("explosionframe4.png");
	static Texture tex5 = new Texture("explosionframe5.png");
	static Texture tex6 = new Texture("explosionframe6.png");
	static Texture tex7 = new Texture("explosionframe7.png");
	static Texture tex8 = new Texture("explosionframe8.png");
	static Texture tex9 = new Texture("explosionframe9.png");
	static Texture tex10 = new Texture("explosionframe10.png");
	static Texture tex11 = new Texture("explosionframe11.png");
	
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
		setTextureCoords();
		setIndices();
		setPoints();
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
		if(lifetime < ticksPerFrame){
			tex1.bind();
		}
		else if(lifetime < ticksPerFrame * 2){
			tex2.bind();
		}
		else if(lifetime < ticksPerFrame * 3){
			tex3.bind();
		}
		else if(lifetime < ticksPerFrame * 4){
			tex4.bind();
		}
		else if(lifetime < ticksPerFrame * 5){
			tex5.bind();
		}
		else if(lifetime < ticksPerFrame * 6){
			tex6.bind();
		}
		else if(lifetime < ticksPerFrame * 7){
			tex7.bind();
		}
		else if(lifetime < ticksPerFrame * 8){
			tex8.bind();
		}
		else if(lifetime < ticksPerFrame * 9){
			tex9.bind();
		}
		else if(lifetime < ticksPerFrame * 10){
			tex10.bind();
		}
		else if(lifetime < ticksPerFrame * 11){
			tex11.bind();
		}
		else{
			destroy();
			game.removeExplosion(this);
		}
	
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
