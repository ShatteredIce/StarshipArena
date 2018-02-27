//Utility class to hold all game textures and load them when requested

public class GameTextures {
	
	static Texture tex1 = new Texture("title_page.png");
	static Texture tex2 = new Texture("level_select.png");
	static Texture tex3 = new Texture("box_select.png");
	static Texture tex4 = new Texture("settings_icon.png");
	static Texture tex5 = new Texture("planet_buyshipsmenu.png");
	static Texture tex6 = new Texture("border.png");
	static Texture tex7 = new Texture("victory_screen.png");
	static Texture tex8 = new Texture("defeat_screen.png");
	static Texture tex9 = new Texture("audio_on.png");
	static Texture tex10 = new Texture("audio_off.png");
	
	public void loadTexture(int id){
		switch (id) {
		case 1:
			tex1.bind();
			break;
		case 2:
			tex2.bind();
			break;
		case 3:
			tex3.bind();
			break;
		case 4:
			tex4.bind();
			break;
		case 5:
			tex5.bind();
			break;
		case 6:
			tex6.bind();
			break;
		case 7:
			tex7.bind();
			break;
		case 8:
			tex8.bind();
			break;
		case 9:
			tex9.bind();
			break;
		case 10:
			tex10.bind();
			break;
		default:
			try {
				throw new GameException("Requested Texture ID does not exist");
			} catch (GameException e) {
				e.printStackTrace();
			}
		}
	}

}
