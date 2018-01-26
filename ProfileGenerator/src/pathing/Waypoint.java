package pathing;
import math.CartesianPoint;

public class Waypoint extends CartesianPoint {

	public final double heading;

	public Waypoint(double x, double y, double heading) {
		super(x, y);
		this.heading = heading;
	}

}