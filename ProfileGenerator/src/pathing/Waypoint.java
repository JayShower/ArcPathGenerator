package pathing;

import math.Vector;

public class Waypoint extends Vector {

	public final Vector heading;

	public final double absVelocity;

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
	 * @param absVelocity
	 *            absVelocity of the robot as it drives through this waypoint
	 */
	public Waypoint(double x, double y, double heading, double absVelocity) {
		super(x, y);
		this.heading = new Vector(heading);
		this.absVelocity = absVelocity;
	}

	public Waypoint(double x, double y, Vector heading, double absVelocity) {
		super(x, y);
		this.heading = heading;
		this.absVelocity = absVelocity;
	}

}