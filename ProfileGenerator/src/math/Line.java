package math;

import pathing.WayPoint;

public class Line implements Curve {

	public final Vector start;
	public final Vector end;
	private Vector direction;

	public Line(Vector start, Vector end) {
		this.start = start;
		this.end = end;
	}

	public Line(WayPoint w) {
		start = w.position;
		end = w.position.add(w.heading);
		direction = w.heading;
	}

	public Vector getDirection() {
		if (direction == null)
			direction = end.subtract(start);
		return direction;
	}

	public Vector getPointAtD(double distance) {
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
		return getPointAtD(percent * getDirection().magnitude());
	}

	public boolean intersect(Line other) {
		return getDirection().slope() != other.getDirection().slope();
	}

	public boolean isParallel(Line other) {
		return (getDirection().cosine() == other.getDirection().cosine())
				&& (getDirection().cosine() == other.getDirection().cosine());
	}

	public boolean isAntiparallel(Line other) {
		return (getDirection().cosine() == -other.getDirection().cosine())
				&& (getDirection().cosine() == -other.getDirection().cosine());
	}

	public Vector getIntersection(Line other) {
		if (!intersect(other))
			return null;
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
		return getDirection().magnitude();
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
