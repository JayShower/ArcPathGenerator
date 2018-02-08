package pathing;

import java.util.ArrayList;

import math.BezierCurve;
import math.Line;
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

	public void addWaypoint(Waypoint w) {
		addWaypoint(w, 0.5);
	}

	public void addWaypoint(Waypoint w, double midControlPercent) {
		double theta1 = prev.heading.getAbsoluteAngle();
		double theta2 = w.heading.getAbsoluteAngle();
		System.out.println("T1 " + theta1);
		System.out.println("T2 " + theta2);
		Vector direction = w.position.subtract(prev.position);
		double alpha = direction.getAbsoluteAngle() - theta1;
		System.out.println("A " + alpha + ", " + Math.PI / 4);
		double beta = theta2 - theta1;
		System.out.println("B " + beta);
		if (Util.epsilonEquals(alpha, 0) && Util.epsilonEquals(beta, 0)) {
			// waypoints are colinear
			System.out.println("Running thing 0");
			Line curve = new Line(prev.position, w.position);
			segments.add(new PathSegment(prev, w, curve));
			prev = w;
		} else if ((alpha >= 0 && beta < alpha) || (alpha <= 0 && beta > alpha)) {
			// not the same as checking if abs(beta) < abs(alpha)
			System.out.println("Running thing 1");
			addCase1(w, midControlPercent);
		} else if (Math.abs(beta) > Math.abs(alpha) && Math.abs(beta) <= Math.PI / 2.0) {
			System.out.println("Running thing 2");
			addCase2(w, midControlPercent);
		} else if (Math.abs(beta) > Math.abs(alpha) && Math.abs(beta) > Math.PI / 2.0) {
			System.out.println("Running thing 3");
			addCase3(w, midControlPercent);
		} else {
			System.err.println("INVALID WAYPOINT");
		}
	}

	private void addCase2(Waypoint w, double n) {
		Line lineA = new Line(prev);
		Line lineB = new Line(w);
		Vector intersection = lineA.getIntersection(lineB);
		Vector v1 = prev.position;
		Vector v5 = w.position;
		Vector v3 = intersection;
		Line ac = new Line(v1, v3);
		Line ce = new Line(v3, v5);
		Vector v2 = ac.getPointAtPercent(n);
		Vector v4 = ce.getPointAtPercent(n);
		BezierCurve curve = new BezierCurve(v1, v2, v3, v4, v5);
		segments.add(new PathSegment(prev, w, curve));
		prev = w;
	}

	private void addCase1(Waypoint w, double n) {
		// could have middle point be along direction at n, or be parallel at n
		// right now doing middle point along direction at n
		Vector v1 = prev.position;
		Vector v7 = w.position;
		Line l17 = new Line(v1, v7);
		Vector v4 = l17.getPointAtPercent(n);
		Vector h3 = prev.heading.rotate(-Math.PI / 2);
		Vector v3 = new Line(v4, v4.add(h3)).getIntersection(new Line(prev));
		Vector h5 = w.heading.rotate(Math.PI / 2);
		Vector v5 = new Line(v4, v4.add(h5)).getIntersection(new Line(w));
		Vector v2 = new Line(v1, v3).getPointAtPercent(n);
		Vector v6 = new Line(v5, v7).getPointAtPercent(n);
		BezierCurve curve = new BezierCurve(v1, v2, v3, v4, v5, v6, v7);
		segments.add(new PathSegment(prev, w, curve));
		prev = w;
	}

	private void addCase3(Waypoint w, double n) {
		Vector v1 = prev.position;
		Vector v7 = w.position;
		Line l17 = new Line(v1, v7);
		Vector m4 = l17.getPointAtPercent(n);
		Vector a4 = l17.getDirection().rotate(Math.PI / 2);
		Vector v4 = m4.add(a4);
		Line l4 = new Line(v4, v4.add(l17.getDirection()));
		Vector v3 = l4.getIntersection(new Line(prev));
		Vector v5 = l4.getIntersection(new Line(w));
		Vector v2 = new Line(v1, v3).getPointAtPercent(n);
		Vector v6 = new Line(v5, v7).getPointAtPercent(n);
		BezierCurve curve = new BezierCurve(v1, v2, v3, v4, v5, v6, v7);
		segments.add(new PathSegment(prev, w, curve));
		prev = w;
	}

	public void addPathSegment(PathSegment seg) {
		segments.add(seg);
	}

	public void generateProfile(double maxAcceleration, double maxVelocity) {
		for (PathSegment seg : segments) {
			if (seg.curve instanceof BezierCurve) {
				((BezierCurve) seg.curve).startMakingTable();
			}
		}
		MotionProfileConstraints constraints = new MotionProfileConstraints(maxVelocity, maxAcceleration);

		MotionState previousState = new MotionState(0, 0, 0, maxAcceleration);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Total length: " + segments.get(0).curve.getTotalArcLength());
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
		double duration = profile.duration();
		int pointCount = (int) (duration / pointDurationSec);
		double increment = duration / (pointCount - 1);

		TrajectoryPoint[] left = new TrajectoryPoint[pointCount];
		TrajectoryPoint[] right = new TrajectoryPoint[pointCount];

		MotionState previousState = profile.stateByTime(0).get();

		left[0] = new TrajectoryPoint(previousState.pos(), previousState.vel(), previousState.acc(), pointDurationSec);
		right[0] = new TrajectoryPoint(previousState.pos(), previousState.vel(), previousState.acc(), pointDurationSec);

		int cs = 0;
		PathSegment currentSegment = segments.get(0);
		double segmentLengthSum = 0;
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
			Vector coord = currentSegment.curve.getPointAtArcLength(state.pos() - segmentLengthSum);
			if (Math.abs(curvature) < 1.0E-20) {
				left[i] = new TrajectoryPoint(coord.x, coord.y, left[i - 1].position + dArc, state.vel(), state.acc(),
						pointDurationSec);
				right[i] = new TrajectoryPoint(coord.x, coord.y, right[i - 1].position + dArc, state.vel(), state.acc(),
						pointDurationSec);
			} else {
				double r = 1 / curvature;
				double lR = Math.abs(r - robotWidth / 2);
				double rR = Math.abs(r + robotWidth / 2);
				r = Math.abs(r);
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