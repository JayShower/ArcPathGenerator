package pathing;

import math.BezierCurve;
import math.DirectedLine;
import math.Vector;
import motion.GenerateMotionProfile;
import motion.MotionProfile;
import motion.MotionProfileConstraints;
import motion.MotionProfileGoal;
import motion.MotionProfileGoal.CompletionBehavior;
import motion.MotionState;
import pathing.Path.TrajectoryHolder;
import plot.Graphing;

public class PathCreation {

	public static void main(String[] args) {
		// Waypoint first = new Waypoint(0, 0, Math.PI / 2, 0);
		// Waypoint second = new Waypoint(0, 150, Math.PI / 2, 50);
		// PathSegment straight = new PathSegment(first, second, 0, new
		// DirectedLine(first, second));
		//
		// Waypoint third = new Waypoint(150, 300, 0, 0);
		// PathSegment curve = new PathSegment(first, second,
		// straight.curve.getTotalArcLength(),
		// connectWaypointsWithBezier(second, third));
		//
		// Path center = PathCreation.generatePath(new PathSegment[] { straight, curve
		// }, 5, 100, 100);
		Waypoint first = new Waypoint(0, 0, Math.PI / 2, 0);
		Waypoint last = new Waypoint(50, 50, 0, 0);
		PathSegment segment = new PathSegment(last, last, 0, connectWaypointsWithBezier(first, last));
		Path center = PathCreation.generatePath(50.0, 10.0, segment);
		TrajectoryHolder traj = center.getTrajectoryPoints(5, 0.01);
		System.out.println(traj.left[traj.left.length - 1].position);
		System.out.println(traj.right[traj.right.length - 1].position);
		Graphing.graphPath(center, "Path 1");
	}

	public static final double midControlPoint = 0.5;

	public static BezierCurve connectWaypointsWithBezier(Waypoint start, Waypoint end) {
		return connectWaypointsWithBezier(start, end, midControlPoint);
	}

	public static BezierCurve connectWaypointsWithBezier(Waypoint start, Waypoint end, double n) {
		DirectedLine lineA = new DirectedLine(start);
		DirectedLine lineB = new DirectedLine(end);
		Vector intersection = lineA.getIntersection(lineB);
		Vector v1 = start;
		Vector v5 = end;
		Vector v3 = intersection;
		DirectedLine ac = new DirectedLine(v1, v3);
		// System.out.println(ac);
		DirectedLine ce = new DirectedLine(v3, v5);
		// System.out.println(ce);
		Vector v2 = ac.getPointAtPercent(n);
		Vector v4 = ce.getPointAtPercent(n);
		// System.out.println(v1);
		// System.out.println(v2);
		// System.out.println(v3);
		// System.out.println(v4);
		// System.out.println(v5);
		return new BezierCurve(new Vector[] { v1, v2, v3, v4, v5 });
	}

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
	 *            duration in milliseconds of each trajectory point. Usually
	 *            around10 or 20.
	 * 
	 * @param maxAcceleration
	 *            absolute maximum robot acceleration in units per second per second
	 * 
	 * @param maxVelocity
	 *            absolute maximum robot velocity in units per second
	 * 
	 * @return the generated MotionProfile
	 */
	public static Path generatePath(double maxAcceleration, double maxVelocity, PathSegment... pathSegments) {
		MotionProfileConstraints constraints = new MotionProfileConstraints(maxVelocity, maxAcceleration);

		MotionState previousState = new MotionState(0, 0, 0, 0);

		Waypoint endWaypoint = pathSegments[0].end;
		MotionProfileGoal goalState = new MotionProfileGoal(pathSegments[0].curve.getTotalArcLength(),
				endWaypoint.absVelocity, CompletionBehavior.OVERSHOOT);
		MotionProfile currentProfile = GenerateMotionProfile.generateStraightMotionProfile(constraints, goalState,
				previousState);
		previousState = currentProfile.endState();

		for (int i = 1; i < pathSegments.length; i++) {
			endWaypoint = pathSegments[i].end;
			goalState = new MotionProfileGoal(pathSegments[i].curve.getTotalArcLength(), endWaypoint.absVelocity,
					CompletionBehavior.OVERSHOOT);
			currentProfile.appendProfile(
					GenerateMotionProfile.generateStraightMotionProfile(constraints, goalState, previousState));
			previousState = currentProfile.endState();
		}

		return new Path(currentProfile, pathSegments);
	}

}
