package mainframe;
//Utility class
public class Point {
	
	double x = 0;
	double y = 0;
	double x_offset = 0;
	double y_offset = 0;
	
	public Point(){
	
	}
	
	public Point(double xvalue, double yvalue){
		x = xvalue;
		y = yvalue;
	}
	
	public Point(double xvalue, double yvalue, boolean byOffset){
		if(byOffset){
			x_offset = xvalue;
			y_offset = yvalue;
		}
		else{
			x = xvalue;
			y = yvalue;
		}
	}
	
	public Point(double startx, double starty, double new_x_offset, double new_y_offset){
		x = startx;
		y = starty;
		x_offset = new_x_offset;
		y_offset = new_y_offset;
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
	
	public void setXOffset(double newx){
		x_offset = newx;
	}
	
	public void setYOffset(double newy) {
		y_offset = newy;
	}
	
	public double getXOffset(){
		return x_offset;
	}
	
	public double getYOffset(){
		return y_offset;
	}
	
	public void rotatePoint(double center_x, double center_y, double angle){
		double cos_angle = Math.cos(Math.toRadians(angle));
		double sin_angle = Math.sin(Math.toRadians(angle));
		double rotated_x = ((x-center_x)*(cos_angle) - (y-center_y)*(sin_angle)) + center_x;
		double rotated_y = ((y-center_y)*(cos_angle) + (x-center_x)*(sin_angle)) + center_y;
		x = rotated_x;
		y = rotated_y;
	}

}