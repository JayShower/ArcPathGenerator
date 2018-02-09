package math;

import pathing.Waypoint;

public class LineSegment implements Curve {

	public final Vector start;
	public final Vector end;
	private Vector direction;

	public LineSegment(Vector start, Vector end) {
		this.start = start;
		this.end = end;
	}

	public LineSegment(Waypoint w) {
		start = w.position;
		end = w.position.add(w.heading);
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

	public Vector getIntersection(LineSegment other) {
		Vector p = start;
		Vector r = getDirection();
		Vector q = other.start;
		Vector s = other.getDirection();
		double cross = r.crossProduct(s);
		Vector diff = q.subtract(p);
		double diffCross = diff.crossProduct(r);
		if (cross == 0 && diffCross == 0) { // segments are collinear
			double t0 = diff.dotProduct(r) / r.dotProduct(r);
			double t1 = t0 + s.dotProduct(r) / r.dotProduct(r);
			return getPointAtPercent((t0 + t1) / 2.0);
		} else if (cross == 0 && diffCross != 0) {
			return null; // don't intersect
		} else {
			double t = diff.crossProduct(s) / r.crossProduct(s);
			return getPointAtPercent(t);
		}
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
	public Vector getPointAtArcLength(double arcLength) {
		return getPointAtD(arcLength);
	}

	@Override
	public String toString() {
		return String.format("(%s), (%s)", start.toString(), end.toString());
	}

}
