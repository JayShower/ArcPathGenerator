package math;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import pathing.PathCreation;
import pathing.Waypoint;
import plot.FalconLinePlot;

public class Test {

	public static void test() {
		Waypoint first = new Waypoint(0, 0, Math.PI / 2, 0);
		Waypoint last = new Waypoint(50, 50, 0, 0);
		Timer timer = new Timer();
		timer.reset();
		BezierCurve curve = PathCreation.connectWaypointsWithBezier(first, last, 0.5);
		// BezierCurve curve = new BezierCurve(new Vector(0, 0), new Vector(0, 5), new
		// Vector(0, 10), new Vector(5, 10),
		// new Vector(10, 10));
		timer.printElapsed("Time to make bezier: ");
		System.out.println(curve);
		System.out.println(curve.derivative);
		System.out.println(curve.derivative.derivative);
		System.out.println();

		// LookupTable lut = new LookupTable(d -> d * d + 1, 0, 9, 10);
		// System.out.println(lut.getOutput(2.5));
		// graphControlPoints(curve);
		// testBezierSpeed(curve);
		testdeCasteljauSpeed(curve);
		// graphDeCasteljau(curve);
		testArcLengthSpeed(curve);
		// graphCurvature(curve);
		// testLUTinvertibility(curve);
	}

	public static void graphControlPoints(BezierCurve curve) {
		double[] x = new double[curve.controlPoints.length];
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			x[i] = curve.controlPoints[i].x;
			y[i] = curve.controlPoints[i].y;
		}
		FalconLinePlot controlPointsGraph = new FalconLinePlot(x, y, Color.red, Color.green);
		controlPointsGraph.yGridOn();
		controlPointsGraph.xGridOn();
		controlPointsGraph.setYLabel("Y");
		controlPointsGraph.setXLabel("X");
		controlPointsGraph.setMaxMin(0, 10, 0, 10);
		controlPointsGraph.setTitle("Control Points");
		System.out.println();
	}

	public static void testBezierSpeed(BezierCurve curve) {
		// double testResolution = 500;
		// Timer timer = new Timer();
		// timer.reset();
		// for (int i = 0; i <= testResolution; i++) {
		// curve.bezier(i / testResolution);
		// }
		// timer.printElapsed("Bezier Time: ");
		// timer.reset();
		// for (int i = 0; i <= testResolution; i++) {
		// curve.bezierX(i / testResolution);
		// }
		// timer.printElapsed("Bezier X Time: ");
		// timer.reset();
		// for (int i = 0; i <= testResolution; i++) {
		// curve.bezierY(i / testResolution);
		// }
		// timer.printElapsed("Bezier Y Time: ");
		// System.out.println();
	}

	public static void testdeCasteljauSpeed(BezierCurve curve) {
		double testResolution = 5000;
		Timer timer = new Timer();
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
		System.out.println();
	}

	public static void graphDeCasteljau(BezierCurve curve) {
		double testResolution = 500;
		ArrayList<Vector> deCasteljau = new ArrayList<Vector>();
		for (int i = 0; i <= testResolution; i++) {
			deCasteljau.add(curve.deCasteljau(i / testResolution));
		}
		double[] x = new double[curve.controlPoints.length];
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			x[i] = curve.controlPoints[i].x;
			y[i] = curve.controlPoints[i].y;
		}
		double[][] deCasteljauPoints = convertToArray(deCasteljau);
		FalconLinePlot fig2 = new FalconLinePlot(deCasteljauPoints[0], deCasteljauPoints[1], Color.blue, Color.green);
		fig2.yGridOn();
		fig2.xGridOn();
		fig2.setYLabel("Y");
		fig2.setXLabel("X");
		fig2.setTitle("deCasteljau Graph");
		// FalconLinePlot controlPointsGraph = new FalconLinePlot(x, y, Color.red,
		// Color.green);
		// controlPointsGraph.yGridOn();
		// controlPointsGraph.xGridOn();
		// controlPointsGraph.setYLabel("Y");
		// controlPointsGraph.setXLabel("X");
		// controlPointsGraph.setMaxMin(0, 60, 0, 60);
		System.out.println();
	}

	public static void testArcLengthSpeed(BezierCurve curve) {

		Timer timer = new Timer();
		timer.reset();
		curve.tToArcLengthTable().setResolution(5000);
		System.out.println("Arc length (5000 pts): " + curve.getArcLength(1));
		timer.printElapsed("Time to calculate LUT: ");

		timer.reset();
		curve.tToArcLengthTable().setResolution(2500);
		System.out.println("Arc length (2500 pts): " + curve.getArcLength(1));
		timer.printElapsed("Time to calculate LUT: ");

		timer.reset();
		curve.tToArcLengthTable().setResolution(500);
		System.out.println("Arc length (500 pts): " + curve.getArcLength(1));
		timer.printElapsed("Time to calculate LUT: ");
		System.out.println();
	}

	public static void graphCurvature(BezierCurve curve) {
		int resolution = 500;
		double increment = 1.0 / (resolution - 1);
		double[] inputs = new double[resolution];
		double[] distance = new double[resolution];
		double[] curvature = new double[resolution];
		Timer timer = new Timer();
		timer.reset();
		for (int i = 0; i < resolution; i++) {
			double t = i * increment;
			inputs[i] = t;
			distance[i] = curve.getArcLength(t);
			curvature[i] = curve.curvature(t);
			// System.out.printf("%.6f, %.6f%n", inputs[i], curvature[i]);
		}
		timer.printElapsed("Time to calculate curvatures: ");

		System.out.println();

		FalconLinePlot fig2 = new FalconLinePlot(distance, curvature, Color.blue, Color.green);
		fig2.yGridOn();
		fig2.xGridOn();
		// fig2.setMaxMin(0, 100, -12000, 0);
		fig2.setYLabel("Curvature");
		fig2.setXLabel("Distance");
		fig2.setTitle("Curvature vs distance");

		FalconLinePlot fig3 = new FalconLinePlot(inputs, curvature, Color.blue, Color.green);
		fig3.yGridOn();
		fig3.xGridOn();
		// fig3.setMaxMin(0, 80, -12000, 0);
		fig3.setYLabel("Curvature");
		fig3.setXLabel("T");
		fig3.setTitle("Curvature vs t");
	}

	public static void testLUTinvertibility(BezierCurve curve) {
		int resolution = 1000;
		double increment = 1.0 / (resolution - 1);
		double[] inputs = new double[resolution];
		double[] distance = new double[resolution];
		double[] inputs2 = new double[resolution];
		double[] error = new double[resolution];
		for (int i = 0; i < resolution; i++) {
			double t = i * increment;
			inputs[i] = t;
			distance[i] = curve.getArcLength(t);
			inputs2[i] = curve.tFromArcLength(distance[i]);
			error[i] = inputs[i] - inputs2[i];
			System.out.printf("%.4f, %.4f, %.4f, %.4f%n", inputs[i], inputs2[i], error[i], distance[i]);
		}
	}

	public static void main(String[] args) {
		test();
	}

	private static class Timer {
		private long startTime = System.nanoTime();

		public void reset() {
			startTime = System.nanoTime();
		}

		public double elapsed() {
			return (System.nanoTime() - startTime) / 1.0e6;
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
