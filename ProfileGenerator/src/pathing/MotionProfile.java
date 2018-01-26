package pathing;

import java.util.ArrayList;
import java.util.List;

public class MotionProfile {

	public final List<TrajectoryPoint> centerPath;

	public final ArrayList<TrajectoryPoint> leftPath;
	public final ArrayList<TrajectoryPoint> rightPath;

	public MotionProfile(List<TrajectoryPoint> centerPath) {
		this.centerPath = centerPath;
		leftPath = null;
		rightPath = null;
	}

	public MotionProfile(List<TrajectoryPoint> centerPath, double robotWidth) {
		this.centerPath = centerPath;
		leftPath = shiftPath(centerPath, -robotWidth / 2);
		rightPath = shiftPath(centerPath, robotWidth / 2);
	}

	private ArrayList<TrajectoryPoint> shiftPath(List<TrajectoryPoint> center, double normalDistance) {
		return null;
	}

	/*
	 * Returns a new motion profile with trajectory position and velocity in sensor
	 * units and sensor units per 100ms, which is what the Talon requires
	 */
	public MotionProfile convertToTalonUnits(double sensorUnitsPerYourUnits) {
		return null;
	}

}