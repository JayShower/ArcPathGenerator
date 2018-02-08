package pathing;

import math.Util;
import math.Vector;

public final class Waypoint {

	public static final Vector RIGHT = new Vector(0);
	public static final Vector STRAIGHT = new Vector(Math.PI / 2);
	public static final Vector LEFT = new Vector(Math.PI);
	public static final Vector BACK = new Vector(3 * Math.PI / 2);

	public final Vector position;
	public final Vector heading;
	public final double vel;

	/**
	 * 
	 * @param x
	 *            position in units
	 * @param y
	 *            position in units <br>
	 *            positive x is towards right of robot, positive y is forwards <br>
	 * @param heading
	 *            way front of robot is facing, in radians. Positve angle is upwards
	 *            from x axis. Must be between 0 and 2pi
	 * @param vel
	 *            absolute velocity of the robot as it passes through this waypoint.
	 */
	public Waypoint(double x, double y, double heading, double vel) {
		this(new Vector(x, y), new Vector(heading), vel);
	}

	/**
	 * 
	 * @param position
	 *            Robot position. Positive x is towards right of robot, positive y
	 *            is forwards <br>
	 * @param heading
	 *            way front of robot is facing, in radians. Positve angle is upwards
	 *            from x axis. Must be between 0 and 2pi
	 * @param vel
	 *            absolute velocity of the robot as it passes through this waypoint.
	 */
	public Waypoint(Vector position, double heading, double vel) {
		this(position, new Vector(heading), heading);
	}

	/**
	 * 
	 * @param position
	 *            Robot position. Positive x is towards right of robot, positive y
	 *            is forwards
	 * @param heading
	 *            Vector in direction of robot heading.
	 * @param vel
	 *            absolute velocity of the robot as it passes through this waypoint.
	 */
	public Waypoint(Vector position, Vector heading, double vel) {
		this.position = position;
		this.heading = heading.normalized();
		this.vel = vel;
	}

	public Waypoint shift(Vector translation, double rotation, Vector point) {
		double dx = position.x - point.x;
		double dy = position.y - point.y;
		Vector pos = new Vector(dx, dy).rotate(rotation).add(point);
		Vector head = heading.rotate(rotation);
		return new Waypoint(pos, head, vel);
	}

	public static boolean areCollinear(Waypoint a, Waypoint b) {
		if (a.heading.equals(b.heading)) {
			double dx = b.position.x - a.position.x;
			double dy = b.position.y - a.position.y;
			Vector direction = new Vector(dx, dy);
			return a.heading.equals(direction.normalized());
		} else {
			return false;
		}
	}

	public static boolean doIntersect(Waypoint a, Waypoint b) {
		return !Util.epsilonEquals(a.heading.slope(), b.heading.slope());
	}

	public static boolean areParallel(Waypoint a, Waypoint b) {
		return a.heading.equals(b.heading);
	}

	public static boolean areAntiparallel(Waypoint a, Waypoint b) {
		boolean cosine = Util.epsilonEquals(a.heading.cosine(), -b.heading.cosine());
		boolean sine = Util.epsilonEquals(a.heading.sine(), -b.heading.sine());
		return cosine && sine;
	}

}