package math;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import pathing.Path;
import pathing.Path.TrajectoryHolder;
import pathing.Waypoint;
import plot.FalconLinePlot;
import plot.Graphing;

public class Test {

	public static void test() {
		Timer timer = new Timer();
		timer.reset();

		BezierCurve curve;

		// WayPoint first = new WayPoint(0, 0, Math.PI / 2, 0);
		// WayPoint last = new WayPoint(150, 150, 0, 0);
		// curve = PathCreation.connectWaypointsWithBezier(first, last, 0.5);

		Vector a = new Vector(0, 0);
		Vector a1 = new Vector(0, 50);
		Vector b = new Vector(0, 100);
		Vector c = new Vector(50, 100);
		Vector d = new Vector(100, 100);
		Vector e1 = new Vector(100, 50);
		Vector e = new Vector(100, 0);
		curve = new BezierCurve(a, a1, b, c, d, e1, e);

		// Vector a = new Vector(0, 0);
		// Vector a1 = new Vector(0, 50);
		// Vector b = new Vector(0, 100);
		// Vector c = new Vector(50, 100);
		// Vector d = new Vector(100, 100);
		// Vector e1 = new Vector(100, 150);
		// Vector e = new Vector(100, 200);
		// curve = new BezierCurve(a, a1, b, c, d, e1, e);

		timer.printElapsed("Time to make bezier: ");

		// System.out.println(curve);
		// System.out.println(curve.derivative());
		// System.out.println(curve.derivative().derivative());

		// LookupTable lut = new LookupTable(d -> d * d + 1, 0, 9, 10);
		// System.out.println(lut.getOutput(2.5));
		// graphControlPoints(curve);
		// testdeCasteljauSpeed(curve);
		// graphDeCasteljau(curve);
		// testArcLengthSpeed(curve);
		// graphCurvature(curve);
		// testLUTinvertibility(curve);
		testPathCreation();
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
		controlPointsGraph.setMaxMin(0, 200, 0, 200);
		controlPointsGraph.setTitle("Control Points");
	}

	public static void testdeCasteljauSpeed(BezierCurve curve) {
		double testResolution = 5000;
		Timer timer = new Timer();
		timer.reset();
		for (int i = 0; i <= testResolution; i++) {
			curve.deCasteljau(i / testResolution);
		}
		timer.printElapsed("deCasteljau Time: ");
		// timer.reset();
		// for (int i = 0; i <= testResolution; i++) {
		// curve.deCasteljauX(i / testResolution);
		// }
		// timer.printElapsed("deCasteljau X Time: ");
		// timer.reset();
		// for (int i = 0; i <= testResolution; i++) {
		// curve.deCasteljauY(i / testResolution);
		// }
		// timer.printElapsed("deCasteljau Y Time: ");
	}

	public static void graphDeCasteljau(BezierCurve curve) {
		double testResolution = 500;
		ArrayList<Vector> deCasteljau = new ArrayList<Vector>();
		for (int i = 0; i < testResolution; i++) {
			deCasteljau.add(curve.deCasteljau(i / testResolution));
		}
		double[][] deCasteljauPoints = convertToArray(deCasteljau);
		FalconLinePlot fig2 = new FalconLinePlot(deCasteljauPoints[0], deCasteljauPoints[1], Color.blue, Color.green);
		fig2.yGridOn();
		fig2.xGridOn();
		fig2.setYLabel("Y");
		fig2.setXLabel("X");
		fig2.setTitle("deCasteljau Graph");
		double[] x = new double[curve.controlPoints.length];
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			x[i] = curve.controlPoints[i].x;
			y[i] = curve.controlPoints[i].y;
		}
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
		System.out.println("Single integral arc length: " + curve.arcLengthIntegral(0, 1));
		System.out.println();
		timer.reset();
		System.out.println("Default table size arc length: " + curve.getArcLength(1));
		timer.printElapsed("Time to calculate with default size: ");
		System.out.println();

		System.out.println("Warning: Inaccurate tests due to java bytecode optimization");
		System.out.println();

		timer.reset();
		curve.getTable().setResolution(5000);
		System.out.println("Arc length (5000 pts): " + curve.getArcLength(1));
		timer.printElapsed("Time to calculate LUT: ");
		System.out.println();

		timer.reset();
		curve.getTable().setResolution(2500);
		System.out.println("Arc length (2500 pts): " + curve.getArcLength(1));
		timer.printElapsed("Time to calculate LUT: ");
		System.out.println();

		timer.reset();
		curve.getTable().setResolution(500);
		System.out.println("Arc length (500 pts): " + curve.getArcLength(1));
		timer.printElapsed("Time to calculate LUT: ");
		System.out.println();

		curve.getTable().setResolution(50);
		System.out.println("Arc length (50 pts): " + curve.getArcLength(1));
		timer.printElapsed("Time to calculate LUT: ");
		System.out.println();
	}

	public static void graphCurvature(BezierCurve curve) {
		int resolution = 500;
		double increment = 1.0 / (resolution - 1);
		double[] inputs = new double[resolution];
		double[] distance = new double[resolution];
		double[] curvature = new double[resolution];
		double maxC = Math.abs(curve.curvature(0));
		double minC = Double.MAX_VALUE;
		Timer timer = new Timer();
		timer.reset();
		for (int i = 0; i < resolution; i++) {
			double t = i * increment;
			inputs[i] = t;
			distance[i] = curve.getArcLength(t);
			curvature[i] = curve.curvature(t);
			maxC = Math.max(maxC, Math.abs(curve.curvature(t)));
			minC = Math.min(minC, Math.abs(curve.curvature(t)));
			// System.out.printf("%.6f, %.6f%n", inputs[i], curvature[i]);
		}
		timer.printElapsed("Time to calculate curvatures: ");
		System.out.println("Max curvature: " + maxC);
		System.out.println("Min curvature: " + minC);
		System.out.println();

		FalconLinePlot fig2 = new FalconLinePlot(distance, curvature, Color.blue, Color.green);
		fig2.yGridOn();
		fig2.xGridOn();
		fig2.setMaxMin(0, 200, -0.03, 0);
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
		int resolution = 5000;
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
			// System.out.printf("%.12f, %.12f, %.12f, %.12f%n", inputs[i], inputs2[i],
			// error[i], distance[i]);
		}
	}

	public static void testPathCreation() {
		Waypoint a = new Waypoint(5, 5, Math.PI / 2, 0);
		Waypoint b = new Waypoint(155, 155, 0, 0);
		Path p = new Path(a, true);
		p.addWaypoint(b);
		double dt = 0.01;
		p.generateProfile(50, 25);
		TrajectoryHolder sides = p.getTrajectoryPoints(15, dt);
		System.out.println(p.getSegments().length);
		System.out.println(p.getProfile().endPos());
		// Test.graphDeCasteljau((BezierCurve) p.getSegments()[0].curve);
		Graphing.graphMyPath(p, p.getTrajectoryPoints(15, dt), dt);
	}

	public static void main(String[] args) {
		test();
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
