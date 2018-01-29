package bezier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import math.Vector;
import plot.FalconLinePlot;

public class Test {

	public static void test() {
		/*
		 * Stuff to test:
		 * 
		 * -speed of bezier vs speed of deCasteljau
		 * 
		 * -accurcay of lookup table
		 * 
		 * 
		 */

		Timer timer = new Timer();

		ArrayList<Vector> controlPoints = new ArrayList<Vector>();
		controlPoints.add(new Vector(-0.38, 1.04));
		controlPoints.add(new Vector(0.97, 4.14));
		// controlPoints.add(new Vector(4.45, 3.37));
		// controlPoints.add(new Vector(3.83, 0.52));
		// controlPoints.add(new Vector(-0.3, -2.1));

		timer.reset();
		BezierCurve curve = new BezierCurve(controlPoints.toArray(new Vector[controlPoints.size()]));
		timer.printElapsed("Time to make bezier: ");

		// test options
		boolean testBezierSpeed = true;
		boolean testdeCasteljauSpeed = true;
		boolean graphCurves = false; // curves are correct
		double testResolution = 500;

		boolean testArcLength = true;

		// tests
		if (testBezierSpeed) {
			timer.reset();
			for (int i = 0; i <= testResolution; i++) {
				curve.bezier(i / testResolution);
			}
			timer.printElapsed("Bezier Time: ");
			timer.reset();
			for (int i = 0; i <= testResolution; i++) {
				curve.bezierX(i / testResolution);
			}
			timer.printElapsed("Bezier X Time: ");
			timer.reset();
			for (int i = 0; i <= testResolution; i++) {
				curve.bezierY(i / testResolution);
			}
			timer.printElapsed("Bezier Y Time: ");
		}
		if (testdeCasteljauSpeed) {
			timer.reset();
			for (int i = 0; i <= testResolution; i++) {
				curve.deCasteljau(i / testResolution);
			}
			timer.printElapsed("deCasteljau Time: ");
			timer.reset();
			for (int i = 0; i <= testResolution; i++) {
				curve.deCasteljauX(i / testResolution);
			}
			timer.printElapsed("deCasteljau X Time: ");
			timer.reset();
			for (int i = 0; i <= testResolution; i++) {
				curve.deCasteljauY(i / testResolution);
			}
			timer.printElapsed("deCasteljau Y Time: ");
		}
		if (graphCurves) {
			ArrayList<Vector> bezier = new ArrayList<Vector>();
			for (int i = 0; i <= testResolution; i++) {
				bezier.add(curve.bezier(i / testResolution));
			}
			double[][] bezierPoints = convertToArray(bezier);
			FalconLinePlot fig1 = new FalconLinePlot(bezierPoints[0], bezierPoints[1], Color.blue, Color.green);
			fig1.yGridOn();
			fig1.xGridOn();
			fig1.setYLabel("Y");
			fig1.setXLabel("X");
			fig1.setYTic(-3, 5, 0.5);
			fig1.setXTic(-1, 5, 0.5);
			fig1.setTitle("Bezier Graph");

			ArrayList<Vector> deCasteljau = new ArrayList<Vector>();
			for (int i = 0; i <= testResolution; i++) {
				deCasteljau.add(curve.deCasteljau(i / testResolution));
			}
			double[][] deCasteljauPoints = convertToArray(deCasteljau);
			FalconLinePlot fig2 = new FalconLinePlot(deCasteljauPoints[0], deCasteljauPoints[1], Color.blue,
					Color.green);
			fig2.yGridOn();
			fig2.xGridOn();
			fig2.setYLabel("Y");
			fig2.setXLabel("X");
			fig2.setYTic(-3, 5, 0.5);
			fig2.setXTic(-1, 5, 0.5);
			fig2.setTitle("deCasteljau Graph");
		}
		if (testArcLength) {
			timer.reset();
			System.out.println("Arc length: " + curve.getDistance(1));
			timer.printElapsed("Time to calculate LUT: ");
		}
	}

	public static void main(String[] args) {
		test();
	}

	private static class Timer {
		private long startTime = System.currentTimeMillis();

		public void reset() {
			startTime = System.currentTimeMillis();
		}

		public long elapsed() {
			return System.currentTimeMillis() - startTime;
		}

		public void printElapsed(String message) {
			System.out.println(message + elapsed());
		}
	}

	public static double[][] convertToArray(List<Vector> v) {
		double[][] result = new double[2][v.size()];
		for (int i = 0; i < v.size(); i++) {
			result[0][i] = v.get(i).x;
			result[1][i] = v.get(i).y;
		}
		return result;
	}

}
