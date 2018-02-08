package math;

import java.util.Arrays;

public final class BezierCurve implements Curve {

	public final Vector[] controlPoints;
	private BezierCurve derivative = null;
	public final double[] controlPointsX;
	public final double[] controlPointsY;
	private LookupTable tToArcLengthTable = null;

	/**
	 * Makes a new Bezier Curve
	 * 
	 * @param controlPoints
	 *            the control points to use. must be at least 2, and no more than 7.
	 */
	public BezierCurve(Vector... controlPoints) {
		this.controlPoints = controlPoints;
		// this.n = controlPoints.length - 1;
		// Timer t = new Timer();
		// t.printElapsed("Time calculating derivatives: ");
		controlPointsX = new double[controlPoints.length];
		controlPointsY = new double[controlPoints.length];
		for (int i = 0; i < controlPoints.length; i++) {
			controlPointsX[i] = controlPoints[i].x;
			controlPointsY[i] = controlPoints[i].y;
		}
	}

	private BezierCurve calculateDerivative() {
		if (controlPoints.length == 1)
			return null;
		Vector[] newPoints = new Vector[controlPoints.length - 1];
		for (int i = 0; i < newPoints.length; i++) {
			newPoints[i] = controlPoints[i + 1].subtract(controlPoints[i]).scale(newPoints.length);
		}
		return new BezierCurve(newPoints);
	}

	public Vector deCasteljau(double t) {
		// return bezier(t);
		return deCasteljau(Arrays.copyOf(controlPointsX, controlPointsX.length),
				Arrays.copyOf(controlPointsY, controlPointsY.length), controlPoints.length, t);
	}

	private Vector deCasteljau(double[] x, double[] y, int length, double t) {
		for (int k = length; k > 1; k--) {
			for (int i = 0; i < k - 1; i++) {
				double mt = 1 - t;
				x[i] = x[i] * mt + x[i + 1] * t;
				y[i] = y[i] * mt + y[i + 1] * t;
			}
		}
		return new Vector(x[0], y[0]);
	}

	public BezierCurve derivative() {
		if (derivative == null)
			derivative = calculateDerivative();
		return derivative;
	}

	public double arcLengthDerivative(double t) {
		return derivative().deCasteljau(t).magnitude();
	}

	public double arcLengthIntegral(double lower, double upper) {
		return Util.gaussQuadIntegrate(this::arcLengthDerivative, lower, upper);
	}

	public void startMakingTable() {
		if (tToArcLengthTable == null) {
			tToArcLengthTable = new LookupTable(this::arcLengthIntegral, 0, 1);
		}
	}

	public LookupTable getTable() {
		startMakingTable();
		tToArcLengthTable.waitToBeDone();
		return tToArcLengthTable;
	}

	public double getArcLength(double t) {
		return getTable().getOutput(t);
	}

	public double tFromArcLength(double arcLength) {
		return getTable().getInput(arcLength);
	}

	public double curvature(double t) {
		Vector d1 = derivative().deCasteljau(t);
		Vector d2 = derivative().derivative().deCasteljau(t);
		return Util.curvature2d(d1.x, d2.x, d1.y, d2.y);
	}

	@Override
	public double getTotalArcLength() {
		return tToArcLengthTable.getOutput(1);
	}

	@Override
	public double getCurvatureAtArcLength(double arcLength) {
		return curvature(tFromArcLength(arcLength));
	}

	@Override
	public Vector getPointAtArcLength(double arcLength) {
		return deCasteljau(tFromArcLength(arcLength));
	}

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < controlPoints.length; i++) {
			result += controlPoints[i].toString() + " | ";
		}
		return result;
	}

}