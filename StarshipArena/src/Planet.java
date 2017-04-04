import java.util.ArrayList;

public class Planet {
	
	StarshipArena game;
	//planet rendering variables
	Model model;
	double[] vertices;
	double[] textureCoords; 
	int[] indices;
	Point center;
	Point[] points;
	int planetSize = 200;
	int texId = 0;
	boolean selected = false;
	static Texture tex0 = new Texture("planet0.png");
	static Texture tex1 = new Texture("planet1.png");
	static Texture tex2 = new Texture("planet2.png");
	static Texture tex3 = new Texture("planet3.png");
	static Texture tex4 = new Texture("planet4.png");
	static Texture tex5 = new Texture("planet5.png");
	static Texture tex6 = new Texture("planet6.png");
	//halo rendering variables
	Model haloModel;
	double[] haloVertices;
	Point[] haloPoints;
	int haloSize = 400;
	static Texture blueHalo = new Texture("blue_halo.png");
	static Texture redHalo = new Texture("red_halo.png");
	static Texture whiteHalo = new Texture("white_halo.png");
	//planet ingame variables
	String team = "none";
	String capturingTeam = "none";
	int captureStrength = 0;
	int captureTime = 500;
	int maxCaptureTime = 500;
	int storedResources = 0;
	int resourcesPerTick = 1;
	int resourcesCooldown = 50;
	int currentCooldown = 50;
	
	
	
	Planet(StarshipArena mygame, int spawnx, int spawny, int newTexId){
		game = mygame;
		texId = newTexId;
		center = new Point(spawnx, spawny);
		points = generatePoints(planetSize);
		vertices = new double[points.length * 2];
		setTextureCoords();
		setIndices();
		setPoints();
		model = new Model(vertices, textureCoords, indices);
		//set up halo around the planet
		haloPoints = generatePoints(haloSize);
		haloVertices = new double[haloPoints.length * 2];
		setHaloPoints();
		haloModel = new Model(haloVertices, textureCoords, indices);
		game.addPlanet(this);
	}
	
	public ArrayList<Starship> getShips(){
		ArrayList<Starship> shipsList = game.getAllShips();
		ArrayList<Starship> orbitingShips = new ArrayList<Starship>();
		for(int i = 0; i < shipsList.size(); i++){
			if(game.distance(center.X(), center.Y(), shipsList.get(i).getX(), shipsList.get(i).getY()) <= haloSize){
				orbitingShips.add(shipsList.get(i));
			}
		}
		return orbitingShips;
	}
	
	public void checkCapturePoint(){
		capturingTeam = "none";
		ArrayList<String> teamsInOrbit = new ArrayList<String>();
		ArrayList<Integer> teamCaptureStrength = new ArrayList<Integer>();
		Boolean foundTeam;
		int largestTeamStrength = 0;
		ArrayList<Starship> orbitingShips = getShips();
		if(orbitingShips.size() != 0){
			//iterate through all the orbiting ships
			for (int i = 0; i < orbitingShips.size(); i++) {
				foundTeam = false;
				Starship s = orbitingShips.get(i);
				//rogue ships cannot capture
				if(s.getTeam().equals("none")){
					break;
				}
				//check to see if team is already in array
				for(int j = 0; j < teamsInOrbit.size(); j++){
					//if team is in array, increment capture strength for that team
					if(teamsInOrbit.get(j).equals(s.getTeam())){
						teamCaptureStrength.set(j, teamCaptureStrength.get(j) + 1);
						foundTeam = true;
					}
				}
				//if team is not in array, add it
				if(foundTeam == false){
					teamsInOrbit.add(s.getTeam());
					teamCaptureStrength.add(1);
				}
			}
			//set largest team strength and capturing team
			for(int t = 0; t < teamCaptureStrength.size(); t++){
				if(largestTeamStrength < teamCaptureStrength.get(t)){
					largestTeamStrength = teamCaptureStrength.get(t);
					capturingTeam = teamsInOrbit.get(t);
				}
			}
			//set capture strength
			captureStrength = largestTeamStrength * 2 - orbitingShips.size();
			if(captureStrength < 0){
				capturingTeam = "none";
				captureStrength *= -1;
			}
			//if capturing team owns the planet
			if(capturingTeam.equals(team)){
				captureTime += Math.round(Math.sqrt(captureStrength));
				if(captureTime > maxCaptureTime){
					captureTime = maxCaptureTime;
				}
			}
			//if capturing team doesn't own the planet
			else{
				captureTime -= Math.round(Math.sqrt(captureStrength));
				if(captureTime <= 0){
					captureTime = 0;
					if(team.equals("none")){
						team = capturingTeam;
					}
					else{
						team = "none";
						captureTime = maxCaptureTime;
					}
				}
			}
		}
		//no orbiting ships
		else{
			if(captureTime < maxCaptureTime){
				captureTime++;
			}
		}
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
	
	public void setHaloPoints(){
		int v_index = 0;
		for (int i = 0; i < haloPoints.length; i++) {
			haloPoints[i].setX(center.X() + haloPoints[i].getXOffset());
			haloPoints[i].setY(center.Y() + haloPoints[i].getYOffset());
			v_index = 2*i;
			haloVertices[v_index] = haloPoints[i].X();
			haloVertices[v_index+1] = haloPoints[i].Y();	
		}
	}
	
	public Point[] generatePoints(int size){
		Point[] points = new Point[]{
			new Point(-size, -size, true),
			new Point(-size, size, true),
			new Point(size, size, true),
			new Point(size, -size, true),
		};
		return points;
	}
	
	public void setTexture(){
		if(texId == 1){
			tex1.bind();
		}
		else if(texId == 2){
			tex2.bind();
		}
		else if(texId == 3){
			tex3.bind();
		}
		else if(texId == 4){
			tex4.bind();
		}
		else if(texId == 5){
			tex5.bind();
		}
		else if(texId == 6){
			tex6.bind();
		}
		else{
			tex0.bind();
		}
	}
	
	public void setHaloTexture(){
		if(team.equals("blue")){
			blueHalo.bind();
		}
		else if(team.equals("red")){
			redHalo.bind();
		}
		else{
			whiteHalo.bind();
		}
	}
	
	public void updateResources() {
		if(team.equals("none")){
			return;
		}
		//if team is not none and cooldown for resources is over, get resources
		else if(currentCooldown == 0){
			storedResources += resourcesPerTick;
			currentCooldown = resourcesCooldown;
		}
		else{
			currentCooldown--;
		}
		
	}
	
	public void setTextureCoords(){
		textureCoords = new double[]{0, 1, 0, 0, 1, 0, 1, 1};
	}
	
	public void setIndices(){
		indices = new int[]{0, 1, 2, 2, 3, 0};
	}
	
	public void display(){
		setTexture();
		model.render(vertices);
		setHaloTexture();
		haloModel.render(haloVertices);
	}
	
	public void destroy(){
		model.destroy();
		game.removePlanet(this);
	}
	
	public int getSize(){
		return planetSize;
	}
	
	public void setResources(int resources){
		storedResources = resources;
	}
	
	public int getResources(){
		return storedResources;
	}
	
	public void setSelected(boolean state){
		selected = state;
	}
	
	public boolean getSelected(){
		return selected;
	}
	
	public String getTeam(){
		return team;
	}
	
	public double getX() {
		return center.X();
	}
	
	public double getY() {
		return center.Y();
	}

}
