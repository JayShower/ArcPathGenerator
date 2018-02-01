package plot;

import java.awt.Color;

import jaci.pathfinder.Trajectory;
import math.BezierCurve;
import math.Vector;
import pathing.Path;
import pathing.Path.TrajectoryHolder;

public class Graphing {

	public static void graphPathfinder(Trajectory center, Trajectory left, Trajectory right) {
		int points = center.length();
		System.out.printf("Lengths: %d, %d, %d%n", center.length(), left.length(), right.length());
		double[] times = new double[points];
		double[] leftPos = new double[points];
		double[] rightPos = new double[points];
		double[] leftVel = new double[points];
		double[] rightVel = new double[points];
		double[] leftAcc = new double[points];
		double[] rightAcc = new double[points];
		double[] x = new double[points];
		double[] y = new double[points];
		for (int i = 0; i < points; i++) {
			Trajectory.Segment centerSeg = center.get(i);
			Trajectory.Segment leftSeg = left.get(i);
			Trajectory.Segment rightSeg = right.get(i);
			times[i] = i * rightSeg.dt * i;
			leftPos[i] = leftSeg.position;
			rightPos[i] = rightSeg.position;
			leftVel[i] = leftSeg.velocity;
			rightVel[i] = rightSeg.velocity;
			leftAcc[i] = leftSeg.acceleration;
			rightAcc[i] = rightSeg.acceleration;
			x[i] = centerSeg.x;
			y[i] = centerSeg.y;
		}
		plot2(times, leftPos, rightPos, "Pathfinder Pos vs Time, L=B,R=R");
		plot2(times, leftVel, rightVel, "Pathfinder Vel vs Time, L=B,R=R");
		plot2(times, leftAcc, rightAcc, "Pathfinder Acc vs Time, L=B,R=R");
		plot1(x, y, "Pathfinder X vs Y");
	}

	public static void graphMyPath(Path center, TrajectoryHolder sides, double dt) {
		double[] times = new double[sides.left.length];
		double[] leftPos = new double[sides.left.length];
		double[] rightPos = new double[sides.right.length];
		double[] leftVel = new double[sides.left.length];
		double[] rightVel = new double[sides.right.length];
		double[] leftAcc = new double[sides.left.length];
		double[] rightAcc = new double[sides.right.length];
		double[] curvatures = new double[sides.right.length];
		double[] x = new double[sides.right.length];
		double[] y = new double[sides.right.length];
		BezierCurve curve = ((BezierCurve) center.pathSegments[0].curve);
		int currentSegment = 0;
		for (int i = 0; i < times.length; i++) {
			times[i] = i * dt;
			double pos = center.profile.stateByTimeClamped(i * dt).pos();
			if (pos > center.pathSegments[currentSegment].curve.getTotalArcLength()) {
				if (currentSegment + 1 >= center.pathSegments.length)
					break;
				currentSegment++;
			}
			leftPos[i] = sides.left[i].position;
			rightPos[i] = sides.right[i].position;
			leftVel[i] = sides.left[i].velocity;
			rightVel[i] = sides.right[i].velocity;
			leftAcc[i] = sides.left[i].acceleration;
			rightAcc[i] = sides.right[i].acceleration;
			Vector v = center.pathSegments[currentSegment].curve.getPointAtArcLength(pos);
			x[i] = v.x;
			y[i] = v.y;
			curvatures[i] = curve.curvature(curve.tFromArcLength(center.profile.stateByTime(i * dt).get().pos()))
					* -200;
		}
		plot2(times, leftPos, rightPos, "My Pos vs Time, L=B,R=R");
		plot3(times, leftVel, rightVel, curvatures, "My Vel vs Time, L=B,R=R");
		plot2(times, leftAcc, rightAcc, "My Acc vs Time, L=B,R=R");
		plot1(x, y, "My X vs Y");
	}

	private static void plot1(double[] x, double[] y1, String title) {
		FalconLinePlot fig2 = new FalconLinePlot(x, y1, Color.BLUE);
		fig2.yGridOn();
		fig2.xGridOn();
		// fig2.setMaxMin(0, 200, 0, 200);
		fig2.setTitle(title);
	}

	private static void plot2(double[] x, double[] y1, double[] y2, String title) {
		FalconLinePlot fig2 = new FalconLinePlot(x, y1, Color.BLUE);
		fig2.yGridOn();
		fig2.xGridOn();
		fig2.addData(x, y2, Color.RED);
		fig2.setTitle(title);
	}

	private static void plot3(double[] x, double[] y1, double[] y2, double[] y3, String title) {
		FalconLinePlot fig2 = new FalconLinePlot(x, y1, Color.BLUE);
		fig2.yGridOn();
		fig2.xGridOn();
		fig2.addData(x, y2, Color.RED);
		fig2.addData(x, y3, Color.GREEN);
		fig2.setTitle(title);
	}
}
