package motion;

public class MotionUtil {
	/**
	 * A constant for consistent floating-point equality checking within this
	 * library.
	 */
	public static double kEpsilon = 1e-6;

	public static boolean epsilonEquals(double a, double b, double epsilon) {
		return (a - epsilon <= b) && (a + epsilon >= b);
	}

	/**
	 * Get a new Setpoint (and generate a new MotionProfile if necessary).
	 * 
	 * @param constraints
	 *            The constraints to use.
	 * @param goal
	 *            The goal to use.
	 * @param prev_state
	 *            The previous setpoint (or measured state of the system to do a
	 *            reset).
	 * @param t
	 *            The time to generate a setpoint for.
	 * @return The new Setpoint at time t.
	 */
	public Setpoint getSetpoint(MotionProfile profile, MotionProfileConstraints constraints, MotionProfileGoal goal,
			MotionState prev_state,
			double t) {

		// Sample the profile at time t.
		Setpoint rv = null;
		if (!profile.isEmpty() && profile.isValid()) {
			MotionState setpoint;
			if (t > profile.endTime()) {
				setpoint = profile.endState();
			} else if (t < profile.startTime()) {
				setpoint = profile.startState();
			} else {
				setpoint = profile.stateByTime(t).get();
			}
			// Shorten the profile and return the new setpoint.
			profile.trimBeforeTime(t);
			rv = new Setpoint(setpoint, profile.isEmpty() || goal.atGoalState(setpoint));
		}

		// Invalid or empty profile - just output the same state again.
		if (rv == null) {
			rv = new Setpoint(prev_state, true);
		}

		if (rv.final_setpoint) {
			// Ensure the final setpoint matches the goal exactly.
			rv.motion_state = new MotionState(rv.motion_state.t(), goal.pos(),
					Math.signum(rv.motion_state.vel()) * Math.max(goal.max_abs_vel(), Math.abs(rv.motion_state.vel())),
					0.0);
		}

		return rv;
	}
}