package pathing;

import java.util.Optional;

import motion.MotionProfile;
import motion.MotionState;

public class Path {

	public final MotionProfile profile;
	public final PathSegment[] pathSegments;

	public Path(MotionProfile profile, PathSegment[] pathSegments) {
		this.profile = profile;
		this.pathSegments = pathSegments;
	}

	public TrajectoryHolder getTrajectoryPoints(double robotWidth, double pointDurationSec) {
		int cs = 0;
		PathSegment currentSegment = pathSegments[0];

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
				if (cs + 1 >= pathSegments.length)
					break;
				cs++;
				currentSegment = pathSegments[cs];
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
				double leftA = state.acc() * lK;
				double rightA = state.acc() * rK;
				if (state.acc() == 0) {
					leftA = (leftV - left[i - 1].velocity) / pointDurationSec;
					rightA = (rightV - right[i - 1].velocity) / pointDurationSec;
				}
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

}
