package plot;

import java.awt.Color;
import java.util.Arrays;

import math.Util;
import pathing.Path;
import pathing.Path.TrajectoryHolder;
import pathing.TrajectoryPoint;

public class Graphing {

	public static void graphMyPath(Path center, TrajectoryHolder sides, double dt) {
		// System.out.println(sides.left.length);
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
		int currentSegment = 0;
		double segmentLengthSum = 0;
		for (int i = 0; i < times.length; i++) {
			times[i] = i * dt;
			double pos = center.getProfile().stateByTimeClamped(i * dt).pos();
			double currentLength = center.getSegments()[currentSegment].curve.getTotalArcLength();
			if (pos > currentLength + segmentLengthSum) {
				if (currentSegment + 1 >= center.getSegments().length)
					break;
				currentSegment++;
				segmentLengthSum += currentLength;
			}
			TrajectoryPoint test = sides.left[i];
			double t = test.position;
			leftPos[i] = sides.left[i].position;
			rightPos[i] = sides.right[i].position;
			leftVel[i] = sides.left[i].velocity;
			rightVel[i] = sides.right[i].velocity;
			leftAcc[i] = sides.left[i].acceleration;
			rightAcc[i] = sides.right[i].acceleration;
			x[i] = sides.left[i].x;
			y[i] = sides.left[i].y;
			System.out.println(x[i] + ", " + y[i]);
			// Vector v2 = curve.getPointAtArcLength(pos);
			// System.out.println(v2.x + ", " + v2.y);
			curvatures[i] = center.getSegments()[currentSegment].curve.getCurvatureAtArcLength(
					center.getProfile().stateByTime(i * dt).get().pos() - segmentLengthSum) * -200;
		}
		System.out.println("X is increasing: " + Util.isIncreasing(x));
		System.out.println("Y is increasing: " + Util.isIncreasing(y));
		plot1(x, y, "My X vs Y");
		// plot2(times, leftPos, rightPos, "My Pos vs Time, L=B,R=R");
		// plot3(times, leftVel, rightVel, curvatures, "My Vel vs Time, L=B,R=R");
		// plot2(times, leftAcc, rightAcc, "My Acc vs Time, L=B,R=R");
	}

	private static void plot1(double[] x, double[] y1, String title) {
		FalconLinePlot fig2 = new FalconLinePlot(x, y1, Color.BLUE, Color.BLUE);
		fig2.yGridOn();
		fig2.xGridOn();
		double maxX = Arrays.stream(x).max().getAsDouble();
		double maxY = Arrays.stream(y1).max().getAsDouble();
		double max = Math.max(maxX, maxY);
		fig2.setMaxMin(0, max, 0, max);
		fig2.setTitle(title);
	}

	private static void plot2(double[] x, double[] y1, double[] y2, String title) {
		FalconLinePlot fig2 = new FalconLinePlot(x, y1, Color.BLUE, Color.BLUE);
		fig2.yGridOn();
		fig2.xGridOn();
		fig2.addData(x, y2, Color.RED, Color.RED);
		fig2.setTitle(title);
	}

	private static void plot3(double[] x, double[] y1, double[] y2, double[] y3, String title) {
		FalconLinePlot fig2 = new FalconLinePlot(x, y1, Color.BLUE, Color.BLUE);
		fig2.yGridOn();
		fig2.xGridOn();
		fig2.addData(x, y2, Color.RED, Color.RED);
		fig2.addData(x, y3, Color.GREEN, Color.GREEN);
		fig2.setTitle(title);
	}
}
