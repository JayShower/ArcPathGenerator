package math;

public class DirectedArc {

	public final PolarPoint start;
	public final PolarPoint end;
	public final CartesianPoint center;

	public DirectedArc(CartesianPoint center, PolarPoint start, PolarPoint end) {
		this.center = center;
		this.start = start;
		this.end = end;
	}

	public DirectedArc(double x, double y, double radius, double startAngle, double endAngle) {
		this(new CartesianPoint(x, y), new PolarPoint(radius, startAngle), new PolarPoint(radius, endAngle));
	}

}