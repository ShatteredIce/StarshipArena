package mainframe;

public class Button {
	
	Point topLeft;
	Point bottomRight;
	
	Button(double topX, double topY, double botX, double botY){
		topLeft = new Point(topX, topY);
		bottomRight = new Point(botX, botY);
	}
	
	public boolean isClicked(double mouseX, double mouseY){
		if(mouseX > topLeft.X() && mouseX < bottomRight.X() && mouseY < topLeft.Y() && mouseY > bottomRight.Y()){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void setTopLeft(double newx, double newy){
		topLeft.setX(newx);
		topLeft.setY(newy);
	}
	
	public void setBottomRight(double newx, double newy){
		bottomRight.setX(newx);
		bottomRight.setY(newy);
	}

}
