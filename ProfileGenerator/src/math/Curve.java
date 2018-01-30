package math;

public abstract class Curve {

	public abstract double getTotalArcLength();

	/**
	 * 
	 * @param arcLength
	 *            arc length along curve
	 * @return curvature. Positive curvature indicates that left side is inner side,
	 *         negative curvature indicates that right side is inner side
	 */
	public abstract double getCurvatureAtArcLength(double arcLength);

}
