package pathing;

import math.Vector;

public class WayPoint extends Vector {

	public final Vector heading;

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
	 */
	public WayPoint(double x, double y, double heading) {
		super(x, y);
		this.heading = new Vector(heading);
	}

	public WayPoint(double x, double y, Vector heading, double absVelocity) {
		super(x, y);
		this.heading = heading;
	}

}