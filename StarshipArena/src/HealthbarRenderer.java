import java.util.ArrayList;

public class HealthbarRenderer {
	
	double[] vertices = new double[8];
	double[] textureCoords = new double[8];
	Point currentShipTrueCenter = new Point();
	static Texture colors = new Texture("hpbar_colors.png");
	static Texture haloTexture = new Texture("ships_halo.png");
	Model box = new Model(vertices, textureCoords, new int[]{0, 1, 2, 2, 1, 3});
	
	public void drawAllHPBars(ArrayList<Starship> selected){
		bindHealthTexture();
		for (Starship s : selected) {
			updateTrueCenter(s);
			drawHPBar(s);
		}
	}
	
	public void drawHPBar(Starship s){	
		setColor(-1); //base color
		setModel(currentShipTrueCenter.X() - (s.getMaxHealth() / 2) * 3, currentShipTrueCenter.Y() - s.getClickRadius(), currentShipTrueCenter.X() + (s.getMaxHealth() / 2) * 3, currentShipTrueCenter.Y() - s.getClickRadius() - 10);
	
		setColor(s.getHealth()/s.getMaxHealth()); //color based on hp remaining
		setModel(currentShipTrueCenter.X() - (s.getMaxHealth() / 2) * 3, currentShipTrueCenter.Y() - s.getClickRadius(), currentShipTrueCenter.X() + (s.getHealth() - s.getMaxHealth() / 2) * 3, currentShipTrueCenter.Y() - s.getClickRadius() - 10);
	}
	
	public void drawAllShipHalos(ArrayList<Starship> selected){
		bindShipHaloTexture();
		for (Starship s : selected) {
			updateTrueCenter(s);
			drawShipHalo(s);
		}
	}
	
	public void drawShipHalo(Starship s){
		setTextureCoords(0, 0, 1, 1);
		setModel(currentShipTrueCenter.X() - s.getHaloSize(), currentShipTrueCenter.Y() + s.getHaloSize(),
				currentShipTrueCenter.X() + s.getHaloSize(), currentShipTrueCenter.Y() - s.getHaloSize());
	}
	
	public void setColor(double percentage){
		if(percentage == -1){
			setTextureCoords(0, 0, 0.5, 0.333);
		}
		else if (percentage > 0.8) {
			setTextureCoords(0, 0.333, 0.5, 0.667);
		}
		else if (percentage > 0.6) {
			setTextureCoords(0, 0.667, 0.5, 1);
		}
		else if (percentage > 0.4) {
			setTextureCoords(0.5, 0, 1, 0.333);
		}
		else if (percentage > 0.2) {
			setTextureCoords(0.5, 0.333, 1, 0.667);
		}
		else{
			setTextureCoords(0.5, 0.667, 1, 1);
		}
	}
	
	public void setTextureCoords(double x1, double y1, double x2, double y2){
		textureCoords[0] = x1;
		textureCoords[1] = y1;
		textureCoords[2] = x1;
		textureCoords[3] = y2;
		textureCoords[4] = x2;
		textureCoords[5] = y1;
		textureCoords[6] = x2;
		textureCoords[7] = y2;
		box.setTextureCoords(textureCoords);
	}
	
	public void setModel(double x1, double y1, double x2, double y2){
		vertices[0] = x1;
		vertices[1] = y1;
		vertices[2] = x1;
		vertices[3] = y2;
		vertices[4] = x2;
		vertices[5] = y1;
		vertices[6] = x2;
		vertices[7] = y2;
		box.render(vertices);
	}
	
	public void updateTrueCenter(Starship s){
		currentShipTrueCenter.setX(s.getX() + s.getXOff());
		currentShipTrueCenter.setY(s.getY() + s.getYOff());
		currentShipTrueCenter.rotatePoint(s.getX(), s.getY(), s.getAngle());
	}
	
	public void bindHealthTexture(){
		colors.bind();
	}
	
	public void bindShipHaloTexture(){
		haloTexture.bind();
	}
	

}
