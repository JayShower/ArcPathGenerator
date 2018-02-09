package pathing;

import java.util.ArrayList;
import java.util.Arrays;

import math.BezierCurve;
import math.Curve;
import math.LineSegment;
import math.Util;
import math.Vector;
import motion.GenerateMotionProfile;
import motion.MotionProfile;
import motion.MotionProfileConstraints;
import motion.MotionProfileGoal;
import motion.MotionProfileGoal.CompletionBehavior;
import motion.MotionSegment;
import motion.MotionState;

/**
 * Class for making continuous-curvature paths.
 * 
 * Most basic shape combinations are continuous-curvature, I haven't tested it
 * all. There are some waypoint combinations where the paths that get made would
 * be very weird, so check all paths by graphing them
 * 
 * @author Jay
 *
 */
public final class Path {

	public final boolean driveForwards;
	private final ArrayList<PathSegment> segments = new ArrayList<>();
	private Waypoint prev;
	private MotionProfile profile;

	/**
	 * 
	 * @param first
	 *            first Waypoint
	 * @param driveForwards
	 *            if this path has the robot drive forwards or backwards
	 */
	public Path(Waypoint first, boolean driveForwards) {
		this.driveForwards = driveForwards;
		this.prev = first;
	}

	public void addWaypoints(Waypoint... waypoints) {
		for (Waypoint w : waypoints) {
			addWaypoint(w);
		}
	}

	public void addWaypoint(Waypoint w) {
		addWaypoint(w, 0.5);
	}

	public void addWaypoint(Waypoint w, double midControlPercent) {
		// System.out.println("ADDING WAYPOINT: " + w.toString());
		double theta1 = prev.heading.getAbsoluteAngle();
		double theta2 = w.heading.getAbsoluteAngle();

		Vector direction = w.position.subtract(prev.position);
		double alpha = direction.getAbsoluteAngle() - theta1;
		double beta = theta2 - theta1;

		// System.out.println("T1 " + theta1);
		// System.out.println("T2 " + theta2);
		// System.out.println("A " + alpha + ", " + Math.PI / 4);
		// System.out.println("B " + beta);
		Curve curve = null;
		if (Util.epsilonEquals(alpha, 0) && Util.epsilonEquals(beta, 0)) {
			// waypoints are colinear
			// System.out.println("Running thing 0");
			curve = new LineSegment(prev.position, w.position);
		} else if ((alpha >= 0 && beta < alpha) || (alpha <= 0 && beta > alpha)) {
			// not the same as checking if abs(beta) < abs(alpha)
			// System.out.println("Running thing 1");
			curve = addCase1(w, midControlPercent);
		} else if (Math.abs(beta) > Math.abs(alpha) && Math.abs(beta) <= 2 * Math.PI / 3) {
			// System.out.println("Running thing 2");
			curve = addCase2(w, midControlPercent);
		} else if (Math.abs(beta) > Math.abs(alpha) && Math.abs(beta) > 2 * Math.PI / 3) {
			// System.out.println("Running thing 3");
			curve = addCase3(w, midControlPercent);
		} else {
			System.err.println("INVALID WAYPOINT/BAD PATH");
		}
		if (curve != null) {
			segments.add(new PathSegment(prev, w, curve));
			prev = w;
		}
	}

	private BezierCurve addCase1(Waypoint w, double n) {
		// could have middle point be along direction at n, or be parallel at n
		// right now doing middle point along direction at n
		Vector v1 = prev.position;
		Vector v7 = w.position;
		LineSegment l17 = new LineSegment(v1, v7);
		Vector v4 = l17.getPointAtPercent(n);
		Vector h3 = prev.heading.rotate(-Math.PI / 2);
		Vector v3 = new LineSegment(v4, v4.add(h3)).getIntersection(new LineSegment(prev));
		Vector h5 = w.heading.rotate(Math.PI / 2);
		Vector v5 = new LineSegment(v4, v4.add(h5)).getIntersection(new LineSegment(w));
		Vector v2 = new LineSegment(v1, v3).getPointAtPercent(n);
		Vector v6 = new LineSegment(v5, v7).getPointAtPercent(n);
		return new BezierCurve(v1, v2, v3, v4, v5, v6, v7);
	}

	private BezierCurve addCase2(Waypoint w, double n) {
		LineSegment lineA = new LineSegment(prev);
		LineSegment lineB = new LineSegment(w);
		// System.out.println("Line A: " + lineA.toString());
		// System.out.println("Line B: " + lineB.toString());
		Vector intersection = lineA.getIntersection(lineB);
		// System.out.println(intersection);
		Vector v1 = prev.position;
		Vector v5 = w.position;
		Vector v3 = intersection;
		LineSegment ac = new LineSegment(v1, v3);
		LineSegment ce = new LineSegment(v3, v5);
		Vector v2 = ac.getPointAtPercent(n);
		Vector v4 = ce.getPointAtPercent(n);
		return new BezierCurve(v1, v2, v3, v4, v5);
	}

	private BezierCurve addCase3(Waypoint w, double n) {
		Vector v1 = prev.position;
		Vector v7 = w.position;
		LineSegment l17 = new LineSegment(v1, v7);
		Vector m4 = l17.getPointAtPercent(n);
		Vector a4 = l17.getDirection().rotate(Math.PI / 2);
		Vector v4 = m4.add(a4.scale(0.5));
		LineSegment l4 = new LineSegment(v4, v4.add(l17.getDirection()));
		Vector v3 = l4.getIntersection(new LineSegment(prev));
		Vector v5 = l4.getIntersection(new LineSegment(w));
		Vector v2 = new LineSegment(v1, v3).getPointAtPercent(n);
		Vector v6 = new LineSegment(v5, v7).getPointAtPercent(n);
		return new BezierCurve(v1, v2, v3, v4, v5, v6, v7);
	}

	public void addPathSegment(PathSegment seg) {
		segments.add(seg);
		prev = seg.end;
	}

	public void addPathSegments(PathSegment... seg) {
		segments.addAll(Arrays.asList(seg));
		prev = seg[seg.length - 1].end;
	}

	public void generateProfile(double maxVelocity, double maxAcceleration) {
		for (PathSegment seg : segments) {
			if (seg.curve instanceof BezierCurve) {
				((BezierCurve) seg.curve).startMakingTable();
			}
		}
		MotionProfileConstraints constraints = new MotionProfileConstraints(maxVelocity, maxAcceleration);

		MotionState previousState = new MotionState(0, 0, 0, maxAcceleration);

		// System.out.println("Total length: " +
		// segments.get(0).curve.getTotalArcLength());
		double goalPos = segments.get(0).curve.getTotalArcLength() + previousState.pos();

		MotionProfileGoal goalState = new MotionProfileGoal(goalPos, Math.abs(segments.get(0).end.vel),
				CompletionBehavior.OVERSHOOT);
		MotionProfile currentProfile = GenerateMotionProfile.generateStraightMotionProfile(constraints, goalState,
				previousState);
		previousState = currentProfile.endState();

		for (int i = 1; i < segments.size(); i++) {
			goalPos = segments.get(i).curve.getTotalArcLength() + previousState.pos();
			goalState = new MotionProfileGoal(goalPos, segments.get(i).end.vel, CompletionBehavior.OVERSHOOT);
			currentProfile.appendProfile(
					GenerateMotionProfile.generateStraightMotionProfile(constraints, goalState, previousState));
			previousState = currentProfile.endState();
		}
		profile = currentProfile;
		if (!driveForwards) {
			for (MotionSegment s : profile.segments()) {
				s.setStart(s.start().flipped());
				s.setEnd(s.end().flipped());
			}
		}
	}

	public PathSegment[] getSegments() {
		return segments.toArray(new PathSegment[segments.size()]);
	}

	public MotionProfile getProfile() {
		return profile;
	}

	public TrajectoryHolder getTrajectoryPoints(double robotWidth, double pointDurationSec) {
		int cs = 0;
		PathSegment currentSegment = segments.get(0);
		double segmentLengthSum = 0;

		double duration = profile.duration();
		int pointCount = (int) (duration / pointDurationSec);
		double increment = duration / (pointCount - 1);

		TrajectoryPoint[] left = new TrajectoryPoint[pointCount];
		TrajectoryPoint[] right = new TrajectoryPoint[pointCount];

		MotionState previousState = profile.stateByTime(0).get();
		Vector coord = currentSegment.curve.getPointAtArcLength(0);

		left[0] = new TrajectoryPoint(coord.x, coord.y, previousState.pos(), previousState.vel(), previousState.acc(),
				pointDurationSec);
		right[0] = new TrajectoryPoint(coord.x, coord.y, previousState.pos(), previousState.vel(), previousState.acc(),
				pointDurationSec);

		for (int i = 1; i < pointCount; i++) {
			MotionState state = profile.stateByTimeClamped(i * increment);
			double currentLength = currentSegment.curve.getTotalArcLength();
			if (state.pos() > currentLength + segmentLengthSum) {
				if (cs + 1 >= segments.size()) {

				} else {
					cs++;
					segmentLengthSum += currentLength;
					currentSegment = segments.get(cs);
				}
			}
			/*
			 * Because K positive is curving to the left, and K negative is to the right,
			 * the side new radii =
			 * 
			 * Math.abs(1/K-a)
			 * 
			 * for left side, and
			 * 
			 * Math.abs(1/K+a)
			 * 
			 * for right side
			 */

			double curvature = currentSegment.curve.getCurvatureAtArcLength(state.pos() - segmentLengthSum);
			double dArc = state.pos() - previousState.pos();
			coord = currentSegment.curve.getPointAtArcLength(state.pos() - segmentLengthSum);
			// System.out.println(coord);
			if (Math.abs(curvature) < 1.0E-20) {
				left[i] = new TrajectoryPoint(coord.x, coord.y, left[i - 1].position + dArc, state.vel(), state.acc(),
						pointDurationSec);
				right[i] = new TrajectoryPoint(coord.x, coord.y, right[i - 1].position + dArc, state.vel(), state.acc(),
						pointDurationSec);
			} else {
				double r = 1 / curvature;
				double lR = Math.abs(r - robotWidth / 2);
				double rR = Math.abs(r + robotWidth / 2);
				r = Math.max(lR, rR);
				double lK = lR / r;
				double rK = rR / r;
				double leftV = state.vel() * lK;
				double rightV = state.vel() * rK;
				double leftA = (leftV - left[i - 1].velocity) / pointDurationSec;
				double rightA = (rightV - right[i - 1].velocity) / pointDurationSec;
				left[i] = new TrajectoryPoint(coord.x, coord.y, left[i - 1].position + dArc * lK, leftV, leftA,
						pointDurationSec);
				right[i] = new TrajectoryPoint(coord.x, coord.y, right[i - 1].position + dArc * rK, rightV, rightA,
						pointDurationSec);
			}
			previousState = state;
		}
		return new TrajectoryHolder(left, right);
	}

	public static boolean isPossible(TrajectoryHolder points, double lKv, double lKa, double lKs, double rKv,
			double rKa, double rKs) {
		double leftMaxVolt = 0;
		double rightMaxVolt = 0;
		for (int i = 0; i < points.left.length; i++) {
			leftMaxVolt = Math.max(leftMaxVolt,
					Math.abs(points.left[i].velocity * lKv + points.left[i].acceleration * lKa + lKs));
			rightMaxVolt = Math.max(rightMaxVolt,
					Math.abs(points.right[i].velocity * rKv + points.right[i].acceleration * rKa + rKs));
		}
		return leftMaxVolt <= 12 && rightMaxVolt <= 12;
	}

	public static class TrajectoryHolder {
		public final TrajectoryPoint[] left;
		public final TrajectoryPoint[] right;

		public TrajectoryHolder(TrajectoryPoint[] left, TrajectoryPoint[] right) {
			super();
			this.left = left;
			this.right = right;
		}
	}

	/**
	 * 
	 * File format: <br>
	 * t, dt, x, y, curvature, leftPos, leftVel, leftAcc, rightPos, rightVel,
	 * rightAcc <br>
	 * 0, 0.01, 0, 0, 0, ... (more numbers in each column) <br>
	 * 
	 * @param path
	 *            written file location
	 */
	public void writeToFile(String path) {

	}
}