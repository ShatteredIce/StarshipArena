//Utility class
public class Point {
	
	double x = 0;
	double y = 0;
	
	public Point(){
	
	}
	
	public Point(double startx, double starty){
		x = startx;
		y = starty;
	}
	
	public void setX(double newx){
		x = newx;
	}
	
	public void setY(double newy) {
		y = newy;
	}
	
	public double X() {
		return x;
	}
	
	public double Y() {
		return y;
	}
	
	public void rotatePoint(double center_x, double center_y, int angle){
		double cos_angle = Math.cos(Math.toRadians(angle));
		double sin_angle = Math.sin(Math.toRadians(angle));
		double rotated_x = ((x-center_x)*(cos_angle) - (y-center_y)*(sin_angle)) + center_x;
		double rotated_y = ((y-center_y)*(cos_angle) + (x-center_x)*(sin_angle)) + center_y;
		x = rotated_x;
		y = rotated_y;
	}

}
