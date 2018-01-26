package pathing;

import math.CartesianPoint;
import math.DirectedLineSegment;
import math.Slope;

public class PathLine extends PathSegment {

	public final DirectedLineSegment lineSegment;

	PathLine(double startVelocity, double endVelocity, double maxAcceleration, DirectedLineSegment lineSegment) {
		super(startVelocity, endVelocity, maxAcceleration);
		this.lineSegment = lineSegment;
	}

	@Override
	public PathSegment getLeftSegment(double robotWidth) {
		Slope slope = new Slope(lineSegment);
		double startX = lineSegment.start.x - slope.sine * robotWidth;
		double startY = lineSegment.start.y + slope.cosine * robotWidth;
		CartesianPoint start = new CartesianPoint(startX, startY);
		double endX = lineSegment.end.x - slope.sine * robotWidth;
		double endY = lineSegment.end.y + slope.cosine * robotWidth;
		CartesianPoint end = new CartesianPoint(endX, endY);
		DirectedLineSegment segment = new DirectedLineSegment(start, end);
		return new PathLine(this.startVelocity, this.endVelocity, this.maxAcceleration, segment);
	}

	@Override
	public PathSegment getRightSegment(double robotWidth) {
		Slope slope = new Slope(lineSegment);
		double startX = lineSegment.start.x + slope.sine * robotWidth;
		double startY = lineSegment.start.y - slope.cosine * robotWidth;
		CartesianPoint start = new CartesianPoint(startX, startY);
		double endX = lineSegment.end.x + slope.sine * robotWidth;
		double endY = lineSegment.end.y - slope.cosine * robotWidth;
		CartesianPoint end = new CartesianPoint(endX, endY);
		DirectedLineSegment segment = new DirectedLineSegment(start, end);
		return new PathLine(this.startVelocity, this.endVelocity, this.maxAcceleration, segment);
	}

	@Override
	public double endPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double timeToComplete() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double velocityAtTime(double t) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double positionAtTime(double t) {
		// TODO Auto-generated method stub
		return 0;
	}

}