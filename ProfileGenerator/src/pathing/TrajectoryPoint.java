package pathing;

public class TrajectoryPoint {

	public final double x;
	public final double y;
	public final double position;
	public final double velocity;
	public final double acceleration;
	public final double heading = 0;
	public final double duration;

	public TrajectoryPoint(double position, double velocity, double acceleration, double duration) {
		this(0, 0, position, velocity, acceleration, duration);
	}

	public TrajectoryPoint(double x, double y, double position, double velocity, double acceleration, double duration) {
		this.x = x;
		this.y = y;
		this.position = position;
		this.velocity = velocity;
		this.duration = duration;
		this.acceleration = acceleration;
	}

}