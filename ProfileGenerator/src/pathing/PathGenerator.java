package pathing;

import java.util.ArrayList;
import java.util.List;

import math.CartesianPoint;
import motion.GenerateMotionProfile;
import motion.MotionProfile;
import motion.MotionProfileConstraints;
import motion.MotionProfileGoal;
import motion.MotionProfileGoal.CompletionBehavior;
import motion.MotionSegment;
import motion.MotionState;

public class PathGenerator {

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
	public static MotionProfile generateSingleProfile(List<Waypoint> waypoints, int pointDuration,
			double maxAcceleration, double maxVelocity) throws InvalidWaypointsException {

		if (waypoints.size() < 2)
			throw new InvalidWaypointsException("Error: waypoints list needs at least 2 waypoints");

		MotionProfileConstraints constraints = new MotionProfileConstraints(maxVelocity, maxAcceleration);
		List<MotionProfile> profiles = new ArrayList<MotionProfile>();

		MotionState previousState = new MotionState(0, 0, 0, 0);

		for (int i = 0; i < waypoints.size() - 1; i++) {
			Waypoint currentWaypoint = waypoints.get(i);
			Waypoint nextWaypoint = waypoints.get(i + 1);
			MotionProfile currentProfile;
			if (currentWaypoint.heading == nextWaypoint.heading) {
				double distance = currentWaypoint.getDistance(nextWaypoint);
				MotionProfileGoal goalState = new MotionProfileGoal(distance, currentWaypoint.absVelocity,
						CompletionBehavior.OVERSHOOT);
				currentProfile = GenerateMotionProfile.generateStraightMotionProfile(constraints, goalState,
						previousState);
			} else {
				double m1 = -1 / Math.tan(currentWaypoint.heading);
				double m2 = -1 / Math.tan(nextWaypoint.heading);
				double y1 = nextWaypoint.y - currentWaypoint.y;
				double x1 = nextWaypoint.x - currentWaypoint.x;
				double x = (y1 - m2 * x1) / (m1 - m2);
				double y = m1 * x;
				x = x + currentWaypoint.x;
				y = y + currentWaypoint.y;
				CartesianPoint circleCenter = new CartesianPoint(x, y);
				double radius = circleCenter.getDistance(currentWaypoint);
				double chord = currentWaypoint.getDistance(nextWaypoint);
				double angle = Math.asin((chord / 2) / radius);
				double circumference = radius * 2 * Math.PI;
				double distance = circumference * (angle * 2) / (Math.PI * 2) * circumference;
				MotionProfileGoal goalState = new MotionProfileGoal(distance, currentWaypoint.absVelocity,
						CompletionBehavior.OVERSHOOT);
				currentProfile = GenerateMotionProfile.generateStraightMotionProfile(constraints, goalState,
						previousState);
			}
			previousState = currentProfile.endState();
			profiles.add(currentProfile);
		}

		List<MotionSegment> segments = new ArrayList<MotionSegment>();
		for (MotionProfile p : profiles) {
			for (MotionSegment s : p.segments()) {
				segments.add(s);
			}
		}

		return new MotionProfile(segments);
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
	 * @param robotWidth
	 *            width of drivebase in units
	 * 
	 * @return the generated MotionProfile array (first spot is left, second spot is
	 *         right)
	 */
	public static MotionProfile[] generateDoubleProfile(List<Waypoint> waypoints, int pointDuration,
			double maxAcceleration, double maxVelocity, double robotWidth) throws InvalidWaypointsException {

		if (waypoints.size() < 2)
			throw new InvalidWaypointsException("Error: waypoints list needs at least 2 waypoints");

		MotionProfileConstraints constraints = new MotionProfileConstraints(maxVelocity, maxAcceleration);
		List<MotionProfile> leftProfiles = new ArrayList<MotionProfile>();
		List<MotionProfile> rightProfiles = new ArrayList<MotionProfile>();

		MotionState previousState = new MotionState(0, 0, 0, 0);

		for (int i = 0; i < waypoints.size() - 1; i++) {
			Waypoint currentWaypoint = waypoints.get(i);
			Waypoint nextWaypoint = waypoints.get(i + 1);
			if (currentWaypoint.heading == nextWaypoint.heading) {
				double distance = currentWaypoint.getDistance(nextWaypoint);
				MotionProfileGoal goalState = new MotionProfileGoal(distance, currentWaypoint.absVelocity,
						CompletionBehavior.OVERSHOOT);
				MotionProfile currentProfile = GenerateMotionProfile.generateStraightMotionProfile(constraints,
						goalState, previousState);
				leftProfiles.add(currentProfile);
				rightProfiles.add(currentProfile);
			} else {
				CartesianPoint circleCenter = calculateCircleCenter(currentWaypoint, nextWaypoint);
				double radius = circleCenter.getDistance(currentWaypoint);
				double chord = currentWaypoint.getDistance(nextWaypoint);
				double angle = Math.asin((chord / 2) / radius);
				double circumference = radius * 2 * Math.PI;
				double distance = circumference * (angle * 2) / (Math.PI * 2);
				MotionProfileGoal goalState = new MotionProfileGoal(distance, currentWaypoint.absVelocity,
						CompletionBehavior.OVERSHOOT);
				MotionProfile currentProfile = GenerateMotionProfile.generateInnerArcMotionProfile(constraints,
						goalState, previousState, radius);
			}
			previousState = currentProfile.endState();
			profiles.add(currentProfile);
		}

		List<MotionSegment> segments = new ArrayList<MotionSegment>();
		for (MotionProfile p : profiles) {
			for (MotionSegment s : p.segments()) {
				segments.add(s);
			}
		}

		return null;
	}

	public static CartesianPoint calculateCircleCenter(Waypoint current, Waypoint next) {
		double m1 = -1 / Math.tan(current.heading);
		double m2 = -1 / Math.tan(next.heading);
		double y1 = next.y - current.y;
		double x1 = next.x - current.x;
		double x = (y1 - m2 * x1) / (m1 - m2);
		double y = m1 * x;
		x = x + current.x;
		y = y + next.y;
		return new CartesianPoint(x, y);
	}

	public static double calculateArcLength(Waypoint current, Waypoint next, CartesianPoint center) {
		double radius = center.getDistance(current);
		double chord = current.getDistance(next);
		double angle = Math.asin((chord / 2) / radius);
		double circumference = radius * 2 * Math.PI;
		return circumference * (angle * 2) / (Math.PI * 2);
	}

	private static double calculateNewRadius(double percent, double constantRadius) {
		if (percent < 0.5) {
			return 1 / (percent * 2) + (constantRadius - 1);
		} else {
			return 1 / ((percent - 1) * -2) + (constantRadius - 1);
		}
	}

}