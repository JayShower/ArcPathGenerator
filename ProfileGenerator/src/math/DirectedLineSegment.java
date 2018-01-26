package math;

public class DirectedLineSegment {

	public final CartesianPoint start;
	public final CartesianPoint end;

	public DirectedLineSegment(CartesianPoint start, CartesianPoint end) {
		this.start = start;
		this.end = end;
	}

	public double getLength() {
		return start.getDistance(end);
	}

}