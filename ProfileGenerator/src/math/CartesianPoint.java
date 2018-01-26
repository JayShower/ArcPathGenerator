package math;
public class CartesianPoint {

	public final double x;
	public final double y;

	public CartesianPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getDistance(CartesianPoint other) {
		return getDistance(this.x, this.y, other.x, other.y);
	}

	public static double getDistance(double x1, double y1, double x2, double y2) {
		return getDistance(x2 - x1, y2 - y1);
	}

	public static double getDistance(double dx, double dy) {
		return Math.sqrt(dx * dx + dy * dy);
	}

}