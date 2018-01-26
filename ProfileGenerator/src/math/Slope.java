package math;

public class Slope {

	public final double cosine;
	public final double sine;

	public Slope(double dx, double dy) {
		double magnitude = Math.sqrt(dx * dx + dy * dy);
		cosine = dx / magnitude;
		sine = dy / magnitude;
	}

	public Slope(double x1, double y1, double x2, double y2) {
		this(x2 - x1, y2 - y1);
	}

	public Slope(CartesianPoint start, CartesianPoint end) {
		this(start.x, start.y, end.x, end.y);
	}

	public Slope(DirectedLineSegment line) {
		this(line.start.x, line.start.y, line.end.x, line.end.y);
	}
}