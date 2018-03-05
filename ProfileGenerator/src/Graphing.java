
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import math.Util;
import pathing.Path;
import pathing.Path.TrajectoryHolder;
import pathing.TrajectoryPoint;
import plot.FalconLinePlot;

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
			// System.out.println(x[i] + ", " + y[i]);
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
		addRectangle(fig2, Field.Scale.LEFT_NULL_ZONE, Color.BLACK);
		addRectangle(fig2, Field.Scale.RIGHT_NULL_ZONE, Color.BLACK);
		addRectangle(fig2, Field.Scale.LEFT_PLATE, Color.BLUE);
		addRectangle(fig2, Field.Scale.RIGHT_PLATE, Color.BLUE);
		addRectangle(fig2, Field.Scale.PLATFORM, Color.RED);
		addRectangle(fig2, Field.Switch.BOUNDARY, Color.GREEN);
		addRectangle(fig2, Field.Zones.EXCHANGE_ZONE, Color.ORANGE);
		addRectangle(fig2, Field.Zones.POWER_CUBE_ZONE, Color.PINK);
		for (int i = 0; i < Field.Switch.CUBES.length; i++) {
			addRectangle(fig2, Field.Switch.CUBES[i], Color.YELLOW);
		}
	}

	private static void addRectangle(FalconLinePlot fig, Rectangle2D r, Color lineColor) {
		fig.addData(getLeft(r), lineColor);
		fig.addData(getRight(r), lineColor);
		fig.addData(getTop(r), lineColor);
		fig.addData(getBottom(r), lineColor);
	}

	private static double[][] getLeft(Rectangle2D r) {
		return get(r.getX(), r.getY(), r.getX(), r.getMaxY());
	}

	private static double[][] getRight(Rectangle2D r) {
		return get(r.getMaxX(), r.getY(), r.getMaxX(), r.getMaxY());
	}

	private static double[][] getTop(Rectangle2D r) {
		return get(r.getX(), r.getMaxY(), r.getMaxX(), r.getMaxY());
	}

	private static double[][] getBottom(Rectangle2D r) {
		return get(r.getX(), r.getY(), r.getMaxX(), r.getY());
	}

	private static final int resolution = 100;

	private static double[][] get(double x0, double y0, double x1, double y1) {
		double[][] line = new double[resolution][2];
		double yInc = (y1 - y0) / (resolution - 1);
		double xInc = (x1 - x0) / (resolution - 1);
		for (int i = 0; i < resolution; i++) {
			line[i][0] = x0 + i * xInc;
			line[i][1] = y0 + i * yInc;
		}
		return line;
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
