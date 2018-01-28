package math;

public class CartesianPoint {

	public static final CartesianPoint origin = new CartesianPoint(0, 0);

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

	public CartesianPoint shift(double dx, double dy) {
		return new CartesianPoint(x + dx, y + dy);
	}

	public CartesianPoint shift(Vector v) {
		return new CartesianPoint(x + v.x, y + v.y);
	}

}