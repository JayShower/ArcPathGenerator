package pathing;

import math.Curve;

public class PathSegment {

	public final WayPoint start;
	public final WayPoint end;
	public final Curve curve;

	public PathSegment(WayPoint start, WayPoint end, Curve curve) {
		this.start = start;
		this.end = end;
		this.curve = curve;
	}

}