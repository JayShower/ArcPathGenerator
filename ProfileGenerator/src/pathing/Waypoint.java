package pathing;

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
	 *            from x axis. Must be between -pi and pi
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
	 *            from x axis. Must be between -pi and pi
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

	@Override
	public String toString() {
		return String.format("(%s), (%s)", position.toString(), heading.toString());
	}

}