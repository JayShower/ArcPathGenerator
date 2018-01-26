package pathing;

public abstract class PathSegment {

	final double startVelocity;
	final double endVelocity;
	final double maxAcceleration;

	PathSegment(double startVelocity, double endVelocity, double maxAcceleration) {
		this.startVelocity = startVelocity;
		this.endVelocity = endVelocity;
		this.maxAcceleration = maxAcceleration;
	}

	public abstract PathSegment getLeftSegment(double robotWidth);

	public abstract PathSegment getRightSegment(double robotWidth);

	/**
	 * @return end position in linear units of this segment
	 */
	public abstract double endPosition();

	/**
	 * @return time in seconds it takes to complete this segment
	 */
	public abstract double timeToComplete();

	/**
	 * @param t
	 *            the time in seconds after the start of this segment
	 * @return the velocity in units/second at that time
	 */
	public abstract double velocityAtTime(double t);
	
	/**
	 * @param t the time in seconds after the start of this segment
	 * @return the velocity in units/second at that time
	 */
	public abstract double positionAtTime(double t);
	
	}

}