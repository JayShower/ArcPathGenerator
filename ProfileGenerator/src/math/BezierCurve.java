package math;

public class BezierCurve implements Curve {

	private static final int[][] BINOMIAL = { { 1 }, { 1, 1 }, { 1, 2, 1 }, { 1, 3, 3, 1 }, { 1, 4, 6, 4, 1 },
			{ 1, 5, 10, 10, 5, 1 }, { 1, 6, 15, 20, 15, 6, 1 } };

	public final Vector[] controlPoints;
	private final int n;
	public final BezierCurve derivative;

	/**
	 * Makes a new Bezier Curve
	 * 
	 * @param controlPoints
	 *            the control points to use. must be at least 2, and no more than 7.
	 */
	public BezierCurve(Vector... controlPoints) {
		this.controlPoints = controlPoints;
		this.n = controlPoints.length - 1;
		this.derivative = calculateDerivative();
	}

	private BezierCurve calculateDerivative() {
		if (controlPoints.length == 1)
			return null;
		Vector[] newPoints = new Vector[controlPoints.length - 1];
		for (int i = 0; i < newPoints.length; i++) {
			newPoints[i] = controlPoints[i + 1].subtract(controlPoints[i]).scale(newPoints.length);
		}
		return new BezierCurve(newPoints); // warning, recursive
	}

	private Vector bezier(double t) {
		Vector point = Vector.ZERO;
		double mt = 1 - t;
		for (int k = 0; k <= n; k++) {
			double term = BINOMIAL[n][k] * Math.pow(mt, n - k) * Math.pow(t, k);
			point = point.add(controlPoints[k].scale(term));
		}
		return point;
	}

	private double bezierX(double t) {
		double result = 0;
		double mt = 1 - t;
		for (int k = 0; k <= n; k++) {
			double term = BINOMIAL[n][k] * Math.pow(mt, n - k) * Math.pow(t, k);
			result = result + controlPoints[k].x * term;
		}
		return result;
	}

	private double bezierY(double t) {
		double result = 0;
		double mt = 1 - t;
		for (int k = 0; k <= n; k++) {
			double term = BINOMIAL[n][k] * Math.pow(mt, n - k) * Math.pow(t, k);
			result = result + controlPoints[k].y * term;
		}
		return result;
	}

	public Vector deCasteljau(double t) {
		return deCasteljau(controlPoints, t);
	}

	private Vector deCasteljau(Vector[] points, double t) {
		if (points.length == 1) {
			return points[0];
		} else {
			Vector[] newPoints = new Vector[points.length - 1];
			for (int i = 0; i < newPoints.length; i++) {
				double mt = 1 - t;
				double x = points[i].x * mt + points[i + 1].x * t;
				double y = points[i].y * mt + points[i + 1].y * t;
				newPoints[i] = new Vector(x, y);
			}
			return deCasteljau(newPoints, t);
		}
	}

	public double deCasteljauX(double t) {
		double[] x = new double[controlPoints.length];
		for (int i = 0; i < x.length; i++) {
			x[i] = controlPoints[i].x;
		}
		return deCasteljauSingle(x, t);
	}

	public double deCasteljauY(double t) {
		double[] y = new double[controlPoints.length];
		for (int i = 0; i < y.length; i++) {
			y[i] = controlPoints[i].y;
		}
		return deCasteljauSingle(y, t);
	}

	private double deCasteljauSingle(double[] points, double t) {
		if (points.length == 1) {
			return points[0];
		} else {
			double[] newPoints = new double[points.length - 1];
			for (int i = 0; i < newPoints.length; i++) {
				double mt = 1 - t;
				newPoints[i] = points[i] * mt + points[i + 1] * t;
			}
			return deCasteljauSingle(newPoints, t);
		}
	}

	public Vector tangent(double t) {
		return derivative.bezier(t);
	}

	public Vector leftNormal(double t) {
		return tangent(t).rotate(Math.PI / 2);
	}

	public Vector rightNormal(double t) {
		return tangent(t).rotate(-Math.PI / 2);
	}

	public double arcLengthDerivative(double t) {
		return derivative.deCasteljau(t).magnitude;
	}

	public double arcLengthIntegral(double t) {
		return Util.gaussQuadIntegrate(this::arcLengthDerivative, 0, t);
	}

	private LookupTable tToArcLengthTable = null;

	private void createTToArcLengthTable() {
		tToArcLengthTable = new LookupTable(this::arcLengthIntegral, 0, 1);
	}

	public LookupTable tToArcLengthTable() {
		if (tToArcLengthTable == null)
			createTToArcLengthTable();
		return tToArcLengthTable;
	}

	@Override
	public double getTotalArcLength() {
		if (tToArcLengthTable == null)
			createTToArcLengthTable();
		return tToArcLengthTable.getOutput(1);
	}

	public double getArcLength(double t) {
		if (tToArcLengthTable == null)
			createTToArcLengthTable();
		return tToArcLengthTable.getOutput(t);
	}

	public double tFromArcLength(double arcLength) {
		if (tToArcLengthTable == null)
			createTToArcLengthTable();
		return tToArcLengthTable.getInput(arcLength);
	}

	public double curvature(double t) {
		Vector d1 = derivative.deCasteljau(t);
		Vector d2 = derivative.derivative.deCasteljau(t);
		return Util.curvature2d(d1.x, d2.x, d1.y, d2.y);
	}

	@Override
	public double getCurvatureAtArcLength(double arcLength) {
		return curvature(tFromArcLength(arcLength));
	}

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < controlPoints.length; i++) {
			result += controlPoints[i].toString() + " | ";
		}
		return result;
	}

	@Override
	public Vector getPointAtArcLength(double arcLength) {
		return deCasteljau(tFromArcLength(arcLength));
	}

	// public BezierCurve[] split(double... t) {
	// BezierCurve[] curves = new BezierCurve[t.length + 1];
	// Vector[] after = new Vector[controlPoints.length];
	// Vector[] before = new Vector[controlPoints.length];
	// deCasteljauSplit(controlPoints, before, after, t[0], 0,
	// controlPoints.length);
	// curves[0] = new BezierCurve(before);
	// for (int i = 1; i < t.length; i++) {
	// before = new Vector[controlPoints.length];
	// // 0.25/1, 0.5/0.75, 0.75/0.5
	// // 0.25 0.333 0.5
	// // (t-prevT) * newTotal = newT
	// deCasteljauSplit(controlPoints, before, after, (t[i] - t[i - 1]) * (1 - t[i -
	// 1]), 0, controlPoints.length);
	// curves[i] = new BezierCurve(before);
	// }
	// curves[curves.length - 1] = new BezierCurve(after);
	// return curves;
	// }

	// private void deCasteljauSplit(Vector[] points, Vector[] before, Vector[]
	// after, double t, int level, int length) {
	// if (points.length == 1) {
	// before[level] = points[0];
	// after[length - level - 1] = points[0]; // length - level - 1 should be 0
	// } else {
	// Vector[] newPoints = new Vector[points.length - 1];
	// before[level] = points[0];
	// after[length - level - 1] = points[points.length - 1];
	// for (int i = 0; i < newPoints.length; i++) {
	// double mt = 1 - t;
	// newPoints[i] = points[i].scale(mt).add(points[i + 1].scale(t));
	// }
	// deCasteljauSplit(newPoints, before, after, t, level + 1, length);
	// }
	// }

	// public HashSet<Double>[] criticalPoints(int numberSteps) {
	// int possibleRoots = controlPoints.length - 1;
	// HashSet[] criticalPoints = { new HashSet<Double>(possibleRoots), new
	// HashSet<Double>(possibleRoots) };
	// double[] newton = new double[2];
	// for (int i = 0; i < numberSteps; i++) {
	// newton[0] = (i + 1.0) / numberSteps;
	// newton[1] = newton[0];
	// newtonRaphson(newton);
	// criticalPoints[0].add(newton[0]);
	// criticalPoints[1].add(newton[1]);
	// }
	// return criticalPoints;
	// }

	// Check that this actually converges to zero decently quickly. May need to use
	// epsilon.
	// private void newtonRaphson(double[] t) {
	// double dx;
	// double dy;
	// double x;
	// double y;
	// while ((dx = derivative.bezierX(t[0])) != 0 && (dy =
	// derivative.bezierY(t[1])) != 0) {
	// x = bezierX(t[0]);
	// y = bezierY(t[1]);
	// t[0] = t[0] - x / dx;
	// t[1] = t[1] - y / dy;
	// }
	// t[0] = Util.limit(t[0], 0, 1);
	// t[1] = Util.limit(t[1], 0, 1);
	// }

	/*
	 * Removes all non-maxima (2nd derivative >= 0 for non-maxima) from copy of
	 * original array
	 */
	// public HashSet<Double>[] maxima(HashSet<Double>[] criticals) {
	// HashSet[] maxima = { new HashSet<Double>(criticals[0]), new
	// HashSet<Double>(criticals[1]) };
	// double ddx;
	// double ddy;
	// Iterator criticalXs = maxima[0].iterator();
	// while (criticalXs.hasNext()) {
	// Double next = (Double) criticalXs.next();
	// ddx = derivative.derivative.bezierX(next);
	// if (ddx >= 0) {
	// maxima[0].remove(next);
	// }
	// }
	// Iterator criticalYs = maxima[1].iterator();
	// while (criticalYs.hasNext()) {
	// Double next = (Double) criticalYs.next();
	// ddy = derivative.derivative.bezierY(next);
	// if (ddy >= 0) {
	// maxima[1].remove(next);
	// }
	// }
	// return maxima;
	// }

	/*
	 * Removes all non-minima (2nd derivative <= 0 for non-minima) from copy of
	 * original array
	 */
	// public HashSet<Double>[] minima(HashSet<Double>[] criticals) {
	// HashSet[] maxima = { new HashSet<Double>(criticals[0]), new
	// HashSet<Double>(criticals[1]) };
	// double ddx;
	// double ddy;
	// Iterator criticalXs = maxima[0].iterator();
	// while (criticalXs.hasNext()) {
	// Double next = (Double) criticalXs.next();
	// ddx = derivative.derivative.bezierX(next);
	// if (ddx >= 0) {
	// maxima[0].remove(next);
	// }
	// }
	// Iterator criticalYs = maxima[1].iterator();
	// while (criticalYs.hasNext()) {
	// Double next = (Double) criticalYs.next();
	// ddy = derivative.derivative.bezierY(next);
	// if (ddy >= 0) {
	// maxima[1].remove(next);
	// }
	// }
	// return maxima;
	// }

}