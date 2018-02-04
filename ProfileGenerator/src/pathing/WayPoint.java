package pathing;

import math.Vector;

public final class WayPoint {

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
	public WayPoint(double x, double y, double heading, double vel) {
		this(new Vector(x, y), new Vector(heading), vel);
	}

	/**
	 * 
	 * @param position
	 *            Robot position. Positive x is towards right of robot, positive y
	 *            is forwards <br>
	 * @param vel
	 *            velocity of the robot as it passes through this waypoint. Make
	 *            sure to specify positive and negative velocity.
	 * @param heading
	 *            way front of robot is facing, in radians. Positve angle is upwards
	 *            from x axis. Must be between 0 and 2pi
	 */
	public WayPoint(Vector position, double heading, double vel) {
		this(position, new Vector(heading), heading);
	}

	/**
	 * 
	 * @param position
	 *            Robot position. Positive x is towards right of robot, positive y
	 *            is forwards
	 * @param vel
	 *            velocity of the robot as it passes through this waypoint. Make
	 *            sure to specify positive and negative velocity.
	 * @param heading
	 *            Unit vector in direction of robot heading.
	 */
	public WayPoint(Vector position, Vector heading, double vel) {
		this.position = position;
		this.heading = heading;
		this.vel = vel;
	}

}