package pathing;
public class TrajectoryPoint {

	public final double position;
	public final double velocity;
	public final double heading = 0;
	public final double duration;

	public TrajectoryPoint(double position, double velocity, double duration) {
		super();
		this.position = position;
		this.velocity = velocity;
		this.duration = duration;
	}

}