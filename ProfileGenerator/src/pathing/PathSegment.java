package pathing;

import math.Curve;

public class PathSegment {

	public final Waypoint start;
	public final Waypoint end;
	public final Curve curve;

	public PathSegment(Waypoint start, Waypoint end, Curve curve) {
		this.start = start;
		this.end = end;
		this.curve = curve;
	}

}