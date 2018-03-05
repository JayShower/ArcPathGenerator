package math;

public interface Curve {

	public double getTotalArcLength();

	/**
	 * 
	 * @param arcLength
	 *            arc length along curve
	 * @return curvature. Positive curvature indicates that left side is inner side,
	 *         negative curvature indicates that right side is inner side
	 */
	public double getCurvatureAtArcLength(double arcLength);

	public Vector getPointAtArcLength(double arcLength);

	/**
	 * heading is defined as positive angles going "west" of north, and negative
	 * angles going "east" of north, with a heading of straight north equaling zero
	 * 
	 * @param arcLength
	 *            length along this curve to get the heading at
	 * @return the heading in radians
	 */
	public double getHeadingAtArcLength(double arcLength);
}
