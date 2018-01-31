package pathing;

import math.Curve;
import math.Vector;

public class PathSegment {

	public final Vector start;
	public final Vector end;
	public final Curve curve;
	public final double absVelocity;

	public PathSegment(Vector start, Vector end, double absVelocity, Curve curve) {
		this.start = start;
		this.end = end;
		this.curve = curve;
		this.absVelocity = absVelocity;
	}

}