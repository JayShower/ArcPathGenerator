package pathing;

import java.util.ArrayList;

import math.Vector;

public final class Path2 {

	private ArrayList<PathSegment> segments = new ArrayList<>();
	public final WayPoint first;
	public final Vector positionShift;
	public final Vector headingShift;
	public final boolean driveForwards;

	public Path2(WayPoint first, boolean driveForwards) {
		this(first, new Vector(0, 0), new Vector(0), driveForwards);
	}

	// you might want to use this constructor if your robot starts at an angle to
	// the DriverStation wall. (E.g., In 2017, we sometimes started our robot
	// pointed towards the hoppers).
	// If you don't use this, your path will be relative to robot starting state.
	public Path2(WayPoint first, Vector positionShift, Vector headingShift, boolean driveForwards) {
		this.first = first;
		this.positionShift = positionShift;
		this.headingShift = headingShift;
		this.driveForwards = driveForwards;

	}

	public void addWaypoint(WayPoint w) {
		addWaypoint(w, 0.5);
	}

	public void addWaypoint(WayPoint w, double midControlPercent) {

	}

	private void addIntersecting(WayPoint w) {

	}

	private void addParallel(WayPoint w) {

	}

	private void addAntiparallel(WayPoint w) {

	}

	public PathSegment[] getPathSegmentArray() {
		return segments.toArray(new PathSegment[segments.size()]);
	}
}