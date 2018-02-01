package math;

import pathing.WayPoint;

public class DirectedLine implements Curve {

	public final Vector start;
	public final Vector end;

	public DirectedLine(Vector start, Vector end) {
		this.start = start;
		this.end = end;
	}

	public DirectedLine(WayPoint w) {
		this(w, w.add(w.heading));
	}

	public Vector getDirection() {
		return end.subtract(start);
	}

	public Vector getPointAtD(double distance) {
		// System.out.println("Point At D");
		// System.out.println(distance);
		// System.out.println(start.cosine() * distance);
		// System.out.println(start.sine() * distance);
		Vector direction = getDirection();
		return start.add(new Vector(direction.cosine() * distance, direction.sine() * distance));
	}

	/**
	 * 
	 * @param percent
	 *            percent along line in decimal format (0.5 = 50%)
	 * @return
	 */
	public Vector getPointAtPercent(double percent) {
		return getPointAtD(percent * getDirection().magnitude);
	}

	public Vector getIntersection(DirectedLine other) {
		double slope1 = getDirection().slope();
		double slope2 = other.getDirection().slope();
		double numerator = slope2 * other.start.x - slope1 * start.x + start.y - other.start.y;
		double denominator = slope2 - slope1;
		double x = numerator / denominator;
		double y = slope1 * (x - start.x) + start.y;
		return new Vector(x, y);
	}

	@Override
	public double getTotalArcLength() {
		return getDirection().magnitude;
	}

	@Override
	public double getCurvatureAtArcLength(double arcLength) {
		return 0;
	}

	@Override
	public String toString() {
		return "Line: " + start.toString() + " " + end.toString();
	}

	@Override
	public Vector getPointAtArcLength(double arcLength) {
		return getPointAtD(arcLength);
	}

}
