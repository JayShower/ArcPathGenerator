package pathing;

import java.util.List;

public class ProfileGenerator {

	/**
	 * Creates a new profile with these specifications
	 * 
	 * @param waypoints
	 *            The points (x units, y units) you want your robot to go drive
	 *            through at a specific heading (radians). <br>
	 *            <br>
	 *            Positive x is to the right of the robot, positive y is to the
	 *            front of the robot. Units must be consistent in all arguments<br>
	 *            <br>
	 *            Consecutive points can be joined by either:
	 *            <ul>
	 *            <li>a straight line (headings line up)</li>
	 *            <li>or an arc.</li>
	 *            </ul>
	 *            The shape connecting 2 points is determined automatically. <br>
	 *            <br>
	 *            If you want two waypoints to be joined by an arc, those waypoints
	 *            must have the same angle relative to the chord connecting them.
	 *            For example, say you had one waypoint at (0,0) with a heading of
	 *            pi/3, and another waypoint at (50,0). The second waypoint's
	 *            heading must be 2*pi/3 in order to be able to make an arc. <br>
	 *            <br>
	 *            The path generated will not follow an arc exactly, instead it will
	 *            follow a path (approximately an arc) that allows the robot to
	 *            deaccelerate smoothly instead of having to instantly deaccelerate
	 *            to follow an arc<br>
	 *            <br>
	 *            If it is not possible to connect the waypoints with an arc or
	 *            line, an exception will be thrown.
	 * 
	 * @param pointDuration
	 *            duration in milliseconds of each trajectory point. Usually around
	 *            10 or 20.
	 * 
	 * @param maxAcceleration
	 *            absolute maximum robot acceleration in units per second per second
	 * 
	 * @param maxVelocity
	 *            absolute maximum robot velocity in units per second
	 * 
	 * @param robotWidth
	 *            width of drivebase in units
	 * 
	 * @return the generated motionprofile
	 */
	public static MotionProfile generateProfile(List<Waypoint> waypoints, int pointDuration, double maxAcceleration,
			double maxVelocity, double robotWidth) throws InvalidWaypointsException {

		List<TrajectoryPoint> centerTrajectory = generateCenterTrajectory(waypoints, pointDuration, maxAcceleration,
				maxVelocity);

		MotionProfile profile = new MotionProfile(centerTrajectory, robotWidth);
		return profile;
	}

	private static List<TrajectoryPoint> generateCenterTrajectory(List<Waypoint> waypoints, int pointDuration,
			double maxAcceleration, double maxVelocity) {

		return null;
	}

}