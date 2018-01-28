package motion;

public class Setpoint {
	public MotionState motion_state;
	public boolean final_setpoint;
	public final double heading = 0;

	public Setpoint(MotionState motion_state, boolean final_setpoint) {
		this.motion_state = motion_state;
		this.final_setpoint = final_setpoint;
	}
}