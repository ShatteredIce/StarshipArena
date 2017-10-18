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
	int planetSize;
	int radar_range;
	int texId = 0;
	boolean selected = false;
	static Texture[] textures = {new Texture("planet0.png"), new Texture("planet1.png"),
			new Texture("planet2.png"), new Texture("planet3.png"), new Texture("planet4.png"),
			new Texture("planet5.png"), new Texture("planet6.png")
	};

	//halo and FOW rendering variables
	Model haloModel;
	double[] haloVertices;
	double[] radarVertices;
	Point[] haloPoints;
	Point[] radarPoints;
	int haloSize;
	static Texture blueHalo = new Texture("blue_halo.png");
	static Texture redHalo = new Texture("red_halo.png");
	static Texture whiteHalo = new Texture("white_halo.png");
	static Texture FOWTexture = new Texture("FOW_halo.png");
	//planet ingame variables
	String team = "none";
	String capturingTeam = "none";
	int captureStrength = 0;
	int captureTime;
	int maxCaptureTime;
	int storedResources = 0;
	int resourcesPerTick = 1;
	int resourcesCooldown = 50;
	int currentCooldown = 50;
	ArrayList<String[]> buildOrders = new ArrayList<String[]>();
	
	//Planet surface dimensions
	double surfaceX;
	double surfaceY;
	double dimensionX;
	double dimensionY;
	
	Planet(StarshipArena mygame, double spawnx, double spawny, int newTexId){
		
		game = mygame;
		
		//Define planet size variables:
		planetSize = (int)(2000 * game.levelScale);
		radar_range = (int)(5000 * game.levelScale);
		haloSize = (int)(3000 * game.levelScale);
		captureTime = (int)(5000 * game.levelScale);
		maxCaptureTime = (int)(5000 * game.levelScale);
				
				
		texId = newTexId;
		center = new Point(spawnx, spawny);
		points = generatePoints(planetSize);
		vertices = new double[points.length * 2];
		setTextureCoords();
		setIndices();
		setPoints();
		model = new Model(vertices, textureCoords, indices);
		//set up halo and FOW around the planet
		haloPoints = generatePoints(haloSize);
		haloVertices = new double[haloPoints.length * 2];
		radarPoints = generatePoints(radar_range);
		radarVertices = new double[radarPoints.length * 2];
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
						storedResources/= 2;
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
	
	public void setRadarPoints(){
		int v_index = 0;
		for (int i = 0; i < radarPoints.length; i++) {
			radarPoints[i].setX(center.X() + radarPoints[i].getXOffset());
			radarPoints[i].setY(center.Y() + radarPoints[i].getYOffset());
			v_index = 2*i;
			radarVertices[v_index] = radarPoints[i].X();
			radarVertices[v_index+1] = radarPoints[i].Y();	
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
		textures[texId].bind();
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
			checkLoop();
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
	
	public void display(boolean inRange){
		setTexture();
		model.render(vertices);
		if(inRange){
			setHaloTexture();
			haloModel.render(haloVertices);
		}
	}
	
	public void showView(){
		setRadarPoints();
		FOWTexture.bind();
		haloModel.render(radarVertices);
	}
	
	public void destroy(){
		model.destroy();
		game.removePlanet(this);
	}
	
	public int getSize(){
		return planetSize;
	}
	
	public int getRadarRange(){
		return radar_range;
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
	
	public void setTeam(String newTeam) {
		team = newTeam;
		captureTime = maxCaptureTime;
	}
	
	public void setLoop(String team, String ship) {
		String[] temp = new String[2];
		temp[0] = team;
		temp[1] = ship;
		for (int i = 0; i < buildOrders.size(); i++) {
			if (buildOrders.get(i)[0].equals(team)) {
				buildOrders.remove(i);
				i--;
			}
		}
		if (!temp.equals("0"))
			buildOrders.add(temp);
	}
	
	public void checkLoop() {
		for (int i = 0; i < buildOrders.size(); i++) {
			if (buildOrders.get(i)[0].equals(team)) {
				if (team.equals("blue")) {
					//This little "temp" workaround is required because buyShips references the planet that the player
					//has selected
					Planet temp = game.player.getSelectedPlanet();
					game.player.setSelectedPlanet(this);
					game.buyShips(game.player, Integer.parseInt(buildOrders.get(i)[1]));
					game.player.setSelectedPlanet(temp);
				}
				else {
					Planet temp = game.enemy.getPlayer().getSelectedPlanet();
					game.enemy.getPlayer().setSelectedPlanet(this);
					game.buyShips(game.enemy.getPlayer(), Integer.parseInt(buildOrders.get(i)[1]));
					game.enemy.getPlayer().setSelectedPlanet(temp);
				}
				//I don't check for "none" player buying ships because they can't
			}
		}
	}

}
