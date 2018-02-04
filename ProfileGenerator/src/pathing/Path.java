package pathing;

import java.util.ArrayList;
import java.util.Optional;

import math.BezierCurve;
import math.Line;
import math.Vector;
import motion.GenerateMotionProfile;
import motion.MotionProfile;
import motion.MotionProfileConstraints;
import motion.MotionProfileGoal;
import motion.MotionProfileGoal.CompletionBehavior;
import motion.MotionState;

/**
 * Class for making continuous-curvature paths.
 * 
 * Most basic shape combinations are continuous-curvature, I haven't tested it
 * all
 * 
 * @author Jay
 *
 */
public final class Path {

	public final boolean driveForwards;
	private final ArrayList<PathSegment> segments = new ArrayList<>();
	private WayPoint prev;
	private MotionProfile profile;

	public Path(boolean driveForwards) {
		this(new WayPoint(Vector.ZERO, WayPoint.STRAIGHT, 0), driveForwards);
	}

	// you might want to use this constructor if your robot starts at an angle to
	// the DriverStation wall. (E.g., In 2017, we sometimes started our robot
	// pointed towards the hoppers).
	// If you don't use this, your path will be relative to robot starting state.
	/**
	 * 
	 * @param first
	 *            first WayPoint
	 * @param driveForwards
	 *            if this path has the robot drive forwards or backwards
	 */
	public Path(WayPoint first, boolean driveForwards) {
		this.driveForwards = driveForwards;
		this.prev = first;
	}

	public void addWaypoint(WayPoint w) {
		addWaypoint(w, 0.5);
	}

	public void addWaypoint(WayPoint w, double midControlPercent) {
		Line l1 = new Line(prev);
		Line l2 = new Line(w);
		if (l1.isParallel(l2))
			addIntersectingAboveOrParallel(w, midControlPercent);
		else if (l1.isAntiparallel(l2))
			addIntersectingBehindOrAntiparallel(w, midControlPercent);
		else
			addIntersectingBelow(w, midControlPercent);
	}

	private void addIntersectingBelow(WayPoint w, double n) {
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

	private void addIntersectingAboveOrParallel(WayPoint w, double n) {
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

	private void addIntersectingBehindOrAntiparallel(WayPoint w, double n) {
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

	/*
	 * Less likely to be continuous curvature if you use this
	 */
	public void addPathSegment(PathSegment seg) {
		segments.add(seg);
	}

	public void generateProfile(double maxAcceleration, double maxVelocity) {
		MotionProfileConstraints constraints = new MotionProfileConstraints(maxVelocity, maxAcceleration);

		// System.out.println(pathSegments[0].curve);
		double goalPos = driveForwards ? segments.get(0).curve.getTotalArcLength()
				: -segments.get(0).curve.getTotalArcLength();
		MotionState previousState = new MotionState(0, 0, 0, maxAcceleration);
		// System.out.println("Goal pos: " + goalPos);
		MotionProfileGoal goalState = new MotionProfileGoal(goalPos,
				Math.abs(segments.get(0).end.vel), CompletionBehavior.OVERSHOOT);
		MotionProfile currentProfile = GenerateMotionProfile.generateStraightMotionProfile(constraints, goalState,
				previousState);
		previousState = currentProfile.endState();

		for (int i = 1; i < segments.size(); i++) {
			goalPos = driveForwards ? segments.get(i).curve.getTotalArcLength()
					: -segments.get(i).curve.getTotalArcLength();
			goalState = new MotionProfileGoal(goalPos, segments.get(i).end.vel,
					CompletionBehavior.OVERSHOOT);
			currentProfile.appendProfile(
					GenerateMotionProfile.generateStraightMotionProfile(constraints, goalState, previousState));
			previousState = currentProfile.endState();
		}
		this.profile = currentProfile;
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

		double duration = profile.duration();
		int pointCount = (int) (duration / pointDurationSec);
		double increment = duration / (pointCount - 1);

		TrajectoryPoint[] left = new TrajectoryPoint[pointCount];
		TrajectoryPoint[] right = new TrajectoryPoint[pointCount];

		MotionState previousState = profile.stateByTime(0).get();

		left[0] = new TrajectoryPoint(previousState.pos(), previousState.vel(), previousState.acc(), pointDurationSec);
		right[0] = new TrajectoryPoint(previousState.pos(), previousState.vel(), previousState.acc(), pointDurationSec);

		for (int i = 1; i < pointCount; i++) {
			Optional<MotionState> om = profile.stateByTime(i * increment);
			if (!om.isPresent()) {
				break; // done reading profile
			}
			MotionState state = om.get();
			if (state.pos() > currentSegment.curve.getTotalArcLength()) {
				if (cs + 1 >= segments.size())
					break;
				cs++;
				currentSegment = segments.get(cs);
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

			double curvature = currentSegment.curve.getCurvatureAtArcLength(state.pos());
			double dArc = state.pos() - previousState.pos();

			if (Math.abs(curvature) < 1.0E-20) {
				left[i] = new TrajectoryPoint(left[i - 1].position + dArc, state.vel(), state.acc(), pointDurationSec);
				right[i] = new TrajectoryPoint(right[i - 1].position + dArc, state.vel(), state.acc(),
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
				left[i] = new TrajectoryPoint(left[i - 1].position + dArc * lK, leftV, leftA, pointDurationSec);
				right[i] = new TrajectoryPoint(right[i - 1].position + dArc * rK, rightV, rightA, pointDurationSec);
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