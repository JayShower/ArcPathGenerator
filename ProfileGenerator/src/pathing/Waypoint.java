package pathing;

import math.CartesianPoint;

public class Waypoint extends CartesianPoint {

	public final double heading;

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
	 *            from x axis
	 * @param absVelocity
	 *            absVelocity of the robot as it drives through this waypoint
	 */
	public Waypoint(double x, double y, double heading, double absVelocity) {
		super(x, y);
		this.heading = adjustHeading(heading);
		this.absVelocity = absVelocity;
	}

	private static double adjustHeading(double heading) {
		heading = heading % 360;
		if (Math.abs(heading) > 180) {
			heading = heading - Math.signum(heading) * 360;
		}
		return heading;
	}

}