package pathing;

import math.Vector;

public class WayPoint extends Vector {

	public final Vector heading;
	public final double vel;

	/**
	 * 
	 * @param x
	 *            position in units
	 * @param y
	 *            position in units <br>
	 *            positive x is towards right of robot, positive y is forwards <br>
	 * @param vel
	 *            velocity of the robot as it passes through this waypoint. Make
	 *            sure to specify positive and negative velocity.
	 * @param heading
	 *            way front of robot is facing, in radians. Positve angle is upwards
	 *            from x axis. Must be between 0 and 2pi
	 */
	public WayPoint(double x, double y, double vel, double heading) {
		super(x, y);
		this.heading = new Vector(heading);
		this.vel = vel;
	}

}